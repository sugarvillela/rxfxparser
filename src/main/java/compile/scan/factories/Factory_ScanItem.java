package compile.scan.factories;

import erlog.Erlog;
import compile.scan.Base_ScanItem;
import static compile.basics.Keywords.DATATYPE;
import static compile.basics.Keywords.DATATYPE.*;
import static compile.scan.factories.Factory_Strategy.PushEnum.*;
import static compile.scan.factories.Factory_Strategy.PopEnum.*;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.*;

public class Factory_ScanItem extends Factory_Strategy{
    public static Base_ScanItem get( DATATYPE h ){
        System.out.println("====Factory_cxs.get()====" + h.toString());
        switch(h){
            case LIST_BOOLEAN:
            case LIST_DISCRETE:
            case LIST_NUMBER:
            case LIST_STRING:
                return new ScanItem(
                    h, 
                    null,
                    getStrategy(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        BACK_POP_ON_ANY_DATATYPE,
                        BACK_POP_ON_TARG_LANG_INSERT,
                        READ_CONSTANT,
                        BACK_POP_ON_ANY_VAR,
                        MANAGE_LISTS
                    ),
                    getPush(ON_PUSH_NO_SNIFF),
                    getPop(ON_POP_LIST)
                );
            case COMMENT:
                return new ScanItem(
                    h,
                    null,
                    getStrategy(POP_ON_ENDLINE),
                    getPush(ON_PUSH_NOP),// silent push pop
                    getPop(ON_POP_NOP)
                );
            case ATTRIB:
                return new ScanItem(
                    h,
                    null,
                    getStrategy(
                        POP_ALL_ON_END_SOURCE,
                        BACK_POP_ON_TARG_LANG_INSERT,
                        PUSH_COMMENT,
                        BACK_POP_ON_ANY_VAR,
                        BACK_POP_ON_ANY_DATATYPE,
                        ADD_KEY_VAL_ATTRIB
                    ),
                    getPush(ON_PUSH_NOP),// silent push pop
                    getPop(ON_POP_NOP)
                );
            case SRCLANG:
                return new ScanItem(
                    h, 
                    new DATATYPE[]{
                        ATTRIB, LIST_BOOLEAN, LIST_DISCRETE, LIST_STRING, LIST_NUMBER, INCLUDE, FUN, RXFX, RX, FX,
                            SCOPE, IF_ELSE, IF, ELSE, CONSTANT
                    },
                    getStrategy(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_TARG_LANG_INSERT,
                        READ_VAR,
                        PUSH_GOOD_DATATYPE,
                        ERR
                    )
                );
            case INCLUDE:
                return new ScanItem(
                        h,
                        null,
                        getStrategy(
                                READ_CONSTANT,
                                READ_INCLUDE
                        ),
                        getPush(ON_PUSH_NOP),// silent push pop
                        getPop(ON_POP_NOP)
                );
            case CONSTANT:
                return new ScanItem(
                        h,
                        null,
                        getStrategy(
                                IGNORE_CONSTANT
                        ),
                        getPush(ON_PUSH_NOP),// silent push pop
                        getPop(ON_POP_NOP)
                );
            case RXFX:
                return new ScanItem(
                    h, 
                    new DATATYPE[]{RX, FX},
                    getStrategy(
                        POP_ALL_ON_END_SOURCE,
                            BACK_POP_ON_TARG_LANG_INSERT,
                        PUSH_COMMENT,
                        READ_VAR,
                            ADD_USER_DEF_NAME,
                        PUSH_GOOD_DATATYPE,
                            BACK_POP_ON_ANY_DATATYPE,
                            BACK_POP_ON_BAD_VAR,
                        ERR
                    ),
                    getPop(RXFX_ERR_ON_POP, ON_POP)
                );
            case RX:
                return new ScanItem(
                    h, 
                    null,
                    getStrategy(
                        POP_ALL_ON_END_SOURCE,
                            BACK_POP_ON_TARG_LANG_INSERT,
                        BACK_POP_ON_ITEM_OPEN,
                        PUSH_COMMENT,
                            ADD_USER_DEF_NAME,
                        READ_VAR,
                            BACK_POP_ON_ANY_DATATYPE,
                            BACK_POP_ON_ANY_VAR,
                        ADD_RX_WORD
                    ),
                    getPush(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                    getPop(ON_POP)
                );
            case FX:
                return new ScanItem(
                    h, 
                    null,
                    getStrategy(
                        POP_ALL_ON_END_SOURCE,
                            BACK_POP_ON_TARG_LANG_INSERT,
                        PUSH_COMMENT,
                            ADD_USER_DEF_NAME,
                        READ_VAR,
                            BACK_POP_ON_ANY_DATATYPE,
                            BACK_POP_ON_ANY_VAR,
                            BACK_POP_ON_ITEM_CLOSE,
                        ADD_FX_WORD
                    ),
                    getPush(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                    getPop(ON_POP)
                );
            case IF_ELSE:
                return new ScanItem(
                    h,
                    new DATATYPE[]{IF, ELSE},
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
                    )
                );
            case IF:
                return new ScanItem(
                        h,
                        new DATATYPE[]{RXFX, RX, FX, IF_ELSE, IF, FUN},
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
                        getPop(ON_POP)
                );
            case BOOL_TEST://helper class
                return new ScanItem(
                        h,
                        new DATATYPE[]{SCOPE, RX},
                        getStrategy(
                                POP_ALL_ON_END_SOURCE,
                                PUSH_COMMENT,
                                BACK_POP_ON_ITEM_OPEN,
                                PUSH_TARG_LANG_INSERT,
                                READ_VAR,
                                BACK_POP_ON_BAD_VAR,
                                PUSH_GOOD_DATATYPE,
                                ERR
                        )
                );
            case ELSE:
                return new ScanItem(
                        h,
                        new DATATYPE[]{RXFX, RX, FX, IF_ELSE, IF, FUN},
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
                        getPop(ON_POP)
                );
            case FOR:
                return new ScanItem(
                    h,
                    new DATATYPE[]{SCOPE, RX},
                    getStrategy(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        READ_VAR,
                        PUSH_GOOD_DATATYPE,
                        BACK_POP_ON_ANY_DATATYPE,
                        ERR
                    )
                );
            // Copy and wait
            case TARGLANG_BASE:
                return new ScanItem(
                    h, 
                    null,
                    getStrategy(
                        PUSH_SOURCE_LANG,
                        ADD_TEXT
                    ),
                    getPush(ON_PUSH_NO_SNIFF),
                    getPop(ON_LAST_POP, ON_POP_NO_SNIFF)
                );
            case TARGLANG_INSERT:
                return new ScanItem(
                    h, 
                    null,
                    getStrategy(MANAGE_TARG_LANG_INSERT),
                    getPush(ON_PUSH_NO_SNIFF),
                    getPop(ON_POP_NO_SNIFF)
                );

            case FUN:
                return new ScanItem(
                        h,
                        null,
                        getStrategy(POP_ON_ITEM_CLOSE),
                        getPush(ON_PUSH_NOP),// silent push pop
                        getPop(ON_POP_NOP)
                );
                
            //========To implement=====================
            case SCOPE:
                return null;//new Scope(h);
            default:
                Erlog.get("Factory_cxs").set("Developer error in get(datatype)", h.toString());
                return null;
        }
    }

    public static class ScanItem extends Base_ScanItem{
        public ScanItem(DATATYPE setH, DATATYPE[] allowedDatatypes, StrategyEnum[] strategies){
            this(
                setH,
                    allowedDatatypes,
                strategies, 
                getPush(ON_PUSH),
                getPop(ON_POP)
            );
        }
        
        public ScanItem( 
            DATATYPE setH,
            DATATYPE[] allowedDatatypes,
            StrategyEnum[] strategies,
            PopEnum[] onPopStrategies
        ){
            this(
                setH,
                    allowedDatatypes,
                strategies, 
                getPush(ON_PUSH),
                onPopStrategies
            );
        }
        
        public ScanItem( 
            DATATYPE setH,
            DATATYPE[] allowedDatatypes,
            StrategyEnum[] strategies,
            PushEnum[] onPushStrategies,
            PopEnum[] onPopStrategies
        ){
            this.h = setH;
            this.debugName = h.toString();
            this.allowedDatatypes = allowedDatatypes;
            this.strategies = strategies;
            this.onPushStrategies = onPushStrategies;
            this.onPopStrategies = onPopStrategies;
        }
    }
    
}
