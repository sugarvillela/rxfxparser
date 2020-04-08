/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

import parse.Keywords.CMD;
import parse.Keywords.HANDLER;
import parse.Keywords.KWORD;

import static parse.Keywords.DEFAULT_KEYNAME;
import static parse.Keywords.EQUAL;
import static parse.Keywords.HANDLER.*;
//import static parse.Keywords.HANDLER.ENUB;
//import static parse.Keywords.HANDLER.ENUD;
//import static parse.Keywords.HANDLER.FX;
//import static parse.Keywords.HANDLER.RX;
//import static parse.Keywords.HANDLER.RXFX;
//import static parse.Keywords.HANDLER.SCOPE;
import static parse.Keywords.SOURCE_OPEN;
import static parse.Keywords.TARGLANG_CLOSE;

/**
 *
 * @author Dave Swanson
 */
public abstract class Factory_Context {
    public static Base_Context get( HANDLER h ){
        switch(h){
            case ENUB:
            case ENUD:
                return new Context_nesting(h);
            case TARGLANG_INSERT:
                return new TargetLanguage_Insert(h);
            case COMMENT:
                return new Comment_short();
            case ATTRIB:
                return new Attrib(h);
            case RX:
                return new RX_Pattern(h);
            case RX_ITEM:
                return new RX_Item(h);
            case RX_KEYVAL:
                return new RX_KeyVal(h);
            case SRCLANG:
                return new SourceLanguage(h);
            case TARGLANG_BASE:
                return new TargetLanguage(h);
                
            //========To implement=====================
            case RXFX:
                return null;//new RXFX(h);
            case FX:
                return null;//new FX_Pattern(h);  RXFX
            case FX_ITEM:
                return null;//new FX_item(h);
            case SCOPE:
                return null;//new Scope(h);
            default:
                return null;
        }
    }
    public static Base_Context get( HANDLER h, String text ){
        switch(h){
            case USERDEF:
                return new UserDefListItem(h, text);
            case VAR:
                return new Var(h, text);
        }
        return null;
    }
    public static class TargetLanguage extends Base_Context{
        public TargetLanguage( HANDLER setH ){
            this.h = setH;
        }
        /**Never pops; pushes SRCLANG or copies text verbatim, no trim
         * @param text a line of text
         */
        @Override
        public void pushPop( String text ){
            switch (text.trim()){
                case SOURCE_OPEN:                        // Start rxfx source code
                    P.fin.setWordGetter();              // rxfx parses word-by-word
                    P.push( get( HANDLER.SRCLANG ) );  // main source handler
                    break; 
                default:// Add exact copy: text not trimmed
                    nodes.add(
                        new IParse.ScanNode( 
                            CMD.ADD_TO, HANDLER.TARGLANG_BASE, text 
                        ) 
                    );
                    break;
            }
        }
    }
    public static class SourceLanguage extends Base_Context{
        SourceLanguage( HANDLER setH ){
            this.h = setH;
            this.allowedHandlers = new HANDLER[]{ATTRIB, ENUB, ENUD, RX, FX };
        }
        /**Pushes handler from list, or sets error; no non-keyword input
         * @param text a word, no space
         */
        @Override
        public void pushPop( String text ){
            // look for end of source or comment
            if( 
                popAll(text) || 
                pushComment(text) ||
                pushTargLang(text)  ||
                ( isUserDef(text) && pushUserDef(VAR, text) )    
            ){
                return;
            }
            // different from pushPopOrErr(): no pop on keyword
            HANDLER keyword = HANDLER.get(text);
            if(keyword != null && !erOnBadHandler(keyword)){
                P.push( get( keyword ) );
            }
        }
    }
    public static class Comment_short extends Base_Context{
        /**Ignores text; pops on end line; obeys CONT_LINE
         * @param text ignored
         */
        @Override
        public void pushPop( String text ){
            if(!popAll(text)){
                if( fin.isEndLine()){
                    P.pop();
                }
            }
        }
        @Override
        public void onPush(){}
        @Override
        public void onPop(){}
    }
    
    // copies all non-keyword items
    public static class Context_non_nesting extends Base_Context{
        public Context_non_nesting( HANDLER setH ){
            h = setH;
        }
        /**copies all non-keyword items; 
         * pops on end line or keyword
         * allows comment and target language insert
         * @param text a word
         */
        @Override
        public void pushPop( String text ){
            if( 
                popAll(text) ||  
                popOnKeyword(text) ||
                pushComment(text) || 
                pushTargLang(text) 
            ){}
            else{
                this.addText(text);
                if( fin.isEndLine() ){
                    P.pop();
                }
            }
        }
    }
    public static class TargetLanguage_Insert extends Context_non_nesting{
        public TargetLanguage_Insert( HANDLER setH ){
            super(setH);
        }
        /**copies trimmed target language text, adding new line where needed
         * @param text a word
         */
        @Override
        public void pushPop( String text ){
            if( TARGLANG_CLOSE.equals(text)){
                P.pop();
            }
            else{
                this.addText( text + " " );
                if( fin.isEndLine() ){
                    nodes.add(
                        new IParse.ScanNode(CMD.SET_ATTRIB, h, KWORD.ENDLINE ) 
                    );
                }
            }
        }
    }
    
