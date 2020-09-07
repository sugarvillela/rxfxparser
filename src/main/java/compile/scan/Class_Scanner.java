
package compile.scan;

import compile.basics.Base_Stack;
import compile.basics.Factory_Node;
import compile.basics.Keywords.HANDLER;
import commons.Commons;
import compile.basics.CompileInitializer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import static compile.basics.Keywords.CONT_LINE;
import static compile.basics.Keywords.INTERIM_FILE_EXTENSION;
import static compile.basics.Keywords.SOURCE_FILE_EXTENSION;
import compile.basics.Factory_Node.ScanNode;
import compile.scan.factories.Factory_ScanItem;
import toksource.Base_TextSource;
import toksource.TextSource_file;
import toksource.TokenSource;

/**Does the language parsing; outputs a list of commands, handlers and text
 *
 * @author Dave Swanson
 */
public class Class_Scanner extends Base_Stack {
    private final ArrayList<ScanNode> nodes;
    private final String inName;
    protected String backText;   // repeat lines
    private Stack<Base_TextSource> fileStack;
    private static Class_Scanner instance;
    
    public Class_Scanner(Base_TextSource fin){
        this.inName = CompileInitializer.getInstance().getInName();
        this.fin = fin;

        nodes = new ArrayList<>();
        fileStack = new Stack<>();
    }
    
    public static Class_Scanner getInstance(){
        return instance;
    }
    public static void init(TokenSource fin){
        instance = new Class_Scanner(fin);
    }
    public static void killInstance(){
        instance = null;
    }

    // Runs Scanner
    @Override
    public void onCreate(){
        if( !fin.hasData() ){
            er.set( "Bad input file name", inName + SOURCE_FILE_EXTENSION );
        }
        CompileInitializer.getInstance().setCurrParserStack(this);
        backText = null;
        String text;
        
        // start with a target language handler
        push(Factory_ScanItem.get(HANDLER.TARGLANG_BASE));//Factory_cxs
        // start in line mode for target language
        fin.setLineGetter();
        while(true){// outer loop on INCLUDE file stack level
            er.setTextStatusReporter(fin);
            while(fin.hasNext()){// inner loop on current file
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

            //
            if(fileStack.empty()){
                System.out.println("stack empty");
                break;
            }
            fin = fileStack.pop();
            System.out.println("not empty: "+fin.readableStatus());
            //System.exit(0);
        }

        // pop target language handler;
        pop();
        //Commons.disp(nodes, "\nClass_Scanner nodes");
    }

    @Override
    public void onQuit(){
        System.out.println( "Scanner onQuit" );

        if(
            inName == null ||
            !Factory_Node.persist(
                inName + INTERIM_FILE_EXTENSION,
                    nodes,
                    "Interim file containing parser instructions"
            )
        ){
            er.set("Failed to write output file", inName);
        }
    }

    public void back( String repeatThis ){// if backText not null, use it
        backText = repeatThis;
    }

    public void include(String fileName){
        if(!fileName.endsWith(SOURCE_FILE_EXTENSION)){
            fileName += SOURCE_FILE_EXTENSION;
        }
        TokenSource newFile = new TokenSource(new TextSource_file(fileName));
        if(newFile.hasData()){
            fileStack.push(fin);
            fin = newFile;
        }
        else{
            er.set("INCLUDE: bad file name", fileName);
        }
    }
    public ArrayList<ScanNode> getScanNodeList(){
        return this.nodes;
    }

    public final void addNode(ScanNode node){
        node.lineCol = fin.readableStatus();
        nodes.add(node);
    }

//    public final void addNodes(ArrayList<ScanNode> newNodes){
//        nodes.addAll(newNodes);
//    }

    // Serialize and deserialize
//    private boolean write_rxlx_file(String path){
//        try(
//            BufferedWriter file = new BufferedWriter(new FileWriter(path))
//        ){
//            for (ScanNode node: this.nodes) {
//                //System.out.println("node:"+node );
//                file.write(node.toString());
//                file.newLine();
//            }
//            file.close();
//            return true;
//        }
//        catch(IOException e){
//            return false;
//        }
//    }
}
