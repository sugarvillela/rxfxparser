package rxfxparser;

import demos.*;
import compile.basics.CompileInitializer;

/**Code generator and translator for RXFX language
 *
 * @author Dave Swanson
 */
public class Rxfxparser {
    public static void main(String[] args) {
        //Itr_file itr = new Itr_file("file01.txt");
        System.out.println("Running...");
        String inName = (args.length > 1)? args[1] : "semantic";
        String outName = (args.length > 2)? args[2] : inName;
        CompileInitializer.getInstance().init(inName, outName);
//        int max = 20;
//        int n = (int)(Math.random()*max);
//        System.out.println(n);
        
        //TokenTools_.itr_file_skips();//Unique_.enudGen();
        //demos.Itr_struct_.itr_file_word();
        //demos.TokenTools_.textSource_list();
        //demos.Stemmer_.stemmer3();
        //Parse_.scanner();
        Parse_.parser();
        //demos.Parse_.parseStack();
        //RxContextDev rxContextDev = new RxContextDev();
        //rxContextDev.testValidate();
        //demos.Commons_.assertFileExt();
        //demos.BIT_.maskRound();
        //System.out.println("12, 3, 45, 6, 7, 8900".matches("[ ,0-9]+"));
        //System.out.println("12, a, 45, 6, 7, 8900".matches("[ ,0-9]+"));
    }
    
}
