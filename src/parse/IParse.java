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
    public final char KEYVAL = '=';             // key=value or key:value ?
    
    
    // List of handlers to be implemented
    public enum H {//values() returns H[] array
        // File generating handlers
        TARGLANG_BASE, ENUB, ENUD, SCOPE, RX, FX,
        // Non-file-generating handlers
        RX_ITEM, SRCLANG, ATTRIB, USERDEF, TARGLANG,
        // Top enum ordinal gives size of list
        NUM_HANDLERS,
        // Keys for setAttrib()
        DEF_NAME, LO, HI
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
    
    // List of commands output by scanner and read by parser
    public enum CMD { 
        PUSH, POP, ADD_TO, SET_ATTRIB;
        public static CMD get( String text ){
            for(CMD cmd : values()){
                if(cmd.toString().equals(text)){
                    return cmd;
                }
            }
            return null;
        }
    }
    // String constants for switches; to match H enums exactly
    public final String DEF_NAME =      "DEF_NAME";
    public final String LO =            "LO";
    public final String HI =            "HI";
    
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
        public static boolean isItemOpener_front( String text ){
            return text.startsWith(ITEM_OPEN);
        }
        public static boolean isItemOpener_back( String text ){
            return text.endsWith(ITEM_OPEN);
        }
        public static boolean isItemCloser( String text ){
            return text.endsWith(ITEM_CLOSE);
        }
        public static String getUserDef( String text ){
            return text.substring(USERDEF_OPEN.length());
        }
        public static String rmItemOpener_front( String text ){
            return text.substring(ITEM_OPEN.length());
        }
        public static String rmItemOpener_back( String text ){
            return text.substring(0, text.length() - ITEM_OPEN.length());
        }
        public static String rmItemCloser( String text ){
            return text.substring(0, text.length() - ITEM_CLOSE.length());
        }
        public static boolean errOBrace( Base_Stack P, String text ){
            if( text.startsWith(ITEM_OPEN) ){
                return false;
            }
            else{
                P.setEr("Opening curly brace required here: "+text);
                return true;
            }
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
