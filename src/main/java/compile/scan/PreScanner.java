package compile.scan;

import compile.basics.Base_Stack;
import compile.basics.Base_StackItem;
import compile.basics.CompileInitializer;
import compile.symboltable.SymbolTable_Fun;
import toksource.Base_TextSource;
import toksource.TokenSource;

import java.util.regex.Pattern;

import static compile.basics.Keywords.*;
import static compile.basics.Keywords.HANDLER.FUN;


public class PreScanner extends Base_Stack {
    private static PreScanner instance;

    public PreScanner(Base_TextSource fin){
        this.inName = CompileInitializer.getInstance().getInName();
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

    private final Pattern FUN_IDENTIFIER = Pattern.compile("^\\"+USERDEF_OPEN+"[a-zA-z]+["+ITEM_OPEN+"]?$");
    private final Pattern OPENER = Pattern.compile("^["+ITEM_OPEN+"]");

    private SymbolTable_Fun symbolTable_fun;
    private final String inName;

    @Override
    public void onCreate(){
        if( !fin.hasData() ){
            er.set( "Bad input file name", inName + SOURCE_FILE_EXTENSION );
        }
        er.setTextStatusReporter(fin);
        CompileInitializer.getInstance().setCurrParserStack(this);
        this.symbolTable_fun = SymbolTable_Fun.getInstance();

        String text;

        // start with a target language handler
        push(new TargetLanguage());

        // start in line mode for target language
        fin.setLineGetter();

        while(fin.hasNext()){// inner loop on current file
            do{
                text = fin.next();
            }
            while(fin.isEndLine() && CONT_LINE.equals(text));// skip "..."

            //System.out.println(fin.readableStatus() + ">>>" + text);
            ((Base_PreScanItem)top).pushPop(text);
        }
        pop();
        //System.out.println("\n===symbolTable_fun===");
        //System.out.println(symbolTable_fun.toString());
        //symbolTable_fun.testItr();
    }
    private abstract class Base_PreScanItem extends Base_StackItem {

        @Override
        public void onPush() {}

        @Override
        public void onPop() {}

        public abstract void pushPop(String text);
    }
    private class TargetLanguage extends Base_PreScanItem {

        @Override
        public void pushPop(String text) {
            //System.out.println("TargetLanguage");
            if(SOURCE_OPEN.equals(text)){
                fin.setWordGetter();
                push(new SouceLanguage());
            }
        }
    }
    private class SouceLanguage extends Base_PreScanItem {
        private static final int WAIT = 0, IDENTIFY = 1, OPEN = 2, PARSE = 3;
        private int state;

        public SouceLanguage(){
            //System.out.println("source language");
            state = 0;
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
                case WAIT:
                    if(FUN.toString().equals(text)){
                        //System.out.println("found FUN: "+text);
                        symbolTable_fun.newFun(fin);
                        state = IDENTIFY;
                    }
                    break;
                case IDENTIFY:
                    if(FUN_IDENTIFIER.matcher(text).find()){
                        int len = text.length();
                        //System.out.println(text.substring(len - 1));
                        if(ITEM_OPEN.equals(text.substring(len - 1))){
                            symbolTable_fun.setFunName(text.substring(1, len - 1));
                            state = PARSE;
                        }
                        else{
                            symbolTable_fun.setFunName(text.substring(1));
                            state = OPEN;
                        }
                    }else{
                        er.set("Expected " + USERDEF_OPEN + "identifier", text);
                    }
                    break;
                case OPEN:
                    if(OPENER.matcher(text).find()){
                        //System.out.println("parse: " + text);
                        if(text.length() > 1){
                            symbolTable_fun.addWord(text.substring(1));
                        }
                        state = PARSE;
                    }
                    break;
                case PARSE:
                    if(ITEM_CLOSE.equals(text)){
                        //System.out.println("end function: " + text);
                        symbolTable_fun.endFun();
                        state = WAIT;
                    }
                    else{
                        symbolTable_fun.addWord(text);
                    }
                    break;
            }

        }
    }
}
