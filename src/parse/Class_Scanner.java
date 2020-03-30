
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
    //private int count;                  // used by RX classes
    ScanUtil_AttachedSymbol attachedSymb;                  // helper class
    
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
        attachedSymb = new ScanUtil_AttachedSymbol();
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
//            if( backText == null ){
//                text = fin.next();
//            }
//            else{
//                text = backText;
//                backText = null;
//            }
            text = fin.next();
            System.out.println( fin.isWordGetter() + "___________________"+text );
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
    
    // handlers for context-sensitive control of stack
    public abstract class Base_Context extends Base_StackItem{
        public Base_Context(){
            P = Class_Scanner.getInstance();
        }
        @Override
        public void onPush(){
            //System.out.println( "called start on " + this.h );
            nodes.add( new ScanNode( CMD.PUSH, this.h ) );
        }
        @Override
        public void onPop(){
            //System.out.println( "called finish on " + this.h );
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
            attachedSymb.reset();// reset { } flags
            text = attachedSymb.rmOSymbol_back(text);//if { attached to keyword
            switch (text){
                case SOURCE_CLOSE:
                    P.setLineGetter();
                    P.popAllSource();
                    break;
                case ATTRIB:
                    //System.out.println( text+" means push an ATTRIB" );
                    P.push( new Context_ATTRIB( H.ATTRIB ) );
                    break;
                case TARGLANG:
                    //System.out.println( text+" means push a TARGLANG" );
                    P.push( new Context_non_nesting( H.TARGLANG ) );
                    break;
                case ENUB:
                    //System.out.println( text+" means push an ENUB" );
                    P.push( new Context_nesting( H.ENUB ) );
                    break;
                case ENUD:
                    //System.out.println( text+" means push an ENUD" );
                    P.push( new Context_nesting( H.ENUD ) );
                    break;
                case RX:
                    //System.out.println( text+" means push an RX" );
                    P.push( new Context_RX() );
                    break;
                case FX:
                    //System.out.println( text+" means push an RX" );
                    P.push( new Context_FX() );
                    break;
                default:
//                    if(!text.startsWith(COMMENT)){//One word comments?? TODO fix
//                        P.setEr("Unknown keyword: " + text);
//                    }
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
                case ITEM_OPEN: // log and skip
                    attachedSymb.forceOpened();
                    break;
                default:
                    // Look for opening symbol connected to first data
                    text = attachedSymb.rmOSymbol_front( text );
                    // Look for closing brace connected to last data
                    text = attachedSymb.rmCSymbol( text );
                    this.addText(text);
                    if( attachedSymb.isClosed() ){
                        P.pop();
                    }
            }
        }
    }
    public class Context_ATTRIB extends Context_non_nesting{
        public Context_ATTRIB( H setH ){
            super(setH); 
        }
        @Override
        protected void addText( String text ){//add more validation
            if( text.chars().filter(ch -> ch == EQUAL).count() != 1 ){
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
            //System.out.println( "called onStart on " + this.h );
            nodes.add( new ScanNode( CMD.PUSH, this.h, this.defName ) );
        }
        @Override
        public void onPop(){
            //System.out.println( "called onFinish on " + this.h );
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
                case ITEM_OPEN: // log and skip
                    attachedSymb.forceOpened();
                    break;
                case TARGLANG:
                    //System.out.println( text+" means push a TARGLANG" );
                    P.push( new Context_non_nesting( H.TARGLANG ) );
                    break;
                default:
                    // Look for opening symbol connected to first data
                    text = attachedSymb.rmOSymbol_front( text );
                    if( ScanUtil.isUserDef(text) ){
                        text = ScanUtil.getUserDef(text);
                        //System.out.println( text+" means push a USERDEF" );
                        
                        attachedSymb.reset();// reset { } flags
                        text = attachedSymb.rmOSymbol_back(text);//if { attached to keyword
                        //System.out.println( text+" edited?" );
                        P.push( new Context_userDef( H.USERDEF, text ) );
                    }
                    //out.add(new ScanNode( h, text));
                    break;
            }
        }
    }
    // RX: Sub-scanner for RX patterns
    public class Context_RX extends Base_Context{
        public Context_RX(){
            h = H.RX;
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
                case ITEM_OPEN: // log and skip
                    attachedSymb.forceOpened();
                    break;
                default:
                    // Look for opening symbol connected to first data
                    text = attachedSymb.rmOSymbol_front( text );
                    // Look for closing brace connected to last data
                    text = attachedSymb.rmCSymbol( text );
//                    // Look for optional double quotes around RX  TODO troubleshoot Itr_file hold
//                    if( (text = Util_string.trimSurrounding('"', text) ).isEmpty() ){
//                        P.setEr("Empty RX pattern");
//                        P.pop();
//                    }
                    // Primary action: push parser for single RX pattern
                    P.push( new Context_RXPattern() );
                    top.pushPop( text );
                    P.pop();
                    
                    // pop if rmCSymbol removed closing brace
                    if( attachedSymb.isClosed() ){
                        P.pop();
                    }
                    break;
            }
        }
    }
    public class Context_RXPattern extends Base_Context{
        protected Util_ScanRX.Range range;
        protected Util_ScanRX.PatternItr itr;
        protected Util_ScanRX.PairMinder pairMinder;
        
        public Context_RXPattern(){
            h = H.RX_PATTERN;
            range = Util_ScanRX.getInstance_Range();
            itr = Util_ScanRX.getInstance_PatternItr();
            pairMinder = Util_ScanRX.getInstance_ParMinder();
        }
        @Override
        public void pushPop( String text ){
            range.init(text);                   // parse RX back end
            nodes.add( new ScanNode( CMD.SET_ATTRIB, H.LO, ""+range.getLo() ) );
            nodes.add( new ScanNode( CMD.SET_ATTRIB, H.HI, ""+range.getHi() ) );
            text = range.getPattern();          //remove range
            text = pairMinder.trimSurrounding(text);// remove outer parenth
            if(!pairMinder.validParenth(text)){ // check open-close ratio
                P.setEr("Mismatched opening/closing parentheses at: "+text);
            }
            if(!pairMinder.validQuotes(text)){ // check open-close ratio
                P.setEr("Mismatched opening/closing quotes at: "+text);
            }
            char disallowed;
            if( (disallowed = pairMinder.disallowedChar("\"+*?", text)) != '\0' ){
                P.setEr( 
                    disallowed + " not allowed here in '" + text + 
                            ")'unless surrounded by single quotes"
                );
            }
            //System.out.println("Context_RXPattern pushPop: text="+text);
            itr.init(text);  // split on RX characters
            nodes.add( new ScanNode( CMD.OPEN, H.IF, "\t\tpattern" ) );
            P.push( new Context_RX_KeyVal() );
            while(itr.hasNext()){
                top.pushPop(itr.next());//itr.next()
            }
            P.pop();
            nodes.add( new ScanNode( CMD.CLOSE, H.IF, "\t\tpattern" ) );
        }
    }
    public class Context_RX_KeyVal extends Base_Context{
        public final char AND = '&';
        public final char OR = '|';
        public final char NOT = '~';
        public final char OPAR = '(';
        public final char CPAR = ')';
        public final char QUOTE = '\'';
        private boolean isLiteral, isNegated, haveKey;
        private String key;
        private int ifLevel;

        public Context_RX_KeyVal(){
            h = H.RX_KEYVAL;
            isLiteral = false;
            isNegated = false;
            haveKey = false;
            key = "";
            ifLevel = 0;      // Scanner field
        }
        @Override
        public void pushPop( String text ){
            if(text.length() == 1){
                switch(text.charAt(0)){
                    case EQUAL:
                        if(!haveKey){
                            P.setEr("Expected key=value format or 'literal' here: "+text);
                        }
                        this.clearNegated(text);
                        break;
                    case OR:
                        System.out.printf( "clearing negated for %s\n", text );
                        this.clearNegated(text);
                        this.or();
                        break;
                    case AND:
                        System.out.printf( "clearing negated for %s\n", text );
                        this.clearNegated(text);
                        this.and();
                        break;
                    case NOT:
                        System.out.printf( "setting negated for %s\n", text );
                        this.setNegated(text);
                        break;
                    case OPAR:
                        //System.out.printf( "OPAR PUSHPOP Context_RX_KeyVal: %s, ifLevel=%d \n", text, ifLevel );
                        P.push( new Context_RX_KeyVal() );
                        break;
                    case CPAR:
                        //System.out.printf( "CPAR PUSHPOP Context_RX_KeyVal: %s, ifLevel=%d \n", text, ifLevel );
                        P.pop();
                        break;
                    default:
                        add(text);
                        break;
                }
            }
            else{
                add(text);
            }
            //System.out.printf( "TEXT=%s, ifLevel=%d \n", text, ifLevel );
        }
        @Override
        public void onPop(){
            popIfLevel();
            clearNegated("ON POP");
            nodes.add( new ScanNode( CMD.POP, this.h ) );
        }
        @Override
        public void add(Object obj){
            String text = trimSurrounding( (String)obj );
            //System.out.printf( "at default: %s, isLiteral=%b \n", text, isLiteral );
            if(haveKey){
                nodes.add( new ScanNode( CMD.SET_ATTRIB, H.KEY, key ) );
                nodes.add( new ScanNode( CMD.SET_ATTRIB, H.VAL, text ) );
                haveKey = false;
            }
            else if(isLiteral){
                nodes.add( new ScanNode( CMD.SET_ATTRIB, H.KEY, DEFAULT_KEYNAME ) );//DEFAULT_KEYNAME in IParse
                nodes.add( new ScanNode( CMD.SET_ATTRIB, H.VAL, text ) );
            }
            else{
                key = text;
                haveKey = true;
            }
        }
        private String trimSurrounding(String text){
            //System.out.println("trimSurrounding: "+text);
            if(text.charAt(0) == '\'' && text.charAt(text.length()-1) == '\''){
                isLiteral = true;
                return text.substring( 1, text.length()-1 );
            }
            else{
                isLiteral = false;
                return text;
            }
        }
        private void or(){
            nodes.add( new ScanNode( CMD.CLOSE, H.IF, "\t\tkvOR" ) );
            nodes.add( new ScanNode( CMD.OPEN, H.ELIF ) );
        }
        private void and(){
            nodes.add( new ScanNode( CMD.OPEN, H.IF, "\t\tkvAND" ) );
            ifLevel ++;
        }
        private void setNegated(String text){
            isNegated = !isNegated;
            if( isNegated ){
                nodes.add( new ScanNode( CMD.OPEN, H.NEGATE, "\t\t\tkv: " + text ) );
            }
            else{
                nodes.add( new ScanNode( CMD.CLOSE, H.NEGATE, "\t\t\tkv: " + text ) );
            }
        }
        private void clearNegated(String text){
            if( isNegated ){
                isNegated = false;
                nodes.add( new ScanNode( CMD.CLOSE, H.NEGATE, "\t\t\tkv: " + text) );
            }
        }
        private void popIfLevel(){
            for(int i=ifLevel; i>0; i--){
                nodes.add( new ScanNode( CMD.CLOSE, H.IF, "\t\tkvPopIfLevel" ) );
            }
            ifLevel=0;
        }
    }
    
    
    
        // RX: Sub-scanner for RX patterns
    public class Context_FX extends Base_Context{
        public Context_FX(){
            h = H.FX;
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
                case ITEM_OPEN: // log and skip
                    attachedSymb.forceOpened();
                    break;
                default:
                    // Look for opening symbol connected to first data
                    text = attachedSymb.rmOSymbol_front( text );
                    // Look for closing brace connected to last data
                    text = attachedSymb.rmCSymbol( text );
                    
                    // Primary action: push parser for single FX pattern
                    P.push( new Context_RXPattern() );
                    top.pushPop( text );
                    P.pop();
                    
                    // pop if rmCSymbol removed closing brace
                    if( attachedSymb.isClosed() ){
                        P.pop();
                    }
                    break;
            }
        }
    }
    public class Context_FXPattern extends Base_Context{
        protected Util_ScanRX.Range range;
        protected Util_ScanRX.PatternItr itr;
        protected Util_ScanRX.PairMinder pairMinder;
        
        public Context_FXPattern(){
            h = H.FX_PATTERN;
            range = Util_ScanRX.getInstance_Range();
            itr = Util_ScanRX.getInstance_PatternItr();
            pairMinder = Util_ScanRX.getInstance_ParMinder();
        }
        @Override
        public void pushPop( String text ){
            range.init(text);                   // parse RX back end
            nodes.add( new ScanNode( CMD.SET_ATTRIB, H.LO, ""+range.getLo() ) );
            nodes.add( new ScanNode( CMD.SET_ATTRIB, H.HI, ""+range.getHi() ) );
            text = range.getPattern();          //remove range
            text = pairMinder.trimSurrounding(text);// remove outer parenth
            if(!pairMinder.validParenth(text)){ // check open-close ratio
                P.setEr("Mismatched opening/closing parentheses at: "+text);
            }
            if(!pairMinder.validQuotes(text)){ // check open-close ratio
                P.setEr("Mismatched opening/closing quotes at: "+text);
            }
            //System.out.println("Context_RXPattern pushPop: text="+text);
            itr.init(text);  // split on RX characters
            
            P.push( new Context_RX_KeyVal() );
            nodes.add( new ScanNode( CMD.OPEN, H.IF ) );
            while(itr.hasNext()){
                top.pushPop(itr.next());//itr.next()
            }
            nodes.add( new ScanNode( CMD.CLOSE, H.IF ) );
            P.pop();
        }
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
