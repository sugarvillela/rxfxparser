package sublang.ut;

import langdef.Keywords;
import listtable.ListTableItemSearch;
import erlog.DevErr;
import erlog.Erlog;
import runstate.Glob;
import sublang.treenode.TreeNodeBase;

import static langdef.Keywords.DATATYPE.RAW_TEXT;
import static langdef.Keywords.PRIM.*;
import static langdef.Keywords.RX_PAR.*;

public class RxParamUtil extends ParamUtil{
    private static RxParamUtil instance;

    private RxParamUtil(){}

    public static RxParamUtil init(){
        return (instance == null)? (instance = new RxParamUtil()): instance;
    }

    private Keywords.RX_PAR paramType;      // General description of the text
    private Keywords.PRIM callerType;       // Always NULL unless this is a function
    protected Keywords.PRIM outType;        // Depends on function type, list type or datatype interpretation of the text
    private Keywords.RX_FUN funType;        // null unless function
    protected ListTableItemSearch listTableItemSearch;

    @Override
    public void findAndSetParam(TreeNodeBase leaf, String text){
        reset();
        if(leaf.quoted){//quoted text is raw text
            item = text;
            fixParamType(TEST_TEXT);
            outType = TEST_TEXT.datatype.outType;
        }
        else{
            mainText = text;
            //listTable = ListTableScanLoader.getInstance();
            listTableItemSearch = Glob.LIST_TABLE.getItemSearch();
            identifyPattern();
        }
    }

    @Override
    protected void setFunType(){
        String[] tok = mainText.split("\\(");
        funType = Keywords.RX_FUN.fromString(tok[0]);
        if(funType == null){
            Erlog.get(this).set("Unknown RX function", mainText);
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
        System.out.println("\nidentifyPattern: mainText="+mainText);
        for(int pari = 0; pari < parTypes.length; pari++){

            matcher = parTypes[pari].pattern.matcher(mainText);
            if(matcher.find()){
                paramType = parTypes[pari];
                System.out.println("found "+paramType);
                switch(paramType.datatype){
                    case FUN:
                        switch(paramType){
                            case EMPTY_PAR:
                                this.unpackEmptyParams();
                            case NUM_PAR:
                                this.unpackIntParams();
                                break;
                            case CONST_PAR:
                                mainText = readConstant(
                                        matcher.group(paramType.groups[0]),
                                        matcher.group(paramType.groups[1])
                                );
                                this.identifyPattern(); // recurse with unpacked constant value
                                return;
                            case RANGE_BELOW:
                                this.unpackRangeBelow();
                                break;
                            case RANGE_ABOVE:
                                this.unpackRangeAbove();
                                break;
                            case RANGE_PAR:
                                this.unpackIntParams();
                                if(intValues[0] >= intValues[1]){
                                    Erlog.get(this).set("Expected range in ascending order", mainText);
                                }
                                break;
                        }
                        setFunType();
                        callerType = funType.caller;
                        outType = funType.outType;
                        break;
                    case BOOL_TEXT:
                        mainText = String.format("VAL_CONTAINER_BOOL(%c)", (TEST_TRUE.equals(paramType))? '1' : '0');
                        this.identifyPattern(); // recurse with value converted to function
                        break;
                    case NUM_TEXT:
                        mainText = String.format("VAL_CONTAINER_INT(%s)", mainText);
                        this.identifyPattern(); // recurse with value converted to function
                        break;
                    case LIST:
                        bracketText = matcher.replaceAll("$1");
                        setTypesFromItem();
                        break;
                    case RAW_TEXT:
                        setTypesFromText();
                        break;
                    default:
                        DevErr.get(this).kill("Developer", mainText);
                }
                mainText = null;
                bracketText = null;
                return;
            }
        }
        Erlog.get(this).set("Syntax error", mainText);
    }
    private void unpackEmptyParams(){
        mainText = matcher.group(paramType.groups[0]);
    }
    private void unpackIntParams(){
        mainText = matcher.group(paramType.groups[0]);
        intValues = new int[paramType.groups.length - 1];
        for(int i = 1; i < paramType.groups.length; i++){
            intValues[i-1] = Integer.parseInt(matcher.group(paramType.groups[i]));
        }
    }
    private void unpackRangeBelow(){
        mainText = matcher.group(paramType.groups[0]);
        intValues = new int[]{
                0,
                Integer.parseInt(matcher.group(paramType.groups[1]))
        };
    }
    private void unpackRangeAbove(){
        mainText = matcher.group(paramType.groups[0]);
        intValues = new int[]{
                Integer.parseInt(matcher.group(paramType.groups[1])),
                MAX
        };
    }

    private void setTypesFromItem(){// error or assume?
        String category = mainText.substring(0, mainText.length() - bracketText.length() -2);
        if(category.equals(listTableItemSearch.categoryByItemName(bracketText))){
            item = bracketText;
            uDefCategory = category;
            listSource = listTableItemSearch.getDataType(uDefCategory);
            outType = listSource.outType;
        }
        else{
            Erlog.get(this).set(bracketText + " not an item in " + mainText, mainText + "[" + bracketText + "]");
//            fixParamType(TEST_TEXT);
//            outType = TEST_TEXT.datatype.outType;
        }
    }

    private void setTypesFromText(){
        String tempCategory = listTableItemSearch.categoryByItemName(mainText);
        Keywords.DATATYPE tempListSource;
        if(
            tempCategory != null &&
            (tempListSource = listTableItemSearch.getDataType(tempCategory)) != null
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
