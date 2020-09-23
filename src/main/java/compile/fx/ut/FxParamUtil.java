package compile.fx.ut;

import compile.basics.Keywords;
import compile.basics.RxFxTreeFactory;
import compile.rx.ut.RxValidator;
import erlog.Erlog;

import java.util.regex.Matcher;

public class FxParamUtil {
    private static FxParamUtil instance;

    private FxParamUtil(){
        parTypes = Keywords.FX_PAR.values();
    }

    public static FxParamUtil getInstance(){
        return (instance == null)? (instance = new FxParamUtil()): instance;
    }

    private final Keywords.FX_PAR[] parTypes;
    private int pari;

    private String mainText, bracketText;
    Keywords.PRIM outType;

    Matcher matcher;

    public void findAndSetParam(RxFxTreeFactory.TreeNode leaf, String text){

    }

    private void identifyPattern(RxFxTreeFactory.TreeNode leaf, String text){
        for(pari = 0; pari < parTypes.length; pari++){
            if(parTypes[pari].pattern == null){
                break;
            }
            matcher = parTypes[pari].pattern.matcher(text);
            if(matcher.find()){
                Keywords.FX_DATATYPE dataType = parTypes[pari].datatype;

                switch(dataType){
                    case ACCESSOR:

                        break;
                    case MUTATOR:

                        break;
                }
                return;
            }
        }
        Erlog.get(this).set("Syntax error", text);
    }
    /*


    private ListTable listTable;
    private Keywords.FX_DATATYPE listSource;

    public void findAndSetParam(RxTree.TreeNode leaf, String text){
        if(leaf.quoted){//quoted text is raw text
            mainText = text;
            bracketText = "";
            fixParamType(TEST_TEXT);
            outType = TEST_TEXT.datatype.outType;
        }
        else{
            listTable = ListTable.getInstance();
            identifyPattern(leaf, text);
        }
    }

    public Keywords.PAR getParamType(){
        //Commons.disp(Keywords.PAR.values(), "Par values, paramType = " + paramType);
        return Keywords.PAR.fromInt(pari);
    }

    public String getMainText(){
        return mainText;
    }

    public String getBracketText(){
        return bracketText;
    }

    public Keywords.RX_FUN getFunType(){// already validated as fun
        return Keywords.RX_FUN.fromString(mainText);
    }
    public Keywords.PRIM getOutType(){
        return outType;
    }
    public void fixParamType(Keywords.PAR newParamType){
        pari = newParamType.ordinal();
    }

    * */
}
