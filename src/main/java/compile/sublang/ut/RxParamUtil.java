package compile.sublang.ut;

import compile.basics.Keywords;
import compile.basics.RxFxTreeFactory;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;

import java.util.regex.Matcher;

import static compile.basics.Keywords.DATATYPE.RAW_TEXT;
import static compile.basics.Keywords.PAR.*;
import static compile.basics.Keywords.USERDEF_OPEN;

public class RxParamUtil extends ParamUtil{
    private static RxParamUtil instance;

    private RxParamUtil(){
        parTypes = Keywords.PAR.values();
    }

    public static RxParamUtil getInstance(){
        return (instance == null)? (instance = new RxParamUtil()): instance;
    }

    private final Keywords.PAR[] parTypes;
//    private int pari;
//    private ListTable listTable;
//    private Keywords.DATATYPE listSource;
//    private String mainText, bracketText;
//    private Keywords.PRIM outType;
//
//    Matcher matcher;

    @Override
    public void findAndSetParam(RxFxTreeFactory.TreeNode leaf, String text){
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

    public Keywords.RX_FUN getFunType(){// already validated as fun
        return Keywords.RX_FUN.fromString(mainText);
    }
    public void fixParamType(Keywords.PAR newParamType){
        pari = newParamType.ordinal();
    }


    private void identifyPattern(RxFxTreeFactory.TreeNode leaf, String text){
        for(pari = 0; pari < parTypes.length; pari++){
            if(parTypes[pari].pattern == null){
                break;
            }
            matcher = parTypes[pari].pattern.matcher(text);
            if(matcher.find()){
                Keywords.DATATYPE dataType = parTypes[pari].datatype;

                switch(dataType){
                    case FUN:
                        bracketText = matcher.replaceAll("$1");
                        mainText = text.substring(0, text.length() - bracketText.length() -2);
                        outType = setOutTypeFromFun();
                        switch(parTypes[pari]){
                            case CONST_PAR:
                                identifyPattern(leaf, readConstant());
                                return;
                            case RANGE_PAR:
                                RxValidator.getInstance().assertValidRange(bracketText);
                                break;
                        }
                        RxValidator.getInstance().assertValidParam(getFunType(), parTypes[pari]);
                        break;
                    case LIST:
                        bracketText = matcher.replaceAll("$1");
                        mainText = text.substring(0, text.length() - bracketText.length() -2);
                        outType = setOutTypeFromItem();
                        break;
                    case RAW_TEXT:
                        outType = setOutTypeFromText(text);
                        break;
                    default:
                        mainText = text;
                        bracketText = "";
                        outType = dataType.outType;
                        break;
                }
                return;
            }
        }
        Erlog.get(this).set("Syntax error", text);
    }

    private Keywords.PRIM setOutTypeFromFun(){
        Keywords.RX_FUN fun = Keywords.RX_FUN.fromString(mainText);
        if(fun == null){
            Erlog.get(this).set( "Invalid RX Function name", mainText);
            return null;
        }
        return fun.outType;
    }

    private Keywords.PRIM setOutTypeFromItem(){// error or assume?
        String category = listTable.getCategory(bracketText);
        //System.out.println("mainText: " + mainText + ", category: " + category);
        if(mainText == null || !makeNotUserDef(mainText).equals(category)){
            Erlog.get(this).set(bracketText + " not an item in " + mainText, mainText + "[" + bracketText + "]");
            fixParamType(TEST_TEXT);
            return TEST_TEXT.datatype.outType;
        }
        listSource = listTable.getDataType(mainText);
        return listSource.outType;
    }

    private Keywords.PRIM setOutTypeFromText(String text){
        if(
            (mainText = listTable.getCategory(text)) == null ||
            (listSource = listTable.getDataType(mainText)) == RAW_TEXT
        ){
            fixParamType(TEST_TEXT);
            mainText = text;
            bracketText = "";
            System.out.println("setOutTypeFromText... "+text+"=text, par="+TEST_TEXT);
            return RAW_TEXT.outType;
        }
        else{
            fixParamType(CATEGORY_ITEM);
            mainText = makeUserDef(mainText);
            bracketText = text;
            System.out.println("setOutTypeFromText... "+text+"=text, par="+CATEGORY_ITEM);
            return listSource.outType;
        }
    }
}
