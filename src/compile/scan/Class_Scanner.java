
package compile.scan;

import compile.basics.Base_Stack;
import compile.basics.Keywords.HANDLER;
import commons.Commons;
import compile.basics.CompileInitializer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static compile.basics.Keywords.CONT_LINE;
import static compile.basics.Keywords.INTERIM_FILE_EXTENSION;
import static compile.basics.Keywords.SOURCE_FILE_EXTENSION;
import compile.basics.Factory_Node.ScanNode;
import compile.scan.factories.Factory_ScanItem;
import toksource.TextSource_file;
import toksource.TokenSource;

/**Does the language parsing; outputs a list of commands, handlers and text
 *
 * @author Dave Swanson
 */
public class Class_Scanner extends Base_Stack {
    private final ArrayList<ScanNode> nodes;
    private final String inName;//, outName;
    
    private static Class_Scanner staticInstance;
    
    public Class_Scanner(String inName, String outName ){
        this.inName = inName + SOURCE_FILE_EXTENSION;
        nodes = new ArrayList<>();
        //er = Erlog.get(this);
    }
    
    public static Class_Scanner getInstance(){
        return staticInstance;
    }
    public static void init( String inName, String outName ){
        staticInstance = new Class_Scanner(inName, outName );
    }
    public static void killInstance(){
        staticInstance = null;
    }

    // Runs Scanner
    @Override
    public void onCreate(){
        fin = new TokenSource(new TextSource_file(inName));
        er.setTextStatusReporter(fin);
        if( !fin.hasData() ){
            er.set( "Bad input file name", inName );
        }
        
        backText = null;
        String text;
        
        // start with a target language handler
        //push(Factory_Context.get(HANDLER.TARGLANG_BASE));//Factory_cxs
        push(Factory_ScanItem.get(HANDLER.TARGLANG_BASE));//Factory_cxs
        // start in line mode for target language
        fin.setLineGetter();
        while(fin.hasNext()){
            if(backText == null){
                do{
                    text = fin.next();
                }
                while(fin.isEndLine() && CONT_LINE.equals(text));// skip "..."
            }
            else{
                text = backText;
                backText = null;
            }

            System.out.println(">>>" + text);
            ((Base_ScanItem)top).pushPop(text);
        }
        // pop target language handler;
        pop();
        Commons.disp(nodes);
    }
    @Override
    public void onQuit(){
        //System.out.println( "Scanner onQuit" );
        //String outName = CompileInitializer.getInstance().getProjName();
        if(inName == null || !write_rxlx_file(inName + INTERIM_FILE_EXTENSION)){
            er.set("Failed to write output file", inName);
        }
    }
    
    public ArrayList<ScanNode> getScanNodeList(){
        return this.nodes;
    }

    public final void addNode(ScanNode node){
        node.lineCol = fin.readableStatus();
        nodes.add(node);
    }
    // Serialize and deserialize
    public boolean write_rxlx_file(String path){
        try( 
            BufferedWriter file = new BufferedWriter(new FileWriter(path)) 
        ){
            for (ScanNode node: this.nodes) {
                //System.out.println("node:"+node );
                file.write(node.toString());
                file.newLine();
            }
            file.close();
            return true;
        }
        catch(IOException e){
            return false;
        }
    }
    public ArrayList<ScanNode> read_rxlx_file(String path){
        TextSource_file rxlx = new TextSource_file( path );
        if( !rxlx.hasData() ){
            er.set( "Reading rxlx file: bad file name", path );
        }
        ArrayList<ScanNode> out = new ArrayList<>();
        String cur;
        while(rxlx.hasNext()){
            if( !(cur = rxlx.next() ).isEmpty() ){
                out.add(
                    read_rxlx_elem( cur )
                );
            }
        }
        return out;
    }
    public ScanNode read_rxlx_elem( String text){
//        String[] tok = new String[4];
//        int j = 0, start = 0;
//        for( int i=0; i<text.length(); i++ ){
//            if( text.charAt(i) == ',' ){
//                tok[j]=text.substring(start, i);
//                System.out.printf("%d, %d, %d, %s \n", i, j, start, tok[j]);
//                start=i+1;
//                j++;
//            }
//            if(j == 4){
//                return new ScanNode(
//                    CMD.get(tok[0]),
//                    HANDLER.get(tok[1]),
//                    KWORD.get(tok[2]),
//                    tok[3]
//                );
//            }
//        }
//        System.out.println(j);
//        er.set( "Bad CSV file format", text );
        return null;
    }
}
