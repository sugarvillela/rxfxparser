package demos;

import commons.BIT;
import commons.Commons;
import commons.Dev;
import itr_struct.Enumstore;
import unique.*;
//import unique.Factory.*;
/**
 *
 * @author newAdmin
 */
public class Unique_ {
    public static final int ENUM0 = 0x38;       //3
    public static final int ENUM1 = 0x1000000;  //6
    public static final int ENUM2 = 0x8000140;  //5
    public static final int ENUM3 = 0x11C0000;  //2
    
    public static void unique_base(){
        Unique uq = new Unique();
        System.out.println("===Demo Unique===");
        for( int i=0; i<10; i++ ){
            System.out.println( uq );
        }
    }
    public static void unique_itr(){
        Unique uq = new Unique();
        uq.name="uq_itr";
        //
        System.out.println("===Demo unique_itr===");
        uq.setFinal(12);
        for( int c : uq ){
            System.out.println( c );
            if(c>=15){break;}
        }
        System.out.println("===initial 2, inc 2, final 10===");
        uq.setInitial(2);
        uq.setInc(2);
        uq.setFinal(10);
        for( int c : uq ){
            System.out.println( c );
            if(c>=15){break;}
        }
    }
    public static void uq_repeater(){
        Uq_repeater uq = new Uq_repeater();
        uq.name="repeater";
        uq.setInc(2);
        uq.setInitial(4);
        uq.setFinal(100);
        uq.setResumePoint(0);
        uq.setBreakPoint(10);
        System.out.println("===Demo unique_itr===");
        int i = 0;
        for( int c : uq ){
            System.out.println( c );
            if( uq.signal() ){
                uq.resume();
            }
            if(25 < i++ ){break;}
        }
    }
    public static void uq_rshift(){
        Enumstore enu = new Enumstore(8, 4);
        enu.setInitial(ENUM0);
        enu.setFinal(ENUM3);
        
        Unique i = new Unique();
        System.out.print( i+": ");
        for( int enumi : enu ){
            System.out.println( i+": "+BIT.str( enumi,4 ));
            if(40 < i.nPrev() ){break;}
        }
//        int wrow = 8, wval=3;
//        int wordLen = 32-wrow;
//        Unique i = new Unique();
//        
//        Unique row = new Unique();
//        row.name="row";
//        row.n();
//        row.setFinal(3);
//        
//        Uq_repeater pos = new Uq_repeater();
//        pos.name="pos";
//        pos.setInc(wval);
//        pos.setInitial(3);
//        pos.setResumePoint(0);
//        
//        Uq_rshift mask = new Uq_rshift();
//        mask.name="mask";
//        mask.setInc(wval);
//        mask.setInitial(0x38);
//        mask.setFinal(ENUM3);
//        mask.setResumePoint(7);
//        mask.setBreakPoint(1<<wordLen);
//        
//        System.out.println("===Demo unique_itr===");
//        for( int c : pos ){
//            System.out.println( i +": "+BIT.str( mask.n(),wval ) +", pos="+c+", row="+row.nPrev() );
//            if(!row.hasNext() && !mask.hasNext()){//
//                System.out.println("Done!!");
//                break;
//            }
//            if( mask.signal() ){
//                System.out.println("Signal: row="+row.hasNext()+", mask="+mask.hasNext());
//                mask.resume();
//                pos.resume();
//                row.n();
//            }
//            if(40 < i.nPrev() ){break;}
//        }
    }

