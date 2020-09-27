package compile.sublang.ut;

import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;
import compile.symboltable.ListTable;
import erlog.Erlog;

import static compile.basics.Keywords.DATATYPE.RAW_TEXT;
import static compile.basics.Keywords.PRIM.*;
import static compile.basics.Keywords.RX_PAR.*;

public class RxParamUtil extends ParamUtil{
    private static RxParamUtil instance;

    private RxParamUtil(){}

    public static RxParamUtil getInstance(){
        return (instance == null)? (instance = new RxParamUtil()): instance;
    }

    //private final Keywords.RX_PAR[] parTypes;

    private Keywords.RX_PAR paramType;      // General description of the text
    private Keywords.PRIM callerType;       // Always NULL unless this is a function
    protected Keywords.PRIM outType;        // Depends on function type, list type or datatype interpretation of the text
    private Keywords.RX_FUN funType;        // null unless function


    @Override
    public void findAndSetParam(TreeFactory.TreeNode leaf, String text){
        mainText = text;
        reset();
        if(leaf.quoted){//quoted text is raw text
            fixParamType(TEST_TEXT);
            outType = TEST_TEXT.datatype.outType;
        }
        else{
            listTable = ListTable.getInstance();
            identifyPattern();
        }
    }
    private void reset(){
        callerType = NULL;
        // paramType to be set
        outType = null;
        funType = null;
        bracketText = null;
        item = null;
        uDefCategory = null;
        listSource = null;
        intValues = null;
    }

    public Keywords.PRIM getCallerType(){
        return callerType;
    }

    public Keywords.RX_PAR getParamType(){
        return paramType;
    }

    public final Keywords.PRIM getOutType(){
        return outType;
    }

    public Keywords.RX_FUN getFunType(){
        return funType;
    }

    public String getUDefCategory(){
        return uDefCategory;
    }

    public Keywords.DATATYPE getListSource(){
        return listSource;
    }

    private void fixParamType(Keywords.RX_PAR newParamType){
        paramType = newParamType;
    }

    /** Expects mainText set */
    private void identifyPattern(){
        Keywords.RX_PAR[] parTypes = Keywords.RX_PAR.values();
        for(int pari = 0; pari < parTypes.length; pari++){

            matcher = parTypes[pari].pattern.matcher(mainText);
            if(matcher.find()){
                paramType = parTypes[pari];

                switch(paramType.datatype){
                    case FUN:
                        bracketText = matcher.replaceAll("$1");
                        mainText = mainText.substring(0, mainText.length() - bracketText.length() -2);
                        if(CONST_PAR.equals(paramType)){
                            mainText = readConstant(mainText, bracketText);
                            identifyPattern();
                            return;
                        }
                        setTypesFromFun();

                        switch(paramType){
                            case NUM_PAR:
                                intValues = new int[]{Integer.parseInt(bracketText)};
                                break;
                            case RANGE_PAR:
                                rangeUtil.rangeToInt("-", bracketText);
                                intValues = new int[]{rangeUtil.getLow(), rangeUtil.getHigh()};
                                if(intValues[0] >= intValues[1]){
                                    Erlog.get(this).set("Expected range in ascending order", bracketText);
                                }
                                break;
                            case RANGE_BELOW:
                                intValues = new int[]{0, Integer.parseInt(bracketText)};
                                break;
                            case RANGE_ABOVE:
                                intValues = new int[]{Integer.parseInt(bracketText), MAX};
                                break;
                        }
                        break;
                    case BOOL_TEXT:
                        intValues = new int[]{(mainText.charAt(0) == 'T')? 1 : 0};
                        outType = BOOLEAN;
                        break;
                    case NUM_TEXT:
                        intValues = new int[]{Integer.parseInt(mainText)};
                        outType = NUMBER;
                        break;
                    case LIST:
                        bracketText = matcher.replaceAll("$1");
                        setTypesFromItem();
                        break;
                    case RAW_TEXT:
                        setTypesFromText();
                        break;
                    default:
                        Erlog.get(this).set("Developer", mainText);
                }
                mainText = null;
                bracketText = null;
                return;
            }
        }
        Erlog.get(this).set("Syntax error", mainText);
    }

    private void setTypesFromFun(){
        funType = Keywords.RX_FUN.fromString(mainText);
        if(funType == null){
            Erlog.get(this).set( "Invalid RX Function name", mainText);
        }
        else{
            //mainText = String.format("$%s(%s)", mainText, bracketText);
            callerType = funType.caller;
            outType = funType.outType;
        }
    }

    private void setTypesFromItem(){// error or assume?
        String category = makeNotUserDef(mainText.substring(0, mainText.length() - bracketText.length() -2));
        if(category.equals(listTable.getCategory(bracketText))){
            item = bracketText;
            uDefCategory = category;
            listSource = listTable.getDataType(uDefCategory);
            outType = listSource.outType;
        }
        else{
            Erlog.get(this).set(bracketText + " not an item in " + mainText, mainText + "[" + bracketText + "]");
//            fixParamType(TEST_TEXT);
//            outType = TEST_TEXT.datatype.outType;
        }
    }

    private void setTypesFromText(){
        String tempCategory = listTable.getCategory(mainText);
        Keywords.DATATYPE tempListSource;
        if(
            tempCategory != null &&
            (tempListSource = listTable.getDataType(tempCategory)) != RAW_TEXT
        ){
            fixParamType(CATEGORY_ITEM);
            item = mainText;
            //mainText = String.format("$%s[%s]", category, bracketText);
            uDefCategory = tempCategory;
            listSource = tempListSource;
            outType = listSource.outType;
        }
        else{
            item = mainText;
            outType = RAW_TEXT.outType;
        }
    }
}
