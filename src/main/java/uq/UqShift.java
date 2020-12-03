package uq;

import commons.BIT;

public class UqShift implements UqGen {
    private int c, halt;
    private boolean done;

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
        done = false;
    }

    @Override
    public void rewind(int setStart) {
        c = setStart;
        done = false;
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
        if(c == halt){
            done = true;
        }
        return !done;
    }

    @Override
    public void newCol() {}

    @Override
    public void newRow() {}

    @Override
    public int getHalt() {
        return halt;
    }

    @Override
    public String toString(){
        return BIT.str(next());
    }
}
