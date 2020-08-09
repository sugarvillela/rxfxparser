package rxfxparser;

import codegen.Widget;
import commons.Dev;
import erlog.Erlog;
import demos.*;

/**Code generator and translator for ReXFeX language
 *
 * @author Dave Swanson
 */
public class Rxfxparser {
    public static void main(String[] args) {
        //Itr_file itr = new Itr_file("file01.txt");
        System.out.println("Running...");
        Widget.setDefaultLanguage(Widget.PHP);
        Erlog.initErlog(Erlog.DISRUPT|Erlog.USESYSOUT);//initialize this in main
//        int max = 20;
//        int n = (int)(Math.random()*max);
//        System.out.println(n);
        Dev.dispOff();
        //TokenTools_.itr_file_skips();//Unique_.enudGen();
        //demos.Itr_struct_.itr_file_word();
        //demos.TokenTools_.textSource_list();
        //demos.Stemmer_.stemmer3();
        demos.Parse_.scanner();
        //demos.Parse_.parseStack();
        //RxContextDev rxContextDev = new RxContextDev();
        //rxContextDev.testValidate();
        //demos.Commons_.assertFileExt();
        //demos.BIT_.maskRound();
        //System.out.println("12, 3, 45, 6, 7, 8900".matches("[ ,0-9]+"));
        //System.out.println("12, a, 45, 6, 7, 8900".matches("[ ,0-9]+"));
    }
    
}
