package sublang.rxfun;

import compile.interfaces.Debuggable;
import compile.symboltable.ConstantTable;
import erlog.DevErr;
import erlog.Erlog;
import langdef.Keywords;
import listtable.ListTableItemSearch;
import runstate.Glob;

import java.util.Arrays;
import java.util.regex.Matcher;

import static langdef.Keywords.RX_PAR.TEST_TEXT;
import static langdef.Keywords.RX_PAR.TEST_TRUE;

public class RxFun implements Debuggable {
    private static final String MAX = "9999";
    private static final String MIN = "0";

    private final ListTableItemSearch itemSearch;
    private Matcher matcher;
    private String mainText;

    private RxFun next;

    private Keywords.RX_PAR paramType;      // General description of the text
    //private Keywords.PRIM callerType;       // Always NULL unless this is a function
    //protected Keywords.PRIM outType;        // Depends on function type, list type or datatype interpretation of the text
    private Keywords.RX_FUN funType;        // null unless function
    private String[] paramValues;

    protected String category, itemName;    // null unless text describes a list item
    protected Keywords.DATATYPE listSource; // null unless text describes a list item
    private boolean haveFunType;

    public RxFun(String mainText) {
        itemSearch = Glob.LIST_TABLE.getItemSearch();
        System.out.println("RxFun: " + mainText);
        this.mainText = mainText;
        this.init();
    }
    private void init(){
        this.findPattern();
    }
    private void findPattern(){
        Keywords.RX_PAR[] parTypes = Keywords.RX_PAR.values();
        System.out.println("\nidentifyPattern: mainText="+mainText);
        for (Keywords.RX_PAR parType : parTypes) {
            matcher = parType.pattern.matcher(mainText);

            if (matcher.find()) {
                haveFunType = true;// to be set false if any convertToFun methods are called
                paramType = parType;
                System.out.println("found " + paramType);
                switch (paramType.datatype) {
                    case FUN:
                        switch (paramType) {
                            case EMPTY_PAR:
                                this.unpackEmptyParams();
                                break;
                            case NUM_PAR:
                            case AL_NUM_PAR:
                                this.unpackParams();
                                break;
                            case NUM_PAR_MULTI:
                            case AL_NUM_PAR_MULTI:// comma-separated, arbitrary number of param
                                this.unpackMultiParams();
                                break;
                            case CONST_PAR:
                                mainText = readConstant(matcher.group(paramType.groups[0]), matcher.group(paramType.groups[1]));
                                this.findPattern(); // recurse with unpacked constant value
                                return;
                            case RANGE_BELOW:// :2
                                this.unpackRangeBelow();
                                break;
                            case RANGE_ABOVE:// 2:
                                this.unpackRangeAbove();
                                break;
                            case RANGE_PAR: // 1:2
                                this.unpackParams();
                                this.validateRange();
                                break;
                        }

                        funType = Keywords.RX_FUN.fromString(mainText);
                        if (funType == null) {
                            Erlog.get(this).set("Unknown RX function", mainText);
                        }
//                        callerType = funType.caller;
//                        outType = funType.outType;

                        break;
                    case BOOL_TEXT:
                        convertToFun_boolText();
                        break;
                    case NUM_TEXT:
                        convertToFun_numText();
                        break;
                    case LIST:
                        switch (paramType) {
                            case CATEGORY_ITEM:
                                if (!this.convertToFun_categoryItem()) {
                                    convertToFun_rawText();
                                }
                                break;
                            case CATEGORY:
                                if (!this.convertToFun_category()) {
                                    convertToFun_rawText();
                                }
                                break;
                        }
                        break;
                    case RAW_TEXT:
                        convertToFun_rawText();
                        break;
                    default:
                        DevErr.get(this).kill("Developer", mainText);
                }

                //mainText = null;
                return;
            }
        }
        Erlog.get(this).set("Syntax error", mainText);
    }

    public boolean haveFunType(){
        return haveFunType;
    }

    public void setFunType(Keywords.RX_FUN funType){
        this.funType = funType;
    }

    /** Expects funName(parameter) to be split: mainText = funName, bracketText = parameter
     * @return rebuilt function call with constant decoded */
    private String readConstant(String mainText_, String bracketText_){
        String read = ConstantTable.init().getConstantValue(bracketText_);
        if(read == null){
            Erlog.get(this).set("Undefined constant", bracketText_);
        }
        return String.format("%s(%s)", mainText_, read);
    }