    // ignores all non-keyword or non-uesr-defined items
    public static class Context_nesting extends Base_Context{
        public Context_nesting( HANDLER setH ){
            h = setH;
            this.allowedHandlers = new HANDLER[]{ATTRIB };
        }
        /**Allows nested handlers as defined by list and items below
         * @param text a word
         */
        @Override
        public void pushPop( String text ){
            if( 
                    popAll(text)        || 
                    pushComment(text)   || 
                    pushTargLang(text)  || 
                    ( isUserDef(text) && pushUserDef(HANDLER.USERDEF, text) ) || 
                    pushPopOrErr(text)
            );
        }
    }
    public static class Attrib extends Context_non_nesting{
        public Attrib( HANDLER setH ){
            super(setH); 
        }
        @Override
        protected void addText( String text ){//adds more validation
            String toks[] = text.split("=");
            if( toks.length != 2 ){
                P.setEr("key=value format is required at " + text);
                return;
            }
            KWORD k = KWORD.get(toks[0]);
            if(k == null){
                P.setEr("Unknown keyword "+toks[0]+" at: " + text);
                return;
            }
            nodes.add(new IParse.ScanNode( CMD.SET_ATTRIB, h, k, toks[1]));
        }
    }
    public static class UserDefListItem extends Context_non_nesting {
        private final String defName;
        
        UserDefListItem(HANDLER setH, String setName){
            super(setH);
            this.defName = setName;
        }
        
        @Override
        public void onPush(){
            //System.out.println( "called onStart on " + this.h );
            nodes.add( new IParse.ScanNode( CMD.PUSH, this.h, KWORD.DEF_NAME, this.defName ) );
        }
        
        @Override
        public void onPop(){
            //System.out.println( "called onFinish on " + this.h );
            nodes.add( new IParse.ScanNode( CMD.POP, this.h, KWORD.DEF_NAME, this.defName ) );
        }
        
        protected final boolean popOnUserDef(String text){
            if( isUserDef(text) ){
                P.back(text);
                P.pop();
                return true;
            }
            return false;
        }
        
        @Override
        public void pushPop( String text ){
            if( 
                popAll(text)        || 
                popOnUserDef(text)  || 
                popOnKeyword(text)  ||
                pushComment(text)   ||
                pushTargLang(text)
            ){
                return;
            }
            this.addText(text);
            if( fin.isEndLine() ){
                P.pop();
            }
        }
    }

    public static class Var extends Base_Context{
        private final String defName;
        
        public Var(HANDLER setH, String setName){
            this.h = setH;
            this.defName = setName;
            this.allowedHandlers = new HANDLER[]{ SCOPE, RXFX, RX, FX };
        }
        
        @Override
        public void pushPop( String text ){
            if( 
                popAll(text)        || 
                pushComment(text)   ||
                pushTargLang(text)  ||
                pushPopOrErr(text)
            ){}
        }
    }
    public static class Scope extends Base_Context{
        public Scope(HANDLER setH ){
            this.h = setH;
            this.allowedHandlers = new HANDLER[]{ ATTRIB, RXFX, RX, FX };
        }
        
