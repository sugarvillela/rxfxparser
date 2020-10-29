package compile.scan.factories;

import compile.symboltable.TextSniffer;
import erlog.DevErr;
import erlog.Erlog;
import compile.scan.Base_ScanItem;
import static compile.basics.Keywords.DATATYPE;
import static compile.basics.Keywords.DATATYPE.*;
import static compile.scan.factories.Factory_Strategy.PushEnum.*;
import static compile.scan.factories.Factory_Strategy.PopEnum.*;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.*;

public class Factory_ScanItem extends Factory_Strategy{
    private static IScanMode instance, preScanMode, scanMode;

    public static void init(){
        instance = preScanMode = new PreScanMode();
        scanMode = new ScanMode();
    }

    public static IScanMode getInstance(){
        return instance;
    }

    private Factory_ScanItem(){}

    public static void enterPreScanMode(){
        instance = preScanMode;
    }

    public static void enterScanMode(){
        System.out.println("====================== enterScanMode");
        instance = scanMode;
    }

    public static boolean isPreScanMode(){
        return instance == preScanMode;
    }


    public interface IScanMode {
        Base_ScanItem get( DATATYPE h );
    }
    public static class PreScanMode implements IScanMode {
        @Override
        public Base_ScanItem get(DATATYPE h) {
            System.out.println("====PreScanMode.get()====" + h.toString());
            switch(h){
                case COMMENT:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(POP_ON_ENDLINE),
                            getPush(ON_PUSH_NOP),// silent push pop
                            getPop(ON_POP_NOP),
                            false
                    );
                case ATTRIB:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    BACK_POP_ON_TARG_LANG_INSERT,
                                    PUSH_COMMENT,
                                    BACK_POP_ON_ANY_VAR,
                                    BACK_POP_ON_ANY_DATATYPE,
                                    ADD_PRESCAN_ATTRIB
                            ),
                            getPush(ON_PUSH_NOP),// silent push pop
                            getPop(ON_POP_NOP),
                            false
                    );
                case TARGLANG_BASE:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(
                                    PUSH_SOURCE_LANG,
                                    ADD_TEXT
                            ),
                            getPush(ON_PUSH),
                            getPop(ON_LAST_POP, ON_POP),
                            false
                    );
                case SRCLANG:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{
                                    COMMENT, INCLUDE, FUN, CONSTANT, ATTRIB,
                                    LIST_BOOLEAN, LIST_DISCRETE, LIST_STRING, LIST_NUMBER, LIST_SCOPES
                            },
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    PUSH_TARG_LANG_INSERT,
                                    READ_VAR,
                                    PUSH_GOOD_DATATYPE
                            ),
                            false
                    );
                case INCLUDE:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(
                                    PUSH_COMMENT,
                                    READ_INCLUDE
                            ),
                            getPush(ON_PUSH_NOP),// silent push pop
                            getPop(ON_POP_NOP),
                            false
                    );
                case CONSTANT:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(
                                    PUSH_COMMENT,
                                    MANAGE_CONSTANT
                            ),
                            getPush(ON_PUSH_NOP),// silent push pop
                            getPop(ON_POP_NOP),
                            false
                    );
                case FUN:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(POP_ON_ITEM_CLOSE, MANAGE_FUN),
                            getPush(ON_PUSH_NOP),// silent push pop
                            getPop(ON_POP_NOP),
                            false
                    );
                case LIST_BOOLEAN:
                case LIST_DISCRETE:
                case LIST_NUMBER:
                case LIST_STRING:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT, ATTRIB},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    READ_CONSTANT,
                                    BACK_POP_ON_TARG_LANG_INSERT,
                                    PUSH_GOOD_DATATYPE,
                                    BACK_POP_ON_ANY_DATATYPE,
                                    BACK_POP_ON_ANY_VAR,
                                    MANAGE_LISTS
                            ),
                            getPush(ON_PUSH_LIST),
                            getPop(ON_POP_LIST),
                            false
                    );
                case LIST_SCOPES:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT, ATTRIB},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    READ_CONSTANT,
                                    BACK_POP_ON_TARG_LANG_INSERT,
                                    PUSH_GOOD_DATATYPE,
                                    BACK_POP_ON_ANY_DATATYPE,
                                    BACK_POP_ON_ANY_VAR,
                                    MANAGE_SCOPES_LIST
                            ),
                            getPush(ON_PUSH_LIST),
                            getPop(ON_POP_LIST),
                            false
                    );

                //========To implement=====================

                default:
                    DevErr.get("Factory_cxs").kill("Developer get(datatype)", h.toString());
                    return null;
            }
        }
    }
    public static class ScanMode implements IScanMode {
        @Override
        public Base_ScanItem get(DATATYPE h) {
            System.out.println("====ScanMode.get()====" + h.toString());
            switch(h){
                case COMMENT:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(POP_ON_ENDLINE),
                            getPush(ON_PUSH_NOP),// silent push pop
                            getPop(ON_POP_NOP),
                            false
                    );
                case FOR: // FOR and SCOPE will be treated the same
                    h = SCOPE;
                case SCOPE:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{
                                    COMMENT, ATTRIB, RXFX, RX, FX, SCOPE
                            },
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    READ_VAR,
                                    MANAGE_SCOPE,
                                    PUSH_GOOD_DATATYPE,
                                    ERR
                            ),
                            true
                    );
                case SCOPE_TEST://helper class
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT, RX},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    READ_VAR,
                                    MANAGE_SCOPE_TEST,
                                    ERR
                            ),
                            false
                    );
                case RXFX:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT, RX, FX},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    BACK_POP_ON_TARG_LANG_INSERT,
                                    PUSH_COMMENT,
                                    READ_VAR,
                                    ADD_USER_DEF_NAME,
                                    PUSH_GOOD_DATATYPE,
                                    BACK_POP_ON_ANY_DATATYPE,
                                    BACK_POP_ON_BAD_VAR,
                                    BACK_POP_ON_ITEM_CLOSE,
                                    ERR
                            ),
                            getPop(RXFX_ERR_ON_POP, ON_POP),
                            true
                    );
                case RX:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    BACK_POP_ON_TARG_LANG_INSERT,
                                    BACK_POP_ON_ITEM_OPEN,
                                    PUSH_COMMENT,
                                    ADD_USER_DEF_NAME,
                                    BACK_POP_ON_ANY_DATATYPE,
                                    BACK_POP_ON_ANY_VAR,
                                    ADD_RX_WORD
                            ),
                            getPush(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                            getPop(ON_POP),
                            true
                    );
                case FX:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    BACK_POP_ON_TARG_LANG_INSERT,
                                    PUSH_COMMENT,
                                    ADD_USER_DEF_NAME,
                                    BACK_POP_ON_ANY_DATATYPE,
                                    BACK_POP_ON_ANY_VAR,
                                    BACK_POP_ON_ITEM_CLOSE,
                                    ADD_FX_WORD
                            ),
                            getPush(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                            getPop(ON_POP),
                            true
                    );
                case IF_ELSE:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT, IF, ELSE},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_TARG_LANG_INSERT,
                                    PUSH_COMMENT,
                                    READ_VAR,
                                    ADD_USER_DEF_NAME,
                                    PUSH_GOOD_DATATYPE,
                                    BACK_POP_ON_ANY_DATATYPE,
                                    BACK_POP_ON_BAD_VAR,
                                    ERR
                            ),
                            true
                    );
                case IF:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT, RXFX, RX, FX, IF_ELSE, IF, FUN},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    MANAGE_IF,
                                    BACK_POP_ON_BAD_VAR,
                                    READ_VAR,
                                    PUSH_TARG_LANG_INSERT,
                                    PUSH_GOOD_DATATYPE,
                                    ERR
                            ),
                            getPush(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                            getPop(ON_POP),
                            true
                    );
                case IF_TEST://helper class
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT, SCOPE, RX},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    READ_VAR,
                                    MANAGE_IF_TEST,
                                    ERR
                            ),
                            false
                    );
                case ELSE:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT, RXFX, RX, FX, IF_ELSE, IF, FUN},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    MANAGE_ELSE,
                                    BACK_POP_ON_BAD_VAR,
                                    READ_VAR,
                                    PUSH_TARG_LANG_INSERT,
                                    PUSH_GOOD_DATATYPE,
                                    ERR
                            ),
                            getPush(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                            getPop(ON_POP),
                            true
                    );
                case ATTRIB:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{COMMENT},
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    BACK_POP_ON_TARG_LANG_INSERT,
                                    PUSH_COMMENT,
                                    BACK_POP_ON_ANY_VAR,
                                    BACK_POP_ON_ANY_DATATYPE,
                                    ADD_SCAN_ATTRIB
                            ),
                            getPush(ON_PUSH_NOP),// silent push pop
                            getPop(ON_POP_NOP),
                            false
                    );
                case TARGLANG_BASE:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(
                                    PUSH_SOURCE_LANG,
                                    ADD_TEXT
                            ),
                            getPush(ON_PUSH),
                            getPop(ON_LAST_POP, ON_POP),
                            false
                    );
                case TARGLANG_INSERT:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(MANAGE_TARG_LANG_INSERT),
                            getPush(ON_PUSH),
                            getPop(ON_POP),
                            false
                    );
                case SRCLANG:
                    return new ScanItem(
                            h,
                            new DATATYPE[]{
                                    COMMENT, INCLUDE, RXFX, RX, FX,
                                    ATTRIB, SCOPE, IF_ELSE, IF, ELSE
                            },
                            getStrategy(
                                    POP_ALL_ON_END_SOURCE,
                                    PUSH_COMMENT,
                                    PUSH_TARG_LANG_INSERT,
                                    READ_VAR,
                                    PUSH_GOOD_DATATYPE
                            ),
                            false
                    );
                case INCLUDE:
                    return new ScanItem(
                            h,
                            null,
                            getStrategy(
                                    PUSH_COMMENT,
                                    READ_INCLUDE
                            ),
                            getPush(ON_PUSH_NOP),// silent push pop
                            getPop(ON_POP_NOP),
                            false
                    );

                default:
                    DevErr.get("Factory_cxs").kill("Developer get(datatype)", h.toString());
                    return null;
            }
        }
    }

    public static class ScanItem extends Base_ScanItem{
        public ScanItem(
            DATATYPE datatype,
            DATATYPE[] allowedDatatypes,
            StrategyEnum[] strategies,
            boolean cacheable
        ){
            this(
                datatype,
                allowedDatatypes,
                strategies,
                getPush(ON_PUSH),
                getPop(ON_POP),
                cacheable
            );
        }

        public ScanItem(
            DATATYPE datatype,
            DATATYPE[] allowedDatatypes,
            StrategyEnum[] strategies,
            PopEnum[] onPopStrategies,
            boolean cacheable
        ){
            this(
                datatype,
                allowedDatatypes,
                strategies,
                getPush(ON_PUSH),
                onPopStrategies,
                cacheable
            );
        }

        public ScanItem( 
            DATATYPE datatype,
            DATATYPE[] allowedDatatypes,
            StrategyEnum[] strategies,
            PushEnum[] onPushStrategies,
            PopEnum[] onPopStrategies,
            boolean cacheable
        ){
            super(cacheable);
            this.datatype = datatype;
            this.debugName = datatype.toString();
            this.allowedDatatypes = allowedDatatypes;
            this.strategies = strategies;
            this.onPushStrategies = onPushStrategies;
            this.onPopStrategies = onPopStrategies;
            System.out.println("Constructor: " +this.getDebugName());
        }
    }
    
}
