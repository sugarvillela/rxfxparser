package compile.scan.factories;

import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import compile.scan.ut.*;
import compile.symboltable.*;
import erlog.Erlog;
import compile.scan.Base_ScanItem;
import compile.scan.Class_Scanner;
import compile.basics.Keywords;
import compile.basics.Keywords.HANDLER;
import compile.basics.Keywords.CMD;

import static compile.basics.Keywords.*;
import static compile.basics.Keywords.HANDLER.*;
import static compile.basics.Keywords.KWORD.*;

import toksource.ScanNodeSource;
import toksource.TextSource_file;

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
        POP_ON_TARG_LANG_INSERT,    // Disallow in certain contexts
        PUSH_TARG_LANG_INSERT,      // Symbol != HANDLER
        ADD_TO_LINE_BUFFER,         // TARGLANG_INSERT background function
        MANAGE_ENU_LISTS,           // ENUB, ENUD set name attribute
        ON_INCLUDE,                 // Add a file to the file stack
        SET_USER_DEF_NAME,          // Set name attribute
        READ_FUN,                   // Unroll function symbol table data
        READ_VAR,                   // Unroll VAR symbol table data
        POP_ON_VAR,                 // Where nested var not allowed, pop instead
        POP_ON_TARGLANG_INSERT_CLOSE,//Symbol != HANDLER
        PUSH_COMMENT,               // Symbol != HANDLER, Silent push pop and ignore
        POP_ON_ENDLINE,             // Comment
        POP_ON_ITEM_CLOSE,          // Used by function definitions
        ERR,                        // Last on list, should not be reached
        
        ON_PUSH,                    // PUSH message
        ON_POP,                     // generate name if not set, POP message
        ON_PUSH_NO_SNIFF,           // Same as ON_PUSH but doesn't call TextSniffer
        ON_POP_NO_SNIFF,            // Same as ON_POP but doesn't call TextSniffer
        ON_POP_ENU,                 // Dump pushPop history to SymbolTable_Enu; no TextSniffer
        DUMP_BUFFER_ON_POP,         // OnPop: TARGLANG_INSERT cleanup function
        ASSERT_TOGGLE_ON_PUSH,      // OnPush: RXFX assert RX -> FX, IF_ELSE asserts IF -> ELSE or IF
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
            case ON_INCLUDE:
                return new OnInclude();
            case PUSH_SOURCE_LANG:
                return new PushSourceLang();
            case PUSH_COMMENT:
                return new PushComment();
            case POP_ON_TARG_LANG_INSERT:
                return new PopOnTargLangInsert();
            case PUSH_TARG_LANG_INSERT:
                return new PushTargLangInsert();
            case MANAGE_ENU_LISTS:
                return new ManageEnuLists();
            case SET_USER_DEF_NAME:
                return new SetUserDefName();
            case READ_FUN:
                return new ReadFun();
            case READ_VAR:
                return new ReadVar();
            case POP_ON_VAR:
                return new PopOnVar();
            case POP_ON_TARGLANG_INSERT_CLOSE:
                return new PopOnTargLangInsertClose();
            case POP_ON_ENDLINE:
                return new PopOnEndLine();
            case POP_ON_KEYWORD:
                return new PopOnKeyword();
            case POP_ON_ITEM_CLOSE:
                return new PopOnItemClose();
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
            case ON_PUSH_NO_SNIFF:
                return new OnPushNoSniff();
            case ASSERT_TOGGLE_ON_PUSH:
                return new AssertToggleOnPush();
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
            case ON_POP_NO_SNIFF:
                return new OnPopNoSniff();
            case ON_POP_ENU:
                return new OnPop_Enu();
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
    private static final RxFxUtil RXFX_UTIL = new RxFxUtil();
    private static final IfElseUtil IF_ELSE_UTIL = new IfElseUtil();
    private static final SymbolTest SYMBOL_TEST = SymbolTest.getInstance();
    private static final Factory_TextNode SYMBOL_TABLE = Factory_TextNode.getInstance();
    private static SymbolTable_Enu SYMBOL_TABLE_ENU = null;
    private static HANDLER lastPopped = null;

    public static abstract class Strategy{
        Class_Scanner P;
        
        public Strategy(){
            P = (Class_Scanner)CompileInitializer.getInstance().getCurrParserStack();
        }
        public abstract boolean go(String text, Base_ScanItem context);
    }

    public static class Err extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            Erlog.get(context).set( "Disallowed handler!!", text);
            return true;
        }
    }

    public static class PushGoodHandler extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            Keywords.HANDLER newHandler = Keywords.HANDLER.fromString(text);
            if(newHandler != null && context.isGoodHandler(newHandler)) {
                if(!addContainer(text, context, newHandler)){
                    P.push(Factory_ScanItem.get(newHandler));
                }
                return true;
            }
            return false;
        }
        private boolean addContainer(String text, Base_ScanItem context, HANDLER newHandler){
            HANDLER topHandler;
            switch(newHandler){
                case IF:
                    topHandler = ((Base_ScanItem)P.getTop()).getHandler();
                    if(!IF_ELSE.equals(topHandler) && !ELSE.equals(topHandler)){
                        P.push(Factory_ScanItem.get(IF_ELSE));
                        P.back(text);
                        context.back();
                        return true;
                    }
                    return false;
                case RX:
                    topHandler = ((Base_ScanItem)P.getTop()).getHandler();
                    if(!RXFX.equals(topHandler)){
                        P.push(Factory_ScanItem.get(RXFX));
                        P.back(text);
                        context.back();
                        return true;
                    }
                    return false;
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
            // Get key = value
            String toks[] = text.split("=");
            if( toks.length != 2 ){
                Erlog.get(this).set("key=value format is required", text);
                return false;
            }

            // assert key is keyword
            Keywords.KWORD key = Keywords.KWORD.fromString(toks[0]);
            String val = toks[1];
            if(key == null){
                Erlog.get(this).set("Unknown keyword: " + toks[0], text);
                return false;
            }

            // intercept key with immediate effects
            switch(key){
                case PROJ_NAME:
                    CompileInitializer.getInstance().setProjName(val);
                    return true;
                case NEW_ENUM_SET:
                    try{
                        CompileInitializer.getInstance().setNewEnumSet(Boolean.parseBoolean(val));
                    }
                    catch(Exception e){
                        Erlog.get(this).set("Expected true or false, found", val);
                    }
                    return true;
                case WROW:
                    try{
                        CompileInitializer.getInstance().setWRow(Integer.parseInt(val));
                    }
                    catch(Exception e){
                        Erlog.get(this).set("Expected numeric, found", val);
                    }
                    return true;
                case WVAL:
                    try{
                        CompileInitializer.getInstance().setWVal(Integer.parseInt(val));
                    }
                    catch(Exception e){
                        Erlog.get(this).set("Expected numeric, found", val);
                    }
                    return true;
            }

            // Set attribute for enclosing handler
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

    public static class PopOnTargLangInsert extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(TARGLANG_INSERT_OPEN.equals(text)){
                context.back();//don't sniff keyword because it will be repeated
                P.back(text);
                P.pop();
                return true;
            }
            return false;
        }
    }
    public static class PushTargLangInsert extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if( TARGLANG_INSERT_OPEN.equals(text)){
                P.push( Factory_ScanItem.get(TARGLANG_INSERT) );
                return true;
            }
            return false;
        }
    }

    public static class ManageEnuLists extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            HANDLER h = context.getHandler();

            if(SYMBOL_TEST.isUserDef(text)){

                // Lazy init allows specifying in attrib whether to use new enum set or add to existing
                if(SYMBOL_TABLE_ENU == null){
                    SymbolTable_Enu.init(
                            (CompileInitializer.getInstance().isNewEnumSet())?
                                    null :
                                    new ScanNodeSource(new TextSource_file(Keywords.fileName_symbolTableEnu()))
                    );
                    SYMBOL_TABLE_ENU = SymbolTable_Enu.getInstance();
                    SYMBOL_TABLE_ENU.onCreate();
                }

                String defName = text.substring(USERDEF_OPEN.length());
                
                // Make sure name doesn't exist
                if(SYMBOL_TABLE_ENU.contains(h, defName)){
                    Erlog.get(this).set(
                            String.format(
                                    "%s already exists...%s categories must be uniquely named",
                                    defName, h.toString()
                            )
                    );
                }
                
                // if not the first list defined, pop current and push another one
                if(context.getDefName() != null){
                    context = Factory_ScanItem.get(h);
                    P.pop();
                    P.push(context);
                }

                // tell context its name
                context.setDefName(defName);

                // build nodes
                context.addNode(
                    Factory_Node.newScanNode(CMD.SET_ATTRIB, h, DEF_NAME, defName )
                );

                System.out.println("ManageEnuLists: name: "+defName + ", handler: "+context.getHandler());
                return true;
            }
