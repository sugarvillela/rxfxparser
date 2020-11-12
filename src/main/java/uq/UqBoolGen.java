package uq;

import commons.BIT;

public class UqBoolGen implements UqGenComposite {
    private final int wrow;
    private final int rowStart, halt;
    private UqShift val;
    private UqGen row;

    public UqBoolGen(int wrow){
        this.wrow = wrow;
        rowStart = Integer.SIZE - wrow;
        halt = (1 << wrow) - 1;
        val = new UqShift(rowStart);
        row = new Uq();
        row.next();
        rewind();
    }
    public UqBoolGen(UqGenComposite prevState){
        this.wrow = prevState.getWRow();
        rowStart = Integer.SIZE - wrow;
        halt = (1 << wrow) - 1;
        val = new UqShift(rowStart);
        row = prevState.getRowGen();
        this.newRow(); // next row after prev state
    }
    @Override
    public final void rewind(){
        val.rewind();
    }

    @Override
    public void rewind(int setStart) {
        val.rewind(setStart);
    }

    @Override
    public int curr() {//?
        return (row.curr() << rowStart) | val.curr();
    }

    @Override
    public int currRowCol() {
        return (row.curr() << rowStart);
    }

    @Override
    public int next(){
        if(!val.hasNext()){
            val.rewind();
            row.next();
        }
        return val.next() | (row.curr() << rowStart);
    }

    @Override
    public boolean hasNext(){
        return row.curr() < halt || (row.curr() == halt && val.hasNext());
    }

    @Override
    public void newCol() {}

    @Override
    public void newRow() {
        val.rewind();
        row.next();
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
        return new Uq(row);
    }

    @Override
    public UqGen getColGen() {
        return null;
    }

}
