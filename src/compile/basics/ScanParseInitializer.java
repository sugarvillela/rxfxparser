package compile.basics;

import codegen.Widget;
import compile.parse.Class_Parser;
import compile.scan.Class_Scanner;
import erlog.Erlog;

/**
 *
 * @author Dave Swanson
 */
public class ScanParseInitializer {
    public static void init(String projName){
        Widget.setDefaultLanguage(Widget.PHP);
        Erlog.initErlog(Erlog.DISRUPT|Erlog.USESYSOUT);
        Class_Scanner.init(projName, projName);
        Class_Parser.init(projName, projName);
    }
}
