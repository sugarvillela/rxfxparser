package uq;

import static compile.basics.Keywords.UQ_FORMAT;

public class Uq implements UqGen {
    private final int halt;
    private int c;
    private boolean done; // lock done state until rewind called

    public Uq(){
        halt = 1 << (Integer.SIZE-1);
        rewind();
    }
    public Uq(int halt){
        this.halt = halt;
        rewind();
    }
    public Uq(UqGen prevState){
        halt = 1 << (Integer.SIZE-1);
        rewind(prevState.next());
    }

    @Override
    public final void rewind(){
        c = -1;
        done = false;
    }

    @Override
    public void rewind(int setStart) {
        c = setStart - 1;
        done = false;
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
        if((c + 1) == halt){
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

    public String toString(String prefix){
        return prefix + String.format(UQ_FORMAT, next());
    }
}
