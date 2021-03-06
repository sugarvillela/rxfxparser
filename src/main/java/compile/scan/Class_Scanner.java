
package compile.scan;

import langdef.Keywords.DATATYPE;

import java.util.ArrayList;

import static langdef.Keywords.CONT_LINE;
import static langdef.Keywords.INTERIM_FILE_EXTENSION;
import static langdef.Keywords.SOURCE_FILE_EXTENSION;

import runstate.Glob;
import scannode.ScanNode;
import compile.scan.factories.Factory_ScanItem;
import compile.symboltable.ConstantTable;
import listtable.ListTable;
import toksource.Base_TextSource;
import toksource.TokenSource;

/**Does the language parsing; outputs a list of commands, datatypes and text
 *
 * @author Dave Swanson
 */
public class Class_Scanner extends Base_Scanner {
    private static Class_Scanner instance;
    
    public Class_Scanner(Base_TextSource fin){
        super(fin);
        nodes = new ArrayList<>();
    }
    
    public static Class_Scanner getInstance(){
        return instance;
    }
    public static void init(TokenSource fin){
        instance = new Class_Scanner(fin);
    }

    private final ArrayList<ScanNode> nodes;
    protected String backText;   // repeat lines

    // Runs Scanner
    @Override
    public void readFile(){
        if( !fin.hasData() ){
            er.set( "Bad input file name", inName + SOURCE_FILE_EXTENSION );
        }
        this.onTextSourceChange(fin);

        readFileLines();

        if(Factory_ScanItem.isPreScanMode()){
            dispPreScanResult();
        }
        else{
            dispScanResult();
        }
    }

    private void dispPreScanResult(){
        System.out.println("==============================================");
        System.out.println("==============================================");
        System.out.println("Constant Table Result");
        System.out.println(Glob.CONSTANT_TABLE.toString());

        System.out.println("\nList Table Result");
        Glob.LIST_TABLE.disp();
    }

    private void dispScanResult(){
        System.out.println("==============================================");
        System.out.println("==============================================");

        System.out.println("\nSymbol Table Result");
        System.out.println(Glob.SYMBOL_TABLE.toString());
    }

    private void readFileLines(){
        backText = null;
        String text;

        // start with a target language datatype
        push(Factory_ScanItem.getInstance().get(DATATYPE.TARGLANG_BASE));
        // start in line mode for target language
        fin.setLineGetter();
        while(true){// outer loop on INCLUDE file stack level
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

                status(text);  // display incoming text
                ((Base_ScanItem)top).pushPop(text);
            }
            //
            if(!restoreTextSource()){
                //System.out.println("stack empty");
                break;
            }
            //System.out.println("not empty: "+fin.loggableStatus());
            //System.exit(0);
        }

        // pop target language datatype;
        pop();
        //Commons.disp(nodes, "\nClass_Scanner nodes");
    }

    private void status(String text){
        boolean showStatus = true;
        if(showStatus){
            String topInfo = (top == null)? "null" : top.getDebugName();
            System.out.printf("%s \t \t %s >>> %s \n", topInfo, fin.readableStatus(), text);        }
    }

    @Override
    public void persist(){
        System.out.println( "Scanner onQuit" );

        if(
            inName == null ||
            !Glob.SCAN_NODE_FACTORY.persist(
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

    public ArrayList<ScanNode> getScanNodeList(){
        return this.nodes;
    }

    public final void addNode(ScanNode node){
        nodes.add(node);
    }

    public final void addNodes(ArrayList<ScanNode> newNodes){
        nodes.addAll(newNodes);
    }

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
