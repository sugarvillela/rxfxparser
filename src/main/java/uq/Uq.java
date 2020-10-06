package uq;

import static compile.basics.Keywords.UQ_FORMAT;

public class Uq implements UniqueItr{
    private final int halt;
    private int c;

    public Uq(){
        halt = 1 << (Integer.SIZE-1);
        rewind();
    }
    public Uq(int halt){
        this.halt = halt;
        rewind();
    }

    @Override
    public final void rewind(){
        c = -1;
    }

    @Override
    public int curr(){
        return c;
    }

    @Override
    public int next(){
        return ++c;
    }

    @Override
    public boolean hasNext(){
        return (c + 1) < halt;
    }

    @Override
    public void newCol() {}

    @Override
    public void newRow() {}

    @Override
    public String toString(){
        return String.format(UQ_FORMAT, next());
    }

    public String toString(String prefix){
        return prefix + String.format(UQ_FORMAT, next());
    }
}
