package demos;

import commons.Commons;
import java.util.ArrayList;
import parse.*;
import parse.factories.Factory_Node.ScanNode;
/**
 *
 * @author Dave Swanson
 */
public class Parse_ {
    
    public static void scanner(){
        Class_Scanner S = Class_Scanner.getInstance("semantic.rxfx", "scanOutput2");//, "scanOutput"
        //ArrayList<IParse.ScanNode_fromFile> list = S.read_rxlx_file("scanOutput.rxlx");
        //Commons.disp(list);
        S.onCreate();
        S.onQuit();
    }
    public static void read_rxlx_file(){
        Class_Scanner S = Class_Scanner.getInstance();//, "scanOutput"
        ArrayList<ScanNode> list = S.read_rxlx_file("scanOutput.rxlx");
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
        P.onCreate();
        P.onQuit();
    }
    
}
