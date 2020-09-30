package compile.symboltable;

import compile.scan.Base_ScanItem;

import static compile.basics.Keywords.USERDEF_OPEN;

public class TextSniffer {
    private static TextSniffer instance;

    private TextSniffer(){
        symbolTest = SymbolTest.getInstance();
        factoryTextNode = SymbolTable.getInstance();
        state = WAIT;
    }

    public static TextSniffer getInstance(){
        return (instance == null)? (instance = new TextSniffer()) : instance;
    }
    public static void killInstance(){
        instance = null;
    }

    private final int WAIT = 0, IDENTIFY = 1, PARSE = 2;
    private int state;
    private int popCount;

    private final SymbolTest symbolTest;
    private final SymbolTable factoryTextNode;
    Base_ScanItem scanItem;

    public void onPush(Base_ScanItem scanItem){
        //System.out.println(state + ": TextSniffer onPush: " + scanItem.getDebugName());
        if(state == WAIT){
            this.scanItem = scanItem;
            state = IDENTIFY;
        }
    }

    public boolean isSniffing(){
        return state != WAIT;
    }

    public void sniff(String text){
        switch(state){
            case IDENTIFY:
                if(symbolTest.isUserDef(text)){
                    String textName = text.substring(USERDEF_OPEN.length());
                    factoryTextNode.startTextNode(scanItem.getDatatype());
                    factoryTextNode.setTextName(textName);
                    factoryTextNode.addWord(scanItem.getDatatype().toString());
                    state = PARSE;
                }
                else{
                    state = WAIT;
                }
                break;
            case PARSE:
                factoryTextNode.addWord(text);
        }
    }
    public void back(){
        factoryTextNode.back();
    }
    public void onPop(Base_ScanItem scanItem){

        //System.out.println(state + ": TextSniffer onPop: " + scanItem.getDebugName());
        if(state == PARSE && scanItem.equals(this.scanItem)){
            //System.out.println("++++TextSniffer onPop++++");
            factoryTextNode.finishTextNode();
            //factoryTextNode.testItr();
            //System.out.println(factoryTextNode.toString());
            //System.out.println("+++++++++++++++++++++++++");
            state = WAIT;
        }
    }
}
