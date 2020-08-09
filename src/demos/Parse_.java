package demos;

import commons.Commons;
import java.util.ArrayList;
import parse.*;
import static parse.Keywords.HANDLER.RX;
import parse.factories.Factory_Node.ScanNode;
import parse.factories.Factory_RxContext;
import parse.factories.Factory_RxContext.Rx;
import toktools.TK;
import toktools.Tokens;
import toktools.Tokens_special;
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
        String text="dru='&'|M=17&LEN()=2";
        Base_Context rx = Factory_RxContext.get(RX);
        rx.pushPop(text);
        
        ArrayList<String> t;
        System.out.printf( "\n=============================\nOrig text:\n%s\n", text );
        Tokens instance = TK.getInstance(
            "&",                            //delimiter
            "('",                          //skip symbol
            TK.IGNORESKIP                      //flag to remove skip symbole, TK.DELIMIN|TK.SYMBOUT
        );
        instance.parse(text);
        t = instance.getTokens();
        Commons.disp( t, "\nTokens:" );
        
        instance = new Tokens_special(
                "&","('",TK.IGNORESKIP
        );
        instance.parse(text);
        t = instance.getTokens();
        Commons.disp( t, "\nTokens:" );
        //S.onPush("\"myPatternIsThis{5-15} orThat* orEvenThis\"");
    }
    
    
    
    public static void parseStack(){
        Class_Parser P = Class_Parser.getInstance("scanOutput.rxlx");
        P.onCreate();
        P.onQuit();
    }
    
}
