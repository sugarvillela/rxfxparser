package compile.scan.factories;

import runstate.RunState;
import compile.basics.Factory_Node;
import compile.sublang.FxLogicTree;
import compile.sublang.RxLogicTree;
import compile.sublang.factories.TreeFactory;
import compile.sublang.ut.FlatTree;
import compile.sublang.ut.ValidatorRx;
import compile.scan.ut.*;
import compile.symboltable.*;
import erlog.DevErr;
import erlog.Erlog;
import compile.scan.Base_ScanItem;
import compile.scan.Class_Scanner;
import compile.basics.Keywords.DATATYPE;
import compile.basics.Keywords.CMD;
import listtable.ListTable;
import listtable.ListTableItemSearch;
import listtable.ListTableScanLoader;

import static compile.basics.Keywords.*;
import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.FIELD.*;

import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public abstract class Factory_Strategy{ // RXFX, RX, FX, IF_ELSE
    public enum StrategyEnum{       // === added to nodes array ====
        PUSH_GOOD_DATATYPE      (new PushGoodDatatype()),               // PUSH datatype if on allowed datatype list
        PUSH_SOURCE_LANG        (new PushSourceLang()),                 // Symbol != DATATYPE, setWordGetter
        PUSH_COMMENT            (new PushComment()),                    // Symbol != DATATYPE, Silent push pop and ignore
        PUSH_TARG_LANG_INSERT   (new PushTargLangInsert()),             // Symbol != DATATYPE, no nesting
        PUSH_ERR_SKIP_DATATYPE  (new PushErrSkipDatatype()),            // Ignore non-datatype, push good, err bad

        ADD_TEXT                (new AddText()),                        // ADD_TO command with text
        ADD_PRESCAN_ATTRIB      (new AddPreScanAttrib()),               // with key = val text
        ADD_SCAN_ATTRIB         (new AddScanAttrib()),                  // with key = val text
        ADD_USER_DEF_NAME       (new AddUserDefName()),                 // Set name attribute
        ADD_RX_WORD             (new AddRxWord()),                      // RX only
        ADD_FX_WORD             (new AddFxWord()),                      // FX only

        MANAGE_CONSTANT         (new ManageConstant()),                 // For CONSTANT definition (Constants populated by preScanner)
        MANAGE_FUN              (new ManageFun()),                      // For FUN definition (populated by preScanner)
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
        //ON_PUSH_NO_SNIFF        (new OnPushNoSniff()),                  // Same as ON_PUSH but doesn't call TextSniffer
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
        //ON_POP_NO_SNIFF         (new OnPopNoSniff()),                   // Same as ON_POP but doesn't call TextSniffer
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

    public static final int IDENTIFY = 0, BEGIN = 1, PARSE = 2, IGNORE = 3;//, IGNORE2 = 4;
    private static final Factory_Node NODE_FACTORY = Factory_Node.getInstance();
    private static final TreeFactory RX_TREE = RxLogicTree.getInstance();
    private static final TreeFactory FX_TREE = FxLogicTree.getInstance();
    private static final RxFxUtil RXFX_UTIL = new RxFxUtil();
    private static final IfElseUtil IF_ELSE_UTIL = new IfElseUtil();
    private static final SymbolTest SYMBOL_TEST = SymbolTest.getInstance();
    private static final ConstantTable CONSTANT_TABLE = ConstantTable.getInstance();
    private static final SymbolTable SYMBOL_TABLE = SymbolTable.getInstance();
    private static ListTableScanLoader listTableScanLoader = null;
    private static ListTableItemSearch listTableItemSearch = null;

    public static abstract class Strategy{
        //Class_Scanner P;
        
        public Strategy(){}

        protected Class_Scanner getScanner(){
            return (Class_Scanner) RunState.getInstance().getCurrParserStack();
        }
        public void push(DATATYPE datatype){
            RunState.getInstance().getCurrParserStack().push(
                    Factory_ScanItem.getInstance().get(datatype)
            );
        }
        public void pop(){
            RunState.getInstance().getCurrParserStack().pop();
        }
        public void back(String text){
            TextSniffer.getInstance().back();//don't sniff keyword because it will be repeated
            getScanner().back(text);//repeat keyword so next datatype can push it
        }
        public void backPush(String text, DATATYPE datatype){
            push(datatype);
            back(text);
        }
        public void backPop(String text, Base_ScanItem context){
            back(text);
            pop();
        }

        /**Every scan item has a loop of strategies it calls;
         * A strategy return true if it changes something.
         * Returning true breaks the loop. False passes the task to the next strategy.
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
            Erlog.get(context).set( "Syntax error in " + context.getDatatype(), text);
            return true;
        }
    }

    public static class PushGoodDatatype extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            DATATYPE newDatatype = DATATYPE.fromString(text);
            if(newDatatype != null && context.isGoodDatatype(newDatatype)) {
                if(!addContainer(text, context, newDatatype)){
                    push(newDatatype);
                }
                return true;
            }
            return false;
        }
        // Some scan items require a container to enforce ordering
        private boolean addContainer(String text, Base_ScanItem context, DATATYPE newDatatype){
            switch(newDatatype){
                case IF:
                    switch(((Base_ScanItem)getScanner().getTop()).getDatatype()){
                        case IF_ELSE:
                            break;
                        default:
                            backPush(text, IF_ELSE);
                            return true;
                    }
                    break;
                case RX:
                    switch(((Base_ScanItem)getScanner().getTop()).getDatatype()){
                        case RXFX:
                        case IF_TEST:
                        case SCOPE_TEST:
                            break;
                        default:
                            backPush(text, RXFX);
                            return true;
                    }
            }
            return false;
        }
    }
    public static class PushErrSkipDatatype extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            DATATYPE newDatatype = DATATYPE.fromString(text);
            if(newDatatype != null) {
                if(context.isGoodDatatype(newDatatype)) {
                    if(!addContainer(text, context, newDatatype)){
                        push(newDatatype);
                    }
                    return true;
                }
                else{
                    Erlog.get(context).set( "Disallowed datatype in " + context.getDatatype(), text);
                }
            }
            return false;
        }
        // Some scan items require a container to enforce ordering
        private boolean addContainer(String text, Base_ScanItem context, DATATYPE newDatatype){
            switch(newDatatype){
                case IF:
                    switch(((Base_ScanItem)getScanner().getTop()).getDatatype()){
                        case IF_ELSE:
                            break;
                        default:
                            backPush(text, IF_ELSE);
                            return true;
                    }
                    break;
                case RX:
                    switch(((Base_ScanItem)getScanner().getTop()).getDatatype()){
                        case RXFX:
                        case IF_TEST:
                        case SCOPE_TEST:
                            break;
                        default:
                            backPush(text, RXFX);
                            return true;
                    }
            }
            return false;
        }
    }

    public static class AddText extends Strategy{// targ lang base
        @Override
        public boolean go(String text, Base_ScanItem context){
            context.addNode(NODE_FACTORY.newScanNode( CMD.ADD_TO, context.getDatatype(), text));
            return true;
        }
    }

    public static class AddPreScanAttrib extends Strategy{
        protected FIELD key;
        protected String text, val;

        protected final boolean readKeyValPair(){
            String[] toks = text.split("=");
            if( toks.length == 2 ){
                key = FIELD.fromString(toks[0]);
                val = toks[1];
                if(key != null){
                    return true;
                }
                else{
                    Erlog.get(this).set("Unknown datatype: " + toks[0], text);
                }
            }
            else{
                Erlog.get(this).set("Expected key=value format (no space around equal sign)", text);

            }
            return false;
        }
        protected final boolean parseBool(){
            try{
                return Boolean.parseBoolean(val);
            }
            catch(Exception e){
                Erlog.get(this).set("Expected true or false, found" + val, text);
                return false;
            }
        }
        private void setAttrib(){
            switch(key){
                case PROJ_NAME:
                    RunState.getInstance().setProjName(val);
                    break;
                case NEW_LIST_SET:
                    RunState.getInstance().setNewEnumSet(parseBool());
                    break;
                case RX_TARGLANG_ON_SPECIAL:
                    RxTargLangUtil.getInstance().setTargRxOnSpecial(parseBool());
                    break;
                default:
                    DevErr.get(this).kill("Developer: unknown key");
            }
        }
        @Override
        public boolean go(String text, Base_ScanItem context){
            this.text = text;
            if(readKeyValPair()){
                setAttrib();
                return true;
            }
            return false;
        }
    }
    public static class AddScanAttrib extends AddPreScanAttrib {
        private void setAttrib(Base_ScanItem context){
            Base_ScanItem below = (Base_ScanItem)context.getBelow();
            if(below != null){
                context.addNode(
                        NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, below.getDatatype(), key, val )
                );
            }
            else{
                DevErr.get(this).kill("Developer: below null");
            }
        }
        @Override
        public boolean go(String text, Base_ScanItem context){
            this.text = text;
            if(readKeyValPair()){
                setAttrib(context);
                return true;
            }
            return false;
        }
    }
    public static class PushSourceLang extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(SOURCE_OPEN.equals(text)){       // Start rxfx source code
                getScanner().setWordGetter();              // rxfx parses word-by-word
                System.out.println("PushSourceLang: P = " + getScanner());
                push(SRCLANG);
                return true;
            } 
            return false;
        }
    }

    public static class PushComment extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(text.startsWith(COMMENT_TEXT)){// okay to discard text
                if(!context.isGoodDatatype(COMMENT)){
                    Erlog.get(this).set("Comment not supported here");
                }
                if(!Erlog.getTextStatusReporter().isEndLine()){
                    push(COMMENT);
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
                push(TARGLANG_INSERT);
                return true;
            }
            return false;
        }
    }

    public static class AddUserDefName extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(context.getState() == IDENTIFY){
                context.setState(PARSE);
                if(SYMBOL_TEST.isUserDef(text)){
                    String defName = SYMBOL_TEST.stripUserDef(text);
                    if(SYMBOL_TEST.isNew(defName)){
                        context.setDefName(defName);
                        context.addNode(
                                NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, context.getDatatype(), DEF_NAME, defName)
                        );
                        return true;
                    }
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
                back(read);
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
                    getScanner().changeTextSource(node);
                }
                else{
                    Erlog.get(this).set(node.getType() + " dereference not allowed here", text);
                }
                return true;
            }
            return false;
        }
    }


    public static class PopOnEndLine extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(Erlog.getTextStatusReporter().isEndLine()){
                pop();
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
                pop();
                return true;
            }
            return false;
        }
    }
    public static class PopAllOnEndSource extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(SOURCE_CLOSE.equals(text)){
                Class_Scanner scanner = getScanner();
                scanner.setLineGetter();
                scanner.popAllSource();
                return true;
            }
            return false;
        }
    }

    public static class ManageConstant extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            switch(context.getState()){
                case IDENTIFY:
                    if(SYMBOL_TEST.isUserDef(text)){
                        String defName = SYMBOL_TEST.stripUserDef(text);
                        SYMBOL_TEST.assertNew(defName);
                        CONSTANT_TABLE.startConstant();
                        CONSTANT_TABLE.setConstantName(defName);
                        context.setState(PARSE);
                    }
                    else{
                        Erlog.get(this).set("Expected " + USERDEF_OPEN + "identifier here", text);
                    }
                    break;
                case PARSE:
                    CONSTANT_TABLE.setValue(text);
                    context.setState(999);
                    break;
                default:
                    pop();
            }
            return true;
        }
    }
    public static class ManageFun extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            switch(context.getState()){
                case IDENTIFY:// Function keyword must be followed by a user def identifier
                    if(SYMBOL_TEST.isUserDef(text)){
                        String defName = SYMBOL_TEST.stripUserDef(text);
                        SYMBOL_TEST.assertNew(defName);
                        SYMBOL_TABLE.setTextName(defName);
                        context.setState(BEGIN);
                    }else{
                        Erlog.get(this).set("Expected " + USERDEF_OPEN + "identifier here", text);
                    }
                    break;
                case BEGIN:// look for opening symbol
                    if(ITEM_OPEN.equals(text)){
                        context.setState(PARSE);
                    }else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN + "identifier here", text);
                    }
                    break;
                case PARSE:// have opening symbol; add words until closing symbol
                    if(ITEM_CLOSE.equals(text)){
                        //System.out.println("end function: " + text);
                        SYMBOL_TABLE.finishTextNode();
                        context.setState(IDENTIFY);
                        pop();
                    }
                    else{
                        SYMBOL_TABLE.addWord(text);
                    }
                    break;
            }
            return false;
        }
    }
    public static class ManageLists extends Strategy{
        //public static final int UDEF = 0, PARSE = 1, IGNORE = 2;

        protected void onUserDef(String text, Base_ScanItem context){
            DATATYPE h = context.getDatatype();
            String defName = SYMBOL_TEST.stripUserDef(text);
            if(listTableItemSearch.contains(h, defName)){
                context.setState(IGNORE);
            }
            else{
                context.setDefName(defName);
                listTableScanLoader.addNode(
                        NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, h, DEF_NAME, defName )
                );
            }
        }
        protected void onNoUserDef(String text, Base_ScanItem context){
            Erlog.get(this).set("Expected user-defined name for list " + context.getDatatype().toString(), text);
        }
        @Override
        public boolean go(String text, Base_ScanItem context){
            DATATYPE h = context.getDatatype();
            switch(context.getState()){
                case IGNORE:
                    return true;
                case IDENTIFY:
                    context.setState(PARSE);
                    if(SYMBOL_TEST.isUserDef(text)){
                        onUserDef(text, context);
                    }
                    else{
                        onNoUserDef(text, context);
                    }
                    break;
                case PARSE:
                    listTableScanLoader.addNode(NODE_FACTORY.newScanNode( CMD.ADD_TO, h, text));
                    break;
            }
            return true;
        }
    }
    public static class ManageScopesList extends ManageLists{
        @Override
        public void onUserDef(String text, Base_ScanItem context){// is def name: ignore and use default name
            if(listTableItemSearch.contains(LIST_SCOPES, SCOPES_DEF_NAME)){
                context.setState(IGNORE);
            }
            else{
                context.setDefName(SCOPES_DEF_NAME);
                listTableScanLoader.addNode(
                    NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, LIST_SCOPES, DEF_NAME, SCOPES_DEF_NAME )
                );
            }
        }
        @Override
        protected void onNoUserDef(String text, Base_ScanItem context){// is first data: add default name and add data
            context.setDefName(SCOPES_DEF_NAME);
            if(listTableItemSearch.contains(LIST_SCOPES, SCOPES_DEF_NAME)){
                context.setState(IGNORE);
            }
            else{
                context.setDefName(SCOPES_DEF_NAME);
                listTableScanLoader.addNode(
                        NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, LIST_SCOPES, DEF_NAME, SCOPES_DEF_NAME )
                );
                //listTableScanLoader.setDefaultCategory(LIST_SCOPES, context.getDefName());
                listTableScanLoader.addNode(
                        NODE_FACTORY.newScanNode( CMD.ADD_TO, LIST_SCOPES, text)
                );
            }
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
                pop();
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
        //protected final int WAIT = 0, IDENTIFIED = 1, OPENED = 2;
        @Override
        public boolean go(String text, Base_ScanItem context){
            switch(context.getState()){
                case IDENTIFY:
                    context.setState(BEGIN);
                    if(SYMBOL_TEST.isUserDef(text)){
                        context.setDefName(SYMBOL_TEST.stripUserDef(text));
                        push(IF_TEST);;//current text is name, next text is part of test
                    }
                    else{
                        backPush(text, IF_TEST);//no name; current text is part of test
                    }
                    return true;
                case BEGIN:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(PARSE);
                    }
                    else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN, text);
                    }
                    return true;
                case PARSE:
                    if(ITEM_CLOSE.equals(text)){
                        context.setState(IDENTIFY);
                        pop();
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
                case IDENTIFY:
                    context.setState(BEGIN);
                    if(RX.toString().equals(text)) {    // Explicit RX call
                        push(RX);
                    }
                    else if(listTableItemSearch.isItem(LIST_SCOPES, SCOPES_DEF_NAME, text)){ // Use a scope
                        context.addNode(NODE_FACTORY.newPushNode(SCOPE_ITEM));
                        context.addNode(
                                NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, SCOPE_ITEM, ITEM_NAME, text )
                        );
                        context.addNode(NODE_FACTORY.newPopNode(SCOPE_ITEM));
                    }
                    else{   // Implicit RX call
                        backPush(text, RX);
                    }
                    return true;
                case BEGIN:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(IDENTIFY);
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
                case IDENTIFY:
                    context.setState(BEGIN);
                    if(SYMBOL_TEST.isUserDef(text)){
                        context.setDefName(SYMBOL_TEST.stripUserDef(text));
                    }
                    else{
                        back(text);
                    }
                    return true;
                case BEGIN:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(PARSE);
                    }
                    else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN, text);
                    }
                    return true;
                case PARSE:
                    if(ITEM_CLOSE.equals(text)){
                        context.setState(IDENTIFY);
                        pop();
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
                case IDENTIFY:
                    context.setState(BEGIN);
                    if(SYMBOL_TEST.isUserDef(text)){
                        context.setDefName(SYMBOL_TEST.stripUserDef(text));
                        push(SCOPE_TEST);//current text is name, next text is part of test
                    }
                    else{
                        backPush(text, SCOPE_TEST);//no name; current text is part of test
                    }
                    return true;
                case BEGIN:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(PARSE);
                    }
                    else{
                        Erlog.get(this).set("Expected " + ITEM_OPEN, text);
                    }
                    return true;
                case PARSE:
                    if(ITEM_CLOSE.equals(text)){
                        context.setState(IDENTIFY);
                        pop();
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
                case IDENTIFY:
                    context.setState(BEGIN);
                    if(RX.toString().equals(text)) {    // Explicit RX call
                        push(RX);
                    }
                    else if(listTableItemSearch.isItem(LIST_SCOPES, SCOPES_DEF_NAME, text)){ // Use a scope
                        if(listTableItemSearch.isSpecialField(LIST_SCOPES, SCOPES_DEF_NAME, text)){
                            context.setSpecialScope();
                        }
                        context.addNode(NODE_FACTORY.newPushNode(SCOPE_ITEM));
                        context.addNode(
                                NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, SCOPE_ITEM, ITEM_NAME, text )
                        );
                        context.addNode(NODE_FACTORY.newPopNode(SCOPE_ITEM));
                    }
                    else{   // Implicit RX call
                        backPush(text, RX);
                    }
                    return true;
                case BEGIN:
                    if(ITEM_OPEN.equals(text)){
                        context.setState(IDENTIFY);
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
            RxTargLangUtil rxTargLangUtil = RxTargLangUtil.getInstance();

            if(rxTargLangUtil.findRegexAndTruncate(text, context)){
                DATATYPE h = RX_TARGLANG;
                context.addNode(NODE_FACTORY.newPushNode(h));
                context.addNode(NODE_FACTORY.newScanNode(CMD.ADD_TO, h, rxTargLangUtil.getTruncated()));
                context.addNode(NODE_FACTORY.newPopNode(h));
                return true;
            }
            else{
                RxRangeUtil rxRangeUtil = RxRangeUtil.getInstance();
                DATATYPE h = RX_WORD;

                if(rxRangeUtil.findAndSetRange(text)){
                    text = rxRangeUtil.getTruncated();
                }
                if(ValidatorRx.getInstance().assertValidRxWord(text)){
                    context.addNode(NODE_FACTORY.newPushNode(h));

                    context.addNode(
                        NODE_FACTORY.newScanNode(CMD.SET_ATTRIB, h, LO, rxRangeUtil.getLowRange())
                    );
                    context.addNode( NODE_FACTORY.newScanNode(
                            CMD.SET_ATTRIB, h, HI, rxRangeUtil.getHighRange())
                    );
                    TreeFactory.TreeNode root = RX_TREE.treeFromWordPattern(text);
                    //ArrayList<Factory_Node.ScanNode> nodes = RX_TREE.treeToScanNodeList(RX, root);
                    //System.out.println(">>>>Root<<<<");
                    //RX_TREE.dispPreOrder(root);
                    FlatTree flatTree = new FlatTree(RX, root);
                    ArrayList<Factory_Node.ScanNode> nodes = flatTree.treeToScanNodeList();
                    //Commons.disp(nodes, "Flat Tree Scan Node List");
                    //testRebuild(root, nodes);
                    context.addNodes(nodes);
                    context.addNode(NODE_FACTORY.newPopNode(h));
                    return true;
                }
            }
            return false;// only false on bad syntax
        }
        private void testRebuild(TreeFactory.TreeNode origRoot, ArrayList<Factory_Node.ScanNode> nodes){
            TreeFactory.TreeNode newRoot = RX_TREE.treeFromScanNodeSource(RX, nodes);
            System.out.println(">>>>RX testRebuild<<<<");
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
            //ArrayList<Factory_Node.ScanNode> nodes = FX_TREE.treeToScanNodeList(FX, root);
            //testRebuild(root, nodes);
            FlatTree flatTree = new FlatTree(FX, root);
            ArrayList<Factory_Node.ScanNode> nodes = flatTree.treeToScanNodeList();
            context.addNodes(nodes);
            context.addNode(NODE_FACTORY.newPopNode(h));
            return true;
        }
        private void testRebuild(TreeFactory.TreeNode origRoot, ArrayList<Factory_Node.ScanNode> nodes){
            TreeFactory.TreeNode newRoot = FX_TREE.treeFromScanNodeSource(FX, nodes);
            System.out.println(">>>>FX testRebuild<<<<");
            RX_TREE.dispBreadthFirst(newRoot);
            FX_TREE.assertEqual(origRoot, newRoot);
        }
    }

    public static class ReadInclude extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            pop();
            getScanner().include(text);
            return false;
        }
    }

    public static class OnPush extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            //TextSniffer.getInstance().onPush(context);
            context.addNode(NODE_FACTORY.newPushNode(context.getDatatype()));
            return false;
        }
    }
    public static class OnPushList extends Strategy{
        // Lazy init allows specifying in attrib whether to use new list set or add to existing
        private void initListTable(){
            listTableScanLoader = ListTable.getInstance().getScanLoader();
            listTableItemSearch = ListTable.getInstance().getItemSearch();
        }
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(!ListTable.isInitialized()){
                initListTable();
            }
            listTableScanLoader.addNode(NODE_FACTORY.newPushNode(context.getDatatype()));
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
            //TextSniffer.getInstance().onPop(context);
            context.assertDoneState();
            context.addNode(NODE_FACTORY.newPopNode(context.getDatatype()));
            return false;
        }
    }

    public static class OnPopList extends Strategy{
        @Override
        public boolean go(String text, Base_ScanItem context){
            if(context.getState() == IGNORE){
                listTableScanLoader.clear();
            }
            else{
                listTableScanLoader.addNode(NODE_FACTORY.newPopNode(context.getDatatype()));
                listTableScanLoader.onPop();
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
//            if(listTableScanLoader != null){
//                listTableScanLoader.onQuit();
//            }
            //SymbolTable.killInstance();
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
