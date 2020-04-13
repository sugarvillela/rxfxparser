package demos;

import toksource.TokenSourceImpl;
import commons.BIT;
import itr_struct.*;
import unique.Unique;
//import java.util.Iterator;
/**
 *
 * @author Dave Swanson
 */
public class Itr_struct_ {
    public static final int ENUMA = 0x1;        //row0 bit 0
    public static final int ENUMB = 0x80;       //row0 bit 7
    public static final int ENUMC = 0x10000;    //row0 bit 16
    public static final int ENUMD = 0x800000;   //row0 bit 23
    public static final int ENUME = 0x1000004;  //row1 bit 2
    public static final int ENUMF = 0x1000080;  //row1 bit 7
    public static final int ENUMG = 0x1100000;  //row1 bit 20
    public static final int ENUMH = 0x1800000;  //row1 bit 23
    
    public static void bb(){
        Enumstore itr =  new Enumstore(8, 4);
        System.out.println("four enums");
        System.out.println(BIT.str(ENUMA)); 
        System.out.println(BIT.str(ENUMB)); 
        System.out.println(BIT.str(ENUMC)); 
        System.out.println(BIT.str(ENUMD));
        System.out.println(BIT.str(ENUME));
        System.out.println(BIT.str(ENUMF));
        System.out.println(BIT.str(ENUMG));
        System.out.println(BIT.str(ENUMH));
        itr.set(ENUMA);
        itr.set(ENUMB);
        itr.set(ENUMC);
        itr.set(ENUMD);
        itr.set(ENUME);
        itr.set(ENUMF);
        itr.set(ENUMG);
        itr.set(ENUMH);
        itr.disp();
//        System.out.println("test get ");
//        System.out.println(itr.get(ENUMA));
//        System.out.println(itr.get(ENUMB)); 
//        System.out.println(itr.get(ENUMC)); 
//        System.out.println(itr.get(ENUMD)); 
//        System.out.println(itr.get(ENUME));
//        System.out.println(itr.get(ENUMF));
//        System.out.println(itr.get(ENUMG));
//        System.out.println(itr.get(ENUMH));
//        System.out.println(itr.get(4)); 
//        System.out.println(itr.get(0x80000)); 
//        System.out.println("exists 0x20000000 "+itr.exists(0x20000000));
//        System.out.println("exists 0x1000000 "+ itr.exists(0x1000000));
        Unique i=new Unique();
        for( int n: itr ){
            if(n==0){
                System.out.printf("%s: none\n", i);
            }
            else{
                System.out.println(i+": "+BIT.str(n));
            }
            if(i.nPrev()>55){
                break;
            }
        }
        System.out.println("=======================================");
        itr.setItrEnum(false);
        i.rewind();
        for( int n: itr ){
            System.out.println(i+": "+n);
            if(i.nPrev()>55){
                break;
            }
        }
    }
    
    public static final int ENUM0 = 0x18;       //row 0 pos 1, val 3
    public static final int ENUM1 = 0xE00000;   //row 0 pos 7, val 7
    public static final int ENUM2 = 0x1000005;   //row 1 pos 0, val 5
    public static final int ENUM3 = 0x11C0000;  //row 1 pos 6, val 7
    public static final int ENUM4 = 0x2001000;  //row 2 pos 4, val 1
    
    public static void bd(){//Bitpack_discrete
        int wval=3;
        Enumstore_discrete itr =  new Enumstore_discrete(8, wval, 3);
        System.out.println("four enums");
        System.out.println(BIT.str(ENUM0, wval)); 
        System.out.println(BIT.str(ENUM1, wval)); 
        System.out.println(BIT.str(ENUM2, wval)); 
        System.out.println(BIT.str(ENUM3, wval));
        System.out.println(BIT.str(ENUM4, wval));
        System.out.println("test set");
        itr.set(ENUM0);
        itr.set(ENUM1);
        itr.set(ENUM2);
        itr.set(ENUM3);
        itr.set(ENUM4);
        itr.disp();
        Unique i=new Unique();
        for( int n: itr ){
            if(n==0){
                i.n();
                //System.out.printf("%s: none\n", i);
            }
            else{
                System.out.println(i+": "+BIT.str(n, wval));
            }
        }
        System.out.println("=======================================");
        itr.setItrEnum(false);
        i.rewind();
        for( int n: itr ){
            if(n==0){
                i.n();
                //System.out.printf("At position %d: none\n", (i.n()%8));
            }
            else{
                System.out.println("Elem="+i+" pos="+(i.nPrev()%8)+" val="+n);
            }
            if(i.nPrev()>20){
                //break;
            }
        }
    }
    public static void listy(){
        // Create Linked List 
        ListNode<String> itr = new ListNode<>(); 

        // Add Elements 
        itr.pushBack("a"); 
        itr.pushBack("b"); 
        itr.pushBack("c"); 
        itr.pushBack("d");
        itr.pushBack("e");
        itr.pushBack("f");
        itr.setItrRange(1, 5);
        //itr.setItrBack();
        //itr.insert(2, "Howdy!!" );
        //System.out.println("0="+itr.getVal(0)); 
        //System.out.println("1="+itr.getVal(1)); 
        for (String string : itr){
            System.out.println(itr.key()); 
            System.out.println(string); 
        } 

    }

}
