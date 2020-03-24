package demos;

import commons.BIT;
import itr_struct.Enumstore;
import unique.*;
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
    public static void unique_random(){
        Unique_random uq = new Unique_random(100);
        System.out.println("===Demo Unique Random===");
        for( int i=1; i<=50; i++ ){
            System.out.print( uq+" " );
            if(i%10==0){
                System.out.println();
            }
        }
        System.out.println();
        
        System.out.println("===Numeric iterator: 50===");
        uq.setInitial(50);
        int i = 1;
        for( int c : uq ){
            System.out.print( uq+" " );
            if(i++%10==0){
                System.out.println();
            }
        }
        System.out.println();
        
        System.out.println("===Value iterator: stop on value===");
        uq = new Unique_random(100);
        uq.setFinal();
        i = 1;
        for( int c : uq ){
            System.out.print( uq+" " );
            if(i++%10==0){
                System.out.println();
            }
        }
        System.out.println();
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
    
    public static void uqx(){
        System.out.println("===Demo Uqx===");
        int wrow=8;
        int wval=3;
        //int rowinc=1<<(32-wrow);
        Uqx row = new Uqx( 0 );
        row.name="row";
        row.setInitial(2);
        row.setFinal(5);
        row.n();
        
        Uqx col = new Uqx( Uqx.GTE );
        col.name="col";
        col.setInitial(wval*2);
        col.setFinal(32-wrow-wval);
        col.setIncrement(wval);
        //col.setInitial2(0);
        int i=0;
        for(int c : col){
            System.out.println( i+": "+c);
            if(10<i++){ break;}
        }
//        int i=0;
//        for(int r : row){
//            System.out.println( i+": "+r);
//            if(10<i++){ break;}
//        }
        

    }

    

}
