package store;

import commons.BIT;
import erlog.Erlog;

public class StoreDiscrete implements BitStore{
    private final int[] store;
    private final int wrow, wcol, wval;
    private final int rowMask, colMask, dataMask, valMask;
    private final int rowStart, colStart;
    private int seekRow, seekCol;

    public StoreDiscrete(int maxRow) {
        store = new int[maxRow + 1];
        //CompileInitializer compileInitializer = CompileInitializer.getInstance();
        this.wrow = 8;//compileInitializer.getWRow();
        this.wcol = 4;//compileInitializer.getWCol();
        this.wval = 4;//compileInitializer.getWVal();

        rowStart = Integer.SIZE - wrow;
        colStart = Integer.SIZE - wrow - wcol;

        rowMask = (1 << wrow) - 1;
        colMask = (1 << wcol) - 1;
        dataMask = (1 << colStart) - 1;
        valMask = (1 << wval) - 1;

        BIT.disp(rowMask);
        BIT.disp(colMask);
        BIT.disp(valMask);
        BIT.disp(dataMask);
    }

    @Override
    public void seek(int integer){
        seekRow = (integer >> rowStart) & rowMask;
        seekCol = (integer >> colStart) & colMask;
//        System.out.println("seekRow=" + seekRow);
//        System.out.println("seekCol=" + seekCol);
    }

    @Override
    public int getSeekRow(){
        return seekRow;
    }

    @Override
    public int getSeekCol(){
        return seekCol;
    }

    @Override
    public void set(int integer){
        seek(integer);
        int posMask = valMask << (seekCol*wval);
        store[seekRow] &= ~posMask;
        store[seekRow] |= (posMask & integer);
    }

    @Override
    public boolean isSet(int integer){
        seek(integer);
        int posMask = valMask << (seekCol*wval);
        return (store[seekRow] & posMask) == (integer & posMask);
    }

    @Override
    public int getState(int integer){   // in-place value only
        seek(integer);
        return store[seekRow] & (valMask << (seekCol*wval));
    }

    @Override
    public int getEnum(int integer){    // in-place value with row, col included
        seek(integer);
        return store[seekRow] | (integer & ~dataMask);
    }

    @Override
    public int getNumber(int integer){// value only, right shifted to true value
        seek(integer);
        int target = store[seekRow];
        int out = (target >> (seekCol*wval)) & valMask;
        return out;
    }

    @Override
    public void drop(int integer){
        seek(integer);
        int posMask = valMask << (seekCol*wval);
        store[seekRow] &= ~posMask;
    }

    @Override
    public int numNonZero(int integer) {
        seek(integer);
        int data = store[seekRow], len = colStart/wval, count = 0;
        for(int i =  0; i < len; i++){
            if((data & valMask) > 0){
                count++;
            }
            data >>= wval;
        }
        return count;
    }

    @Override
    public boolean anyNonZero(int integer) {
        seek(integer);
        return (store[seekRow] & dataMask) > 0;
    }

    @Override
    public void disp(){
        System.out.println("Store Discrete Display");
        for(int i = 0; i < store.length; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(store[i]));
        }
        System.out.println("======================");
    }

    public void assertSeek(int integer){
        seekRow = (integer >> rowStart);
        seekCol = (integer >> colStart) & colMask;
        if(seekRow >= store.length){
            Erlog.get(this).set("Row out of range", BIT.str(integer));
        }
        if(seekCol >= (colStart/wval)){
            Erlog.get(this).set("Column out of range", BIT.str(integer));
        }
    }
}
