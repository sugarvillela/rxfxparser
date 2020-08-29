package compile.scan.factories;

import compile.basics.Factory_Node;
import erlog.Erlog;
import compile.scan.Base_ScanItem;
import compile.basics.Base_Stack;
import compile.basics.Base_StackItem;
import compile.scan.Class_Scanner;
import compile.basics.Keywords;
import static compile.basics.Keywords.COMMENT_TEXT;
import compile.basics.Keywords.HANDLER;
import compile.basics.Keywords.CMD;
import static compile.basics.Keywords.HANDLER.FX_WORD;
import static compile.basics.Keywords.HANDLER.RXFX;
import static compile.basics.Keywords.HANDLER.RX_WORD;
import static compile.basics.Keywords.HANDLER.SCOPE;
import static compile.basics.Keywords.HANDLER.TARGLANG_INSERT;
import static compile.basics.Keywords.SOURCE_CLOSE;
import static compile.basics.Keywords.SOURCE_OPEN;
import static compile.basics.Keywords.TARGLANG_INSERT_CLOSE;
import static compile.basics.Keywords.TARGLANG_INSERT_OPEN;
import static compile.basics.Keywords.USERDEF_OPEN;
import static compile.basics.Keywords.HANDLER.USER_DEF_LIST;
import static compile.basics.Keywords.KWORD.ANON_NAME;
import static compile.basics.Keywords.KWORD.DEF_NAME;
import static compile.basics.Keywords.KWORD.HI;
import static compile.basics.Keywords.KWORD.LO;
import compile.scan.ut.LineBuffer;
import compile.scan.ut.RxFxUtil;
import compile.scan.ut.RxRangeUtil;
import compile.scan.ut.RxValidator;
import compile.scan.ut.ScannerSymbolTable;

/**
 *
 * @author Dave Swanson
 */
public abstract class Factory_Strategy{ 
    public enum StrategyEnum{       // === added to nodes array ====
        PUSH_SOURCE_LANG,           // Symbol != HANDLER, setWordGetter
        POP_ALL_ON_END_SOURCE,      // Symbol != HANDLER, leave target lang on stack
        PUSH_GOOD_HANDLER,          // PUSH handler if on allowed handler list
        POP_ON_KEYWORD,             // The main stack transition: pop current, add new
        ADD_TEXT,                   // ADD_TO command with text
        ADD_KEY_VAL_ATTRIB,         // SET_ATTRIB with key = val text
        ADD_RX_WORD,                // RX only
        ADD_FX_WORD,                // FX only
        PUSH_TARG_LANG_INSERT,      // Symbol != HANDLER
        ADD_TO_LINE_BUFFER,         // TARGLANG_INSERT background function
        SET_ENU_NAME,               // ENUB, ENUD set name attribute
        SET_ENU_KEYWORD,            // Define SCOPE presets
        SET_USER_DEF_NAME,          // Set name attribute
        POP_ON_TARGLANG_INSERT_CLOSE,//Symbol != HANDLER
        PUSH_COMMENT,               // Symbol != HANDLER, Silent push pop and ignore
        POP_ON_ENDLINE,             // Comment
        ERR,                        // Last on list, should not be reached
        
