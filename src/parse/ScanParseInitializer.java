package parse;

import codegen.Widget;
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
