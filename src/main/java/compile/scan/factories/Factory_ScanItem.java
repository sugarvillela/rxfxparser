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
                        POP_ON_KEYWORD,
                        POP_ON_TARG_LANG_INSERT,
                        MANAGE_ENU_LISTS,
                        ADD_TEXT
                    ),
                    setPopStrategies(ON_POP_ENU)
                );
            case SRCLANG:
                return new ScanItem(
                    h, 
                    new HANDLER[]{ATTRIB, ENUB, ENUD, INCLUDE, RXFX, RX, FX, SCOPE },
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_TARG_LANG_INSERT,
                        PUSH_GOOD_HANDLER,
                        ERR
                    )
                );
            case INCLUDE:
                return new ScanItem(
                        h,
                        null,
                        setStrategies(
                                ON_INCLUDE
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
                        PUSH_COMMENT,
                        SET_USER_DEF_NAME,
                        POP_ON_TARG_LANG_INSERT,
                        PUSH_GOOD_HANDLER,
                        POP_ON_KEYWORD,
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
                        PUSH_COMMENT,
                        SET_USER_DEF_NAME,
                            POP_ON_TARG_LANG_INSERT,
                        POP_ON_KEYWORD,
                        ADD_RX_WORD
                    ),
                    setPushStrategies(ON_PUSH, ASSERT_TOGGLE),
                    setPopStrategies(ON_POP)
                );
            case FX:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        SET_USER_DEF_NAME,
                            POP_ON_TARG_LANG_INSERT,
                        POP_ON_KEYWORD,
                        ADD_FX_WORD
                    ),
                    setPushStrategies(ON_PUSH, ASSERT_TOGGLE),
                    setPopStrategies(ON_POP)
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
                    setPopStrategies(ON_LAST_POP, ON_POP)
                );
            case TARGLANG_INSERT:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ON_TARGLANG_INSERT_CLOSE,
                        ADD_TO_LINE_BUFFER
                    ),
                    setPopStrategies(DUMP_BUFFER_ON_POP, ON_POP)
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
                    setStrategies(POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        POP_ON_KEYWORD,
                            POP_ON_TARG_LANG_INSERT,
                        ADD_KEY_VAL_ATTRIB
                    ),
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
