package uq;

import commons.BIT;

public class UqBoolGen implements UqGenComposite {
    private final int wrow;
    private final int rowStart;
    private final UqShift valGen;
    private final UqGen rowGen;

    public UqBoolGen(int wrow){
        this.wrow = wrow;
        rowStart = Integer.SIZE - wrow;
        valGen = new UqShift(rowStart);
        rowGen = new Uq(1 << wrow);
        rowGen.next();
        rewind();
    }
    public UqBoolGen(UqGenComposite prevState){
        this.wrow = prevState.getWRow();
        rowStart = Integer.SIZE - wrow;
        valGen = new UqShift(rowStart);
        rowGen = prevState.getRowGen();
        this.newRow(); // next row after prev state
    }
    @Override
    public final void rewind(){
        valGen.rewind();
    }

    @Override
    public void rewind(int setStart) {
        valGen.rewind(setStart);
    }

    @Override
    public int curr() {//?
        return (rowGen.curr() << rowStart) | valGen.curr();
    }

    @Override
    public int currRowCol() {
        return (rowGen.curr() << rowStart);
    }

    @Override
    public int curRowOffset() {
        return rowGen.curr();
    }

    @Override
    public int next(){
        if(!valGen.hasNext()){// rowGen.hasNext() &&
            valGen.rewind();
            rowGen.next();
        }
        return valGen.next() | (rowGen.curr() << rowStart);
    }

    @Override
    public boolean hasNext(){
        //System.out.printf("%b, %b \n", rowGen.hasNext(), valGen.hasNext());
        return rowGen.hasNext() || valGen.hasNext();
    }

    @Override
    public void newCol() {}

    @Override
    public void newRow() {
        valGen.rewind();
        rowGen.next();
    }

    @Override
    public int getHalt() {
        return rowGen.getHalt();
    }

    @Override
    public String toString(){
        return BIT.str(next());
    }

    @Override
    public int getWRow() {
        return Integer.SIZE - rowStart;
    }

    @Override
    public int getWCol() {
        return -1;
    }

    @Override
    public int getWVal() {
        return -1;
    }

    @Override
    public UqGen getRowGen() {
        return new Uq(rowGen);
    }

    @Override
    public UqGen getColGen() {
        return null;
    }

}
