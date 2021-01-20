package uqlong;

import static langdef.Keywords.UQ_FORMAT;

public class UqLong implements UqGenLong{
    private final long halt;
    private long c;

    public UqLong(){
        halt = 1 << (Long.SIZE-2);
        rewind();
    }
    public UqLong(long halt){
        this.halt = halt;
        rewind();
    }
    public UqLong(UqGenLong prevState){
        halt = 1 << (Long.SIZE-2);
        rewind(prevState.next());
    }

    @Override
    public void rewind() {
        c = -1;
    }

    @Override
    public void rewind(long setStart) {
        c = setStart - 1;
    }

    @Override
    public long curr() {
        return c;
    }

    @Override
    public long next() {
        return ++c;
    }

    @Override
    public boolean hasNext() {
        return (c + 1) < halt;
    }

    @Override
    public void newCol() {}

    @Override
    public void newRow() {}

    public String toString(String prefix){
        return prefix + String.format(UQ_FORMAT, next());
    }
}
