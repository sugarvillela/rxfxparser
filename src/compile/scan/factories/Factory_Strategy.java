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
import static compile.basics.Keywords.HANDLER.RX_WORD;
import static compile.basics.Keywords.HANDLER.TARGLANG_INSERT;
import static compile.basics.Keywords.SOURCE_CLOSE;
import static compile.basics.Keywords.SOURCE_OPEN;
import static compile.basics.Keywords.TARGLANG_INSERT_CLOSE;
import static compile.basics.Keywords.TARGLANG_INSERT_OPEN;
import static compile.basics.Keywords.USERDEF_OPEN;
import static compile.basics.Keywords.HANDLER.USER_DEF_LIST;
import static compile.basics.Keywords.KWORD.DEF_NAME;
import static compile.basics.Keywords.KWORD.HI;
import static compile.basics.Keywords.KWORD.LO;
import compile.scan.ut.LineBuffer;
import compile.scan.ut.RxFxUtil;
import compile.scan.ut.RxWordUtil;
import compile.scan.ut.ScannerSymbolTable;

/**
 *
 * @author Dave Swanson
 */
public abstract class Factory_Strategy{ 
    public enum StrategyEnum{
        ON_PUSH,
        ON_POP,
        PUSH_GOOD_HANDLER,
        ADD_TEXT,
        ADD_KEY_VAL_TEXT,
        ADD_RX_WORD,
        ADD_FX_WORD,
        ADD_TO_LINE_BUFFER,
        PUSH_SOURCE_LANG,
        PUSH_COMMENT,
        PUSH_TARG_LANG_INSERT,
        PUSH_USER_DEF_LIST,
        PUSH_USER_DEF_VAR,
        SET_USER_DEF_NAME,
        POP_ON_TARGLANG_INSERT_CLOSE,
        POP_ON_ENDLINE,
        POP_ON_USERDEF,
        POP_ON_KEYWORD,
        POP_ALL_ON_END_SOURCE,
        DUMP_BUFFER_ON_POP,
        RX_ON_PUSH,
        RXFX_ERR_ON_POP,
        ON_LAST_POP,
        NOP,
        ERR
    }
    public static Strategy getStrategy(StrategyEnum strategyEnum){
        switch (strategyEnum){
            case ON_PUSH:
                return new OnPush();
            case ON_POP:
                return new OnPop();
            case PUSH_GOOD_HANDLER:
                return new PushGoodHandler();
            case ADD_TEXT:
                return new AddText();
            case ADD_KEY_VAL_TEXT:
                return new AddKeyValText();
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
            case PUSH_USER_DEF_LIST:
                return new PushUserDefList();
            case SET_USER_DEF_NAME:
                return new setUserDefName();
            case POP_ON_TARGLANG_INSERT_CLOSE:
                return new PopOnTargLangClose();
            case POP_ON_ENDLINE:
                return new PopOnEndLine();
            case POP_ON_USERDEF:
                return new PopOnUserDef();
            case POP_ON_KEYWORD:
                return new PopOnKeyword();
            case POP_ALL_ON_END_SOURCE:
                return new PopAllOnEndSource();
            case ERR:
                return new Err();
            case DUMP_BUFFER_ON_POP:
                return new DumpBufferOnPop();
            case RX_ON_PUSH:
                return new RxOnPush();
            case RXFX_ERR_ON_POP:
                return new RxFxErrOnPop();
            case ON_LAST_POP:
                return new OnLastPop();
            case NOP:
                return new Nop();
            default:
                Erlog.get("Strategy").set("Bad strategy enum", strategyEnum.toString());
                return null;
        }
    }
    public static Strategy getStrategy(String name, StrategyEnum strategyEnum){
        switch (strategyEnum){
            case ON_PUSH:
                return new OnPushUserDef(name);
            case ON_POP:
                return new OnPopUserDef(name);
            default:
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
    public static Strategy[] setStrategies( String name, StrategyEnum... enums){
        Strategy[] currentActions = new Strategy[enums.length];
        for(int i = 0; i < enums.length; i++){
            currentActions[i] = getStrategy(name, enums[i]);
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
            if(keyword != null && !context.isBadHandler(keyword)) {
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
    public static class AddKeyValText extends Strategy{
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
            context.addNode(
                Factory_Node.newScanNode(CMD.SET_ATTRIB, context.getHandler(), key, val ) 
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
    public static class PushUserDefList extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(text.startsWith(USERDEF_OPEN) && !text.equals(USERDEF_OPEN)){
                P.push(Factory_ScanItem.get(USER_DEF_LIST, text.substring(USERDEF_OPEN.length())));
                return true;
            }
            return false;
        }
    }
    public static class setUserDefName extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            System.out.println("setUserDefName: " + text + " ... " + context.getDebugName());
            HANDLER handler = context.getHandler();
            if(SCANNER_SYMBOL_TABLE.isNewUserDef(text, handler)){
                System.out.println("found userDef!!!: " + text);
                context.addNode(
                    Factory_Node.newScanNode(
                        CMD.SET_ATTRIB, 
                        context.getHandler(), 
                        DEF_NAME, 
                        text.substring(USERDEF_OPEN.length()) 
                    )
                );
                return true;
            }
            return false;
        }
    }
    public static class popPushOnVar extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            System.out.println("accessUserDefName: " + text + " ... " + context.getDebugName());
            HANDLER handler = context.getHandler();
            if(SCANNER_SYMBOL_TABLE.isOldUserDef(text, handler)){
                System.out.println("found userDef!!!: " + text);
                context.addNode(
                    Factory_Node.newScanNode(
                        CMD.SET_ATTRIB, 
                        context.getHandler(), 
                        DEF_NAME, 
                        text.substring(USERDEF_OPEN.length()) 
                    )
                );
                return true;
            }
            return false;
        }
    }
    public static class PopOnTargLangClose extends Strategy{
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
            RxWordUtil rxWordUtil = RxWordUtil.getInstance();
            HANDLER h = RX_WORD;

            if(rxWordUtil.findAndSetRange(text)){
                text = rxWordUtil.getTruncated();
            }
            if(rxWordUtil.assertValidRxWord(text)){
                context.addNode( Factory_Node.newScanNode( CMD.PUSH, h ));
                
                context.addNode(
                    Factory_Node.newScanNode( CMD.ADD_TO, h, text)
                );
                context.addNode( Factory_Node.newScanNode( 
                    CMD.SET_ATTRIB, h, LO, rxWordUtil.getLowRange())
                );
                context.addNode( Factory_Node.newScanNode( 
                    CMD.SET_ATTRIB, h, HI, rxWordUtil.getHighRange())
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
            context.addNode( Factory_Node.newScanNode( CMD.POP, context.getHandler() ) );
            return false;
        }
    }
    public static class OnPushUserDef extends Strategy{
        private final String name;
        public OnPushUserDef(String name){
            this.name = name;
        }
        
        @Override
        public boolean go(String text, Base_ScanItem context){
            addToSymbolTable(context);
            context.addNode(
                Factory_Node.newScanNode( 
                    CMD.PUSH, context.getHandler(), Keywords.KWORD.DEF_NAME, this.name
                )
            );
            return false;
        }
        private void addToSymbolTable(Base_ScanItem context){
            Base_ScanItem below = (Base_ScanItem)context.getBelow();
            if(below != null){
                SCANNER_SYMBOL_TABLE.assertNew(this.name, below.getHandler());
            }
        }
    }
    public static class OnPopUserDef extends Strategy{
        private final String name;
        public OnPopUserDef(String name){
            this.name = name;
        }
        
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode(
                Factory_Node.newScanNode( 
                    CMD.POP, context.getHandler(), Keywords.KWORD.DEF_NAME, this.name
                )
            );
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
    public static class RxOnPush extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            return RXFX_UTIL.assertGoodToggle(context.getHandler());
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
}
