package compile.sublang.ut;

import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;
import compile.symboltable.ListTable;
import erlog.Erlog;

public class FxAccessUtil extends ParamUtil{
    private static FxAccessUtil instance;

    private FxAccessUtil(){}

    public static FxAccessUtil getInstance(){
        return (instance == null)? (instance = new FxAccessUtil()): instance;
    }

    private Keywords.FX_ACCESS paramType;

    @Override
    public void findAndSetParam(TreeFactory.TreeNode leaf, String text) {
        listTable = ListTable.getInstance();
        mainText = text;
        intValues = null;
        identifyPattern();
    }
    public Keywords.FX_ACCESS getParamType(){
        return paramType;
    }
    private void identifyPattern(){
        Keywords.FX_ACCESS[] parTypes = Keywords.FX_ACCESS.values();
        for(int pari = 0; pari < parTypes.length; pari++){
            matcher = parTypes[pari].pattern.matcher(mainText);
            if(matcher.find()){
                paramType = parTypes[pari];
                System.out.println("identifyPattern: paramType:" + paramType);
                System.out.println("                  datatype:" + paramType.datatype);
                switch(paramType.datatype){
                    case ACCESSOR_C:
                        break;
                    case ACCESSOR_N:
                        bracketText = matcher.replaceAll("$1"); // don't need main text
                        intValues = new int[]{Integer.parseInt(bracketText)};
                        break;
                    case ACCESSOR_R:
                        bracketText = matcher.replaceAll("$1"); // don't need main text
                        System.out.println("ACCESSOR_R: bracketText=" + bracketText);
                        rangeUtil.rangeToInt("-", bracketText);
                        intValues = new int[]{rangeUtil.getLow(), rangeUtil.getHigh()};
                        if(intValues[0] >= intValues[1]){
                            Erlog.get(this).set("Expected range in ascending order", bracketText);
                        }
                        break;
                    default:
                        Erlog.get(this).set("Developer", mainText);
                        break;
                }
                mainText = null; // don't need these
                bracketText = null; // don't need these
                return;
            }
        }
        Erlog.get(this).set("Syntax error", mainText);
    }
}