    private boolean convertToFun_category(){
        haveFunType = false;
        Keywords.DATATYPE tempListSource;
        if((tempListSource = itemSearch.datatypeByCategoryName(mainText)) == null){
            System.out.println("not a category: " + mainText);
            return false;
        }
        else{
            category = mainText;
            listSource = tempListSource;
            System.out.println("is a category: " + mainText);
            System.out.println("tempListSource=" + tempListSource);
            return true;
        }
        //funType = Keywords.RX_FUN.VAL_CONTAINER_INT;
    }
    private boolean convertToFun_categoryItem(){
        haveFunType = false;
        String givenCategory = matcher.group(paramType.groups[0]);
        String givenItemName = matcher.group(paramType.groups[1]);
        String tempCategory = itemSearch.categoryByItemName(givenItemName);

        System.out.println("givenCategory: " + givenCategory);
        System.out.println("givenItemName: " + givenItemName);
        System.out.println("tempCategory " + tempCategory);
        Keywords.DATATYPE tempListSource;
        if(
            !givenCategory.equals(tempCategory) ||
            (tempListSource = itemSearch.datatypeByCategoryName(tempCategory)) == null
        ){
            System.out.println("===not a category: " + mainText);
            return false;
        }
        else{
            itemName = givenItemName;
            category = givenCategory;
            listSource = tempListSource;
            System.out.println("===is a category: " + mainText);
            System.out.println("listSource=" + tempListSource);
            return true;
        }
        //funType = Keywords.RX_FUN.VAL_CONTAINER_INT;
    }
    private void convertToFun_boolText(){
        haveFunType = false;
        funType = Keywords.RX_FUN.VAL_CONTAINER_INT;
        String val = TEST_TRUE.equals(paramType)? "1" : "0";
        paramValues = new String[]{val};
    }
    private void convertToFun_numText(){
        haveFunType = false;
        funType = Keywords.RX_FUN.VAL_CONTAINER_INT;
        paramValues = new String[]{mainText};
    }
    private void convertToFun_rawText(){
        haveFunType = false;
        paramType = TEST_TEXT;
        funType = Keywords.RX_FUN.VAL_CONTAINER_OBJECT;
        paramValues = new String[]{mainText};
    }

    private void unpackEmptyParams(){
        mainText = matcher.group(paramType.groups[0]);
    }
    private void unpackParams(){
        mainText = matcher.group(paramType.groups[0]);
        paramValues = new String[paramType.groups.length - 1];
        for(int i = 1; i < paramType.groups.length; i++){
            paramValues[i-1] = matcher.group(paramType.groups[i]);
        }
    }
    private void unpackMultiParams(){
        mainText = matcher.group(paramType.groups[0]);
        String paramText = matcher.group(paramType.groups[1]);
        paramValues = paramText.split(",");
    }
    private void unpackRangeBelow(){
        mainText = matcher.group(paramType.groups[0]);
        paramValues = new String[]{
                MIN,
                matcher.group(paramType.groups[1])
        };
    }
    private void unpackRangeAbove(){
        mainText = matcher.group(paramType.groups[0]);
        paramValues = new String[]{
                matcher.group(paramType.groups[1]),
                MAX
        };
    }

    private void validateRange(){
        int lo = Integer.parseInt(paramValues[0]);
        int hi = Integer.parseInt(paramValues[1]);
        if(lo >= hi){
            Erlog.get(this).set("Expected range in ascending order", mainText);
        }
    }

    public Keywords.RX_PAR getParamType() {
        return paramType;
    }

//    public Keywords.PRIM getCallerType() {
//        return callerType;
//    }
//
//    public Keywords.PRIM getOutType() {
//        return outType;
//    }

    public Keywords.RX_FUN getFunType() {
        return funType;
    }

    public String[] getParamValues() {
        return paramValues;
    }

    public String getCategory() {
        return category;
    }

    public String getItemName() {
        return itemName;
    }

    public Keywords.DATATYPE getListSource() {
        return listSource;
    }

    @Override
    public String toString() {
        String callerType = (funType == null)? "none" : funType.caller.toString();
        String outType = (funType == null)? "none" : funType.outType.toString();

        return "\nRxFun{" +
                "mainText='" + mainText + '\'' +
                "\n    paramValues=" + Arrays.toString(paramValues) +
                "\n    paramType=" + paramType +
                "\n    callerType=" + callerType +
                "\n    outType=" + outType +
                "\n    funType=" + funType +
                "\n}";
    }

    @Override
    public String getDebugName() {
        return this.toString();
    }

    @Override
    public void disp() {
        System.out.println(this.toString());
    }

    public RxFun getNext() {
        return next;
    }

    public void setNext(RxFun next) {
        this.next = next;
    }
}
