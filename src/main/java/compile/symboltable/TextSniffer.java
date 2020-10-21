package compile.symboltable;

import compile.scan.Base_ScanItem;
import erlog.Erlog;

import static compile.basics.Keywords.USERDEF_OPEN;

public class TextSniffer {
    private static TextSniffer instance;

    private TextSniffer(){
        symbolTest = SymbolTest.getInstance();
        symbolTable = SymbolTable.getInstance();
        setState(WAIT);
        showStatus = false;
    }

    public static void init(){// call in CompileInitializer
        instance = new TextSniffer();
    }
    public static TextSniffer getInstance(){
        return instance;
    }
    public static void killInstance(){
        instance = null;
    }

    private final int WAIT = 0, IDENTIFY = 1, PARSE = 2, SLEEP = 3;
    private int state;
    private boolean showStatus;

    private final SymbolTest symbolTest;
    private final SymbolTable symbolTable;
    Base_ScanItem scanItem;

    private void setState(int newState){
        state = newState;
    }
    private void status(String funct, String text){
        if(showStatus){
            String stateStr = "";
            switch(state){
                case SLEEP:
                    stateStr = "SLEEP";
                    break;
                case WAIT:
                    stateStr = "WAIT";
                    break;
                case PARSE:
                    stateStr = "PARSE";
                    break;
                case IDENTIFY:
                    stateStr = "IDENTIFY";
                    break;
            }
            System.out.printf("TextSniffer: State = %s: %s: %s \n",  stateStr, funct, text);
        }
    }
    public final void onPush(Base_ScanItem scanItem){
        status("onPush", scanItem.getDebugName());
        if(scanItem.cacheable && state == WAIT){
            this.scanItem = scanItem;
            setState(IDENTIFY);
        }
    }

    public final void sniff(String text){
        status("sniff", text);
        switch(state){
            case SLEEP:
            case WAIT:
                break;
            case PARSE:
                symbolTable.addWord(text);
                break;
            case IDENTIFY:
                if(symbolTest.isUserDef(text)){
                    String textName = text.substring(USERDEF_OPEN.length());
                    symbolTable.startTextNode(scanItem.getDatatype());
                    symbolTable.setTextName(textName);
                    symbolTable.addWord(scanItem.getDatatype().toString());
                    setState(PARSE);
                }
                else{
                    setState(WAIT);
                }
                break;
        }
    }
    public final void back(){
        status("back", "");
        symbolTable.back();
    }
    public final void onPop(Base_ScanItem scanItem){
        status("onPop", scanItem.getDebugName());
        if(state == PARSE && scanItem.equals(this.scanItem)){
            //System.out.println("++++TextSniffer onPop++++");
            symbolTable.finishTextNode();
            //factoryTextNode.testItr();
            //System.out.println(factoryTextNode.toString());
            //System.out.println("+++++++++++++++++++++++++");
            setState(WAIT);;
        }
    }
    public final void sleep(){
        setState(SLEEP);
    }
    public final void wake(){
        setState(WAIT);
    }
}
