/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

import java.util.ArrayList;
import static parse.Keywords.COMMENT_TEXT;
import static parse.Keywords.SOURCE_CLOSE;
import static parse.Keywords.USERDEF_OPEN;
import static parse.IParse.ScanNode;
import parse.Keywords.HANDLER;
import itr_struct.StringSource;
import static parse.Keywords.COMMENT_OPEN;

/**
 *
 * @author Dave Swanson
 */
    // handlers for context-sensitive control of stack
    public abstract class Base_Context extends Base_StackItem{
        protected ArrayList<ScanNode> nodes;
        protected StringSource fin;
        
        public Base_Context(){
            P = Class_Scanner.getInstance();
            nodes = P.getScanNodeList();
            fin = P.getStringSource();
        }
        // Utilities for subclasses: call in this order
        protected final boolean popAll(String text){
            if(SOURCE_CLOSE.equals(text)){
                P.setLineGetter();
                P.popAllSource();
                return true;
            }
            return false;
        }
        // detects connected or unconnected 
        protected final boolean pushComment(String text){
            if(text.startsWith(COMMENT_TEXT)){// okay to discard text
                P.push( Factory_Context.get(HANDLER.COMMENT) );
                return true;
            }
            if(text.startsWith(COMMENT_OPEN)){// okay to discard text
                P.push( Factory_Context.get(HANDLER.COMMENT_LONG) );
                return true;
            }
            return false;
        }
        protected final boolean pushUserDefListItem(String text){
            if( text.startsWith(USERDEF_OPEN) ){
                P.push( 
                    Factory_Context.get(
                        HANDLER.USERDEF, text.substring(USERDEF_OPEN.length())
                    )
                );
                return true;
            }
            return false;
        }
        protected boolean popOnKeyword(String text){
            Keywords.HANDLER keyword = Keywords.HANDLER.get(text);
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
            P.setEr( "Unknown keyword in: " + text );
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
        public StringSource getStringSource(){
            if(fin==null){
                commons.Erlog.getInstance().set(
                    "Developer: StringSource not initialized in "+this.getClass().getSimpleName()
                );
            }
            return fin;
        }
    }
    
