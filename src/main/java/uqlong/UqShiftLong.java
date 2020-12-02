package uqlong;

import commons.BIT;

public class UqShiftLong implements UqGenLong{
    private long c, halt;

    public UqShiftLong(){
        halt = 0;
        rewind();
    }
    public UqShiftLong(int haltBit){
        this.halt = 1 << haltBit;
        rewind();
    }
    public final void rewind(){
        c = 1;
    }

    @Override
    public void rewind(long setStart) {
        c = setStart;
    }

    @Override
    public long curr() {//?
        return c;
    }

    public long next(){
        long temp = c;
        c = c << 1;
        return temp;
    }
    public boolean hasNext(){
        return c != halt; // c == 1 || (c >> 1 ) != halt;
    }

    @Override
    public void newCol() {}

    @Override
    public void newRow() {}

    @Override
    public String toString(){
        return BIT.str(next());
    }
}
