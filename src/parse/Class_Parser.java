/*
 */
package parse;

import parse.Keywords.HANDLER;
import parse.Keywords.CMD;

import codegen.*;
import toksource.*;
import java.util.ArrayList;
import java.util.Iterator;
import parse.interfaces.IContext;
import parse.factories.Factory_Node.ScanNode;
import parse.factories.Factory_Node.GroupNode;
import parse.factories.Factory_Node;
import unique.*;

/**
 * @author Dave Swanson
 */
public class Class_Parser extends Base_Stack {
    
    private Widget[] widgets;   // output generator
    private Widget baseWidget;
    protected String inName, outName;
    private final ArrayList<ScanNode> nodes;
    
    private static Class_Parser staticInstance;
    
    private Class_Parser(String inName, String outName){
        this.inName = inName + ".rxlx";
        this.outName = outName;
        nodes = new ArrayList<>();
        widgets = new Widget[HANDLER.NUM_HANDLERS.ordinal()]; //TODO remove
    }
    
    // Singleton pattern
    public static Class_Parser getInstance(){
        return staticInstance;
    }
    public static void init(String inName, String outName){
        staticInstance = new Class_Parser(inName, outName);
    }
    public static void killInstance(){
        staticInstance = null;
    }
    
    @Override
    public void onCreate(){
        fin = new TokenSource(new TextSource_file(inName));
        er.setTextStatusReporter(fin);
        if( !fin.hasData() ){
            er.set( "Bad file name", inName );
        }
        
    }

    @Override
    public void onQuit(){
        //System.out.println( "parser onQuit" ); 
//        String wErr;
//        for(Widget w : widgets ){
//            if( w !=null && ( wErr = w.finish() )!= null ){
//                er.set( wErr ); 
//            }
//        }
    }
}
