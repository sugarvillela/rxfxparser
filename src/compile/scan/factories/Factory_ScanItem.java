package compile.scan.factories;

import erlog.Erlog;
import compile.scan.Base_ScanItem;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.HANDLER.ATTRIB;
import static compile.basics.Keywords.HANDLER.ENUB;
import static compile.basics.Keywords.HANDLER.ENUD;
import static compile.basics.Keywords.HANDLER.FX;
import static compile.basics.Keywords.HANDLER.RX;
import static compile.basics.Keywords.HANDLER.USER_DEF_LIST;
import static compile.basics.Keywords.HANDLER.USER_DEF_VAR;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.ADD_KEY_VAL_TEXT;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.ADD_TEXT;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.ADD_RX_WORD;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.ADD_FX_WORD;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.ADD_TO_LINE_BUFFER;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.DUMP_BUFFER_ON_POP;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.ERR;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.NOP;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.ON_POP;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.ON_PUSH;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.POP_ON_ENDLINE;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.POP_ON_KEYWORD;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.POP_ON_USERDEF;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.PUSH_COMMENT;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.PUSH_SOURCE_LANG;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.PUSH_TARG_LANG_INSERT;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.PUSH_USER_DEF_VAR;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.POP_ALL_ON_END_SOURCE;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.PUSH_GOOD_HANDLER;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.PUSH_USER_DEF_LIST;
import static compile.scan.factories.Factory_Strategy.StrategyEnum.POP_ON_TARGLANG_INSERT_CLOSE;
import static compile.scan.factories.Factory_Strategy.getStrategy;

public class Factory_ScanItem extends Factory_Strategy{
    public static Base_ScanItem get( HANDLER h ){
        System.out.println("====Factory_cxs.get()====" + h.toString());
        switch(h){
            // push only
            case ENUB:
            case ENUD:
                return new ScanItem(
                    h, 
                    new HANDLER[]{
                        ATTRIB, USER_DEF_LIST
                    },
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_TARG_LANG_INSERT,
                        POP_ON_KEYWORD,
                        PUSH_USER_DEF_LIST
                    )
                );
            case SRCLANG:
                return new ScanItem(
                    h, 
                    new HANDLER[]{ATTRIB, ENUB, ENUD, RX, FX },
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_TARG_LANG_INSERT,
                        PUSH_USER_DEF_VAR,
                        PUSH_GOOD_HANDLER,
                        ERR
                    )
                );
            case RXFX:
                return new ScanItem(
                    h, 
                    new HANDLER[]{RX, FX},
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_TARG_LANG_INSERT,
                        PUSH_GOOD_HANDLER,
                        POP_ON_KEYWORD,
                        ERR
                    )
                );
            case RX:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_TARG_LANG_INSERT,
                        ADD_RX_WORD
                    )
                );
            case FX:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_TARG_LANG_INSERT,
                        ADD_FX_WORD
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
                    )
                );
            case TARGLANG_INSERT:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ON_TARGLANG_INSERT_CLOSE,
                        ADD_TO_LINE_BUFFER
                    ),
                    setStrategies(DUMP_BUFFER_ON_POP, ON_POP)
                );
            case COMMENT:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(POP_ON_ENDLINE),
                    setStrategies(NOP),
                    setStrategies(NOP)
                        
                );
            case ATTRIB:
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        POP_ON_KEYWORD,
                        POP_ON_USERDEF,
                        ADD_KEY_VAL_TEXT
                    )
                );
                
            //========To implement=====================
            case SCOPE:
                return null;//new Scope(h);
            default:
                Erlog.get("Factory_cxs").set("Developer error in get(handler)", h.toString());
                return null;
        }
    }
    public static Base_ScanItem get( HANDLER h, String text ){
        System.out.println("====Factory_cxs.get()====" + h.toString());
        switch(h){
            case USER_DEF_LIST://                action.popAll(text)        || 
                return new ScanItem(
                    h, 
                    null,
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        POP_ON_USERDEF,
                        POP_ON_KEYWORD,
                        ADD_TEXT,
                        POP_ON_ENDLINE
                    ),
                    setStrategies(text, ON_PUSH),
                    setStrategies(text, ON_POP)
                );
            case VAR:
            case SCOPE:
                return new ScanItem(
                    USER_DEF_VAR, 
                    new HANDLER[]{RX, FX},
                    setStrategies(
                        POP_ALL_ON_END_SOURCE,
                        PUSH_COMMENT,
                        PUSH_GOOD_HANDLER,
                        POP_ON_KEYWORD,
                        ERR
                    ),
                    setStrategies(text, ON_PUSH),
                    setStrategies(text, ON_POP)
                );
        }
        return null;
    }

    public static class ScanItem extends Base_ScanItem{
        public ScanItem( HANDLER setH, HANDLER[] allowedHandlers, Strategy[] strategies){
            this(
                setH, 
                allowedHandlers, 
                strategies, 
                new Strategy[]{getStrategy(ON_PUSH)}, 
                new Strategy[]{getStrategy(ON_POP)}
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
                new Strategy[]{getStrategy(ON_PUSH)}, 
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
