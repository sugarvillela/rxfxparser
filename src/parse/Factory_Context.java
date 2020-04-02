/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

import static parse.Keywords.COMMENT_CLOSE;
import static parse.Keywords.DEFAULT_KEYNAME;
import static parse.Keywords.EQUAL;
import static parse.Keywords.SOURCE_OPEN;
import static parse.Keywords.USERDEF_OPEN;
import parse.Keywords.HANDLER;

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
            case TARGLANG:
                return new Context_non_nesting(h);
            case COMMENT:
                return new Comment_short();
            case COMMENT_LONG:
                return new Comment_long();
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
        }
        return null;
    }
    public static class TargetLanguage extends Base_Context{
        public TargetLanguage( HANDLER setH ){
            this.h = setH;
        }
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
                            Keywords.CMD.ADD_TO, HANDLER.TARGLANG_BASE, text 
                        ) 
                    );
                    break;
            }
        }
    }
    public static class SourceLanguage extends Base_Context{
        SourceLanguage( HANDLER setH ){
            this.h = setH;
            this.allowedHandlers = new HANDLER[]{HANDLER.ATTRIB, HANDLER.TARGLANG, HANDLER.ENUB, HANDLER.ENUD, HANDLER.RX, HANDLER.FX };
        }
        
        @Override
        public void pushPop( String text ){
            // look for end of source or comment
            if( popAll(text) || pushComment(text)){
                return;
            }
            // different from pushPopOrErr(): no pop on keyword
            HANDLER keyword = HANDLER.get(text);
            if(keyword != null && !erOnBadHandler(keyword)){
                P.push( get( keyword ) );
            }
        }
    }
    // ignores commented text until end of line
    public static class Comment_short extends Base_Context{
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
    
    // ignores commented text until closing symbol
    public static class Comment_long extends Comment_short{
        @Override
        public void pushPop( String text ){
            if(!popAll(text)){
                if( text.endsWith(COMMENT_CLOSE)){ 
                    P.pop();
                }
            }
        }
    }
    
    // copies all non-keyword items
    public static class Context_non_nesting extends Base_Context{
        //public Context_non_nesting(){}
        public Context_non_nesting( HANDLER setH ){
            h = setH;
        }
        @Override
        public void pushPop( String text ){
            if( popAll(text) || popOnKeyword(text)){
                return;
            }
            this.addText(text);
            if( fin.isEndLine() ){
                P.pop();
            }
        }
    }
    
// ignores all non-keyword or non-uesr-defined items
    public static class Context_nesting extends Base_Context{
        public Context_nesting( HANDLER setH ){
            h = setH;
            this.allowedHandlers = new HANDLER[]{HANDLER.ATTRIB, HANDLER.TARGLANG };
        }
        @Override
        public void pushPop( String text ){
            if( 
                    popAll(text) || 
                    pushComment(text) || 
                    pushUserDefListItem(text) || 
                    pushPopOrErr(text)
            );
        }
    }
    public static class Attrib extends Context_non_nesting{
        public Attrib( HANDLER setH ){
            super(setH); 
        }
        @Override
        protected void addText( String text ){//add more validation
            if( text.chars().filter(ch -> ch == EQUAL).count() != 1 ){
                P.setEr("key=value format is required at " + text);
            }
            nodes.add(new IParse.ScanNode( Keywords.CMD.ADD_TO, h, text));
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
            nodes.add( new IParse.ScanNode( Keywords.CMD.PUSH, this.h, this.defName ) );
        }
        @Override
        public void onPop(){
            //System.out.println( "called onFinish on " + this.h );
            nodes.add( new IParse.ScanNode( Keywords.CMD.POP, this.h, this.defName ) );
        }
        protected final boolean popOnUserDef(String text){
            if( text.startsWith(USERDEF_OPEN) ){
                P.back(text);
                P.pop();
                return true;
            }
            return false;
        }
        @Override
        public void pushPop( String text ){
            if( popAll(text) || popOnUserDef(text) || popOnKeyword(text) ){
                return;
            }
            this.addText(text);
            if( fin.isEndLine() ){
                P.pop();
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
            nodes.add( new IParse.ScanNode( Keywords.CMD.SET_ATTRIB, HANDLER.LO, ""+range.getLo() ) );
            nodes.add( new IParse.ScanNode( Keywords.CMD.SET_ATTRIB, HANDLER.HI, ""+range.getHi() ) );
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
            nodes.add( new IParse.ScanNode( Keywords.CMD.OPEN, HANDLER.IF, "\t\tpattern" ) );
            P.push( get(HANDLER.RX_KEYVAL) );
            while(itr.hasNext()){
                P.getTop().pushPop(itr.next());//itr.next()
            }
            P.pop();
            nodes.add( new IParse.ScanNode( Keywords.CMD.CLOSE, HANDLER.IF, "\t\tpattern" ) );
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
            nodes.add( new IParse.ScanNode( Keywords.CMD.POP, this.h ) );
        }
        @Override
        public void add(Object obj){
            String text = trimSurrounding( (String)obj );
            //System.out.printf( "at default: %s, isLiteral=%b \n", text, isLiteral );
            if(haveKey){
                nodes.add( new IParse.ScanNode( Keywords.CMD.SET_ATTRIB, HANDLER.KEY, key ) );
                nodes.add( new IParse.ScanNode( Keywords.CMD.SET_ATTRIB, HANDLER.VAL, text ) );
                haveKey = false;
            }
            else if(isLiteral){
                nodes.add( new IParse.ScanNode( Keywords.CMD.SET_ATTRIB, HANDLER.KEY, DEFAULT_KEYNAME ) );//DEFAULT_KEYNAME in IParse
                nodes.add( new IParse.ScanNode( Keywords.CMD.SET_ATTRIB, HANDLER.VAL, text ) );
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
            nodes.add( new IParse.ScanNode( Keywords.CMD.CLOSE, HANDLER.IF, "\t\tkvOR" ) );
            nodes.add( new IParse.ScanNode( Keywords.CMD.OPEN, HANDLER.ELIF ) );
        }
        private void and(){
            nodes.add( new IParse.ScanNode( Keywords.CMD.OPEN, HANDLER.IF, "\t\tkvAND" ) );
            ifLevel ++;
        }
        private void setNegated(String text){
            isNegated = !isNegated;
            if( isNegated ){
                nodes.add( new IParse.ScanNode( Keywords.CMD.OPEN, HANDLER.NEGATE, "\t\t\tkv: " + text ) );
            }
            else{
                nodes.add( new IParse.ScanNode( Keywords.CMD.CLOSE, HANDLER.NEGATE, "\t\t\tkv: " + text ) );
            }
        }
        private void clearNegated(String text){
            if( isNegated ){
                isNegated = false;
                nodes.add( new IParse.ScanNode( Keywords.CMD.CLOSE, HANDLER.NEGATE, "\t\t\tkv: " + text) );
            }
        }
        private void popIfLevel(){
            for(int i=ifLevel; i>0; i--){
                nodes.add( new IParse.ScanNode( Keywords.CMD.CLOSE, HANDLER.IF, "\t\tkvPopIfLevel" ) );
            }
            ifLevel=0;
        }
    }
}
