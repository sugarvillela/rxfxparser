/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compile.basics;

import java.util.regex.Pattern;

/**Contains the language definition, including enums, constants
 *
 * @author Dave Swanson
 */
public final class Keywords {
    public static final String SOURCE_FILE_EXTENSION = ".rxfx";
    public static final String INTERIM_FILE_EXTENSION = ".rxlx";
    public static final String STATUS_FORMAT = "%s line %d word %d";

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
        FOR,
        IF, ELSE,
        RX, FX, SRCLANG,
        // Constant types
        CONSTANT,
        // Non-file-generating handlers
        ATTRIB, INCLUDE, FUN, RAW_TEXT,
        // sub-handlers not actually in the language
        IF_ELSE, BOOL_TEST, RX_WORD, RX_BUILDER, FX_WORD, //RX_STATEMENT,
        SYMBOL_TABLE, 
        // handlers whose text indicators are not the same as enum name
        TARGLANG_INSERT, COMMENT, USER_DEF_LIST, USER_DEF_VAR,
        // error indicator
        //UNKNOWN_HANDLER,
        // Top enum ordinal gives size of list
        NUM_HANDLERS
        ;
        
        public static HANDLER fromString( String text ){
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
    public enum FIELD {
        // Keys for setAttrib()
        // keywords that can be specified in language
        PROJ_NAME, KEY, VAL, WROW, WVAL, NEW_ENUM_SET,
        // Internal keywords for communicating between components
        DEF_NAME, //ANON_NAME, // named and anonymous variables
        LO, HI // RX ranges
        //rx
        //RX_AND1, RX_OR1, RX_EQUAL, RX_GT, RX_LT, RX_PAYLOAD, RX_NEGATE,
        //BRANCH, LEAF //tree structure roles
        //, IF, ELIF, ELSE, NEGATE, ENDLINE, PARSE_STATUS
        ;
        public static FIELD fromString(String text ){
            for(FIELD k : values()){
                if(k.toString().equals(text)){
                    return k;
                }
            }
            return null;
        }
    }
    public enum OP{
        AND     ('&'),
        OR      ('|'),
        EQUAL   ('='),
        GT      ('>'),
        LT      ('<'),
        NOT     ('~'),
        PAYLOAD ('P'),
        OPAR    ('('),
        CPAR    (')'),
        SQUOTE  ('\'')
        ;
        
        public final char asChar;
        private OP(char asChar){
            this.asChar = asChar;
        }
        public char toChar(){
            return asChar;
        }
        public static OP fromChar(char ch){
            for(OP op : values()){
                if(op.asChar == ch){
                    return op;
                }
            }
            return null;
        }
        public static OP fromString( String text ){
            for(OP  op : values()){
                if(op.toString().equals(text)){
                    return op;
                }
            }
            return null;
        }
    }
    public enum FUNCT{
        // function names for Rx logic
        FIRST, LAST, LEN
        ;
        public static FUNCT get( String text ){
            for(FUNCT f : values()){
                if(f.toString().equals(text)){
                    return f;
                }
            }
            return null;
        }
    }
    //EMPTY_PARAM = 0, NUM_PARAM = 1, NUM_RANGE = 2, NUM_RANGE = 3, CONST_PARAM = 4, NO_FUN

    public enum RX_PARAM_TYPE{
        // Rx Functions
        EMPTY_PARAM     (0, true,  Pattern.compile("\\(\\)$")),
        NUM_PARAM       (1, true,  Pattern.compile("\\([0-9]+\\)$")),
        NUM_RANGE_PARAM (2, true,  Pattern.compile("\\([0-9]+[-][0-9]\\)$")),
        ALPHA_NUM_PARAM (3, true,  Pattern.compile("\\([A-Za-z0-9_]+\\)$")),
        CONST_PARAM     (4, true,  Pattern.compile("\\([$][A-Za-z][A-Za-z0-9_]*\\)$")),
        SINGLE_FIELD    (5, false, Pattern.compile("^[a-zA-z][a-zA-z0-9_]*$")),
        DOTTED_FIELD    (6, false, Pattern.compile("^([a-zA-z][a-zA-z0-9_]*\\.)+[a-zA-z][a-zA-z0-9_]*$"))
        ;

        public final int asInt;
        public final boolean isFun;
        public final Pattern pattern;
        private RX_PARAM_TYPE(int asInt, boolean isFun, Pattern pattern){
            this.asInt = asInt;
            this.isFun = isFun;
            this.pattern = pattern;
        }

        public static RX_PARAM_TYPE fromInt(int i){
            for(RX_PARAM_TYPE t : values()){
                if(t.asInt == i){
                    return t;
                }
            }
            return null;
        }
        public static RX_PARAM_TYPE get( String text ){
            for(RX_PARAM_TYPE f : values()){
                if(f.toString().equals(text)){
                    return f;
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
    public static final String USERDEF_OPEN = "$";     // user-defined name
    public static final String COMMENT_TEXT = "//";    //
    public static final String TEXT_FIELD_NAME = "text";// class WORD text field

    public static String fileName_symbolTableEnu(){
        return String.format(
            "%s_%s_%s", 
            HANDLER.SYMBOL_TABLE, 
            "ENU", 
            CompileInitializer.getInstance().getProjName()) + INTERIM_FILE_EXTENSION;
    }
}
