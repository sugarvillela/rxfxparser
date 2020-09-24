package compile.sublang.ut;

import commons.RangeUtil;
import compile.basics.Keywords;
import compile.basics.RxFxTreeFactory;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;

import java.util.regex.Matcher;

import static compile.basics.Keywords.DATATYPE.RAW_TEXT;

public class FxParamUtil extends ParamUtil{
    private static FxParamUtil instance;

    private FxParamUtil(){
        parTypes = Keywords.FX_PAR.values();
    }

    public static FxParamUtil getInstance(){
        return (instance == null)? (instance = new FxParamUtil()): instance;
    }

    private final Keywords.FX_PAR[] parTypes;
    private final int MAX = 1024;

    public void findAndSetParam(RxFxTreeFactory.TreeNode leaf, String text){
        listTable = ListTable.getInstance();
        identifyPattern(leaf, text);
    }

    private void identifyPattern(RxFxTreeFactory.TreeNode leaf, String text){
        for(pari = 0; pari < parTypes.length; pari++){
            if(parTypes[pari].pattern == null){
                break;
            }
            matcher = parTypes[pari].pattern.matcher(text);
            if(matcher.find()){
                mainText = text;
                bracketText = null;
                low = high = -1;
                Keywords.FX_PAR paramType = parTypes[pari];
                System.out.println("identifyPattern: paramType:" + paramType);
                System.out.println("                  datatype:" + paramType.datatype);
                switch(paramType.datatype){
                    case ACCESSOR_C:
                    case MUTATOR_C:
                        break;
                    case ACCESSOR_N:
                        bracketText = matcher.replaceAll("$1"); // don't need main text
                        low = high = rangeUtil.unwrapInt(mainText);
                        break;
                    case ACCESSOR_R:
                        bracketText = matcher.replaceAll("$1"); // don't need main text
                        System.out.println("ACCESSOR_R: bracketText=" + bracketText);
                        rangeUtil.rangeToInt(":", bracketText);
                        low = rangeUtil.getLow();
                        high = rangeUtil.getHigh();
                        if(low >= high){
                            Erlog.get(this).set("Expected range in ascending order", bracketText);
                        }
                        break;
                    case MUTATOR:
                        bracketText = matcher.replaceAll("$1");
                        mainText = text.substring(0, text.length() - bracketText.length() -2);

                        switch(paramType){
                            case FUN_CONST: // Constant
                                identifyPattern(leaf, readConstant());
                                return;
                            case FUN_NUM:
                                low = high = Integer.parseInt(bracketText);
                                break;
                            case FUN_BELOW:
                                low = 0;
                                high = Integer.parseInt(bracketText);
                                break;
                            case FUN_ABOVE:
                                low = Integer.parseInt(bracketText);
                                high = MAX;
                                break;
                            case FUN_RANGE:
                                rangeUtil.rangeToInt("-", bracketText);
                                low = rangeUtil.getLow();
                                high = rangeUtil.getHigh();
                                if(low >= high){
                                    Erlog.get(this).set("Expected range in ascending order", bracketText);
                                }
                                break;
                            case FUN_MULTI:
                                funMulti();
                                break;
                            case FUN_CAT:
                            case FUN_CAT_MULTI:
                                break;
                            default:
                                Erlog.get(this).set("Syntax error", text);
                        }
                        break;
                    default:
                        Erlog.get(this).set("Syntax error", text);
                        // case ACCESSOR_C:
                        break;
                }
                return;
            }
        }
        Erlog.get(this).set("Syntax error", text);
    }
    private void funMulti(){
        bracketText = bracketText.replace(",", "|");
    }
    private void funCat(){
        String funCategory;
        if(
                (funCategory = listTable.getCategory(bracketText)) == null ||
                        (listSource = listTable.getDataType(funCategory)) == RAW_TEXT
        ){
            System.out.println("Err: bracketText... "+bracketText+", funCategory="+funCategory);
        }
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
