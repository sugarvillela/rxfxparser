package uq;

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
    private int rowHalt;//,  colHalt, valHalt;
    private int valShift;
    private UqGen row, col, val;

    public UqDiscreteGen(int wrow, int wcol, int wval){
        this.wrow = wrow;
        this.wcol = wcol;
        this.wval = wval;
        rowStart = Integer.SIZE - wrow;
        colStart = Integer.SIZE - wrow - wcol;
        int colHalt = colStart/wcol;
        row = new Uq((1 << wrow)+1);
        col = new Uq(colHalt+1);
        val = new Uq(1 << wval);
        rowHalt = (1 << wrow) - 1;
        rewind();
    }
    public UqDiscreteGen(UqDiscreteGen prevState){
        this.wrow = prevState.getWRow();
        this.wcol = prevState.getWCol();
        this.wval = prevState.getWVal();
        rowStart = Integer.SIZE - wrow;
        colStart = Integer.SIZE - wrow - wcol;
        int colHalt = colStart/wcol;
        row = prevState.getRowGen();
        col = prevState.getColGen();
        val = new Uq(1 << wval);
        rowHalt = (1 << wrow) - 1;
    }
    @Override
    public final void rewind(){
        row.rewind();
        col.rewind();
        val.rewind();
        row.next();
        col.next();
        valShift = 0;
    }

    @Override
    public void rewind(int setStart) {
        row.rewind(setStart);
        col.rewind();
        val.rewind();
        row.next();
        col.next();
        valShift = 0;
    }

    @Override
    public int curr() {//?
        return (row.curr() << rowStart) | (col.curr() << colStart) | (val.curr() << valShift);
    }
    @Override
    public int currRowCol() {
        return (row.curr() << rowStart) | (col.curr() << colStart);
    }

    @Override
    public int curRowOffset() {
        return row.curr();
    }

    @Override
    public int next(){
        if(!val.hasNext()){
            newCol();
        }
        int cRow = row.curr(), cCol = col.curr(), cVal = val.next();
        //System.out.printf("row=%x, col=%x, val=%x, shift=%x\n", cRow, cCol, cVal, valShift);
        return (cRow << rowStart) | (cCol << colStart) | (cVal << valShift);
    }

    @Override
    public boolean hasNext(){
        return row.hasNext();
    }

    @Override
    public void newCol(){
        col.next();
        val.rewind();
        valShift += wval;
        if(!col.hasNext()){//(valShift) >= colStart
            col.rewind();
            col.next();
            row.next();
            valShift = 0;
        }
    }

    @Override
    public void newRow(){
        col.rewind();
        val.rewind();
        row.next();
        col.next();
        valShift = 0;
        //newCol();
//        col.rewind();
//        col.next();
//        val.rewind();
//        row.next();
//        valShift = 0;
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
        return new Uq(row);
    }

    @Override
    public UqGen getColGen()  {
        return new Uq(col);
    }
}
