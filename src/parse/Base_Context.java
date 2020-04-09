/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

import java.util.ArrayList;

import parse.Keywords.HANDLER;

import static parse.Keywords.COMMENT_TEXT;
import static parse.Keywords.SOURCE_CLOSE;
import static parse.Keywords.USERDEF_OPEN;
import static parse.Keywords.HANDLER.TARGLANG_INSERT;
import static parse.Keywords.TARGLANG_OPEN;

import static parse.IParse.ScanNode;
import toksource.StringSource;
import toksource.TokenSource;
/**
 *
 * @author Dave Swanson
 */
    // handlers for context-sensitive control of stack
    public abstract class Base_Context extends Base_StackItem{
        protected ArrayList<ScanNode> nodes;
        protected TokenSource fin;
        
        public Base_Context(){
            P = Class_Scanner.getInstance();
            nodes = P.getScanNodeList();
            fin = P.getTokenSource();
        }
        // First: Utilities for subclasses:
        
        // detect connected symbol; false on symbol alone
        protected final boolean isUserDef(String text){
            return text.startsWith(USERDEF_OPEN) && 
                    !text.equals(USERDEF_OPEN);
        }
        // Push/Pops: Call popAll first, if you call it...
        protected final boolean popAll(String text){
            if(SOURCE_CLOSE.equals(text)){
                P.setLineGetter();
                P.popAllSource();
                return true;
            }
            return false;
        }
        // detects connected or unconnected comment symbol
        protected final boolean pushComment(String text){
            if(text.startsWith(COMMENT_TEXT)){// okay to discard text
                P.push( Factory_Context.get(HANDLER.COMMENT) );
                return true;
            }
            return false;
        }
        protected final boolean pushTargLang(String text){
            if( TARGLANG_OPEN.equals(text) ){
                P.push( Factory_Context.get(TARGLANG_INSERT) );
                return true;
            }
            return false;
        }
        protected final boolean pushUserDef(HANDLER uDefHandler, String text){
            P.push( 
                Factory_Context.get(
                    uDefHandler, text.substring(USERDEF_OPEN.length())
                )
            );
            return true;
        }
        protected boolean popOnKeyword(String text){
            HANDLER keyword = HANDLER.get(text);
            if( keyword != null){
                P.back(text);//repeat keyword so next handler can push it
                P.pop();
                return true;
            }
            return false;
        }
        // Err if not keyword; push if allowed; pop if not allowed
        protected boolean pushPopOrErr(String text){
            HANDLER keyword = HANDLER.get(text);
            if( keyword != null ){
                if(isAllowedHandler(keyword)){
                    P.push( Factory_Context.get(keyword) );
                    return true;
                }
                else{
                    P.back(text);
                    P.pop();
                    return false;
                }
            }
            P.setEr( "Unknown keyword: " + text );
            return false;
        }
        
        @Override
        public void onPush(){
            //System.out.println( "called start on " + this.h );
            nodes.add( new IParse.ScanNode( Keywords.CMD.PUSH, this.h ) );
        }
        @Override
        public void onPop(){
            //System.out.println( "called finish on " + this.h );
            nodes.add( new IParse.ScanNode( Keywords.CMD.POP, this.h ) );
        }
        // helper Util
        protected void addText( String text ){//override to add more validation
            nodes.add(new IParse.ScanNode( Keywords.CMD.ADD_TO, h, text));
        }
        @Override
        public ArrayList<ScanNode> getScanNodeList(){
            if(nodes==null){
                commons.Erlog.getInstance().set(
                    "Developer: scan node list not initialized in "+this.getClass().getSimpleName()
                );
            }
            return nodes;
        }
        @Override
        public Base_StackItem getTop(){
            return P.getTop();
        }
        @Override
        public TokenSource getTokenSource(){
            if(fin==null){
                commons.Erlog.getInstance().set(
                    "Developer: StringSource not initialized in "+this.getClass().getSimpleName()
                );
            }
            return fin;
        }
    }
    
