/*
 */
package compile.parse;

import compile.basics.Base_Stack;

import compile.basics.CompileInitializer;
import compile.basics.RxlxReader;
import erlog.Erlog;
import static compile.basics.Keywords.INTERIM_FILE_EXTENSION;
import toksource.ScanNodeSource;
import compile.basics.Factory_Node.ScanNode;
import compile.parse.factories.Factory_ParseItem;
import toksource.TextSource_file;

/**
 * @author Dave Swanson
 */
public class Class_Parser extends RxlxReader {
    protected String inName, outName;
    
    private static Class_Parser staticInstance;
    
    private Class_Parser(ScanNodeSource fin){
        super(fin);
        this.inName = CompileInitializer.getInstance().getInName();
        this.outName = outName;
        //er = Erlog.get(this);
    }
    @Override
    protected Base_ParseItem get(ScanNode node) {
        return Factory_ParseItem.get(node);
    }
    // Singleton pattern
    public static Class_Parser getInstance(){
        return staticInstance;
    }
    public static void init(ScanNodeSource fin){
        staticInstance = new Class_Parser(fin);
    }
    public static void killInstance(){
        staticInstance = null;
    }
    
    @Override
    public void onCreate(){
        fin = new ScanNodeSource(new TextSource_file(inName + INTERIM_FILE_EXTENSION));
        er.setTextStatusReporter(fin);
        if( !fin.hasData() ){
            return;
        }
        while(fin.hasNext()){
            ScanNode currNode = ((ScanNodeSource)fin).nextNode();
            //System.out.println("\nNode: " + currNode.toString());
            
            if(currNode.cmd == null || currNode.h == null){
                er.set("onCreate: Null command");
            }
            switch (currNode.cmd){
                case PUSH:
                    push(this.get(currNode));
                    break;
                case POP://   
                    if(!currNode.h.equals(((Base_ParseItem)getTop()).getNode().h)){
                        er.set("POP: Stack mismatch, check source file", currNode.h.toString());
                    }
                    pop();
                    break;
                case ADD_TO:
                    ((Base_ParseItem)getTop()).addTo(currNode.h, currNode.k, currNode.data);
                    break;
                case SET_ATTRIB:
                    if(currNode.k == null){
                        er.set("SET_ATTRIB: Null key");
                    }
                    ((Base_ParseItem)getTop()).setAttrib(currNode.h, currNode.k, currNode.data);
                    break;
                default:
                    er.set("onCreate: rxlx file improperly edited", currNode.cmd.toString());
            }
            //disp();
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
