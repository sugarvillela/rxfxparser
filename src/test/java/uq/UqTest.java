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
        UqShift uq = new UqShift(16);
        for(int i = 0; i<35; i++){
            BIT.disp(uq.next());
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqBoolGen(){
        UqBoolGen uq = new UqBoolGen(24);
        for(int i = 0; i<50; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqBoolPrevState(){
        UqGenComposite uq1 = new UqBoolGen(24);
        for(int i = 0; i<20; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq1.next()));
            if(!uq1.hasNext()){break;}
        }
        System.out.println("change");
        UqGenComposite uq2 = new UqBoolGen(uq1);
        for(int i = 0; i<30; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq2.next()));
            if(!uq2.hasNext()){break;}
        }
    }
    public static void uqBoolPrevDisc(){
        UqGenComposite uq1 = new UqDiscreteGen(20, 3, 3);
        uq1.newRow();
        for(int i = 0; i<45; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq1.next(), 3));
            if(i > 0 && i%3 == 0){
                System.out.println(i+": newCol");
                uq1.newCol();
            }
            if(!uq1.hasNext()){break;}
        }
        System.out.println("change");
        System.out.println("offset="+uq1.curRowOffset());
        UqGenComposite uq2 = new UqBoolGen(uq1);
        for(int i = 0; i<30; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq2.next(), 3));
            if(!uq2.hasNext()){break;}
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
        UqDiscreteGen uq = new UqDiscreteGen(20, 4, 4);
        uq.newRow();
        for(int i = 0; i<50; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqDiscPrevState(){
        UqDiscreteGen uq1 = new UqDiscreteGen(20, 3, 3);
        uq1.newRow();
        for(int i = 0; i<45; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq1.next(), 3));
//            if(i > 0 && i%3 == 0){
//                System.out.println(i+": newCol");
//                uq1.newCol();
//            }
            if(!uq1.hasNext()){break;}
        }

        System.out.println("change");
        UqGenComposite uq2 = new UqDiscreteGen(uq1);
        System.out.printf("new UqDiscreetGen: %s \n", BIT.str(uq2.curr(), 3));
        for(int i = 0; i<40; i++){
            System.out.printf("%02d: %s \n", i, BIT.str(uq2.next(), 3));
            if(!uq2.hasNext()){break;}
        }
    }
    public static void uqDiscreteGenNewCol(){
        UqDiscreteGen uq = new UqDiscreteGen(14, 3, 3);
        uq.newRow();
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
            uq.newRow();
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            i++;
        }
    }
    public static void uqBoolGenHalt(){
        UqBoolGen uq = new UqBoolGen(4);
        int i = 0;
        while(uq.hasNext()){
            if(i < 15){
                uq.newRow();
            }
            System.out.printf("%02d: %s \n", i, BIT.str(uq.next()));
            i++;
            if(i > 45){break;}
        }
    }
}
