package uqlong;

import commons.BIT;
import uq.UqBoolGen;
import uq.UqDiscreteGen;
import uq.UqGenComposite;
import uq.UqShift;

public class UqLongTest {
    public static void uq(){
        UqGenLong uq = new UqLong();
        for(int i = 0; i<20; i++){
            System.out.println(uq.next());
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqShift(){
        UqGenLong uq = new UqShiftLong(4);
        for(int i = 0; i<35; i++){
            System.out.println(BIT.str(uq.next()));
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqBoolGen(){
        UqGenCompositeLong uq = new UqBoolGenLong(56);
        for(int i = 0; i<50; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqBoolGenNewRow(){
        UqGenCompositeLong uq = new UqBoolGenLong(56);
        for(int i = 0; i<50; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            if(i > 0 && i%3 == 0){
                System.out.println(i+": new row");
                uq.newRow();
            }
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqDiscreteGen(){
        UqGenCompositeLong uq = new UqDiscreteGenLong(60, 2, 2);
        for(int i = 0; i<50; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqDiscPrevState(){
        UqDiscreteGenLong uq1 = new UqDiscreteGenLong(60, 2, 2);
        for(int i = 0; i<45; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq1.next()));
            if(!uq1.hasNext()){break;}
        }

        System.out.println("change");
        UqGenCompositeLong uq2 = new UqDiscreteGenLong(uq1);
        for(int i = 0; i<40; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq2.next()));
            if(!uq2.hasNext()){break;}
        }
    }
    public static void uqDiscreteGenNewCol(){
        UqDiscreteGen uq = new UqDiscreteGen(14, 3, 3);
        for(int i = 0; i<50; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next(), 3));
            if(i > 0 && i%3 == 0){
                System.out.println(i+": newCol");
                uq.newCol();
            }
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqDiscreteGenNewRow(){
        UqDiscreteGen uq = new UqDiscreteGen(14, 3, 3);
        for(int i = 0; i<20; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next(), 3));
            if(i > 0 && i%9 == 0){
                System.out.println(i+": newRow");
                uq.newRow();
            }
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqDiscreteGenHalt(){
        UqDiscreteGenLong uq = new UqDiscreteGenLong(4, 4,4);
        int i = 0;
        while(uq.hasNext()){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            uq.newRow();
            i++;
        }
    }
    public static void uqBoolPrevState(){
        UqGenCompositeLong uq1 = new UqBoolGenLong(60);
        for(int i = 0; i<20; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq1.next()));
            if(!uq1.hasNext()){break;}
        }
        System.out.println("change");
        UqGenCompositeLong uq2 = new UqBoolGenLong(uq1);
        for(int i = 0; i<30; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq2.next()));
            if(!uq2.hasNext()){break;}
        }
    }
    public static void uqBoolPrevDisc(){
        UqDiscreteGenLong uq1 = new UqDiscreteGenLong(60, 2, 2);
        uq1.newRow();
        for(int i = 0; i<45; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq1.next()));
            if(!uq1.hasNext()){break;}
        }
        System.out.println("change");
        System.out.println("offset="+uq1.curRowOffset());
        UqGenCompositeLong uq2 = new UqBoolGenLong(uq1);
        for(int i = 0; i<30; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq2.next()));
            if(!uq2.hasNext()){break;}
        }
    }
}
