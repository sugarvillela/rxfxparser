
package parse;

import commons.Commons;
import commons.Util_string;
import itr_struct.Itr_file;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import toktools.TK;

/**Does the language parsing; outputs a list of commands, handlers and text
 *
 * @author Dave Swanson
 */
public class Class_Scanner extends Base_Stack {
    private ArrayList<ScanNode> nodes;
    private String foutName;
    private boolean oBrace;                     // if opening brace was found
    
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
        push(new Context_targetLang_base(H.TARGLANG_BASE));
        // start in line mode for target language
        fin.setLineGetter();
        while( fin.hasNext() ){
            if( backText == null ){
                text = fin.next();
            }
            else{
                text = backText;
                backText = null;
            }
            System.out.println( "___________________"+text );
            top.pushPop(text);
        }
        // pop target language handler;
        pop();
        Commons.disp(nodes);
    }
    @Override
    public void onQuit(){
        System.out.println( "Scanner onQuit" );
        if(foutName != null && !write_rxlx_file(foutName)){
            P.setEr("Failed to write output file for name: " + foutName);
        }
    }
    
    // handlers for context-sensitive control of stack
    // handlers for context-sensitive control of stack
    public abstract class Base_Context extends Base_StackItem{
        public Base_Context(){
            P = Class_Scanner.getInstance();
        }
        @Override
        public void onPush(){
            System.out.println( "called start on " + this.h );
            nodes.add( new ScanNode( CMD.PUSH, this.h ) );
        }
        @Override
        public void onPop(){
            System.out.println( "called finish on " + this.h );
            nodes.add( new ScanNode( CMD.POP, this.h ) );
        }
        // helper Util
        protected void addText( String text ){//override to add more validation
            nodes.add(new ScanNode( CMD.ADD_TO, h, text));
        }
    }
    public class Context_targetLang_base extends Base_Context{
        public Context_targetLang_base(){}
        public Context_targetLang_base( H setH ){
            this.h = setH;
        }
        @Override
        public void pushPop( String text ){
            switch (text.trim()){
                case SOURCE_OPEN:                        // Start rxfx source code
                    P.fin.setWordGetter();              // rxfx parses word-by-word
                    P.push(new Context_sourceLang( H.SRCLANG ) );  // main source handler
                    break; 
                default:
                    nodes.add(new ScanNode( CMD.ADD_TO, H.TARGLANG_BASE, text ) );// Exact copy: text not trimmed
                    break;
            }
        }
    }
    public class Context_sourceLang extends Base_Context{
        Context_sourceLang( H setH ){
            this.h = setH;
        }
        
        @Override
        public void pushPop( String text ){
            text = text.trim();
            if(SOURCE_CLOSE.equals(text)){
                System.out.println( text+" means pop in Handler_source" );
                P.fin.setLineGetter();
                P.pop();
            }
            if( ScanUtil.isItemOpener_back(text) ){
                oBrace = true;
                text = ScanUtil.rmItemOpener_back(text);
            }
            else{
                oBrace = false;
            }
            switch (text){
                case SOURCE_CLOSE:
                    P.popAllSource();
                    break;
                case ATTRIB:
                    System.out.println( text+" means push an ATTRIB" );
                    P.push( new Context_ATTRIB( H.ATTRIB ) );
                    break;
                case TARGLANG:
                    System.out.println( text+" means push a TARGLANG" );
                    P.push( new Context_non_nesting( H.TARGLANG ) );
                    break;
                case ENUB:
                    System.out.println( text+" means push an ENUB" );
                    P.push( new Context_nesting( H.ENUB ) );
                    break;
                case ENUD:
                    System.out.println( text+" means push an ENUD" );
                    P.push( new Context_nesting( H.ENUD ) );
                    break;
                case RX:
                    System.out.println( text+" means push an RX" );
                    P.push( new Context_RX() );
                    break;
                default:
                    if(!text.startsWith(COMMENT)){//One word comments?? TODO fix
                        P.setEr("Unknown keyword: " + text);
                    }
                    break;
            }
        }
    }
    // copies all non-keyword items
    public class Context_non_nesting extends Base_Context{
        //public Context_non_nesting(){}
        public Context_non_nesting( H setH ){
            h = setH;
        }
        @Override
        public void pushPop( String text ){
            switch (text){
                case SOURCE_CLOSE:
                    P.popAllSource();
                    break;
                case ITEM_CLOSE:
                    P.pop();
                    break;
                default:
                    // Look for opening brace connected to first data
                    if(!oBrace && !ScanUtil.errOBrace( P, text )){
                        oBrace = true;
                        text = ScanUtil.rmItemOpener_front( text );
                    }
                    // Look for closing brace connected to last data
                    if( ScanUtil.isItemCloser(text) ){
                        this.addText( ScanUtil.rmItemCloser(text) );
                        P.pop();
                    }
                    else{
                        this.addText(text);
                    }
            }
        }
    }
    public class Context_ATTRIB extends Context_non_nesting{
        public Context_ATTRIB( H setH ){
            super(setH); 
        }
        @Override
        protected void addText( String text ){//override to add more validation
            if( text.chars().filter(ch -> ch == KEYVAL).count() != 1 ){
                P.setEr("For ATTRIB tag key=value format is required at " + text);
            }
            nodes.add(new ScanNode( CMD.ADD_TO, h, text));
        }
    }
    public class Context_userDef extends Context_non_nesting {
        private final String defName;
        Context_userDef(H setH, String setName){
            super(setH);
            this.defName = setName;
        }
        @Override
        public void onPush(){
            System.out.println( "called onStart on " + this.h );
            nodes.add( new ScanNode( CMD.PUSH, this.h, this.defName ) );
        }
        @Override
        public void onPop(){
            System.out.println( "called onFinish on " + this.h );
            nodes.add( new ScanNode( CMD.POP, this.h, this.defName ) );
        }
    }
    // ignores all non-keyword or non-uesr-defined items
    public class Context_nesting extends Base_Context{
        public Context_nesting( H setH ){
            h = setH;
        }
        @Override
        public void pushPop( String text ){
            switch (text){
                case SOURCE_CLOSE:
                    P.popAllSource();
                    break;
                case ITEM_CLOSE:
                    P.pop();
                    break;
                case TARGLANG:
                    System.out.println( text+" means push a TARGLANG" );
                    P.push( new Context_non_nesting( H.TARGLANG ) );
                    break;
                default:
                    // Look for opening brace connected to first data
                    if(!oBrace && !ScanUtil.errOBrace( P, text )){
                        oBrace = true;
                        text = ScanUtil.rmItemOpener_front( text );
                    }
                    if( ScanUtil.isUserDef(text) ){
                        text = ScanUtil.getUserDef(text);
                        System.out.println( text+" means push a USERDEF" );
                        if( ScanUtil.isItemOpener_back(text) ){
                            System.out.println( text+" is item opener" );
                            oBrace = true;
                            text = ScanUtil.rmItemOpener_back(text);
                        }
                        else{
                            oBrace = false;
                        }
                        System.out.println( text+" edited?" );
                        P.push( new Context_userDef( H.USERDEF, text ) );
                    }
                    //out.add(new ScanNode( h, text));
                    break;
            }
        }
    }
    // RX: Sub-scanner for RX patterns
    public class Context_RX extends Base_Context{
        protected boolean cBrace;
        protected Util_ScanRX.Range range;
        protected Util_ScanRX.PatternItr itr;
        
        public Context_RX(){
            h = H.RX;
            range = Util_ScanRX.getInstance_Range();
            itr = Util_ScanRX.getInstance_PatternItr();
        }
        @Override
        public void pushPop( String text ){
            switch (text){
                case SOURCE_CLOSE:
                    P.popAllSource();
                    break;
                case ITEM_CLOSE:
                    P.pop();
                    break;
                default:
                    // Reach default once, then ITEM_CLOSE or SOURCE_CLOSE
                    if(cBrace){
                        P.setEr("Expected closing brace here: "+text);
                    }
                    // Look for opening brace before
                    if(!oBrace && !IParse.ScanUtil.errOBrace( P, text )){
                        oBrace = true;
                        text = IParse.ScanUtil.rmItemOpener_front( text );
                    }
                    // Look for closing brace after
                    if( ScanUtil.isItemCloser(text) ){
                        ScanUtil.rmItemCloser(text);
                        cBrace = true;
                    }
                    // Look for surrounding quotes
                    if(text.charAt(0) != '"' || text.charAt(text.length()-1) != '"' ){
                        P.setEr("RX item must be in quotes: "+text);
                        P.pop();
                    }
                    if( text.length() == 0){
                        P.setEr("Empty RX pattern");
                        P.pop();
                    }
                    //scanAll(text.substring( 1, text.length()-1 ));
                    if(cBrace){
                        P.pop();
                    }
                    cBrace = true;// expect next token to be closing symbol
                    break;
            }
        }
    
        protected void scanAll( String text ){
            String[] patterns = TK.toArr(' ', text);
            for (String pattern : patterns) {
                System.out.println("setNodes:" + pattern);
                scanOne( pattern );
            }
        }
        protected void scanOne( String text ){
            P.push( new Context_RXItem( true ) );
            range.init(text);
            nodes.add( new ScanNode( CMD.SET_ATTRIB, H.LO, ""+range.getLo() ) );
            nodes.add( new ScanNode( CMD.SET_ATTRIB, H.HI, ""+range.getHi() ) );
            itr.init(P, text);
            while(itr.hasNext()){
                top.pushPop(itr.next());
            }
            P.pop();
        }
    }
    public class Context_RXItem extends Base_Context{
        public final String DELIMS = "=()'&|";
        public final char EQUAL = '=';
        public final char AND = '&';
        public final char OR = '|';
        public final char OPAR = '(';
        public final char CPAR = '}';
        public final char QUOTE = '\'';
        
        private boolean ignorePar;

        public Context_RXItem( boolean ignoreParentheses ){
            ignorePar = ignoreParentheses;
            h = H.RX_ITEM;
        }
        @Override
        public void pushPop( String text ){
            if(text.length() == 1){
                switch(text.charAt(0)){
                    case EQUAL:
                        break;
                    case AND:
                        break;
                    case OR:
                        break;
                    case OPAR:
                        if(!ignorePar){
                            P.push( new Context_RXItem( false ) );
                        }
                        break;
                    case CPAR:
                        if(!ignorePar){
                            P.pop();
                        }
                        break;
                    case QUOTE:
                        break;
                    default:
                        break;
                }
            
            }
        }
        // helper Util
//        protected void addText( String text ){//override to add more validation
//            nodes.add(new ScanNode( CMD.ADD_TO, h, text));
//        }
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
    public ArrayList<ScanNode_fromFile> read_rxlx_file(String path){
        Itr_file rxlx = new Itr_file( path );
        if( !rxlx.hasFile() ){
            er.set( "Reading rxlx file: bad file name: "+path );
        }
        rxlx.setLineGetter();
        ArrayList<ScanNode_fromFile> out = new ArrayList<>();
        String cur;
        while(rxlx.hasNext()){
            if( !(cur = rxlx.next() ).isEmpty() ){
                out.add(new ScanNode_fromFile(this, cur));
            }
        }
        return out;
    }
    public ScanNode_fromFile read_rxlx_elem( String text){
        return new ScanNode_fromFile(this, text);
    }
}
