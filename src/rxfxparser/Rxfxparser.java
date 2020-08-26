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
        String toRegexList = "FIRST|LAST|LEN";
        String regex = String.format("^(%s)(\\([0-9]*\\))?$", toRegexList);
        Pattern allowed = Pattern.compile(regex);
        System.out.println(regex);
        String text = "BORK()";
        Matcher matcher = allowed.matcher(text);
        if(matcher.find()){
            String truncated = matcher.replaceFirst("");
            // text = matcher.group();
            // System.out.println(truncated);
            // System.out.println(text);
            System.out.println("Yeah");
        }
        else{
            System.out.println("nope");
        }
        
//        String inName = (args.length > 1)? args[1] : "semantic";
//        String outName = (args.length > 2)? args[2] : inName;
//        CompileInitializer.getInstance().init(inName, outName);

        //Parse_.scanner();
//        RxTreeUtil u = RxTreeUtil.getInstance();
//        u.test4();
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
