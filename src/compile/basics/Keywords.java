/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compile.basics;

/**Contains the language definition, including enums, constants
 *
 * @author Dave Swanson
 */
public final class Keywords {
    public static final String SOURCE_FILE_EXTENSION = ".rxfx";
    public static final String INTERIM_FILE_EXTENSION = ".rxlx";
    
    public enum KWORD_TYPE{
        H,C
    }
    // List of commands to instruct parser
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
    // List of handlers to be implemented
    public enum HANDLER {//values() returns H[] array
        // File generating handlers
        TARGLANG_BASE, 
        ENUB, 
        ENUD, 
        VAR, 
        SCOPE, 
        RXFX, 
        // Non-file-generating handlers
        RX, FX, SRCLANG, ATTRIB, 
        // sub-handlers not actually in the language
        RX_WORD, FX_WORD, //RX_STATEMENT, 
        //replace with above line
        RX_ITEM, RX_KEYVAL, FX_ITEM,
        // handlers whose text indicators are not the same as enum name
        TARGLANG_INSERT, COMMENT, USER_DEF_LIST, USER_DEF_VAR,
        // Top enum ordinal gives size of list
        NUM_HANDLERS
        ;
        
        public static HANDLER get( String text ){
            for(HANDLER h : values()){
                if(h.toString().equals(text)){
                    return h;
                }
            }
            return null;
        }
//        public static boolean isKeyword( String text ){
//            return get(text) != null;
//        }  
    }
    public enum KWORD{
        // Keys for setAttrib()
        // keywords that can be specified in language
        PROJ_NAME, KEY, VAL, WROW, WVAL,
        // Internal keywords for communicating between components
        DEF_NAME, LO, HI, ABOVE;//, IF, ELIF, ELSE, NEGATE, ENDLINE, PARSE_STATUS
        ;
        public static KWORD get( String text ){
            for(KWORD k : values()){
                if(k.toString().equals(text)){
                    return k;
                }
            }
            return null;
        }
    }

    // String constants for switches: defines language behavior
    public static final String CONT_LINE = "...";      // Matlab-like extension
    public static final String SOURCE_OPEN = "/*$";    // pushes source handler
    public static final String SOURCE_CLOSE = "$*/";   // pops all source handlers
    public static final String TARGLANG_INSERT_OPEN = "*/"; // inserts target language without popping source
    public static final String TARGLANG_INSERT_CLOSE = "/*";// pops target language insert
    public static final String ITEM_OPEN = "{";        // surrounds item content
    public static final String ITEM_CLOSE = "}";       // ends item content
    public static final String USERDEF_OPEN = "$";     // user-defined heading
    public static final String COMMENT_TEXT = "//";    // Widget.getCommentSymbol() TODO
    public static final char   EQUAL = '=';            // key=value or key:value ?
    public static final String DEFAULT_KEYNAME = "text";// class WORD text field
    public static final String 	RXAND = "&";            // RX symbol
    public static final String 	RXOR = "|";             // RX symbol
    public static final String 	RXNOT = "~";            // RX symbol
}
