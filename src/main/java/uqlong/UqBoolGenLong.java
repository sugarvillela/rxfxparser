package uqlong;

import commons.BIT;

public class UqBoolGenLong  implements UqGenCompositeLong {
    private final int wrow, rowStart;
    private final long halt;
    private UqShiftLong val;
    private UqGenLong row;

    public UqBoolGenLong(int wrow){
        this.wrow = wrow;
        rowStart = Long.SIZE - wrow;
        halt = (1 << wrow) - 1;
        val = new UqShiftLong(rowStart);
        row = new UqLong();
        row.next();
        rewind();
    }
    public UqBoolGenLong(UqGenCompositeLong prevState){
        this.wrow = prevState.getWRow();
        rowStart = Long.SIZE - wrow;
        halt = (1 << wrow) - 1;
        val = new UqShiftLong(rowStart);
        row = prevState.getRowGen();
        this.newRow(); // next row after prev state
    }
    @Override
    public final void rewind(){
        val.rewind();
    }

    @Override
    public void rewind(long setStart) {
        val.rewind(setStart);
    }

    @Override
    public long curr() {//?
        return (row.curr() << rowStart) | val.curr();
    }

    @Override
    public long currRowCol() {
        return ((row.curr() << (long)rowStart));
    }

    @Override
    public long curRowOffset() {
        return row.curr();
    }

    @Override
    public long next(){
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
        return Long.SIZE - rowStart;
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
    public UqGenLong getRowGen() {
        return new UqLong(row);
    }

    @Override
    public UqGenLong getColGen() {
        return null;
    }
}
