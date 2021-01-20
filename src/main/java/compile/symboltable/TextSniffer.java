package compile.symboltable;

import compile.scan.Base_ScanItem;
import runstate.Glob;

import static langdef.Keywords.USERDEF_OPEN;

public class TextSniffer {
    private static TextSniffer instance;

    private TextSniffer(){
        symbolTable = Glob.SYMBOL_TABLE;
        setState(WAIT);
    }

    public static TextSniffer init(){
        return (instance == null)? (instance = new TextSniffer()) : instance;
    }

    private final int WAIT = 0, IDENTIFY = 1, PARSE = 2, SLEEP = 3;
    private int state;

    private final SymbolTable symbolTable;
    Base_ScanItem scanItem;

    private void setState(int newState){
        state = newState;
    }

    public final void onPush(Base_ScanItem scanItem){
        disp("onPush", scanItem.getDebugName());
        if(scanItem.cacheable && state == WAIT){
            this.scanItem = scanItem;
            setState(IDENTIFY);
        }
    }

    public final void sniff(String text){
        disp("sniff", text);
        switch(state){
            case SLEEP:
            case WAIT:
                break;
            case PARSE:
                symbolTable.addWord(text);
                break;
            case IDENTIFY:
                if(Glob.SYMBOL_TEST.isUserDef(text)){
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
        disp("back", "");
        symbolTable.back();
    }

    public final void onPop(Base_ScanItem scanItem){
        disp("onPop", scanItem.getDebugName());
        if(state == PARSE && scanItem.equals(this.scanItem)){
            //System.out.println("++++TextSniffer onPop++++");
            symbolTable.finishTextNode();
            //factoryTextNode.testItr();
            //System.out.println(factoryTextNode.toString());
            //System.out.println("+++++++++++++++++++++++++");
            setState(WAIT);;
        }
    }

    public final void setStateSleep(){
        setState(SLEEP);
    }

    public final void setStateWake(){
        setState(WAIT);
    }

    private void disp(String funct, String text){
        boolean showStatus = false;
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
}
