package store;

import commons.BIT;
import compile.basics.Keywords;
import compile.symboltable.ListTableTypeCount;
import compile.symboltable.ListTable;
import erlog.Erlog;

import static compile.basics.Keywords.DATATYPE.*;
import static java.lang.Math.max;

public class StoreFactory {
    private static StoreFactory instance;

    public static StoreFactory getInstance(){
        return (instance == null)? (instance = new StoreFactory()) : instance;
    }

    private StoreFactory() {

//        store = new BitStore[NUM_STORES];
//        store[0] = new StoreFlags(fieldCalculator.listBoolSize);
//        store[1] = new StoreDiscrete(fieldCalculator.listDiscreteSize);
    }

    private static final int NUM_STORES = 5;
    //private final BitStore[] store;

    public BitStore getStore(Keywords.DATATYPE datatype){
        switch(datatype){
            case LIST_BOOLEAN:
                return new StoreFlags(5);
            case LIST_DISCRETE:
                return new StoreDiscrete(5);
            case LIST_NUMBER:
            case LIST_STRING:
        }
        return null;
    }


    public static class StoreNumber implements BitStore{
        private final int[] store;
        private int seekRow;

        StoreNumber(int maxRow){
            store = new int[maxRow];
        }
        @Override
        public void seek(int integer) {
            seekRow = integer;
        }

        @Override
        public int getSeekRow() {
            return seekRow;
        }

        @Override
        public int getSeekCol() {
            return 0;
        }

        @Override
        public void set(int integer) {
            store[integer] = 0;
        }

        @Override
        public boolean isSet(int integer) {
            return store[integer] != 0;
        }

        @Override
        public int getState(int integer) {
            return store[integer];
        }

        @Override
        public int getEnum(int integer) {
            return 0;
        }

        @Override
        public int getNumber(int integer) {
            return 0;
        }

        @Override
        public void drop(int integer) {
            store[integer] = 0;
        }

        @Override
        public int numNonZero(int integer) {
            return 0;
        }

        @Override
        public boolean anyNonZero(int integer) {
            return false;
        }

        @Override
        public void disp() {

        }
    }
}
