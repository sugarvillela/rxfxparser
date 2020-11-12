package store;

import commons.BIT;
import compile.basics.CompileInitializer;
import listtable.ListTable;

public class TestStore {
    public static void discrete(){
        StoreDiscrete store = new StoreDiscrete(5);
        int heading1 =  commons.BIT.binStrToInt("0000_0001_0000_0000_0000_0000_0000_0000");
        int heading2 =  commons.BIT.binStrToInt("0000_0010_0000_0000_0000_0000_0000_0000");
        int heading3 =  commons.BIT.binStrToInt("0000_0011_0000_0000_0000_0000_0000_0000");

        int fred =      commons.BIT.binStrToInt("0000_0011_0000_0000_0000_0000_0000_0011");
        int barney =    commons.BIT.binStrToInt("0000_0011_0100_1110_0000_0000_0000_0000");
        int wilma =     commons.BIT.binStrToInt("0000_0000_0000_0000_0000_0000_0000_1000");
        int pebbles =   commons.BIT.binStrToInt("0000_0010_0010_0000_0000_1001_0000_0000");
        int bamBam =    commons.BIT.binStrToInt("0000_0001_0100_0111_0000_0000_0000_0000");
        //System.out.println(n);
        //BIT.disp(n);
        store.set(fred);
        store.set(barney);
        store.set(wilma);
        store.set(pebbles);
        store.set(bamBam);

        store.disp();

        System.out.print("fred state:    "); BIT.disp(store.getState(fred));
        System.out.print("fred getNumber: "); BIT.disp(store.getNumber(fred));
        System.out.println("isSet bamBam: " + store.isSet(bamBam));
        System.out.println("isSet almost: " + store.isSet(commons.BIT.binStrToInt("0000_0100_0100_0110_0000_0000_0000_0000")));
        System.out.println("isSet random: " + store.isSet(commons.BIT.binStrToInt("0000_0001_0010_0000_0000_0100_0000_0000")));
        System.out.println("num set 1:     " +  store.numNonZero(heading1));
        System.out.println("num set 2:     " +  store.numNonZero(heading2));
        System.out.println("num set 3:     " +  store.numNonZero(heading3));
        store.seek(pebbles);
        System.out.printf("pebbles at Row=%x Col=%x \n", store.getSeekRow(), store.getSeekCol());
        System.out.println("\nAfter drop wilma and barney");
        store.drop(wilma);
        store.drop(barney);
        store.disp();
    }
    public static void flags(){
        StoreFlags store = new StoreFlags(5);
        int heading1 =  commons.BIT.binStrToInt("0000_0001_0000_0000_0000_0000_0000_0000");
        int heading2 =  commons.BIT.binStrToInt("0000_0010_0000_0000_0000_0000_0000_0000");
        int heading3 =  commons.BIT.binStrToInt("0000_0011_0000_0000_0000_0000_0000_0000");

        int fred =      commons.BIT.binStrToInt("0000_0001_0000_0000_0000_0000_0000_1000");
        int barney =    commons.BIT.binStrToInt("0000_0010_0000_0000_0001_0000_0000_0000");
        int wilma =     commons.BIT.binStrToInt("0000_0010_0000_0000_0010_0000_0000_0000");
        int pebbles =   commons.BIT.binStrToInt("0000_0010_0000_0000_0100_0000_0000_0000");
        int bamBam =    commons.BIT.binStrToInt("0000_0010_0000_0000_1000_0000_0000_0000");
        //System.out.println(n);
        //BIT.disp(n);
        store.set(fred);
        store.set(barney);
        store.set(wilma);
        store.set(pebbles);
        store.set(bamBam);

        store.disp();

        System.out.print  ("fred state:     "); BIT.disp(store.getState(fred));
        System.out.print  ("fred getNumber: "); BIT.disp(store.getNumber(fred));
        System.out.println("fred isSet:     " + store.isSet(fred));
        System.out.println("random isSet:   " + store.isSet(commons.BIT.binStrToInt("0000_0010_0000_0000_0000_1000_0000_0000")));
        System.out.println("num set 1:     " +  store.numNonZero(heading1));
        System.out.println("num set 2:     " +  store.numNonZero(heading2));
        System.out.println("num set 3:     " +  store.numNonZero(heading3));
        store.seek(barney);
        System.out.printf("barney at Row=%x \n", store.getSeekRow());
        System.out.println("\nAfter drop wilma");
        store.drop(wilma);
        store.disp();
    }
}
