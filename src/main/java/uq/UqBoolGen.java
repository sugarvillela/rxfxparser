package uq;

import commons.BIT;

public class UqBoolGen {
    private final int WORD_LEN = 32;
    private int wordLen, halt;
    private UqShift col;
    private Uq row;

    public UqBoolGen(int wrow){
        wordLen = WORD_LEN - wrow;
        halt = (1 << wrow) - 1;
        col = new UqShift(wordLen);
        row = new Uq();
        row.next();
        rewind();
    }
    public final void rewind(){
        col.rewind();
    }
    public int next(){
        if(!col.hasNext()){
            col.rewind();
            row.next();
        }
        return col.next() | (row.curr() << wordLen);
    }
    public boolean hasNext(){
        return row.curr() < halt || (row.curr() == halt && col.hasNext());
    }
    @Override
    public String toString(){
        return BIT.str(next());
    }
}