        ON_PUSH,                    // PUSH message
        ON_POP,                     // generate name if not set, POP message
        DUMP_BUFFER_ON_POP,         // OnPop: TARGLANG_INSERT cleanup function
        ASSERT_TOGGLE,              // OnPush: RXFX assert RX -> FX -> RX -> FX etc
        RXFX_ERR_ON_POP,            // OnPop: RXFX ends with FX
        ON_LAST_POP,                // OnPop: cleanup activities on TargLangBase pop
        NOP                         // OnPush, OnPop, not added to node
    }
    private static Strategy getStrategy(StrategyEnum strategyEnum){
        switch (strategyEnum){
            case PUSH_GOOD_HANDLER:
                return new PushGoodHandler();
            case ADD_TEXT:
                return new AddText();
            case ADD_KEY_VAL_ATTRIB:
                return new AddKeyValAttrib();
            case ADD_RX_WORD:
                return new AddRxWord();
            case ADD_FX_WORD:
                return new AddFxWord();
            case ADD_TO_LINE_BUFFER:
                return new AddToLineBuffer();
            case PUSH_SOURCE_LANG:
                return new PushSourceLang();
            case PUSH_COMMENT:
                return new PushComment();
            case PUSH_TARG_LANG_INSERT:
                return new PushTargLangInsert();
            case SET_ENU_NAME:
                return new SetEnuName();
            case SET_USER_DEF_NAME:
                return new SetUserDefName();
            case POP_ON_TARGLANG_INSERT_CLOSE:
                return new PopOnTargLangInsertClose();
            case POP_ON_ENDLINE:
                return new PopOnEndLine();
            case POP_ON_KEYWORD:
                return new PopOnKeyword();
            case POP_ALL_ON_END_SOURCE:
                return new PopAllOnEndSource();
            case ERR:
                return new Err();
            default:
                Erlog.get("Strategy").set("Bad strategy enum", strategyEnum.toString());
                return null;
        }
    }
    private static Strategy getPushStrategy(StrategyEnum strategyEnum){
        switch (strategyEnum){
            case ON_PUSH:
                return new OnPush();
            case NOP:
                return new Nop();
            case ASSERT_TOGGLE:
                return new AssertToggle();
            default:
                Erlog.get("getPushStrategy").set("Bad strategy enum", strategyEnum.toString());
                return null;
        }
    }
    private static Strategy getPopStrategy(StrategyEnum strategyEnum){
        switch (strategyEnum){
            case ON_POP:
                return new OnPop();
            case NOP:
                return new Nop();
            case ON_LAST_POP:
                return new OnLastPop();
            case DUMP_BUFFER_ON_POP:
                return new DumpBufferOnPop();
            case RXFX_ERR_ON_POP:
                return new RxFxErrOnPop();
            default:
                Erlog.get("getPopStrategy").set("Bad strategy enum", strategyEnum.toString());
                return null;
        }
    }
    
    public static Strategy[] setStrategies( StrategyEnum... enums){
        Strategy[] currentActions = new Strategy[enums.length];
        for(int i = 0; i < enums.length; i++){
            currentActions[i] = getStrategy(enums[i]);
        }
        return currentActions;
    }

    public static Strategy[] setPushStrategies( StrategyEnum... enums){
        Strategy[] currentActions = new Strategy[enums.length];
        for(int i = 0; i < enums.length; i++){
            currentActions[i] = getPushStrategy(enums[i]);
        }
        return currentActions;
    }
    public static Strategy[] setPopStrategies( StrategyEnum... enums){
        Strategy[] currentActions = new Strategy[enums.length];
        for(int i = 0; i < enums.length; i++){
            currentActions[i] = getPopStrategy(enums[i]);
        }
        return currentActions;
    }
    
    private static final LineBuffer LINEBUFFER = new LineBuffer();
    private static final ScannerSymbolTable SCANNER_SYMBOL_TABLE = new ScannerSymbolTable();
    private static final RxFxUtil RXFX_UTIL = new RxFxUtil();
    
    public static abstract class Strategy{
        Base_Stack P;
        
