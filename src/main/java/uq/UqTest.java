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
        UqBoolGen uq = new UqBoolGen(2);
        for(int i = 0; i<20; i++){
            BIT.disp(uq.next());
            if(!uq.hasNext()){break;}
        }
    }
    public static void uqDiscreteGen(){
        UqDiscreteGen uq = new UqDiscreteGen(16, 2);
        for(int i = 0; i<20; i++){
            BIT.disp(uq.next());
            if(!uq.hasNext()){break;}
        }
    }
}
