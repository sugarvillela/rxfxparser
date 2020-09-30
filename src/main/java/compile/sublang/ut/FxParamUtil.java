package compile.sublang.ut;

import commons.Commons;
import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;
import compile.symboltable.ListTable;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

import static compile.basics.Keywords.DATATYPE.RAW_TEXT;
import static compile.basics.Keywords.FX_PAR.FUN_CAT;
import static compile.basics.Keywords.FX_PAR.FUN_CAT_MULTI;

public class FxParamUtil extends ParamUtil{
    private static FxParamUtil instance;

    private FxParamUtil(){

        commaTokenizer = new Tokens_special(",", "'", TK.IGNORESKIP );
    }

    public static FxParamUtil getInstance(){
        return (instance == null)? (instance = new FxParamUtil()): instance;
    }

    private final Tokens_special commaTokenizer;

    private Keywords.FX_PAR paramType;
    Keywords.FX_FUN funType;
    // mainText, bracketText inherited
    private String[] items;
    private String[] uDefCategories;
    private Keywords.DATATYPE[] listSources;
    // int[] intValues inherited

    private void reset(){
        // paramType to be set
        funType = null;
        bracketText = null;
        items = null;
        uDefCategories = null;
        listSources = null;
        intValues = null;
    }

    public Keywords.FX_PAR getParamType(){
        return paramType;
    }
    public Keywords.FX_FUN getFunType(){
        return funType;
    }
    public Keywords.DATATYPE[] getListSources(){
        return listSources;
    }
    public String[] getCategories(){
        return uDefCategories;
    }
    public String[] getItems(){
        return items;
    }

    @Override
    public void findAndSetParam(TreeFactory.TreeNode leaf, String text){
        listTable = ListTable.getInstance();
        mainText = text;
        reset();
        identifyPattern();
    }

    @Override
    protected void setFunType(){
        String[] tok = mainText.split("\\(");
        funType = Keywords.FX_FUN.fromString(tok[0]);
        if(funType == null){
            Erlog.get(this).set("Unknown FX function", mainText);
        }
    }

    private void fixParamType(Keywords.FX_PAR newParamType){
        paramType = newParamType;
    }

    private void identifyPattern(){
        Keywords.FX_PAR[] parTypes = Keywords.FX_PAR.values();
        for(int pari = 0; pari < parTypes.length; pari++){
            matcher = parTypes[pari].pattern.matcher(mainText);
            if(matcher.find()){
                paramType = parTypes[pari];
                //System.out.println("identifyPattern: paramType:" + paramType);
                //System.out.println("                  datatype:" + paramType.datatype);
                bracketText = matcher.replaceAll("$1");
                setFunType();

                switch(paramType){
                    case FUN_EMPTY:
                    case FUN_ALL:
                    case FUN_BOT:
                    case FUN_TOP:
                        break;
                    case FUN_CONST: // Constant
                        mainText = readConstant(mainText, bracketText);
                        identifyPattern();
                        return;
                    case FUN_NUM:
                        intValues = new int[]{Integer.parseInt(bracketText)};
                        break;
                    case FUN_NUM_MULTI:
                        funNumMulti();
                        bracketText = null;
                        break;
                    case FUN_BELOW:
                        intValues = new int[]{0, Integer.parseInt(bracketText)};
                        break;
                    case FUN_ABOVE:
                        intValues = new int[]{Integer.parseInt(bracketText), MAX};
                        break;
                    case FUN_RANGE:
                        rangeUtil.rangeToInt("-", bracketText);
                        intValues = new int[]{rangeUtil.getLow(), rangeUtil.getHigh()};
                        if(intValues[0] >= intValues[1]){
                            Erlog.get(this).set("Expected range in ascending order", bracketText);
                        }
                        break;
                    case FUN_AL_NUM:
                        items = new String[]{bracketText};
                        if(isListItem(bracketText)){
                            uDefCategories = new String[]{uDefCategory};
                            listSources = new Keywords.DATATYPE[]{listSource};
                            fixParamType(FUN_CAT);
                        }
                        break;
                    case FUN_AL_NUM_Q:
                        items = new String[]{bracketText};
                        break;
                    case FUN_MULTI:
                        funMulti();
                        break;
                    case FUN_MULTI_Q:
                        items = commaTokenizer.toArr(bracketText);
                        break;
                    case FUN_CAT:
                        items = new String[]{bracketText};
                        uDefCategories = new String[1];
                        listSources = new Keywords.DATATYPE[1];
                        funCat(bracketText, 0);
                        break;
                    case FUN_CAT_MULTI:
                        funCatMulti();
                        break;
                    default:
                        Erlog.get(this).set("Developer", mainText);
                }
                mainText = null; // don't need these
                bracketText = null; // don't need these
                return;
            }
        }
        Erlog.get(this).set("Syntax error", mainText);
    }


    private void funNumMulti(){
        String[] tok = bracketText.split(",");
        intValues = new int[tok.length];
        for(int i = 0; i < tok.length; i++){
            intValues[i] = Integer.parseInt(tok[i]);
        }
    }
    private boolean isListItem(String bracketText_){
        uDefCategory = listTable.getCategory(bracketText_);
        return(
            uDefCategory != null &&
            (listSource = listTable.getDataType(uDefCategory)) != RAW_TEXT
        );
    }
    private void funMulti(){
        items = commaTokenizer.toArr(bracketText);
        if(isListItem(items[0])){
            uDefCategories = new String[items.length];
            listSources = new Keywords.DATATYPE[items.length];

            uDefCategories[0] = uDefCategory;
            listSources[0] = listSource;
            for(int i = 1; i < items.length; i++){
                if(isListItem(items[i])){
                    uDefCategories[i] = uDefCategory;
                    listSources[i] = listSource;
                }
                else{
                    Erlog.get(this).set("First function parameter was a list item; expected all list items", bracketText);
                }
            }
            fixParamType(FUN_CAT_MULTI);
        }
        //Commons.disp(items, "funMulti items");
        //Commons.disp(uDefCategories, "funMulti uDefCategories");
        //Commons.disp(listSources, "funMulti listSources");
    }
    private void funCat(String text, int i){
        String category, item;
        Keywords.DATATYPE listSource;
        {
            String[] tok = text.split("\\[");
            category = tok[0];
            item = tok[1].substring(0, tok[1].length() - 1);
        }

        if(
            category.equals(listTable.getCategory(item)) &&
            (listSource = listTable.getDataType(category)) != RAW_TEXT
        ){
            items[i] =       item;
            uDefCategories[i] =  category;
            listSources[i] = listSource;
        }
        else{
            Erlog.get(this).set(item + " not an item in " + category, bracketText);
        }
    }
    private void funCatMulti(){
        items = commaTokenizer.toArr(bracketText);
        uDefCategories = new String[items.length];
        listSources = new Keywords.DATATYPE[items.length];
        for(int i = 0; i < items.length; i++){
            funCat(items[i], i);
        }
//        Commons.disp(items,      "funCatMulti items");
//        Commons.disp(uDefCategories, "            uDefCategories");
//        Commons.disp(listSources,"            listSources");
    }
}