        @Override
        public void pushPop( String text ){
            if( 
                popAll(text)        || 
                pushComment(text)   ||
                pushTargLang(text)  ||
                pushPopOrErr(text)
            ){}
        }
    }
    public static class Rxfx extends Base_Context{
        private boolean haveRx, haveFx, haveAntiFx;
        public Rxfx(HANDLER setH ){
            this.h = setH;
            this.allowedHandlers = new HANDLER[]{ ATTRIB };
            haveRx = haveFx = haveAntiFx = false;
        }
        @Override
        public void pushPop(String text){
            if( 
                popAll(text)        || 
                pushComment(text)   ||
                pushTargLang(text)
            ){
                return;
            }
            HANDLER keyword = HANDLER.get(text);
            if( keyword == null ){
                P.setEr( "Unknown keyword: " + text );
                return;
            }
            switch(keyword){
                case ATTRIB:
                    break;
                case RX:
                    if(!haveRx){
                        haveRx = true;
                        P.push( Factory_Context.get(keyword) );
                    }
                    else{
                        P.setEr( "RXFX must contain one RX" );
                    }
                    break;
                case FX:
                    if( haveRx ){
                        if( !haveFx ){
                            haveFx = true;
                            P.push( Factory_Context.get(keyword) );
                        }
                        else{
                            P.setEr( "RXFX must contain one FX" );
                        }
                    }
                    else{
                        P.setEr( "FX must follow RX");
                    }
                    break;
                default:
                    pushPopOrErr(text);
            }
            
        }
    }
    // RX: Sub-scanner for RX patterns
    public static class RX_Pattern extends Base_Context{
        public RX_Pattern( HANDLER setH ){
            this.h = setH;
        }
        @Override
        public void pushPop( String text ){
            if( popAll(text) || popOnKeyword(text) ){
                return;//
            }
            P.push( get(HANDLER.RX_ITEM) );
            P.getTop().pushPop( text );
            P.pop();
            if( fin.isEndLine() ){
                P.pop();
            }
        }
    }
    public static class RX_Item extends Base_Context{
        protected Util_ScanRX.Range range;
        protected Util_ScanRX.PatternItr itr;
        protected Util_ScanRX.PairMinder pairMinder;
        public RX_Item( HANDLER setH ){
            h = setH;
            range = Util_ScanRX.getInstance_Range();
            itr = Util_ScanRX.getInstance_PatternItr();
            pairMinder = Util_ScanRX.getInstance_ParMinder();
        }
        @Override
        public void pushPop( String text ){
            range.init(text);                   // parse RX back end
            nodes.add( new IParse.ScanNode( CMD.SET_ATTRIB, h, KWORD.LO, ""+range.getLo() ) );
            nodes.add( new IParse.ScanNode( CMD.SET_ATTRIB, h, KWORD.HI, ""+range.getHi() ) );
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
            nodes.add( new IParse.ScanNode( CMD.BEGIN, h, KWORD.IF, "\t\tpattern" ) );
            P.push( get(HANDLER.RX_KEYVAL) );
            while(itr.hasNext()){
                P.getTop().pushPop(itr.next());//itr.next()
            }
            P.pop();
            nodes.add( new IParse.ScanNode( CMD.END, h, KWORD.IF, "\t\tpattern" ) );
        }
    }
    public static class RX_KeyVal extends Base_Context{
        public final char AND = '&';
        public final char OR = '|';
        public final char NOT = '~';
        public final char OPAR = '(';
        public final char CPAR = ')';
        public final char QUOTE = '\'';
        private boolean isLiteral, isNegated, haveKey;
        private String key;
        private int ifLevel;

        public RX_KeyVal( HANDLER setH ){
            h = setH;
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
                        P.push(new RX_KeyVal(HANDLER.RX_KEYVAL) );
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
            nodes.add( new IParse.ScanNode( CMD.POP, this.h ) );
        }
        @Override
        public void add(Object obj){
            String text = trimSurrounding( (String)obj );
            //System.out.printf( "at default: %s, isLiteral=%b \n", text, isLiteral );
            if(haveKey){
                nodes.add( new IParse.ScanNode( CMD.SET_ATTRIB, h, KWORD.KEY, key ) );
                nodes.add( new IParse.ScanNode( CMD.SET_ATTRIB, h, KWORD.VAL, text ) );
                haveKey = false;
            }
            else if(isLiteral){
                nodes.add( new IParse.ScanNode( CMD.SET_ATTRIB, h, KWORD.KEY, DEFAULT_KEYNAME ) );//DEFAULT_KEYNAME in IParse
                nodes.add( new IParse.ScanNode( CMD.SET_ATTRIB, h, KWORD.VAL, text ) );
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
            nodes.add( new IParse.ScanNode( CMD.END, h, KWORD.IF, "\t\tkvOR" ) );
            nodes.add( new IParse.ScanNode( CMD.BEGIN, h, KWORD.ELIF ) );
        }
        private void and(){
            nodes.add( new IParse.ScanNode( CMD.BEGIN, h, KWORD.IF, "\t\tkvAND" ) );
            ifLevel ++;
        }
        private void setNegated(String text){
            isNegated = !isNegated;
            if( isNegated ){
                nodes.add( new IParse.ScanNode( CMD.BEGIN, h, KWORD.NEGATE, "\t\t\tkv: " + text ) );
            }
            else{
                nodes.add( new IParse.ScanNode( CMD.END, h, KWORD.NEGATE, "\t\t\tkv: " + text ) );
            }
        }
        private void clearNegated(String text){
            if( isNegated ){
                isNegated = false;
                nodes.add( new IParse.ScanNode( CMD.END, h, KWORD.NEGATE, "\t\t\tkv: " + text) );
            }
        }
        private void popIfLevel(){
            for(int i=ifLevel; i>0; i--){
                nodes.add( new IParse.ScanNode( CMD.END, h, KWORD.IF, "\t\tkvPopIfLevel" ) );
            }
            ifLevel=0;
        }
    }
}
