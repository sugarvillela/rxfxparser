package uq;

import commons.BIT;

public class UqTest {
    public static void uq(){
        Uq uq = new Uq(10);
        for(int i = 0; i<20; i++){
            System.out.println(uq.next());
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqShift(){
        UqShift uq = new UqShift();
        for(int i = 0; i<35; i++){
            BIT.disp(uq.next());
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqBoolGen(){
        UqBoolGen uq = new UqBoolGen(16);
        for(int i = 0; i<50; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqBoolGenNewRow(){
        UqBoolGen uq = new UqBoolGen(16);
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
        UqDiscreteGen uq = new UqDiscreteGen(14, 3, 3);
        for(int i = 0; i<50; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next(), 3));
            if(!uq.hasNext()){break;}
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
        UqDiscreteGen uq = new UqDiscreteGen(4, 4,4);
        int i = 0;
        while(uq.hasNext()){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            uq.newRow();
            i++;
        }
    }
}
