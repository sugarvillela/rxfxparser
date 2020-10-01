package uq;

import static compile.basics.Keywords.UQ_FORMAT;

public class Uq {
    private int c, halt;

    public Uq(){
        halt = 0x40000000;
        rewind();
    }
    public Uq(int halt){
        this.halt = halt;
        rewind();
    }
    public final void rewind(){
        c = -1;
    }
    public int curr(){
        return c;
    }
    public int next(){
        return ++c;
    }
    public boolean hasNext(){
        return (c + 1) < halt;
    }
    @Override
    public String toString(){
        return String.format(UQ_FORMAT, next());
    }
    public String toString(String prefix){
        return prefix + String.format(UQ_FORMAT, next());
    }
}
