/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

/**
 *
 * @author admin
 */
public final class Keywords {
    public enum KWORD_TYPE{
        H,C
    }
    // List of handlers to be implemented
    public enum HANDLER {//values() returns H[] array
        // File generating handlers
        TARGLANG_BASE, ENUB, ENUD, SCOPE, RXFX, 
        // Non-file-generating handlers
        RX, FX, SRCLANG, ATTRIB, TARGLANG,
        // sub-handlers not actually in the language
        RX_ITEM, RX_KEYVAL, FX_ITEM,
        // handlers whose text indicators are not the same as enum name
        USERDEF, COMMENT, COMMENT_LONG,
        // Top enum ordinal gives size of list
        NUM_HANDLERS,
        // Keys for setAttrib()
        DEF_NAME, LO, HI, KEY, VAL, IF, ELIF, ELSE, NEGATE
        ;
        public static HANDLER get( String text ){
            for(HANDLER h : values()){
                if(h.toString().equals(text)){
                    return h;
                }
            }
            return null;
        }
        public static boolean isKeyword( String text ){
            return get(text) != null;
        }  
    }
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
    // String constants for switches: defines language behavior
    public static final String CONT_LINE = "...";      // Matlab-like extension
    public static final String SOURCE_OPEN = "/*$";    // pushes source handler
    public static final String SOURCE_CLOSE = "$*/";   // pops all source handlers
    public static final String ITEM_OPEN = "{";        // surrounds item content
    public static final String ITEM_CLOSE = "}";       // ends item content
    public static final String USERDEF_OPEN = "$";     // user-defined heading
    public static final String COMMENT_TEXT = "//";    // Widget.getCommentSymbol() TODO
    public static final String COMMENT_OPEN = "*/";// Widget.getCommentSymbol() TODO
    public static final String COMMENT_CLOSE = "*/";// Widget.getCommentSymbol() TODO
    public static final char EQUAL = '=';              // key=value or key:value ?
    public static final String DEFAULT_KEYNAME = "text";// class WORD text field
}
