package uq;

import commons.BIT;

import static compile.basics.Keywords.UQ_FORMAT;

public class UqShift {
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
    public int next(){
        int temp = c;
        c = c << 1;
        return temp;
    }
    public boolean hasNext(){
        return c != halt; // c == 1 || (c >> 1 ) != halt;
    }
    @Override
    public String toString(){
        return BIT.str(next());
    }
}
