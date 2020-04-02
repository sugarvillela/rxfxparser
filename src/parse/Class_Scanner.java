
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
        int start = 0, i, j=0;
        HANDLER h = null;
        CMD cmd = null;
        String data;
        
        for( i=0; i<text.length(); i++ ){
            if( text.charAt(i) == ',' ){
                cmd = CMD.get(text.substring(start, i));

                start=i+1;
                System.out.println(cmd+": i = "+i);
                break;
            }
        }
        for( i=start; i<text.length(); i++ ){
            if( text.charAt(i) == ',' ){
                h = HANDLER.get(text.substring(start, i));
                System.out.println(h+": i = "+i);
                start=i+1;
                break;
            }
        }
        data = text.substring(start, text.length()-1);
        System.out.println(text);
        System.out.println(data+": i = "+i);
        System.out.println(this.toString());
        return new ScanNode(cmd, h, data);
    }
}