        public Strategy(){
            P = Class_Scanner.getInstance();
        }
        public abstract boolean go(String text, Base_ScanItem context);
    }
    public static class Err extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            Erlog.get(context).set( "Disallowed handler!!: ", text);
            return true;
        }
    }
    public static class PushGoodHandler extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            Keywords.HANDLER keyword = Keywords.HANDLER.get(text);
            if(keyword != null && context.isGoodHandler(keyword)) {
                P.push(Factory_ScanItem.get(keyword));
                return true;
            }
            return false;
        }
    }
    public static class AddText extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode(
                Factory_Node.newScanNode( CMD.ADD_TO, context.getHandler(), text)
            );
            return false;
        }
    }
    public static class AddKeyValAttrib extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            String toks[] = text.split("=");
            if( toks.length != 2 ){
                Erlog.get(this).set("key=value format is required", text);
                return false;
            }
            Keywords.KWORD key = Keywords.KWORD.get(toks[0]);
            String val = toks[1];
            if(key == null){
                Erlog.get(this).set("Unknown keyword: " + toks[0], text);
                return false;
            }
            Base_ScanItem below = (Base_ScanItem)context.getBelow();
            if(below == null){
                Erlog.get(this).set("Developer: below null");
                return false;
            }
            context.addNode(
                Factory_Node.newScanNode(CMD.SET_ATTRIB, below.getHandler(), key, val ) 
            );
            return true;
        }
    }
    public static class AddToLineBuffer extends Strategy{
        public AddToLineBuffer(){
            LINEBUFFER.clear();
        }
        
        @Override
        public boolean go(String text, Base_ScanItem context){
            LINEBUFFER.add(text);
            if(Erlog.get().getTextStatusReporter().isEndLine()){
                context.addNode(
                    Factory_Node.newScanNode( 
                        CMD.ADD_TO, context.getHandler(), LINEBUFFER.dump()
                    )
                );
            }
            return true;
        }
    }
    
    public static class PushSourceLang extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(SOURCE_OPEN.equals(text)){       // Start rxfx source code
                P.setWordGetter();              // rxfx parses word-by-word
                P.push(                         // main source handler
                    Factory_ScanItem.get( Keywords.HANDLER.SRCLANG ) 
                );
                return true;
            } 
            return false;
        }
    }
    public static class PushComment extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(text.startsWith(COMMENT_TEXT)){// okay to discard text
                if(!Erlog.get().getTextStatusReporter().isEndLine()){
                    P.push(Factory_ScanItem.get(Keywords.HANDLER.COMMENT));
                }
                return true;
            }
            return false;
        }
    }
    public static class PushTargLangInsert extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            
            if( TARGLANG_INSERT_OPEN.equals(text) ){
                P.push( Factory_ScanItem.get(TARGLANG_INSERT) );
                return true;
            }
            return false;
        }
    }
    public static class SetEnuName extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            
            if(text.startsWith(USERDEF_OPEN) && !text.equals(USERDEF_OPEN)){
                //System.out.println("is userdef");
                HANDLER h = context.getHandler();
                text = text.substring(USERDEF_OPEN.length());
                
                if(SCANNER_SYMBOL_TABLE.assertNew(text, h)){
                    System.out.println("assertNew");
                    if(context.getDefName() != null){// if not the first list defined, pop current and push another one
                        context = Factory_ScanItem.get(h);
                        P.pop();
                        P.push(context);
                    }
                    context.setDefName(text);
                    context.addNode(
                        Factory_Node.newScanNode(CMD.SET_ATTRIB, h, DEF_NAME, text )
                    );
                    return true;
                }
            }
            return false;
        }
    }
    public static class SetEnuKeyword extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            
            if(text.equals(SCOPE.toString())){
                HANDLER h = context.getHandler();
                text = text.substring(USERDEF_OPEN.length());
                
                if(SCANNER_SYMBOL_TABLE.assertNew(text, h)){
                    System.out.println("assertNew");
                    if(context.getDefName() != null){// if not the first list defined, pop current and push another one
                        context = Factory_ScanItem.get(h);
                        P.pop();
                        P.push(context);
                    }
                    context.setDefName(text);
                    context.addNode(
                        Factory_Node.newScanNode(CMD.SET_ATTRIB, h, DEF_NAME, text )
                    );
                    return true;
                }
            }
            return false;
        }
    }
    public static class SetUserDefName extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            HANDLER h = context.getHandler();
            if(SCANNER_SYMBOL_TABLE.isUserDef(text)){
                text = text.substring(USERDEF_OPEN.length());
                if(SCANNER_SYMBOL_TABLE.addIfNew(text, h)){
                    context.addNode(
                        Factory_Node.newScanNode(CMD.SET_ATTRIB, h, DEF_NAME, text)
                    );
                    return true;
                }
            }
            return false;
        }
    }
    public static class popPushOnVar extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            System.out.println("accessUserDefName: " + text + " ... " + context.getDebugName());
