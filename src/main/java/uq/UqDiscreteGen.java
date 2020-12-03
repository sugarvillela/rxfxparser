package uq;

import commons.BIT;

/**
 The idea is to use these as enumerations and store their values in
 an integer array to hold states, accessing them by their enum name

 =====================================================================
 Example calculation using default sizes: 5, 3, 3
 r = row number, c = column of interest, v = col holding a value

 Usage   r c v v v v v v v v
 Size    5|3|3|3|3|3|3|3|3|3 = 32

 32 - 5 - 3 leaves 24 bits for discretes
 wval of 3 allows discrete range 0-7
 24/3 gives 8 columns per row
 Total uniques: 2^5 * 8 = 256 small discrete numbers with value 0-7
 =====================================================================
 Same calculation with wval of 4:

 Usage   r c v v v v v v
 Size    5|3|4|4|4|4|4|4 = 32

 32 - 5 - 3 leaves 24 bits for discretes
 wval of 4 allows discrete range 0-15
 24/4 gives 6 columns per row
 Total uniques: 2^5 * 6 = 192 small discrete numbers with value 0-15
 =====Caveats==========================================================
 No mechanism for resetting; max discrete determined by sizes chosen.
 Make val widths divide evenly into the allotted space
 For aligning, you can skip rows/cols by calling newCol and newRow.
 See class StoreBool for the decoding algorithms for row, col and val
 */

public class UqDiscreteGen  implements UqGenComposite {
    private final int wrow, wcol, wval;
    private int rowStart, colStart;
    private int valShift;
    private UqGen rowGen, colGen, valGen;

    public UqDiscreteGen(int wrow, int wcol, int wval){
        this.wrow = wrow;
        this.wcol = wcol;
        this.wval = wval;
        rowStart = Integer.SIZE - wrow;
        colStart = Integer.SIZE - wrow - wcol;
        int colHalt = colStart/wcol + 1;
        //System.out.println("UqDiscreteGen constructor1: colHalt: " + colHalt);
        rowGen = new Uq(1 << wrow);
        colGen = new Uq(colHalt);
        valGen = new Uq(1 << wval);
        rewind();
    }
    public UqDiscreteGen(UqDiscreteGen prevState){
        this.wrow = prevState.getWRow();
        this.wcol = prevState.getWCol();
        this.wval = prevState.getWVal();
        rowStart = Integer.SIZE - wrow;
        colStart = Integer.SIZE - wrow - wcol;
        rowGen = prevState.getRowGen();
        colGen = prevState.getColGen();
        valGen = new Uq(1 << wval);
        newRow();
    }
    @Override
    public final void rewind(){
        rowGen.rewind();
        colGen.rewind();
        valGen.rewind();
        rowGen.next();
        colGen.next();
        valShift = 0;
    }

    @Override
    public void rewind(int setStart) {
        rowGen.rewind(setStart);
        colGen.rewind();
        valGen.rewind();
        rowGen.next();
        colGen.next();
        valShift = 0;
    }

    @Override
    public int curr() {//?
        return (rowGen.curr() << rowStart) | (colGen.curr() << colStart) | (valGen.curr() << valShift);
    }
    @Override
    public int currRowCol() {
        return (rowGen.curr() << rowStart) | (colGen.curr() << colStart);
    }

    @Override
    public int curRowOffset() {
        return rowGen.curr();
    }

    @Override
    public int next(){
        if(!valGen.hasNext()){
            newCol();
        }
        int cRow = rowGen.curr(), cCol = colGen.curr(), cVal = valGen.next();
        //System.out.printf("row=%x, col=%x, val=%x, shift=%x\n", cRow, cCol, cVal, valShift);
        return (cRow << rowStart) | (cCol << colStart) | (cVal << valShift);
    }

    @Override
    public boolean hasNext(){
        return rowGen.hasNext();
    }

    @Override
    public void newCol(){
        //System.out.printf("newCol 1: halt=%d, colGen.curr=%d, hasNext=%b\n", colGen.getHalt(), colGen.curr(), colGen.hasNext());
        colGen.next();
        //System.out.printf("newCol 2: halt=%d, colGen.curr=%d, hasNext=%b\n", colGen.getHalt(), colGen.curr(), colGen.hasNext());
        valGen.rewind();
        valShift += wval;
        if(!colGen.hasNext()){//(valShift) >= colStart
            //System.out.println("!col.hasNext()");
            colGen.rewind();
            colGen.next();
            rowGen.next();
            valShift = 0;
        }
    }

    @Override
    public void newRow(){
        colGen.rewind();
        valGen.rewind();
        rowGen.next();
        colGen.next();
        valShift = 0;
    }

    @Override
    public int getHalt() {
        return rowGen.getHalt();
    }

    @Override
    public int getWRow() {
        return wrow;
    }

    @Override
    public int getWCol() {
        return wcol;
    }

    @Override
    public int getWVal() {
        return wval;
    }

    @Override
    public UqGen getRowGen() {
        return rowGen;
    }

    @Override
    public UqGen getColGen()  {
        return colGen;
    }
}
