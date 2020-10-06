package store;

import commons.BIT;

public class StoreFlags implements BitStore{
    private final int[] store;
    private final int wrow;
    private final int rowMask, dataMask;
    private final int rowStart;
    private int seekRow;

    public StoreFlags(int maxRow) {
        store = new int[maxRow + 1];
        this.wrow = 8;

        rowStart = Integer.SIZE - wrow;

        rowMask = (1 << wrow) - 1;
        this.dataMask = (1 << rowStart) - 1;

        BIT.disp(rowMask);
        BIT.disp(dataMask);
    }

    @Override
    public void seek(int integer){
        seekRow = (integer >> rowStart) & rowMask;
//        System.out.println("seekRow=" + seekRow);
    }

    @Override
    public int getSeekRow(){
        return seekRow;
    }

    @Override
    public int getSeekCol() {
        return 0;
    }

    @Override
    public void set(int integer){
        seek(integer);
        store[seekRow] |= (integer & dataMask);
    }

    @Override
    public boolean isSet(int integer){// most appropriate for this datatype
        seek(integer);
        //System.out.println("isSet: ")
        return (store[seekRow] & integer & dataMask) > 0;
    }

    @Override
    public int getState(int integer) {
        seek(integer);
        return store[seekRow] & integer;
    }

    @Override
    public int getEnum(int integer) {
        return (isSet(integer))? integer : integer & ~dataMask;
    }

    @Override
    public int getNumber(int integer) {
        return (isSet(integer))? 1 : 0;
    }

    @Override
    public void drop(int integer){
        seek(integer);
        store[seekRow] &= ~(integer & dataMask);
    }

    @Override
    public int numNonZero(int integer) {
        seek(integer);
        int data = store[seekRow], count = 0;
        for(int i =  0; i < rowStart; i++){
            if((data & 1) == 1){
                count++;
            }
            data >>= 1;
        }
        return count;
    }

    @Override
    public boolean anyNonZero(int integer) {
        seek(integer);
        return (store[seekRow] & dataMask) > 0;
    }

    @Override
    public void disp() {
        System.out.println("Store Flags Display");
        for(int i = 0; i < store.length; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(store[i]));
        }
        System.out.println("======================");
    }
}
