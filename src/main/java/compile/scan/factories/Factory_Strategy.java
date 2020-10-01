package compile.scan.factories;

import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import compile.sublang.FxLogicTree;
import compile.sublang.RxLogicTree;
import compile.sublang.factories.TreeFactory;
import compile.sublang.ut.ValidatorRx;
import compile.scan.ut.*;
import compile.symboltable.*;
import erlog.Erlog;
import compile.scan.Base_ScanItem;
import compile.scan.Class_Scanner;
import compile.basics.Keywords.DATATYPE;
import compile.basics.Keywords.CMD;

import static compile.basics.Keywords.*;
import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.FIELD.*;

import toksource.ScanNodeSource;
import toksource.TextSource_file;

import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public abstract class Factory_Strategy{ 
    public enum StrategyEnum{       // === added to nodes array ====
        PUSH_GOOD_DATATYPE      (new PushGoodDatatype()),               // PUSH datatype if on allowed datatype list
        PUSH_SOURCE_LANG        (new PushSourceLang()),                 // Symbol != DATATYPE, setWordGetter
        PUSH_COMMENT            (new PushComment()),                    // Symbol != DATATYPE, Silent push pop and ignore
        PUSH_TARG_LANG_INSERT   (new PushTargLangInsert()),             // Symbol != DATATYPE, no nesting


        ADD_TEXT                (new AddText()),                        // ADD_TO command with text
        ADD_KEY_VAL_ATTRIB      (new AddKeyValAttrib()),                // SET_ATTRIB with key = val text
        ADD_USER_DEF_NAME       (new AddUserDefName()),                 // Set name attribute
        ADD_RX_WORD             (new AddRxWord()),                      // RX only
        ADD_FX_WORD             (new AddFxWord()),                      // FX only

        MANAGE_TARG_LANG_INSERT (new ManageTargLangInsert()),           // TARGLANG_INSERT background function
        MANAGE_LISTS            (new ManageLists()),                    // LIST_* background function
        MANAGE_SCOPES_LIST      (new ManageScopesList()),               // Same as LIST_* but enforces singleton SCOPES list
        MANAGE_IF               (new ManageIf()),                       // State machine to 'if' and 'then'
        MANAGE_IF_TEST          (new ManageIfTest()),                   // Handle the boolean part of if
        MANAGE_ELSE             (new ManageElse()),                     // State machine to manage else,
        MANAGE_SCOPE            (new ManageScope()),                    // State machine to manage 'for' and 'do'
        MANAGE_SCOPE_TEST       (new ManageScopeTest()),                // Handle the list fetching part of 'for'

        READ_INCLUDE            (new ReadInclude()),                    // push a file onto the file stack
        READ_VAR                (new ReadVar()),                        // Unroll variable or function symbol table text (Functions populated by preScanner)
        READ_CONSTANT           (new ReadConstant()),                   // Get a single word from ConstantTable
        IGNORE_CONSTANT         (new IgnoreConstant()),                 // For ignoring CONSTANT definition (Constants populated by preScanner)

        POP_ALL_ON_END_SOURCE   (new PopAllOnEndSource()),              // Symbol != DATATYPE, leave target lang on stack
        POP_ON_ENDLINE          (new PopOnEndLine()),                   // Used by COMMENT
        BACK_POP_ON_ANY_DATATYPE (new BackPopOnAnyDatatype()),          // The main stack transition: pop current, add new
        BACK_POP_ON_BAD_VAR     (new BackPopOnBadVar()),                // Var exists but datatype type not allowed
        BACK_POP_ON_ANY_VAR     (new BackPopOnAnyVar()),                // Pop where no nesting allowed

        BACK_POP_ON_ITEM_OPEN   (new BackPopOnItemOpen()),              // for IF ELSE
        BACK_POP_ON_ITEM_CLOSE  (new BackPopOnItemClose()),             // for IF ELSE
        BACK_POP_ON_TARG_LANG_INSERT (new BackPopOnTargLangInsert()),   // Pop where TARG_LANG_INSERT not allowed
        POP_ON_ITEM_CLOSE       (new PopOnItemClose()),                 // Used by function, if, else definitions

        ERR                     (new Err())                             // Last on list, should not be reached
        ;

        public final Strategy strategy;
        StrategyEnum(Strategy strategy) {
            this.strategy = strategy;
        }
    }
    public enum PushEnum{
        ON_PUSH                 (new OnPush()),                         // PUSH message
        ON_PUSH_NO_SNIFF        (new OnPushNoSniff()),                  // Same as ON_PUSH but doesn't call TextSniffer
        ON_PUSH_LIST            (new OnPushList()),                     // Same as ON_PUSH_NO_SNIFF but initializes LIST_TABLE
        ASSERT_TOGGLE_ON_PUSH   (new AssertToggleOnPush()),             // OnPush: RXFX assert RX -> FX, IF_ELSE asserts IF -> ELSE or IF
        ON_PUSH_NOP             (new Nop())                             // OnPush, OnPop, not added to node
        ;

        public final Strategy strategy;
        PushEnum(Strategy strategy) {
            this.strategy = strategy;
        }
    }
    public enum PopEnum{
        ON_POP                  (new OnPop()),                          // generate name if not set, POP message
        ON_POP_NO_SNIFF         (new OnPopNoSniff()),                   // Same as ON_POP but doesn't call TextSniffer
        CLEAR_STATE             (new ClearState()),                     // Give state machines multiple exit routes without error
        ON_POP_LIST             (new OnPopList()),                      // Dump pushPop history to SymbolTable_Enu; no TextSniffer

        RXFX_ERR_ON_POP         (new RxFxErrOnPop()),                   // OnPop: RXFX ends with FX
        ON_LAST_POP             (new OnLastPop()),                      // OnPop: cleanup activities on TargLangBase pop
        ON_POP_NOP              (new Nop())                             // OnPush, OnPop, not added to node
        ;
        public final Strategy strategy;

        PopEnum(Strategy strategy) {
            this.strategy = strategy;
        }
    }
    
    public static StrategyEnum[] getStrategy(StrategyEnum... enums){
        return enums;
    }

    public static PushEnum[] getPush(PushEnum... enums){
        return enums;
    }

    public static PopEnum[] getPop(PopEnum... enums){
        return enums;
    }

    private static final Factory_Node NODE_FACTORY = Factory_Node.getInstance();
    private static final TreeFactory RX_TREE = RxLogicTree.getInstance();
    private static final TreeFactory FX_TREE = FxLogicTree.getInstance();
    private static final RxFxUtil RXFX_UTIL = new RxFxUtil();
    private static final IfElseUtil IF_ELSE_UTIL = new IfElseUtil();
    private static final SymbolTest SYMBOL_TEST = SymbolTest.getInstance();
    private static final ConstantTable CONSTANT_TABLE = ConstantTable.getInstance();
    private static final SymbolTable SYMBOL_TABLE = SymbolTable.getInstance();
    private static ListTable LIST_TABLE = null;

    public static abstract class Strategy{
        Class_Scanner P;
        
        public Strategy(){
            P = (Class_Scanner)CompileInitializer.getInstance().getCurrParserStack();
        }

        public void back(String text, Base_ScanItem context){
            context.back();//don't sniff keyword because it will be repeated
            P.back(text);//repeat keyword so next datatype can push it
        }
        public void backPush(String text, Base_ScanItem oldContext, Base_ScanItem newContext){
            P.push(newContext);
            back(text, oldContext);
        }
        public void backPop(String text, Base_ScanItem context){
            back(text, context);
            P.pop();
        }

        /**Every scan item has a loop of strategies it calls;
         * A strategy should break the loop if it changes something.
         *
         * 1. Strategies are singleton and state-less. Can borrow the state variable in ScanItem, making it
         *    possible to run multiple state machines with the same strategy.
         * 2. Except for pop actions, each strategy should be temporally uncoupled from other strategies,
         *    meaning that a test performed by one should not affect the actions of another.
         *    Accept non-optimization for encapsulation (may perform the same test twice).
         * 3. ManageTargLangInsert breaks the no-state rule, but it cleans up before popping.
         *    TargLangInsert does not allow nesting.
         *
         * @param text The current word from iterator
         * @param context The scan item using the strategy
         * @return True if context should break the loop; true if this object changed something
         */
        public abstract boolean go(String text, Base_ScanItem context);
    }

    public static class Err extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            Erlog.get(context).set( "Disallowed nesting in " + context.getDatatype(), text);
            return true;
        }
    }

    public static class PushGoodDatatype extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            DATATYPE newDatatype = DATATYPE.fromString(text);
            if(newDatatype != null && context.isGoodDatatype(newDatatype)) {
                if(!addContainer(text, context, newDatatype)){
                    P.push(Factory_ScanItem.get(newDatatype));
                }
                return true;
            }
            return false;
        }
        // Some scan items require a container to enforce ordering
        private boolean addContainer(String text, Base_ScanItem context, DATATYPE newDatatype){
            switch(newDatatype){
                case IF:
                    switch(((Base_ScanItem)P.getTop()).getDatatype()){
                        case IF_ELSE:
                            break;
                        default:
                            backPush(text, context, Factory_ScanItem.get(IF_ELSE));
                            return true;
                    }
                    break;
                case RX:
                    switch(((Base_ScanItem)P.getTop()).getDatatype()){
                        case RXFX:
                        case IF_TEST:
                        case SCOPE_TEST:
                            break;
                        default:
                            backPush(text, context, Factory_ScanItem.get(RXFX));
                            return true;
                    }
            }
            return false;
        }
    }

    public static class AddText extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode(
                NODE_FACTORY.newScanNode( CMD.ADD_TO, context.getDatatype(), text)
            );
            return true;
        }
    }

    public static class AddKeyValAttrib extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            // Get key = value
            String[] toks = text.split("=");
            if( toks.length != 2 ){
                Erlog.get(this).set("Expected key=value format (no space around equal sign)", text);
                return false;
            }

            // assert key is keyword
            FIELD key = FIELD.fromString(toks[0]);
            String val = toks[1];
            if(key == null){
                Erlog.get(this).set("Unknown datatype: " + toks[0], text);
                return false;
            }

            // intercept key with immediate effects
            switch(key){
                case PROJ_NAME:
                    CompileInitializer.getInstance().setProjName(val);
                    return true;
                case NEW_LIST_SET:
                    try{
                        CompileInitializer.getInstance().setNewEnumSet(Boolean.parseBoolean(val));
                    }
                    catch(Exception e){
                        Erlog.get(this).set("Expected true or false, found" + val, text);
                    }
                    return true;
                case WROW:
                    try{
                        CompileInitializer.getInstance().setWRow(Integer.parseInt(val));
                    }
                    catch(Exception e){
                        Erlog.get(this).set("Expected numeric, found" + val, text);
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

            // Set attribute for enclosing datatype
            Base_ScanItem below = (Base_ScanItem)context.getBelow();
            if(below == null){
                Erlog.get(this).set("Developer: below null");
                return false;
            }
            context.addNode(
                NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, below.getDatatype(), key, val )
            );
            return true;
        }
    }

    public static class PushSourceLang extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(SOURCE_OPEN.equals(text)){       // Start rxfx source code
                P.setWordGetter();              // rxfx parses word-by-word
                P.push(                         // main source datatype
                    Factory_ScanItem.get( DATATYPE.SRCLANG )
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
                if(!Erlog.getTextStatusReporter().isEndLine()){
                    P.push(Factory_ScanItem.get(DATATYPE.COMMENT));
                }
                return true;
            }
            return false;
        }
    }

    public static class BackPopOnTargLangInsert extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(TARGLANG_INSERT_OPEN.equals(text)){
                backPop(text, context);
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

//    public static class PushScopeItem extends Strategy{
//        @Override
//        public boolean go(String text, Base_ScanItem context){
//            if(LIST_TABLE.isItem(LIST_SCOPES, SCOPES_DEF_NAME, text)){
//                P.push( Factory_ScanItem.get(SCOPE_ITEM) );
//                context.addNode(
//                        NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, SCOPE_ITEM, ITEM_NAME, text )
//                );
//                return true;
//            }
//            return false;
//        }
//    }
    public static class AddUserDefName extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(SYMBOL_TEST.isUserDef(text) && context.getDefName() == null){
                String defName = SYMBOL_TEST.stripUserDef(text);
                if(SYMBOL_TEST.isNew(defName)){
                    context.setDefName(defName);
                    context.addNode(
                        NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, context.getDatatype(), DEF_NAME, defName)
                    );
                    return true;
                }
            }
            return false;
        }
    }
    public static class ReadConstant extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            String read = CONSTANT_TABLE.readConstant(text);
            if(read != null){
                back(read, context);
                return true;
            }
            return false;
        }
    }
    public static class ReadVar extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            SymbolTable.Base_TextNode node = SYMBOL_TABLE.readVar(text);
            if(node != null && context.isGoodDatatype(node.getType())){
                if(context.isGoodDatatype(node.getType())){
                    P.changeTextSource(node);
                }
                else{
                    Erlog.get(this).set(node.getType() + " dereference not allowed here", text);
                }
                return true;
            }
            return false;
        }
    }
    public static class IgnoreConstant extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(context.getState() == 1){
                context.setState(0);
                P.pop();
            }
            else{
                context.setState(1);
            }
            return true;
        }
    }

    public static class PopOnEndLine extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(Erlog.getTextStatusReporter().isEndLine()){
                P.pop();
                return true;
            }
            return false;
        }
    }
    public static class BackPopOnAnyDatatype extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if( DATATYPE.fromString(text) != null){
                backPop(text, context);
                return true;
            }
            return false;
        }
    }
    public static class BackPopOnAnyVar extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            //System.out.println("PopOnVar: " + context.getDatatype());
            if(SYMBOL_TEST.isUserDef(text) && SYMBOL_TABLE.isTextNode(SYMBOL_TEST.stripUserDef(text))){
                //System.out.println("isUserDef and isNode: " + text);
                backPop(text, context);
                return true;
            }
            //System.out.println("not UserDef: " + text);
            return false;
        }
    }
    public static class BackPopOnBadVar extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            //System.out.println("ReadVar: " + context.getDatatype());
            if(SYMBOL_TEST.isUserDef(text)){
                //System.out.println("isUserDef" + text);
                String defName = SYMBOL_TEST.stripUserDef(text);
                SymbolTable.Base_TextNode node = SYMBOL_TABLE.getTextNode(defName);
                if(node != null && !context.isGoodDatatype(node.getType())){
                    backPop(text, context);
                    return true;
                }
                //System.out.println(" not goodType: " + defName);
                return false;
            }
            //System.out.println("not UserDef: " + text);
            return false;
        }
    }
    public static class BackPopOnItemOpen extends Strategy{// test listener
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(ITEM_OPEN.equals(text)){
                backPop(text, context);
                return true;
            }
            return false;
        }
    }
    public static class BackPopOnItemClose extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(ITEM_CLOSE.equals(text)){
                backPop(text, context);
                return true;
            }
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

    public static class ManageLists extends Strategy{
        protected final int UDEF = 0, FIRST = 1, PARSE = 2;
        @Override
        public boolean go(String text, Base_ScanItem context){
            DATATYPE h = context.getDatatype();
            switch(context.getState()){
                case UDEF:
                    if(SYMBOL_TEST.isUserDef(text)){
                        String defName = SYMBOL_TEST.stripUserDef(text);
                        if(LIST_TABLE.contains(h, defName)){
                            Erlog.get(this).set(
                                    String.format(
                                            "%s already exists...%s categories must be uniquely named",
                                            defName, h.toString()
                                    )
                            );
                        }
                        context.setDefName(defName);
                        context.addNode(
                                NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, h, DEF_NAME, defName )
                        );
                        context.setState(FIRST);
                    }
                    else{
                        Erlog.get(this).set("Expected user-defined name for this list", text);
                    }
                    break;
                case FIRST:
                    LIST_TABLE.setDefaultFieldString(h, context.getDefName(),text);
                    context.addNode(NODE_FACTORY.newScanNode( CMD.ADD_TO, h, text));
                    context.setState(PARSE);
                    break;
                case PARSE:
                    context.addNode(NODE_FACTORY.newScanNode( CMD.ADD_TO, h, text));
                    break;
            }
            return true;
        }
    }
    public static class ManageScopesList extends ManageLists{
        @Override
        public boolean go(String text, Base_ScanItem context){
            DATATYPE h = context.getDatatype();
            switch(context.getState()){
                case UDEF:
                    context.setDefName(SCOPES_DEF_NAME);
                    context.addNode(NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, h, DEF_NAME, SCOPES_DEF_NAME ));
                    if(SYMBOL_TEST.isUserDef(text)){
                        context.setState(FIRST);
                    }
                    else{
                        LIST_TABLE.setDefaultFieldString(h, context.getDefName(),text);
                        context.addNode(NODE_FACTORY.newScanNode( CMD.ADD_TO, h, text));
                        context.setState(PARSE);
                    }
                    break;
                case FIRST:
                    LIST_TABLE.setDefaultFieldString(h, context.getDefName(),text);
                    context.addNode(NODE_FACTORY.newScanNode( CMD.ADD_TO, h, text));
                    context.setState(PARSE);
                    break;
                case PARSE:
                    context.addNode(NODE_FACTORY.newScanNode( CMD.ADD_TO, h, text));
                    break;
            }
            return true;
        }
    }
    public static class ManageTargLangInsert extends Strategy{
        private static final LineBuffer LINEBUFFER = new LineBuffer();//lineBuffer clears itself on dump

        @Override
        public boolean go(String text, Base_ScanItem context){
            if( TARGLANG_INSERT_CLOSE.equals(text) ){
                if(!LINEBUFFER.isEmpty()){
                    context.addNode(
                            NODE_FACTORY.newScanNode(
                                    CMD.ADD_TO, context.getDatatype(), LINEBUFFER.dump()
                            )
                    );
                }
                P.pop();
            }
            else{
                LINEBUFFER.add(text);
                if(Erlog.getTextStatusReporter().isEndLine()){
                    context.addNode(
                            NODE_FACTORY.newScanNode(
                                    CMD.ADD_TO, context.getDatatype(), LINEBUFFER.dump()
                            )
                    );
                }
            }
            return true;
        }
    }
    public static class ManageIf extends Strategy{
        protected final int WAIT = 0, IDENTIFIED = 1, OPENED = 2;
        @Override
        public boolean go(String text, Base_ScanItem context){
            switch(context.getState()){
                case WAIT:
                    context.setState(IDENTIFIED);
                    if(SYMBOL_TEST.isUserDef(text)){
                        context.setDefName(SYMBOL_TEST.stripUserDef(text));
                        P.push( Factory_ScanItem.get(IF_TEST) );//current text is name, next text is part of test
                    }
                    else{
                        backPush(text, context, Factory_ScanItem.get(IF_TEST));//no name; current text is part of test
                    }
                    return true;
                case IDENTIFIED:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(OPENED);
                    }
                    else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN, text);
                    }
                    return true;
                case OPENED:
                    if(ITEM_CLOSE.equals(text)){
                        context.setState(WAIT);
                        P.pop();
                        return true;
                    }
            }
            return false;
        }
    }
    public static class ManageIfTest extends ManageIf{// identical for now to ManageScopeTest
        @Override
        public boolean go(String text, Base_ScanItem context){
            switch(context.getState()){
                case WAIT:
                    context.setState(IDENTIFIED);
                    if(RX.toString().equals(text)) {    // Explicit RX call
                        P.push(Factory_ScanItem.get(RX));
                    }
                    else if(LIST_TABLE.isItem(LIST_SCOPES, SCOPES_DEF_NAME, text)){ // Use a scope
                        context.addNode(NODE_FACTORY.newPushNode(SCOPE_ITEM));
                        context.addNode(
                                NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, SCOPE_ITEM, ITEM_NAME, text )
                        );
                        context.addNode(NODE_FACTORY.newPopNode(SCOPE_ITEM));
                    }
                    else{   // Implicit RX call
                        backPush(text, context, Factory_ScanItem.get(RX));
                    }
                    return true;
                case IDENTIFIED:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(WAIT);
                        backPop(text, context);
                        return true;
                    }
                    else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN, text);
                    }
            }
            return false;
        }
    }
    public static class ManageElse extends ManageIf{
        @Override
        public boolean go(String text, Base_ScanItem context){
            switch(context.getState()){
                case WAIT:
                    context.setState(IDENTIFIED);
                    if(SYMBOL_TEST.isUserDef(text)){
                        context.setDefName(SYMBOL_TEST.stripUserDef(text));
                    }
                    else{
                        back(text, context);
                    }
                    return true;
                case IDENTIFIED:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(OPENED);
                    }
                    else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN, text);
                    }
                    return true;
                case OPENED:
                    if(ITEM_CLOSE.equals(text)){
                        context.setState(WAIT);
                        P.pop();
                        return true;
                    }
            }
            return false;
        }
    }
    public static class ManageScope extends ManageIf{
        @Override
        public boolean go(String text, Base_ScanItem context){
            switch(context.getState()){
                case WAIT:
                    context.setState(IDENTIFIED);
                    if(SYMBOL_TEST.isUserDef(text)){
                        context.setDefName(SYMBOL_TEST.stripUserDef(text));
                        P.push( Factory_ScanItem.get(SCOPE_TEST) );//current text is name, next text is part of test
                    }
                    else{
                        backPush(text, context, Factory_ScanItem.get(SCOPE_TEST));//no name; current text is part of test
                    }
                    return true;
                case IDENTIFIED:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(OPENED);
                    }
                    else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN, text);
                    }
                    return true;
                case OPENED:
                    if(ITEM_CLOSE.equals(text)){
                        context.setState(WAIT);
                        P.pop();
                        return true;
                    }
            }
            return false;
        }
    }
    public static class ManageScopeTest extends ManageIf{
        @Override
        public boolean go(String text, Base_ScanItem context){
            switch(context.getState()){
                case WAIT:
                    context.setState(IDENTIFIED);
                    if(RX.toString().equals(text)) {    // Explicit RX call
                        P.push(Factory_ScanItem.get(RX));
                    }
                    else if(LIST_TABLE.isItem(LIST_SCOPES, SCOPES_DEF_NAME, text)){ // Use a scope
                        context.addNode(NODE_FACTORY.newPushNode(SCOPE_ITEM));
                        context.addNode(
                                NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, SCOPE_ITEM, ITEM_NAME, text )
                        );
                        context.addNode(NODE_FACTORY.newPopNode(SCOPE_ITEM));
                    }
                    else{   // Implicit RX call
                        backPush(text, context, Factory_ScanItem.get(RX));
                    }
                    return true;
                case IDENTIFIED:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(WAIT);
                        backPop(text, context);
                        return true;
                    }
                    else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN, text);
                    }
            }
            return false;
        }
    }
    public static class AddRxWord extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            RxRangeUtil rxRangeUtil = RxRangeUtil.getInstance();
            DATATYPE h = RX_WORD;

            if(rxRangeUtil.findAndSetRange(text)){
                text = rxRangeUtil.getTruncated();
            }
            if(ValidatorRx.getInstance().assertValidRxWord(text)){
                context.addNode(NODE_FACTORY.newPushNode(h));

                context.addNode( NODE_FACTORY.newScanNode(
                    CMD.SET_ATTRIB, h, LO, rxRangeUtil.getLowRange())
                );
                context.addNode( NODE_FACTORY.newScanNode(
                    CMD.SET_ATTRIB, h, HI, rxRangeUtil.getHighRange())
                );
                TreeFactory.TreeNode root = RX_TREE.treeFromWordPattern(text);
                //RX_TREE.dispBreadthFirst(root);
                ArrayList<Factory_Node.ScanNode> nodes = RX_TREE.treeToScanNodeList(RX, root);
                //testRebuild(root, nodes);
                context.addNodes(nodes);
                context.addNode(NODE_FACTORY.newPopNode(h));
                return true;
            }
            return false;
        }
        private void testRebuild(TreeFactory.TreeNode origRoot, ArrayList<Factory_Node.ScanNode> nodes){
            TreeFactory.TreeNode newRoot = RX_TREE.treeFromScanNodeSource(RX, nodes);
            //System.out.println(">>>>RX testRebuild<<<<");
            //RX_TREE.dispBreadthFirst(newRoot);
            RX_TREE.assertEqual(origRoot, newRoot);
        }

    }
    public static class AddFxWord extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            DATATYPE h = FX_WORD;
            context.addNode(NODE_FACTORY.newPushNode(h));
            TreeFactory.TreeNode root = FX_TREE.treeFromWordPattern(text);
            ArrayList<Factory_Node.ScanNode> nodes = FX_TREE.treeToScanNodeList(FX, root);
            //testRebuild(root, nodes);
            context.addNodes(nodes);
            context.addNode(NODE_FACTORY.newPopNode(h));
            return true;
        }
        private void testRebuild(TreeFactory.TreeNode origRoot, ArrayList<Factory_Node.ScanNode> nodes){
            TreeFactory.TreeNode newRoot = FX_TREE.treeFromScanNodeSource(FX, nodes);
            //System.out.println(">>>>FX testRebuild<<<<");
            //RX_TREE.dispBreadthFirst(newRoot);
            FX_TREE.assertEqual(origRoot, newRoot);
        }
    }

    public static class ReadInclude extends Strategy{
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
            context.addNode(NODE_FACTORY.newPushNode(context.getDatatype()));
            return false;
        }
    }
    public static class OnPushNoSniff extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode(NODE_FACTORY.newPushNode(context.getDatatype()));
            return false;
        }
    }
    public static class OnPushList extends Strategy{
        // Lazy init allows specifying in attrib whether to use new list set or add to existing
        private void initListTable(){
            ListTable.init(
                    (CompileInitializer.getInstance().isNewEnumSet())?
                            null :
                            new ScanNodeSource(new TextSource_file(ListTable_RxlxReader.listTableFileName()))
            );
            LIST_TABLE = ListTable.getInstance();
            LIST_TABLE.onCreate();
        }
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(LIST_TABLE == null){
                initListTable();
            }
            context.addNode(NODE_FACTORY.newPushNode(context.getDatatype()));
            return false;
        }
    }

    public static class ClearState extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.setState(0);
            return false;
        }
    }
    public static class OnPop extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            TextSniffer.getInstance().onPop(context);
            context.assertDoneState();
            context.addNode(NODE_FACTORY.newPopNode(context.getDatatype()));
            return false;
        }
    }
    public static class OnPopNoSniff extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.assertDoneState();
            context.addNode(NODE_FACTORY.newPopNode(context.getDatatype()));
            return false;
        }
    }

    public static class OnPopList extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            //context.assertDoneState();
            context.addNode(NODE_FACTORY.newPopNode(context.getDatatype()));
            String defName = context.getDefName();
            //Commons.disp(context.getScanNodeList(), "\n OnPop_Enu");
            if(defName != null){
                LIST_TABLE.readList(context.getScanNodeList());
            }
            return false;
        }
    }

    public static class Nop extends Strategy{// Comment onPush, onPop
        @Override
        public boolean go(String text, Base_ScanItem context){
            return false;
        }
    }
    public static class AssertToggleOnPush extends Strategy{// RXFX toggle
        @Override
        public boolean go(String text, Base_ScanItem context){
            Base_ScanItem below = (Base_ScanItem)context.getBelow();
            if(below != null){
                DATATYPE datatype = below.getDatatype();
                switch(datatype){
                    case RXFX:
                        RXFX_UTIL.assertToggle(context.getDatatype());
                        break;
                    case IF_ELSE:
                        IF_ELSE_UTIL.assertToggle(context.getDatatype());
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
            if(LIST_TABLE != null){
                LIST_TABLE.onQuit();
            }
            SymbolTable.killInstance();
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
//                NODE_FACTORY.newScanNode(
//                    CMD.PUSH, context.getDatatype(), Keywords.FIELD.DEF_NAME, this.name
//                )
//            );
//            return false;
//        }
//        private void addToSymbolTable(Base_ScanItem context){
//            Base_ScanItem below = (Base_ScanItem)context.getBelow();
//            if(below != null){
//                SCANNER_SYMBOL_TABLE.assertNew(this.name, below.getDatatype());
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
//                NODE_FACTORY.newScanNode(
//                    CMD.POP, context.getDatatype(), Keywords.FIELD.DEF_NAME, this.name
//                )
//            );
//            return false;
//        }
//    }
}
