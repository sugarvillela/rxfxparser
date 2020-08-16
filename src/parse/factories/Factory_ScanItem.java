package parse.factories;

import erlog.Erlog;
import parse.Base_ScanItem;
import static parse.Keywords.HANDLER;
import static parse.Keywords.HANDLER.ATTRIB;
import static parse.Keywords.HANDLER.ENUB;
import static parse.Keywords.HANDLER.ENUD;
import static parse.Keywords.HANDLER.FX;
import static parse.Keywords.HANDLER.RX;
import static parse.Keywords.HANDLER.USER_DEF_LIST;
import static parse.factories.Factory_Strategy.StrategyEnum.ADD_KEY_VAL_TEXT;
import static parse.factories.Factory_Strategy.StrategyEnum.ADD_TEXT;
import static parse.factories.Factory_Strategy.StrategyEnum.ADD_RX_WORD;
import static parse.factories.Factory_Strategy.StrategyEnum.ADD_FX_WORD;
import static parse.factories.Factory_Strategy.StrategyEnum.ADD_TO_LINE_BUFFER;
import static parse.factories.Factory_Strategy.StrategyEnum.DUMP_BUFFER_ON_POP;
import static parse.factories.Factory_Strategy.StrategyEnum.ERR;
import static parse.factories.Factory_Strategy.StrategyEnum.NOP;
import static parse.factories.Factory_Strategy.StrategyEnum.ON_POP;
import static parse.factories.Factory_Strategy.StrategyEnum.ON_PUSH;
import static parse.factories.Factory_Strategy.StrategyEnum.POP_ON_ENDLINE;
import static parse.factories.Factory_Strategy.StrategyEnum.POP_ON_KEYWORD;
import static parse.factories.Factory_Strategy.StrategyEnum.POP_ON_USERDEF;
import static parse.factories.Factory_Strategy.StrategyEnum.PUSH_COMMENT;
import static parse.factories.Factory_Strategy.StrategyEnum.PUSH_SOURCE_LANG;
import static parse.factories.Factory_Strategy.StrategyEnum.PUSH_TARG_LANG_INSERT;
import static parse.factories.Factory_Strategy.StrategyEnum.PUSH_USER_DEF_VAR;
import static parse.factories.Factory_Strategy.StrategyEnum.POP_ALL_ON_END_SOURCE;
import static parse.factories.Factory_Strategy.StrategyEnum.PUSH_GOOD_HANDLER;
import static parse.factories.Factory_Strategy.StrategyEnum.PUSH_USER_DEF_LIST;
import static parse.factories.Factory_Strategy.StrategyEnum.POP_ON_TARGLANG_INSERT_CLOSE;
import static parse.factories.Factory_Strategy.getStrategy;

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
                System.out.println("====TARGLANG_INSERT====" + h.toString());
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
                    null, 
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
                return new ScanItem(
                    h, 
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
            this.h = setH;
            this.allowedHandlers = allowedHandlers;
            this.strategies = strategies;
            this.onPushStrategies = new Strategy[]{getStrategy(ON_PUSH)};
            this.onPopStrategies = new Strategy[]{getStrategy(ON_POP)};
        }
        
        public ScanItem( 
            HANDLER setH, 
            HANDLER[] allowedHandlers,
            Strategy[] strategies,
            Strategy[] onPopStrategies
        ){
            this.h = setH;
            this.allowedHandlers = allowedHandlers;
            this.strategies = strategies;
            this.onPushStrategies = new Strategy[]{getStrategy(ON_PUSH)};
            this.onPopStrategies = onPopStrategies;
        }
        
        public ScanItem( 
            HANDLER setH, 
            HANDLER[] allowedHandlers,
            Strategy[] strategies,
            Strategy[] onPushStrategies,
            Strategy[] onPopStrategies
        ){
            this.h = setH;
            this.allowedHandlers = allowedHandlers;
            this.strategies = strategies;
            this.onPushStrategies = onPushStrategies;
            this.onPopStrategies = onPopStrategies;
        }
    }
    
//    public static class UserDef extends ScanItem{
//        private final String defName;
//        
//        public UserDef(
//            String defName,
//            HANDLER setH, 
//            HANDLER[] allowedHandlers,
//            Strategy[] strategies
//        ){
//            super(setH, allowedHandlers, strategies);
//            this.defName = defName;
//        }
//        @Override
//        public void onPush(){
//            P.addNode( Factory_Node.newScanNode( Keywords.CMD.PUSH, this.h, Keywords.KWORD.DEF_NAME, this.defName ) );
//        }
//        
//        @Override
//        public void onPop(){
//            P.addNode( Factory_Node.newScanNode( Keywords.CMD.POP, this.h, Keywords.KWORD.DEF_NAME, this.defName ) );
//        }
//    }
}
