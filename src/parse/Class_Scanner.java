
package parse;

import parse.Keywords.HANDLER;
import parse.Keywords.CMD;
import commons.Commons;
import itr_struct.StringSource_file;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static parse.Keywords.CONT_LINE;
import parse.Keywords.KWORD;
import static parse.Keywords.KWORD.ENDLINE;

/**Does the language parsing; outputs a list of commands, handlers and text
 *
 * @author Dave Swanson
 */
public class Class_Scanner extends Base_Stack {
    private ArrayList<ScanNode> nodes;
    private String foutName;
    
    private static Class_Scanner staticInstance;
    
    public Class_Scanner(){
        nodes = new ArrayList<>();
    }
    public Class_Scanner(String inFileName){
        this( inFileName, null);
    }
    public Class_Scanner(String inFileName, String outFileName ){
        setFile( inFileName, "rxfx" );//rxfx validates file extension .rxfx
        nodes = new ArrayList<>();
        foutName = outFileName;
    }
    
    // Singleton pattern
    public static Class_Scanner getInstance(){
        return (staticInstance == null )? 
            (staticInstance = new Class_Scanner()) : staticInstance;
    }
    public static Class_Scanner getInstance( String inFileName ){
        return (staticInstance = new Class_Scanner(inFileName));
    }
    public static Class_Scanner getInstance( String inFileName, String outFileName ){
        return ( staticInstance = new Class_Scanner(inFileName, outFileName ) );
    }
    public static void killInstance(){
        staticInstance = null;
    }

    // Runs Scanner
    @Override
    public void onPush(){
        backText = null;
        String text;
        
        // start with a target language handler
        push(Factory_Context.get(HANDLER.TARGLANG_BASE));
        // start in line mode for target language
        fin.setLineGetter();
        while( fin.hasNext() ){
            if( backText == null ){
                do{
                    text = fin.next();
                }
                while(fin.isEndLine() && CONT_LINE.equals(text));// skip "..."
            }
            else{
                text = backText;
                backText = null;
            }

            System.out.println( fin.isEndLine() + "___________________"+text );
            top.pushPop(text);
        }
        // pop target language handler;
        pop();
        Commons.disp(nodes);
    }
    @Override
    public void onQuit(){
        //System.out.println( "Scanner onQuit" );
        if(foutName != null && !write_rxlx_file(foutName)){
            setEr("Failed to write output file for name: " + foutName);
        }
    }
    
    @Override
    public ArrayList<ScanNode> getScanNodeList(){
        return this.nodes;
    }

    // Serialize and deserialize
    public boolean write_rxlx_file(String f){
        f = Commons.assertFileExt(f, "rxlx");
        try( 
            BufferedWriter file = new BufferedWriter(new FileWriter(f)) 
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
        StringSource_file rxlx = new StringSource_file( path );
        if( !rxlx.hasFile() ){
            er.set( "Reading rxlx file: bad file name: "+path );
        }
        rxlx.setLineGetter();
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
        String[] tok = new String[4];
        int j = 0, start = 0;
        for( int i=0; i<text.length(); i++ ){
            if( text.charAt(i) == ',' ){
                tok[j]=text.substring(start, i);
                System.out.printf("%d, %d, %d, %s \n", i, j, start, tok[j]);
                start=i+1;
                j++;
            }
            if(j == 4){
                return new ScanNode(
                    CMD.get(tok[0]),
                    HANDLER.get(tok[1]),
                    KWORD.get(tok[2]),
                    tok[3]
                );
            }
        }
        System.out.println(j);
        setEr( "Bad CSV file format at: " + text );
        return null;
    }
}
