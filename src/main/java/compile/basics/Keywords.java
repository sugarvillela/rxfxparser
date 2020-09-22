/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compile.basics;

import compile.symboltable.ListTable;

import java.util.regex.Matcher;
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
        public static CMD fromString(String text ){
            for(CMD cmd : values()){
                if(cmd.toString().equals(text)){
                    return cmd;
                }
            }
            return null;
        }
    }

    // List of datatypes to be implemented
    public enum DATATYPE {//values() returns H[] array
        // File generating datatypes
        NONE            (PRIM.NULL),
        LIST_BOOLEAN    (PRIM.BOOLEAN),
        LIST_DISCRETE   (PRIM.DISCRETE),
        LIST_STRING     (PRIM.STRING),
        LIST_NUMBER     (PRIM.NUMBER),
        RAW_TEXT        (PRIM.STRING),
        NUM_TEXT        (PRIM.NUMBER),
        BOOL_TEXT       (PRIM.BOOLEAN),
        LIST,
        TARGLANG_BASE,
        VAR, 
        SCOPE, 
        RXFX,
        FOR,
        IF, ELSE,
        RX, FX, SRCLANG,
        // Constant types
        CONSTANT,
        // Non-file-generating datatypes
        ATTRIB, INCLUDE, FUN,
        // sub-datatypes not actually in the language
        IF_ELSE, BOOL_TEST, RX_WORD, RX_BUILDER, FX_WORD, //RX_STATEMENT,
        // datatypes whose text indicators are not the same as enum name
        TARGLANG_INSERT, COMMENT, USER_DEF_LIST, USER_DEF_VAR,
        // error indicator
        //UNKNOWN_DATATYPE,
        // test text

        // Top enum ordinal gives size of list
        NUM_DATATYPES
        ;

        private static final Pattern CHEVRONS = Pattern.compile("[<]([A-Z]+)[>]$");
        public final PRIM outType;
        public final boolean interpret;

        private DATATYPE(boolean interpret){
            this.interpret = interpret;
            outType = null;
        }

        private DATATYPE(){
            interpret = false;
            outType = null;
        }
        private DATATYPE(PRIM outType){
            interpret = false;
            this.outType = outType;
        }



        public static DATATYPE fromString(String text ){
            Matcher matcher = CHEVRONS.matcher(text);
            if(matcher.find()){
                text = matcher.replaceAll("_$1");
            }
            for(DATATYPE h : values()){
                if(h.toString().equals(text)){
                    return h;
                }
            }
            return null;
        }
//        public static DATATYPE fromBool( boolean state ){
//            return (state)? TRUE : FALSE;
//        }
//        public static boolean isKeyword( String text ){
//            return get(text) != null;
//        }  
    }
    public enum FIELD {
        // Keys for setAttrib()
        // keywords that can be specified in language
        PROJ_NAME, KEY, VAL, WROW, WVAL, NEW_LIST_SET,
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

    public enum PRIM {// PRIMITIVE
        BOOLEAN    (Pattern.compile("^(TRUE)|(FALSE)$"),    new OP[]{OP.EQUAL}),
        DISCRETE   (Pattern.compile("^[0-9]+$"),            new OP[]{OP.GT, OP.LT, OP.EQUAL}),
        NUMBER     (Pattern.compile("^[0-9]+$"),            new OP[]{OP.GT, OP.LT, OP.EQUAL}),
        STRING     (Pattern.compile("."),                   new OP[]{OP.EQUAL}),
        NULL       (Pattern.compile("null"),                new OP[]{OP.EQUAL})
        ;

        public final Pattern pattern;
        public final OP[] allowedOps;

        private PRIM(Pattern pattern, OP[] allowedOps){
            this.pattern = pattern;
            this.allowedOps = allowedOps;
        }
        public static PRIM fromString(String text ){
            for(PRIM p : values()){
                if(p.toString().equals(text)){
                    return p;
                }
            }
            return null;
        }
        public PRIM whatIsIt(String text){
            PRIM out = STRING;
            for(PRIM p : values()){
                if(p.pattern.matcher(text).find()){
                    out = p;
                    break;
                }
            }
            if(out == DISCRETE){
                return (CompileInitializer.getInstance().fitToWVal(text))? DISCRETE : NUMBER;
            }
            else{
                return out;
            }
        }
        public boolean isAllowedOp(OP op){
            for(OP allowedOp: allowedOps){
                if(allowedOp.equals(op)){
                    return true;
                }
            }
            return false;
        }
    }
    public enum PAR {// RX PARAMETER
        // Rx Functions
        EMPTY_PAR       (DATATYPE.FUN,      Pattern.compile("^[^.]+\\(()\\)$")),
        NUM_PAR         (DATATYPE.FUN,      Pattern.compile("^[^.]+\\(([0-9]+)\\)$")),
        RANGE_PAR       (DATATYPE.FUN,      Pattern.compile("^[^.]+\\(([0-9]+[-][0-9]+)\\)$")),
        RANGE_BELOW     (DATATYPE.FUN,      Pattern.compile("^[^.]+\\(([-][0-9]+)\\)$")),
        RANGE_ABOVE     (DATATYPE.FUN,      Pattern.compile("^[^.]+\\(([0-9]+[-])\\)$")),
        CONST_PAR       (DATATYPE.FUN,      Pattern.compile("^[^.]+\\(([$][A-Za-z][A-Za-z0-9_]*)\\)$")),
        AL_NUM_PAR      (DATATYPE.FUN,      Pattern.compile("^[^.]+\\(([A-Za-z0-9_\\.]+)\\)$")),
        TEST_TRUE       (DATATYPE.BOOL_TEXT,Pattern.compile("^TRUE$")),
        TEST_FALSE      (DATATYPE.BOOL_TEXT,Pattern.compile("^FALSE$")),
        TEST_NUM        (DATATYPE.NUM_TEXT, Pattern.compile("^[0-9]+$")),
        DOTTED_FUN      (DATATYPE.NONE,     Pattern.compile("^[$a-zA-Z0-9_.]+\\(.*\\)$")),
        CATEGORY_ITEM   (DATATYPE.LIST,     Pattern.compile("^[$][a-zA-Z][a-zA-Z0-9_]*\\[([a-zA-Z][a-zA-Z0-9_]*)\\]$")),
        TEST_TEXT       (DATATYPE.RAW_TEXT, Pattern.compile("."))
        ;

        public final DATATYPE datatype;
        public final Pattern pattern;
        private PAR(DATATYPE datatype, Pattern pattern){
            this.datatype = datatype;
            this.pattern = pattern;
        }

        public static PAR fromInt(int i){
            for(PAR p : values()){
                if(p.ordinal() == i){
                    return p;
                }
            }
            return null;
        }
        public static PAR fromString(String text ){
            for(PAR p : values()){
                if(p.toString().equals(text)){
                    return p;
                }
            }
            return null;
        }
    }

    public enum RX_FUN {
        // function names for Rx logic
        FIRST   (PRIM.STRING, new PAR[]{PAR.EMPTY_PAR},                                                 PRIM.STRING),
        LAST    (PRIM.STRING, new PAR[]{PAR.EMPTY_PAR},                                                 PRIM.STRING),
        LEN     (PRIM.STRING, new PAR[]{PAR.EMPTY_PAR},                                                 PRIM.NUMBER),
        RANGE   (PRIM.NUMBER, new PAR[]{PAR.NUM_PAR, PAR.RANGE_PAR, PAR.RANGE_BELOW, PAR.RANGE_ABOVE},  PRIM.BOOLEAN),
        ;

        public final PRIM outType;
        public final PAR[] parTypes;
        public final PRIM caller;

        private RX_FUN(PRIM caller, PAR[] parTypes, PRIM outType){
            this.outType =outType;
            this.parTypes = parTypes;
            this.caller = caller;
        }
        public static RX_FUN fromString(String text ){
            for(RX_FUN f : values()){
                if(f.toString().equals(text)){
                    return f;
                }
            }
            return null;
        }
        public boolean isAllowedCaller(PRIM caller){
            return this.caller.equals(caller);
        }
        public boolean isAllowedPar(PAR parType){
            for(PAR allowedParType: parTypes){
                if(allowedParType.equals(parType)){
                    return true;
                }
            }
            return false;
        }
        public String readableParTypes(){
            String[] out = new String[parTypes.length];
            for(int i = 0; i < parTypes.length; i++){
                out[i] = parTypes[i].toString();
            }
            return String.join(", ", out);
        }
    }


    //EMPTY_PARAM = 0, NUM_PARAM = 1, NUM_RANGE = 2, NUM_RANGE = 3, CONST_PARAM = 4, NO_FUN


    // String constants for switches: defines language behavior
    public static final String CONT_LINE = "...";      // Matlab-like extension
    public static final String SOURCE_OPEN = "/*$";    // pushes source datatype
    public static final String SOURCE_CLOSE = "$*/";   // pops all source datatypes
    public static final String TARGLANG_INSERT_OPEN = "*/"; // inserts target language without popping source
    public static final String TARGLANG_INSERT_CLOSE = "/*";// pops target language insert
    public static final String ITEM_OPEN = "{";        // surrounds item content
    public static final String ITEM_CLOSE = "}";       // ends item content
    public static final String USERDEF_OPEN = "$";     // user-defined name
    public static final String COMMENT_TEXT = "//";    //
    public static final String DEFAULT_FIELD_FORMAT = "$%s[%s]"; // category[item]

    public static String listTableFileName(){
        return String.format(
            "%s_%s%s",
            CompileInitializer.getInstance().getInName(),
            ListTable.class.getSimpleName(),
            INTERIM_FILE_EXTENSION
        );
    }
}
