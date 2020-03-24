/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demos;

import tinymaps.*;
import tinymaps.CSVMap;
import tinymaps.CountingSet;
import tinymaps.TinyMap;
import tinymaps.TinySet;

/**
 *
 * @author admin
 */
public class TinyMap_ {
    public static void all(){
        demo();
        demo2();
        commaSeparated();
        varArgs();
        intint();
        floatchar();
        stringint();
        tinySet();
        countingSet();
    }
    public static void demo(){
        System.out.println("demo()");
        TinyMap m = new TinyMap(4, "nope!!");
        m.put("dog", "bark");
        m.put("cow", "moo");
        m.put("cat", "meow");
        m.put("bird", "chirp");
        m.put("duck", "quack");//triggers resize
        m.disp();
        System.out.println("cow says... " + m.get("cow"));//moo
        System.out.println("fish says... " + m.get("fish"));//default return
        m.remove("duck");
        m.disp();
    }
    public static void demo2(){//test equals()
        System.out.println("\ndemo2()");
        TinyMap m = new TinyMap(4, "nope!!");
        m.put("dog", "bark");
        m.put("cow", "moo");
        m.put("cat", "meow");
        m.put("bird", "chirp");
        
        TinyMap k = new TinyMap(4, "nope!!");
        k.put("dog", "bark");
        k.put("cow", "moo");
        k.put("cat", "meow");
        k.put("bird", "chirp");
        System.out.println("equals... " + m.equals(k));
        k.remove("cat");
        System.out.println("equals... " + m.equals(k));
        m.clear();
        k.clear();
        System.out.println("equals... " + m.equals(k));
    }
    public static void commaSeparated(){//test csv constructor
        System.out.println("\ncommaSeparated()");
        String csl = "dog=bark, cow=moo, cat=meow, bird=chirp, nope!!";
        CSVMap m = new CSVMap(csl);
        m.disp();
        System.out.println("mom says... " + m.get("mom"));
    }
    public static void varArgs(){//test varArg constructor and resize
        System.out.println("\nvarArgs()");
        CSVMap m = new CSVMap("dog", "bark", "cow", "moo", "cat", "meow", "bird", "chirp", "nope!!");
        m.disp();
        System.out.println("mom says... " + m.get("mom"));
        m.put("mom", "i love you");
        m.disp();
        System.out.println("mom says... " + m.get("mom"));
    }
    public static void intint(){
        System.out.println("\ninteger keys");
        TinyMap m = new TinyMap(5, 0, 0);
        m.put(5, 0);
        m.put(6, 1);
        m.put(7, 2);
        m.put(8, 3);
        m.put(9, 4);
        m.put(10, 5);
        m.put(11, 6);
        m.disp();
    }
    public static void floatchar(){
        System.out.println("\nfloatchar keys");
        TinyMap m = new TinyMap(5, 0.0, 'a');
        m.put(5.5, 'a');
        m.put(5.6, 'b');
        m.put(5.7, 'c');
        m.put(5.8, 'd');
        m.put(5.9, 'e');
        m.put(5.10, 'f');
        m.put(5.1, 'g');
        m.disp();
    }
    public static void stringint(){
        System.out.println("\nstringint keys");
        TinyMap m = new TinyMap(5, null, 0);
        m.put("a", 0);
        m.put("b", 1);
        m.put("c", 2);
        m.put("d", 3);
        m.disp();
    }
    public static void tinySet(){
        System.out.println("\ncountingSet()");
        TinySet m = new TinySet(5, null);
        m.add("spam");
        m.add("meatloaf");
        m.add("bacon");
        m.add("spam");
        m.add("bacon");
        m.add("bacon");
        m.add("chicken");
        m.disp();
        System.out.println("bacon="+m.contains("bacon"));
        System.out.println("salami="+m.contains("salami"));
    }
    public static void countingSet(){
        System.out.println("\ncountingSet()");
        CountingSet m = new CountingSet(5, null);
        m.put("spam");
        m.put("meatloaf");
        m.put("bacon");
        m.put("spam");
        m.put("bacon");
        m.put("bacon");
        m.put("chicken");
        m.disp();
        System.out.println("bacon="+m.get("bacon"));
        System.out.println("salami="+m.get("salami"));
    }    
    public static void tinyStack(){
        System.out.println("\ntinyStack()");
        TinyStack s = new TinyStack(7, "EMPTY");
        s.push("spam");
        s.push("meatloaf");
        s.push("bacon");
        s.push("sirloin");
        s.push("sausage");
        s.push("chicken");
        s.disp();
        System.out.println("peek="+s.peek());
        for(int i=0; i<10; i++){
            System.out.println("popped="+s.pop());
        }
    }
    public static void tinyStack2(){
        System.out.println("\ntinyStack()");
        TinyStack s = new TinyStack(7, "EMPTY");
        s.push("spam");
        s.push("meatloaf");
        s.push("bacon");
        s.push("sirloin");
        s.push("sausage");
        s.push("chicken");
        s.disp();
        while(!s.isEmpty()){
            System.out.println("popped="+s.pop());
        }
    }     
}
