/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

import commons.Commons;

/**Contains the language definition, including enums, constants, node classes
 *
 * @author Dave Swanson
 */
public interface IParse {
    
    // String constants for switches: defines language behavior
    public final String SOURCE_OPEN = "/*$";    // pushes source handler
    public final String SOURCE_CLOSE = "$*/";   // pops all source handlers
    public final String ITEM_OPEN = "{";        // surrounds item content
    public final String ITEM_CLOSE = "}";       // ends item content
    public final String USERDEF_OPEN = "$";     // user-defined heading
    public final String COMMENT = "//";         // Widget.getCommentSymbol() TODO
    public final char EQUAL = '=';             // key=value or key:value ?
    public final String DEFAULT_KEYNAME = "text";// implemenation default for text field
    
    
    
    // List of handlers to be implemented
    public enum H {//values() returns H[] array
        // File generating handlers
        TARGLANG_BASE, ENUB, ENUD, SCOPE, RX, FX,
        // Non-file-generating handlers
        SRCLANG, ATTRIB, USERDEF, TARGLANG,
        RX_PATTERN, RX_KEYVAL, FX_PATTERN, 
        // Top enum ordinal gives size of list
        NUM_HANDLERS,
        // Keys for setAttrib()
        DEF_NAME, LO, HI, KEY, VAL, IF, ELIF, ELSE, NEGATE
        ;
        public static H get( String text ){
            for(H h : values()){
                if(h.toString().equals(text)){
                    return h;
                }
            }
            return null;
        }
        public static boolean isH( String text ){
            return get(text) != null;
        }  
    }
    // String constants for switches; must match H enums exactly
    public final String ATTRIB =        "ATTRIB";
    public final String ENUB =          "ENUB";
    public final String ENUD =          "ENUD";
    public final String FX =            "FX";
    public final String RX =            "RX";
    public final String SCOPE =         "ATTRIB";
    public final String SRCLANG =       "SRCLANG";
    public final String TARGLANG_BASE = "TARGLANG_BASE";
    public final String TARGLANG =      "TARGLANG";
    public final String USERDEF =       "USERDEF";
    public final String DEF_NAME =      "DEF_NAME";
    public final String LO =            "LO";
    public final String HI =            "HI";
    
    // List of commands output by scanner and read by parser
    public enum CMD { 
        PUSH, POP, ADD_TO, SET_ATTRIB, OPEN, CLOSE;
        public static CMD get( String text ){
            for(CMD cmd : values()){
                if(cmd.toString().equals(text)){
                    return cmd;
                }
            }
            return null;
        }
    }
    
    // node for input and output list
    public class ScanNode{
        public CMD cmd;
        public H h;
        public String data;

        public ScanNode(){}
        public ScanNode(CMD setCommand, H setHandler){
            this(setCommand, setHandler, "");
        }
        public ScanNode(CMD setCommand, H setHandler, String setData){
            h = setHandler;
            cmd = setCommand;
            data = setData;
        }
        @Override
        public String toString(){
            return Commons.objStr(cmd) + "," + 
                    Commons.objStr(h) + "," + data + ",";
        }
    }
    // scan node with self-parser in constructor
    public class ScanNode_fromFile extends ScanNode {
        public ScanNode_fromFile(Base_Stack P, String text){
            int start = 0, i, j=0;
            System.out.println(text);
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
                    h = H.get(text.substring(start, i));
                    System.out.println(h+": i = "+i);
                    start=i+1;
                    break;
                }
            }
            data = text.substring(start, text.length()-1);
            System.out.println(data+": i = "+i);
            System.out.println(this.toString());
        }
    }
    public class GroupNode{
        public String n;
        public int s, e;
        public GroupNode( String name, int n ){
            this.n = name;
            this.s = this.e = n;
        }
        @Override
        public String toString(){
            return this.n + ": start=" + s + " end=" + e;
        }
    }
    
    public class ScanUtil{
        public static boolean isUserDef( String text ){
            return text.startsWith(USERDEF_OPEN);
        }
        public static String getUserDef( String text ){
            return text.substring(USERDEF_OPEN.length());
        }
    }
    public class ScanUtil_AttachedSymbol{
        // Unattached symbol handled in pushPop switch;
        // Attached symbol needs string manip
        private boolean oSymbol, cSymbol;
        
        public void reset(){
            oSymbol = cSymbol = false;
        }
        public String rmOSymbol_back( String text ){
            //System.out.print("rmOSymbol_back: text="+text);
            if( text.endsWith(ITEM_OPEN) ){// keyword{
                oSymbol = true;
                //System.out.println(": found it");
                return text.substring(0, text.length() - ITEM_OPEN.length());
            }
            else{
                //System.out.println(": no find");
                return text;
            }
        }
        public String rmOSymbol_front( String text ){//found it, has it, or error
            //System.out.print("rmOSymbol_front: text="+text);
            if( oSymbol ){// already found and removed
                //System.out.println(": already done");
                return text;
            }
            oSymbol = true; // ignore after first call
            if( text.startsWith(ITEM_OPEN) ){//
                return text.substring(ITEM_OPEN.length());
            }
            else{
                Class_Scanner.getInstance().setEr("Opening curly brace required here: "+text);
                return text;
            }
        }
        public String rmCSymbol( String text ){// handles this: data}
            if( text.endsWith(ITEM_CLOSE) ){// 
                cSymbol = true;
                return text.substring(0, text.length() - ITEM_CLOSE.length());
            }
            else{
                return text;
            }
        }
        public boolean isOpened(){
            return oSymbol;
        }
        public boolean isClosed(){
            return cSymbol;
        }
        public void forceOpened(){
            oSymbol = true;
        }
        public void forceClosed(){
            cSymbol = true;
        }
    }
    
    // Interface
    public void push( Base_StackItem nuTop );   // enum CMD
    public void pop();                          // enum CMD
    public void add(Object obj);                // enum CMD
    
    public void setAttrib(String key, Object value);// enum KEY
    public Object getAttrib(String key);           // enum KEY
    
    public void onCreate(); // call if object exists before push
    public void onPush();   // call immediately after push
    public void onPop();    // call just before pop
    public void onQuit();   // call when program/task is finished
    
    public void disp();
}
