package demos;

import commons.Commons;
import java.util.ArrayList;
import parse.*;
/**
 *
 * @author Dave Swanson
 */
public class Parse_ {
    
    public static void scanner(){
        Class_Scanner S = Class_Scanner.getInstance("test.rxfx", "scanOutput");//, "scanOutput"
        //ArrayList<IParse.ScanNode_fromFile> list = S.read_rxlx_file("scanOutput.rxlx");
        //Commons.disp(list);
        S.onPush();
        //S.onQuit();
    }
    public static void read_rxlx_file(){
        Class_Scanner S = Class_Scanner.getInstance();//, "scanOutput"
        ArrayList<IParse.ScanNode> list = S.read_rxlx_file("scanOutput.rxlx");
        Commons.disp(list);
    }
    public static void testEnub(){
        Class_Parser P = new Class_Parser();
        P.test_Gen_ENUD();
    }
    public static void scannerRX(){
        //Util_ScanRX S = new Util_ScanRX(null, null);
        //S.onPush("\"myPatternIsThis{5-15} orThat* orEvenThis\"");
    }
    
    
    
    public static void parseStack(){
        Class_Parser P = Class_Parser.getInstance("scanOutput.rxlx");
        //Parsestack.setAttrib(key, val);
        P.onPush();
    }
    
}
