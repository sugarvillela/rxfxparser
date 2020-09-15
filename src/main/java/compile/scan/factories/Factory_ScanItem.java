package compile.scan.factories;

import erlog.Erlog;
import compile.scan.Base_ScanItem;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.HANDLER.*;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.*;

public class Factory_ScanItem extends Factory_Strategy{
    public static Base_ScanItem get( HANDLER h ){
        System.out.println("====Factory_cxs.get()====" + h.toString());
        switch(h){
            case ENUB:
            case ENUD:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        BACK_POP_ON_ANY_HANDLER,
                        BACK_POP_ON_TARG_LANG_INSERT,
                        READ_CONSTANT,
                        BACK_POP_ON_ANY_VAR,
                        MANAGE_ENU_LISTS,
                        ADD_TEXT
                    ),
                    setPushStrategies(ON_PUSH_NO_SNIFF),
                    setPopStrategies(ON_POP_ENU)
                );
            case COMMENT:
                return new ScanItem(
                    h,
                    null,
                    setStrategies(POP_ON_ENDLINE),
                    setPushStrategies(NOP),// silent push pop
                    setPopStrategies(NOP)
                );
            case ATTRIB:
                return new ScanItem(
                    h,
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        BACK_POP_ON_TARG_LANG_INSERT,
                        PUSH_COMMENT,
                        BACK_POP_ON_ANY_VAR,
                        BACK_POP_ON_ANY_HANDLER,
                        ADD_KEY_VAL_ATTRIB
                    ),
                    setPushStrategies(NOP),// silent push pop
                    setPopStrategies(NOP)
                );
            case SRCLANG:
                return new ScanItem(
                    h, 
                    new HANDLER[]{ATTRIB, ENUB, ENUD, INCLUDE, FUN, RXFX, RX, FX, SCOPE, IF_ELSE, IF, ELSE, CONSTANT },
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_TARG_LANG_INSERT,
                        READ_VAR,
                        PUSH_GOOD_HANDLER,
                        ERR
                    )
                );
            case INCLUDE:
                return new ScanItem(
                        h,
                        null,
                        setStrategies(
                                READ_CONSTANT,
                                ON_INCLUDE
                        ),
                        setPushStrategies(NOP),// silent push pop
                        setPopStrategies(NOP)
                );
            case CONSTANT:
                return new ScanItem(
                        h,
                        null,
                        setStrategies(
                                IGNORE_CONSTANT
                        ),
                        setPushStrategies(NOP),// silent push pop
                        setPopStrategies(NOP)
                );
            case RXFX:
                return new ScanItem(
                    h, 
                    new HANDLER[]{RX, FX},
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                            BACK_POP_ON_TARG_LANG_INSERT,
                        PUSH_COMMENT,
                        READ_VAR,
                        SET_USER_DEF_NAME,
                        PUSH_GOOD_HANDLER,
                            BACK_POP_ON_ANY_HANDLER,
                            BACK_POP_ON_BAD_VAR,
                        ERR
                    ),
                    setPopStrategies(RXFX_ERR_ON_POP, ON_POP)
                );
            case RX:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                            BACK_POP_ON_TARG_LANG_INSERT,
                        BACK_POP_ON_ITEM_OPEN,
                        PUSH_COMMENT,
                        SET_USER_DEF_NAME,
                        READ_VAR,
                            BACK_POP_ON_ANY_HANDLER,
                            BACK_POP_ON_ANY_VAR,
                        ADD_RX_WORD
                    ),
                    setPushStrategies(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                    setPopStrategies(ON_POP)
                );
            case FX:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                            BACK_POP_ON_TARG_LANG_INSERT,
                        PUSH_COMMENT,
                        SET_USER_DEF_NAME,
                        READ_VAR,
                            BACK_POP_ON_ANY_HANDLER,
                            BACK_POP_ON_ANY_VAR,
                            BACK_POP_ON_ITEM_CLOSE,
                        ADD_FX_WORD
                    ),
                    setPushStrategies(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                    setPopStrategies(ON_POP)
                );
            case IF_ELSE:
                return new ScanItem(
                    h,
                    new HANDLER[]{IF, ELSE},
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_TARG_LANG_INSERT,
                        PUSH_COMMENT,
                        READ_VAR,
                        SET_USER_DEF_NAME,
                        PUSH_GOOD_HANDLER,
                            BACK_POP_ON_ANY_HANDLER,
                            BACK_POP_ON_BAD_VAR,
                        ERR
                    )
                );
            case IF:
                return new ScanItem(
                        h,
                        new HANDLER[]{RXFX, RX, FX, IF_ELSE, IF, FUN},
                        setStrategies(
                                POP_ALL_ON_END_SOURCE,
                                PUSH_COMMENT,
                                MANAGE_IF,
                                BACK_POP_ON_BAD_VAR,
                                READ_VAR,
                                PUSH_TARG_LANG_INSERT,
                                PUSH_GOOD_HANDLER,
                                ERR
                        ),
                        setPushStrategies(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                        setPopStrategies(ON_POP)
                );
            case BOOL_TEST://helper class
                return new ScanItem(
                        h,
                        new HANDLER[]{SCOPE, RX},
                        setStrategies(
                                POP_ALL_ON_END_SOURCE,
                                PUSH_COMMENT,
                                BACK_POP_ON_ITEM_OPEN,
                                PUSH_TARG_LANG_INSERT,
                                READ_VAR,
                                BACK_POP_ON_BAD_VAR,
                                PUSH_GOOD_HANDLER,
                                ERR
                        )
                );
            case ELSE:
                return new ScanItem(
                        h,
                        new HANDLER[]{RXFX, RX, FX, IF_ELSE, IF, FUN},
                        setStrategies(
                                POP_ALL_ON_END_SOURCE,
                                PUSH_COMMENT,
                                MANAGE_ELSE,
                                BACK_POP_ON_BAD_VAR,
                                READ_VAR,
                                PUSH_TARG_LANG_INSERT,
                                PUSH_GOOD_HANDLER,
                                ERR
                        ),
                        setPushStrategies(ON_PUSH, ASSERT_TOGGLE_ON_PUSH),
                        setPopStrategies(ON_POP)
                );
            case FOR:
                return new ScanItem(
                    h,
                    new HANDLER[]{SCOPE, RX},
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        READ_VAR,
                        PUSH_GOOD_HANDLER,
                            BACK_POP_ON_ANY_HANDLER,
                        ERR
                    )
                );
            // Copy and wait
            case TARGLANG_BASE:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        PUSH_SOURCE_LANG,
                        ADD_TEXT
                    ),
                    setPushStrategies(ON_PUSH_NO_SNIFF),
                    setPopStrategies(ON_LAST_POP, ON_POP_NO_SNIFF)
                );
            case TARGLANG_INSERT:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(MANAGE_TARG_LANG_INSERT),
                    setPushStrategies(ON_PUSH_NO_SNIFF),
                    setPopStrategies(ON_POP_NO_SNIFF)
                );

            case FUN:
                return new ScanItem(
                        h,
                        null,
                        setStrategies(POP_ON_ITEM_CLOSE),
                        setPushStrategies(NOP),// silent push pop
                        setPopStrategies(NOP)
                );
                
            //========To implement=====================
            case SCOPE:
                return null;//new Scope(h);
            default:
                Erlog.get("Factory_cxs").set("Developer error in get(handler)", h.toString());
                return null;
        }
    }

    public static class ScanItem extends Base_ScanItem{
        public ScanItem( HANDLER setH, HANDLER[] allowedHandlers, Strategy[] strategies){
            this(
                setH, 
                allowedHandlers, 
                strategies, 
                setPushStrategies(ON_PUSH), 
                setPopStrategies(ON_POP)
            );
        }
        
        public ScanItem( 
            HANDLER setH, 
            HANDLER[] allowedHandlers,
            Strategy[] strategies,
            Strategy[] onPopStrategies
        ){
            this(
                setH, 
                allowedHandlers, 
                strategies, 
                setPushStrategies(ON_PUSH), 
                onPopStrategies
            );
        }
        
        public ScanItem( 
            HANDLER setH, 
            HANDLER[] allowedHandlers,
            Strategy[] strategies,
            Strategy[] onPushStrategies,
            Strategy[] onPopStrategies
        ){
            this.h = setH;
            this.debugName = h.toString();
            this.allowedHandlers = allowedHandlers;
            this.strategies = strategies;
            this.onPushStrategies = onPushStrategies;
            this.onPopStrategies = onPopStrategies;
        }
    }
    
}