//            HANDLER handler = context.getHandler();
//            if(SCANNER_SYMBOL_TABLE.isOldUserDef(text, handler)){
//                System.out.println("found userDef!!!: " + text);
//                context.addNode(
//                    Factory_Node.newScanNode(
//                        CMD.SET_ATTRIB, 
//                        context.getHandler(), 
//                        DEF_NAME, 
//                        text.substring(USERDEF_OPEN.length()) 
//                    )
//                );
//                return true;
//            }
            return false;
        }
    }
    public static class PopOnTargLangInsertClose extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if( TARGLANG_INSERT_CLOSE.equals(text) ){
                P.pop();
                return true;
            }
            return false;
        }
    }
    public static class PopOnEndLine extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(Erlog.get().getTextStatusReporter().isEndLine()){
                P.pop();
                return true;
            }
            return false;
        }
    }
    public static class PopOnUserDef extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(text.startsWith(USERDEF_OPEN) && !text.equals(USERDEF_OPEN)){
                P.back(text);
                P.pop();
                return true;
            }
            return false;
        }
    }
    public static class PopOnKeyword extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            Keywords.HANDLER keyword = Keywords.HANDLER.get(text);
            if( Keywords.HANDLER.get(text) != null){
                P.back(text);//repeat keyword so next handler can push it
                P.pop();
                return true;
            }
            return false;
        }
    }
    public static class PopAllOnEndSource extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(SOURCE_CLOSE.equals(text)){
                P.setLineGetter();
                P.popAllSource();
                return true;
            }
            return false;
        }
    }
    
    public static class AddRxWord extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            RxRangeUtil rxRangeUtil = RxRangeUtil.getInstance();
            HANDLER h = RX_WORD;

            if(rxRangeUtil.findAndSetRange(text)){
                text = rxRangeUtil.getTruncated();
            }
            if(RxValidator.getInstance().assertValidRxWord(text)){
                context.addNode( Factory_Node.newScanNode( CMD.PUSH, h ));
                
                context.addNode(
                    Factory_Node.newScanNode( CMD.ADD_TO, h, text)
                );
                context.addNode( Factory_Node.newScanNode( 
                    CMD.SET_ATTRIB, h, LO, rxRangeUtil.getLowRange())
                );
                context.addNode( Factory_Node.newScanNode( 
                    CMD.SET_ATTRIB, h, HI, rxRangeUtil.getHighRange())
                );                
                context.addNode( Factory_Node.newScanNode( CMD.POP, h ));
                return true;
            }
            return false;
        }
    }
    public static class AddFxWord extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            HANDLER h = FX_WORD;
            context.addNode( Factory_Node.newScanNode( CMD.PUSH, h ));
            context.addNode(
                Factory_Node.newScanNode( CMD.ADD_TO, h, text)
            );
            context.addNode( Factory_Node.newScanNode( CMD.POP, h ));
            return true;
        }
        
    }
    public static class OnPush extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode( Factory_Node.newScanNode( CMD.PUSH, context.getHandler() ) );
            return false;
        }
    }
    public static class OnPop extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(context.getDefName() == null){
                HANDLER h = context.getHandler();
                String anon = SCANNER_SYMBOL_TABLE.genAnonName(h);//
                context.addNode(
                    Factory_Node.newScanNode(CMD.SET_ATTRIB, h, ANON_NAME, anon)
                );
            }
            context.addNode( Factory_Node.newScanNode( CMD.POP, context.getHandler() ) );
            return false;
        }
    }

    public static class Nop extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            return false;
        }
    }
    public static class DumpBufferOnPop extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(!LINEBUFFER.isEmpty()){
                context.addNode(
                    Factory_Node.newScanNode( 
                        CMD.ADD_TO, context.getHandler(), LINEBUFFER.dump()
                    )
                );
            }
            return false;
        }
    }
    public static class AssertToggle extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            Base_ScanItem below = (Base_ScanItem)context.getBelow();
            if(below != null && RXFX.equals(below.getHandler())){
                RXFX_UTIL.assertToggle(context.getHandler());
            }
            return false;
        }
    }
    public static class RxFxErrOnPop extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            return RXFX_UTIL.errOnPop();
        }
    }
    public static class OnLastPop extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.prependNodes(SCANNER_SYMBOL_TABLE.getSymbolTable());
            return false;
        }
    }
    
//    public static class OnPushUserDef extends Strategy{
//        private final String name;
//        public OnPushUserDef(String name){
//            this.name = name;
//        }
//        
//        @Override
//        public boolean go(String text, Base_ScanItem context){
//            addToSymbolTable(context);
//            context.addNode(
//                Factory_Node.newScanNode( 
//                    CMD.PUSH, context.getHandler(), Keywords.KWORD.DEF_NAME, this.name
//                )
//            );
//            return false;
//        }
//        private void addToSymbolTable(Base_ScanItem context){
//            Base_ScanItem below = (Base_ScanItem)context.getBelow();
//            if(below != null){
//                SCANNER_SYMBOL_TABLE.assertNew(this.name, below.getHandler());
//            }
//        }
//    }
//    public static class OnPopUserDef extends Strategy{
//        private final String name;
//        public OnPopUserDef(String name){
//            this.name = name;
//        }
//        
//        @Override
//        public boolean go(String text, Base_ScanItem context){
//            context.addNode(
//                Factory_Node.newScanNode( 
//                    CMD.POP, context.getHandler(), Keywords.KWORD.DEF_NAME, this.name
//                )
//            );
//            return false;
//        }
//    }
}
