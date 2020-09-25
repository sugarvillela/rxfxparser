/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demos;
import commons.*;
import toktools.TK;
/**
 *
 * @author newAdmin
 */
public class Stemmer_ {
    public static void stemmer(){
        String text1 = "Do you really think it is weakness that yields to temptation? I tell you that there are terrible temptations which it requires strength, strength and courage to yield to ~ Oscar Wilde";
        String text2 = "do you realli think it is weak that yield to temptat i tell you that there ar terribl temptat which it requir strength strength and courag to yield to oscar wild";
        
        //System.out.println(text1);
        System.out.println(StringComparator.equals_r( Stemmer.stemAll(text1), TK.toArr(' ', text2)) );
    }
    public static void stemmer2(){
        String text1 = "That makes it official. The Senate voted this afternoon, 51 to 49, to move ahead with the Senate impeachment trial of Donald Trump without further witnesses or documents. This is after the Friday’s news that John Bolton’s forthcoming book says Trump asked Bolton to help with the Ukraine pressure campaign at the heart of impeachment as far back as May 2019. The only two Republicans to cross lines were Senator Susan Collins (R-Maine) and Senator Mitt Romney (R-Utah). There we have it folks.";
        String text2 = "That make it offici The Senat vote thi afternoon 51 to 49 to move ahead with the Senat impeach trial of Donald Trump without further wit or document Thi is after the Fridai s new that John Bolton s forthcom book sai Trump ask Bolton to help with the Ukrain pressur campaign at the heart of impeach as far back as Mai 2019 The onli two Republican to cross line were Senat Susan Collin R Main and Senat Mitt Romnei R Utah There we have it folk"
                    .toLowerCase();
        
        //System.out.println(text1);
        System.out.println(StringComparator.equals_r( Stemmer.stemAll(text1), TK.toArr(' ', text2)) );
    }
    public static void stemmer3(){
        String text1 = "I have recently transferred from another blogging service and i’m very happy here at WordPress. And i’m obviously still getting used to the differences. One of the neat little things that the other blogging site could do was to take you to a random blog. This was often quite a treat and you could find yourself reading something you’d never have normally found. Is there any similar feature here? I’ve tried search the Support but found nothing.";
        String text2 = "I have recent transfer from anoth blog servic and i m veri happi here at WordPress And i m obvious still get us to the differ On of the neat littl thing that the other blog site could do wa to take you to a random blog Thi wa often quit a treat and you could find yourself read someth you d never have normal found Is there ani similar featur here I ve tri search the Support but found noth"
                .toLowerCase();
        //System.out.println(text1);
        StringComparator.Result r;
        System.out.println((r = StringComparator.equals_r( Stemmer.stemAll(text1), TK.toArr(' ', text2))).success() );
        System.out.println(r.intVal());
    }
    public static void stemmer4(){
        String text1 = "";
        String text2 = ""
                .toLowerCase();
        //System.out.println(text1);
        StringComparator.Result r;
        System.out.println((r = StringComparator.equals_r( Stemmer.stemAll(text1), TK.toArr(' ', text2))).success() );
        System.out.println(r.intVal());
    }
    public static void test(){
        System.out.println(Stemmer.stem("one") );
    }
}
