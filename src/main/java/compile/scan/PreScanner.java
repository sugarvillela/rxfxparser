package compile.scan;

import compile.basics.Base_Stack;
import compile.basics.Base_StackItem;
import compile.basics.CompileInitializer;
import compile.basics.Keywords;
import compile.scan.factories.Factory_ScanItem;
import compile.scan.factories.Factory_Strategy;
import erlog.Erlog;
import toksource.TokenSource;

import java.util.regex.Pattern;

import static compile.basics.Keywords.*;
import static compile.basics.Keywords.HANDLER.FUN;


public class PreScanner extends Base_Stack {
    private final String inName;
    private final Pattern FUN_IDENTIFIER = Pattern.compile("^\\"+USERDEF_OPEN+"[a-zA-z]+["+ITEM_OPEN+"]?$");
    private final Pattern OPENER = Pattern.compile("^["+ITEM_OPEN+"]");

    private static PreScanner instance;

    public PreScanner(TokenSource fin){
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

    @Override
    public void onCreate(){
        if( !fin.hasData() ){
            er.set( "Bad input file name", inName + SOURCE_FILE_EXTENSION );
        }
        CompileInitializer.getInstance().setCurrParserStack(this);
        String text;

        // start with a target language handler
        push(new TargetLanguage());

        // start in line mode for target language
        fin.setLineGetter();
        er.setTextStatusReporter(fin);
        while(fin.hasNext()){// inner loop on current file
            do{
                text = fin.next();
            }
            while(fin.isEndLine() && CONT_LINE.equals(text));// skip "..."

            System.out.println(fin.readableStatus() + ">>>" + text);
            ((Base_PreScanItem)top).pushPop(text);
        }
        pop();
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
            System.out.println("source language");
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
                        System.out.println("found FUN: "+text);
                        state = IDENTIFY;
                    }
                    break;
                case IDENTIFY:
                    if(FUN_IDENTIFIER.matcher(text).find()){
                        int len = text.length();
                        //System.out.println(text.substring(len - 1));
                        if(ITEM_OPEN.equals(text.substring(len - 1))){
                            setAttrib(text.substring(1, len - 1));
                            state = PARSE;
                        }
                        else{
                            setAttrib(text.substring(1));
                            state = OPEN;
                        }
                    }else{
                        er.set("Expected " + USERDEF_OPEN + "identifier", text);
                    }
                    break;
                case OPEN:
                    if(OPENER.matcher(text).find()){
                        System.out.println("parse: " + text);
                        if(text.length() > 1){
                            addTo(text.substring(1));
                        }
                        state = PARSE;
                    }
                    break;
                case PARSE:
                    if(ITEM_CLOSE.equals(text)){
                        System.out.println("end function: " + text);
                        state = WAIT;
                    }
                    else{
                        addTo(text);
                    }
                    break;
            }

        }
        private void setAttrib(String defName){
            System.out.println("setAttrib: " + defName);
        }
        private void addTo(String text){
            System.out.println("addTo: " + text);
        }
    }
}
