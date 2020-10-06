package uq;

import commons.BIT;

public class UqBoolGen implements UniqueItr{
    private int rowStart, halt;
    private UqShift col;
    private Uq row;

    public UqBoolGen(int wrow){
        rowStart = Integer.SIZE - wrow;
        halt = (1 << wrow) - 1;
        col = new UqShift(rowStart);
        row = new Uq();
        row.next();
        rewind();
    }

    @Override
    public final void rewind(){
        col.rewind();
    }

    @Override
    public int curr() {//?
        return col.curr() | (row.curr() << rowStart);
    }

    @Override
    public int next(){
        if(!col.hasNext()){
            col.rewind();
            row.next();
        }
        return col.next() | (row.curr() << rowStart);
    }

    @Override
    public boolean hasNext(){
        return row.curr() < halt || (row.curr() == halt && col.hasNext());
    }

    @Override
    public void newCol() {}

    @Override
    public void newRow() {
        col.rewind();
        row.next();
    }

    @Override
    public String toString(){
        return BIT.str(next());
    }
}
