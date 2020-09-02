package rxfxparser;

import demos.*;
import compile.basics.CompileInitializer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Code generator and translator for RXFX language
 *
 * @author Dave Swanson
 */
public class Rxfxparser {

    public static void main(String[] args) {
        //Itr_file itr = new Itr_file("file01.txt");
        System.out.println("Running...");
        
//        String regex = "\\([0-9]*\\)$";
//        Pattern allowed = Pattern.compile(regex);
//        System.out.println(regex);
//        String text = "text()";
//        Matcher matcher = allowed.matcher(text);
//        if(matcher.find()){
//            String truncated = matcher.replaceFirst("");
//            System.out.println("Yeah");
//            text = matcher.group();
//            System.out.println(truncated);
//            System.out.println(text);
//            
//        }
//        else{
//            System.out.println("nope");
//        }
        
        String inName = (args.length > 1)? args[1] : "semantic";
        String outName = (args.length > 2)? args[2] : inName;
        CompileInitializer.getInstance().init(inName, outName);

        //Parse_.scanner();
//        RxTree_ u = RxTree_.getInstance();
//        u.test3();
        //TokenTools_.tokens_special();
        //demos.Parse_.parseStack();
        //RxContextDev rxContextDev = new RxContextDev();
        //rxContextDev.testValidate();
        //demos.Commons_.assertFileExt();
        //demos.BIT_.maskRound();
        //System.out.println("12, 3, 45, 6, 7, 8900".matches("[ ,0-9]+"));
        //System.out.println("12, a, 45, 6, 7, 8900".matches("[ ,0-9]+"));
    }
    
}