    public static void uq_enumgen(){
        System.out.println("===Demo unique_bitpack_indexed===");
        System.out.println("Make row area too wide so we see increment sooner:");
        Uq_enumgen uq = new Uq_enumgen(28);
        uq.setInitial(4);
        uq.setFinal(66);
        Unique i = new Unique();
        for( int enu : uq ){
            System.out.println( i+": "+BIT.str( enu ) );
            if(i.nPrev()==6){
                //i.n();
                System.out.println("newRow");
                uq.newRow();
            }
            if(i.nPrev()>20){
                break;
            }
        }
    }
    public static void uq_enumgen_discrete(){
        Uq_enumgen uq = new Uq_enumgen(26, 3);
        uq.setInitial(4);
        uq.setFinal(144);
        Unique i = new Unique();
        for( int enu : uq ){
            System.out.println( i+": "+BIT.str( enu, 3 ) );
            if(i.nPrev()==1){
                //i.n();
//                System.out.println("newRow");
//                uq.newRow();
                System.out.println("newCol");
                uq.newCol();
            }
            if(i.nPrev()>35){
                break;
            }
        }
    }
    

    public static void sequential(){
        Factory.setPrefix(4, "sequential_");
        Factory.UQSequence u = Factory.getSequential(0x100, 0x0, 0x1000);
        //Factory.UQSequence u = Factory.getSequential(1, 0, 15);
        int i = 0;
        while(u.hasNext()){
            System.out.println( BIT.str(u.next()) + " =\t" + u + "\t");
            if(i>25){break;}
            i++;
        }
        i = 0;
        u.rewind();
        while(u.hasNext()){
            System.out.println( BIT.str(u.next()) + " =\t" + u + "\t");
            if(i>25){break;}
            i++;
        }
    }
    public static void random(){
        Factory.setPrefix(4, "random_");
        Factory.UQSequence u = Factory.getRandom(50);
        int i = 0;
        while(u.hasNext()){
            System.out.println( u.next() + " =\t" + u);
            if(i>25){break;}
            i++;
        }
    }
    public static void shift(){
        //Factory.setPrefix(8, "");
        Factory.UQSequence u = Factory.getShift();
        int i = 0;
        while(u.hasNext()){
            System.out.println( BIT.str(u.next(), 4) + " =\t" + u);
            if(i>10){break;}
            i++;
        }
    }
    public static void mask(){
        //Factory.setPrefix(11, "shift_");
        Factory.UQSequence u = Factory.getMask(4,1,5);
        int i = 0;
        while(u.hasNext()){
            System.out.println( BIT.str(u.next(), 4) + " =\t" + u);
            if(i>12){break;}
            i++;
        }
    }
    public static void discrete(){
        //Factory.setPrefix(11, "shift_");
        Factory.UQSequence u;
        {
            u = Factory.getDiscrete(4,0);
            int i = 0;
            while(u.hasNext()){
                System.out.println( BIT.str(u.next(), 4) + " =\t" + u);
                if(i>35){break;}
                i++;
            }
        }
        {
            u = Factory.getDiscrete(4,16);
            int i = 0;
            while(u.hasNext()){
                System.out.println( BIT.str(u.next(), 4) + " =\t" + u);
                if(i>35){break;}
                i++;
            }
        }
    }
    public static void enubGen(){
        // getEnubGen( int wrow, int initialEnub, int finalEnub )
        Factory.UQSequence u;
        {
            u = Factory.getEnubGen(24, 0x80, 0x202);
            int i = 0;
            while(u.hasNext()){
                System.out.println( BIT.str(u.next(), 4) + " =\t" + u);
                if(i>35){break;}
                i++;
            }
        }
    }
    public static void enudGen(){
        // getEnudGen( int wrow, int initialEnub, int finalEnub )
        Factory.UQSequence u;
        {
            int init = 0xF0;
            int fin = 0x1F0;
            Dev.bnow("init, fin", init, fin);
            u = Factory.getEnudGen(24, 4, 0x0F0, 0x1F0);
            int i = 0;
            while(u.hasNext()){
                System.out.println( BIT.str(u.next(), 4) + " =\t" + u);
                if(i>45){break;}
                i++;
            }
        }
//        {
//            u = Factory.getEnubGen(24, 1, 0x100);
//            int i = 0;
//            while(u.hasNext()){
//                System.out.println( BIT.str(u.next(), 4) + " =\t" + u);
//                if(i>35){break;}
//                i++;
//            }
//        }

    }
}
