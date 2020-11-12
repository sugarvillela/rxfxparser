package uq;

import commons.BIT;

public class UqShift implements UqGen {
    private int c, halt;

    public UqShift(){
        halt = 0;
        rewind();
    }
    public UqShift(int haltBit){
        this.halt = 1 << haltBit;
        rewind();
    }
    public final void rewind(){
        c = 1;
    }

    @Override
    public void rewind(int setStart) {
        c = setStart;
    }

    @Override
    public int curr() {//?
        return c;
    }

    public int next(){
        int temp = c;
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
