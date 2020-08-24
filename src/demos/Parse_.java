package demos;

import compile.scan.Class_Scanner;
import compile.parse.Class_Parser;
import commons.Commons;
import java.util.ArrayList;
import static compile.basics.Keywords.HANDLER.RX;
import compile.basics.Factory_Node.ScanNode;
import toksource.ScanNodeSource;
import toksource.TextSource_file;
import toktools.TK;
import toktools.Tokens;
import toktools.Tokens_special;
/**
 *
 * @author Dave Swanson
 */
public class Parse_ {
    
    public static void scanner(){
        Class_Scanner S = Class_Scanner.getInstance();
        //ArrayList<IParse.ScanNode_fromFile> list = S.read_rxlx_file("scanOutput.rxlx");
        //Commons.disp(list);
        S.onCreate();
        S.onQuit();
    }
    public static void read_rxlx_file(){
        ScanNodeSource source = new ScanNodeSource(new TextSource_file("semantic"));
        ArrayList<ScanNode> out = new ArrayList<>();
        if( source.hasData() ){
            while(source.hasNext()){
                out.add(source.nextNode());
            }
        }
        Commons.disp(out);
    }
    public static void testEnub(){
        Class_Parser P = Class_Parser.getInstance();
        //P.test_Gen_ENUD();
    }
    public static void scannerRX(){
//        String text="dru='&'|M=17&LEN()=2";
//        Base_Context rx = Factory_RxContext.get(RX);
//        rx.pushPop(text);
//        
//        ArrayList<String> t;
//        System.out.printf( "\n=============================\nOrig text:\n%s\n", text );
//        Tokens instance = TK.getInstance(
//            "&",                            //delimiter
//            "('",                          //skip symbol
//            TK.IGNORESKIP                      //flag to remove skip symbole, TK.DELIMIN|TK.SYMBOUT
//        );
//        instance.parse(text);
//        t = instance.getTokens();
//        Commons.disp( t, "\nTokens:" );
//        
//        instance = new Tokens_special(
//                "&","('",TK.IGNORESKIP
//        );
//        instance.parse(text);
//        t = instance.getTokens();
//        Commons.disp( t, "\nTokens:" );
        //S.onPush("\"myPatternIsThis{5-15} orThat* orEvenThis\"");
    }
    
    
    
    public static void parser(){
        Class_Parser P = Class_Parser.getInstance();
        P.onCreate();
        P.onQuit();
    }
    
}
