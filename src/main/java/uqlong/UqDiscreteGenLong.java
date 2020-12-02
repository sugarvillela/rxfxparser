package uqlong;

public class UqDiscreteGenLong implements UqGenCompositeLong {
    private final int wrow, wcol, wval;
    private final int rowStart, colStart;
    private final long rowHalt;//,  colHalt, valHalt;
    private int valShift;
    private final UqGenLong row, col, val;

    public UqDiscreteGenLong(int wrow, int wcol, int wval){
        this.wrow = wrow;
        this.wcol = wcol;
        this.wval = wval;
        rowStart = Long.SIZE - wrow;
        colStart = Long.SIZE - wrow - wcol;
        row = new UqLong((1 << wrow)+1);
        col = new UqLong(colStart/wcol + 1);
        val = new UqLong(1 << wval);
        rowHalt = (1 << wrow) - 1;
        rewind();
    }
    public UqDiscreteGenLong(UqDiscreteGenLong prevState){
        this.wrow = prevState.getWRow();
        this.wcol = prevState.getWCol();
        this.wval = prevState.getWVal();
        rowStart = Long.SIZE - wrow;
        colStart = Long.SIZE - wrow - wcol;
        int colHalt = colStart/wcol;
        row = prevState.getRowGen();
        col = prevState.getColGen();
        val = new UqLong(1 << wval);
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
    public void rewind(long setStart) {
        row.rewind(setStart);
        col.rewind();
        val.rewind();
        row.next();
        col.next();
        valShift = 0;
    }

    @Override
    public long curr() {//?
        return (row.curr() << rowStart) | (col.curr() << colStart) | (val.curr() << valShift);
    }
    @Override
    public long currRowCol() {
        return (row.curr() << rowStart) | (col.curr() << colStart);
    }

    @Override
    public long curRowOffset() {
        return row.curr();
    }

    @Override
    public long next(){
        if(!val.hasNext()){
            newCol();
        }
        long cRow = row.curr(), cCol = col.curr(), cVal = val.next();
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
    public UqGenLong getRowGen() {
        return new UqLong(row);
    }

    @Override
    public UqGenLong getColGen()  {
        return new UqLong(col);
    }
}
