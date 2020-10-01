package uq;

public class UqDiscreteGen {
    private final int WORD_LEN = 32;
    private int wordLen, colHalt, rowHalt;
    private int wval, colShift;
    private Uq row, col;

    public UqDiscreteGen(int wrow, int wval){
        wordLen = WORD_LEN - wrow;
        row = new Uq((1 << wrow) - 1);
        col = new Uq((1 << wval) - 1);
        this.wval = wval;
        rowHalt = (1 << wrow) - 1;
        rewind();
    }
    public final void rewind(){
        row.rewind();
        col.rewind();
        row.next();
        col.next();
        colShift = 0;
    }
    public int next(){
        if(!col.hasNext()){
            col.rewind();
            row.next();
            col.next();
            colShift += wval;
        }
        int cCol = col.next(), cRow = row.curr();
        System.out.printf("row=%x, col=%x, shift=%x\n", cRow, cCol, colShift);
        return cCol << colShift| (cRow << wordLen);
    }
    public boolean hasNext(){
        return true;
    }
}