//            else{// is an item
//                System.out.printf("     : %s: %s \n", context.getDefName(), text);
//                SYMBOL_TABLE_ENU.addTo(h, null, text);
//            }
            return false;
        }
    }
    public static class SetUserDefName extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(SYMBOL_TEST.isUserDef(text) && context.getDefName() == null){
                String defName = text.substring(USERDEF_OPEN.length());
                if(SYMBOL_TEST.isNew(defName)){
                    context.setDefName(defName);
                    context.addNode(
                        Factory_Node.newScanNode(CMD.SET_ATTRIB, context.getHandler(), DEF_NAME, defName)
                    );
                    return true;
                }
            }
            return false;
        }
    }
    public static class ReadVar extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            //System.out.println("ReadVar: " + context.getHandler());
            if(SYMBOL_TEST.isUserDef(text)){
                //System.out.println("isUserDef" + text);
                String defName = text.substring(USERDEF_OPEN.length());
                Factory_TextNode.Base_TextNode node = SYMBOL_TABLE.getTextNode(defName);
                if(node != null && context.isGoodHandler(node.getType())){
                    //System.out.println("goodType: " + defName);
                    P.changeTextSource(node);
                    return true;
                }
                //System.out.println(" not goodType: " + defName);
                return false;
            }
            //System.out.println("not UserDef: " + text);
            return false;
        }
    }
    public static class ReadFun extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(SYMBOL_TEST.isUserDef(text)){
                String identifier = text.substring(USERDEF_OPEN.length());
                Factory_TextNode.Base_TextNode node = SYMBOL_TABLE.getTextNode(identifier, FUN);
                if(node != null){
                    P.changeTextSource(node);
                }
            }
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
    public static class PopOnKeyword extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if( Keywords.HANDLER.fromString(text) != null){
                context.back();//don't sniff keyword because it will be repeated
                P.back(text);//repeat keyword so next handler can push it
                P.pop();

                return true;
            }
            return false;
        }
    }
    public static class PopOnVar extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            //System.out.println("PopOnVar: " + context.getHandler());
            if(SYMBOL_TEST.isUserDef(text) && SYMBOL_TABLE.isTextNode(text.substring(USERDEF_OPEN.length()))){
                //System.out.println("isUserDef and isNode: " + text);
                context.back();//don't sniff keyword because it will be repeated
                P.back(text);//repeat keyword so next handler can push it
                P.pop();
                return true;
            }
            System.out.println("not UserDef: " + text);
            return false;
        }
    }
    public static class PopOnItemClose extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(ITEM_CLOSE.equals(text)){
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
    public static class OnInclude extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            P.pop();
            P.include(text);
            return false;
        }
    }
    public static class OnPush extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            TextSniffer.getInstance().onPush(context);
            context.addNode( Factory_Node.newScanNode( CMD.PUSH, context.getHandler() ) );
            return false;
        }
    }
    public static class OnPop extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            TextSniffer.getInstance().onPop(context);
            context.addNode( Factory_Node.newScanNode( CMD.POP, context.getHandler() ) );
            lastPopped = context.getHandler();
            return false;
        }
    }
    public static class OnPushNoSniff extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode( Factory_Node.newScanNode( CMD.PUSH, context.getHandler() ) );
            return false;
        }
    }
    public static class OnPopNoSniff extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode( Factory_Node.newScanNode( CMD.POP, context.getHandler() ) );
            lastPopped = context.getHandler();
            return false;
        }
    }
    public static class OnPop_Enu extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode( Factory_Node.newScanNode( CMD.POP, context.getHandler() ) );
            String defName = context.getDefName();
            //Commons.disp(context.getScanNodeList(), "\n OnPop_Enu");
            if(defName != null){
                SYMBOL_TABLE_ENU.readList(context.getScanNodeList());
            }
            lastPopped = context.getHandler();
            return false;
        }
    }

    public static class Nop extends Strategy{// Comment onPush, onPop
        @Override
        public boolean go(String text, Base_ScanItem context){
            return false;
        }
    }
    public static class DumpBufferOnPop extends Strategy{// TargLangInsert
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
    public static class AssertToggleOnPush extends Strategy{// RXFX toggle
        @Override
        public boolean go(String text, Base_ScanItem context){
            Base_ScanItem below = (Base_ScanItem)context.getBelow();
            if(below != null){
                HANDLER handler = below.getHandler();
                switch(handler){
                    case RXFX:
                        RXFX_UTIL.assertToggle(context.getHandler());
                        break;
                    case IF_ELSE:
                        IF_ELSE_UTIL.assertToggle(context.getHandler());
                        break;
                }
            }
            return false;
        }
    }
    public static class RxFxErrOnPop extends Strategy{// RXFX
        @Override
        public boolean go(String text, Base_ScanItem context){
            return RXFX_UTIL.errOnPop();
        }
    }
    public static class OnLastPop extends Strategy{// Target Language Base
        @Override
        public boolean go(String text, Base_ScanItem context){
//            SYMBOL_TABLE_VAR.write_rxlx_file(
//                Keywords.fileName_symbolTableAll()
//            );
            if(SYMBOL_TABLE_ENU != null){
                SYMBOL_TABLE_ENU.onQuit();
            }
            SYMBOL_TABLE.killInstance();
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
