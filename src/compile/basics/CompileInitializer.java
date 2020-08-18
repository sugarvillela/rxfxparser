package compile.basics;

import codegen.Widget;
import commons.Dev;
import compile.parse.Class_Parser;
import compile.scan.Class_Scanner;
import erlog.Erlog;

/**
 *
 * @author Dave Swanson
 */
public class CompileInitializer {
    private static int wrow, wval;
    public static void init(String projName){
        wrow = 8;// TODO load from properties file
        wval = 4;
        Dev.dispOn();
        Widget.setDefaultLanguage(Widget.PHP);
        Erlog.initErlog(Erlog.DISRUPT|Erlog.USESYSOUT);
        Class_Scanner.init(projName, projName);
        Class_Parser.init(projName, projName);
    }
    public static int getWRow(){ return wrow; }
    public static int getWVal(){ return wval; }
}
