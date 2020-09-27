package compile.scan;

import compile.basics.Base_StackItem;
import compile.basics.CompileInitializer;
import compile.symboltable.ConstantTable;
import compile.symboltable.SymbolTable;
import compile.symboltable.SymbolTest;
import erlog.Erlog;
import toksource.Base_TextSource;
import toksource.TokenSource;

import static compile.basics.Keywords.*;
import static compile.basics.Keywords.DATATYPE.FUN;

/** A stripped down scanner that only sees target language, source language,
 * include statements and function definitions.
 * Pre-scans function text to an iterable node to use as text source during scanning
 *
 */
public class PreScanner extends Base_Scanner {
    private static PreScanner instance;

    public PreScanner(Base_TextSource fin){
        super(fin);
        this.symbolTest = SymbolTest.getInstance();
        this.fin = fin;
    }

    public static PreScanner getInstance(){
        return instance;
    }
    public static void init(TokenSource fin){
        instance = new PreScanner(fin);
    }
    public static void killInstance(){
        instance = null;
    }

    //private final Pattern FUN_IDENTIFIER = Pattern.compile("^\\"+USERDEF_OPEN+"[a-zA-z]+["+ITEM_OPEN+"]?$");
    //private final Pattern OPENER = Pattern.compile("^["+ITEM_OPEN+"]");

    private SymbolTable factoryTextNode;
    private ConstantTable constantTable;
    private final SymbolTest symbolTest;

    @Override
    public void onCreate(){
        if( !fin.hasData() ){
            er.set( "Bad input file name", inName + SOURCE_FILE_EXTENSION );
        }
        this.onTextSourceChange(fin);
        CompileInitializer.getInstance().setCurrParserStack(this);
        this.factoryTextNode = SymbolTable.getInstance();
        this.constantTable = ConstantTable.getInstance();

        String text;

        // start with a target language datatype
        push(new TargetLanguage());

        // start in line mode for target language
        fin.setLineGetter();
        while(true){// outer loop on INCLUDE file stack level
            Erlog.setTextStatusReporter(fin);
            while(fin.hasNext()){// inner loop on current file
                do{
                    text = fin.next();
                }
                while(fin.isEndLine() && CONT_LINE.equals(text));// skip "..."

                //System.out.println(fin.readableStatus() + ">>>" + text);
                ((Base_PreScanItem)top).pushPop(text);
            }

            if(!restoreTextSource()){
                System.out.println("stack empty");
                break;
            }
            //System.out.println("not empty: "+fin.loggableStatus());
            //System.exit(0);
        }
        pop();
        //System.out.println("\n===symbolTable_fun===");
        //System.out.println(symbolTable_fun.toString());
        //symbolTable_fun.testItr();
    }

//    public void include(String fileName){
//        if(!fileName.endsWith(SOURCE_FILE_EXTENSION)){
//            fileName += SOURCE_FILE_EXTENSION;
//        }
//        TokenSource newFile = new TokenSource(new TextSource_file(fileName));
//        if(newFile.hasData()){
//            fileStack.push(fin);
//            fin = newFile;
//            //CompileInitializer.getInstance().onTextSourceChange(fin, this);
//        }
//        else{
//            er.set("INCLUDE: bad file name", fileName);
//        }
//    }

    private abstract class Base_PreScanItem extends Base_StackItem {
        protected static final int WAIT = 0, IDENTIFY = 1, OPEN = 2, PARSE = 3, TO_INCLUDE = 4;

        @Override
        public void onPush() {}

        @Override
        public void onPop() {}

        public abstract void pushPop(String text);
    }
    private class TargetLanguage extends Base_PreScanItem {
        @Override
        public void pushPop(String text) {
            if(SOURCE_OPEN.equals(text)){
                fin.setWordGetter();
                push(new SourceLanguage());
            }
        }
    }
    private class SourceLanguage extends Base_PreScanItem {
        private int state;

        public SourceLanguage(){
            state = WAIT;
        }
        @Override
        public void pushPop(String text) {
            if(SOURCE_CLOSE.equals(text)){
                if(state != WAIT){
                    er.set("Expected '" + ITEM_CLOSE + "' after function definition", text);
                }
                fin.setLineGetter();
                popAllSource();
            }
            switch(state){
                case TO_INCLUDE:// Changing text source to read file as expected by scanner
                    state = WAIT;
                    include(text);
                    break;
                case WAIT:// Wait for function keyword or include keyword
                    DATATYPE datatype = DATATYPE.fromString(text);
                    if(datatype != null){
                        switch(datatype){
                            case FUN:
                                factoryTextNode.startTextNode(FUN);
                                state = IDENTIFY;
                                break;
                            case INCLUDE:
                                push(new ManageInclude());
                                break;
                            case CONSTANT:
                                push(new ManageConstant());
                                break;
                        }
                    }
                    break;
                case IDENTIFY:// Function keyword must be followed by a user def identifier
                    if(symbolTest.isUserDef(text)){
                        String defName = symbolTest.stripUserDef(text);
                        symbolTest.assertNew(defName);
                        factoryTextNode.setTextName(defName);
                        state = OPEN;
                    }else{
                        er.set("Expected " + USERDEF_OPEN + "identifier here", text);
                    }
                    break;
                case OPEN:// look for opening symbol
                    if(ITEM_OPEN.equals(text)){
                        state = PARSE;
                    }else{
                        er.set("Expected " + ITEM_OPEN + "identifier here", text);
                    }
                    break;
                case PARSE:// have opening symbol; add words until closing symbol
                    if(ITEM_CLOSE.equals(text)){
                        //System.out.println("end function: " + text);
                        factoryTextNode.finishTextNode();
                        state = WAIT;
                    }
                    else{
                        factoryTextNode.addWord(text);
                    }
                    break;
            }

        }
    }
    private class ManageInclude extends Base_PreScanItem {
        @Override
        public void pushPop(String text) {
            if(symbolTest.isUserDef(text)){
                String defName = symbolTest.stripUserDef(text);
                if(constantTable.isConstant(defName)){
                    include(constantTable.getValue(defName));
                }
            }else{
                include(text);
            }
            pop();
        }
    }
    private class ManageConstant extends Base_PreScanItem {
        private int state;

        public ManageConstant(){
            state = IDENTIFY;
        }
        @Override
        public void pushPop(String text) {
            switch(state){
                case IDENTIFY:
                    if(symbolTest.isUserDef(text)){
                        constantTable.startConstant();
                        String defName = symbolTest.stripUserDef(text);
                        symbolTest.assertNew(defName);
                        constantTable.setConstantName(defName);
                        state = PARSE;
                    }else{
                        er.set("Expected " + USERDEF_OPEN + "identifier here", text);
                    }
                    break;
                case PARSE:
                    constantTable.setValue(text);
                default:
                    pop();
            }
        }
    }
}
