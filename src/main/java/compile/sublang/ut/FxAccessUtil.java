package compile.sublang.ut;

import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;
import erlog.DevErr;
import erlog.Erlog;

import static compile.basics.Keywords.ACCESS_MOD;

public class FxAccessUtil extends ParamUtil{
    private static FxAccessUtil instance;

    private FxAccessUtil(){}

    public static FxAccessUtil getInstance(){
        return (instance == null)? (instance = new FxAccessUtil()): instance;
    }

    private Keywords.FX_ACCESS paramType;
    private int accessMod;

    @Override
    public void findAndSetParam(TreeFactory.TreeNode leaf, String text) {
        if(text.startsWith(ACCESS_MOD)){
            accessMod = 1;
            leaf.data = mainText = text.substring(ACCESS_MOD.length());
        }
        else{
            accessMod = 0;
            mainText = text;
        }
        //listTable = ListTableScanLoader.getInstance();
        //listTableItemSearch = new ListTableItemSearch();
        intValues = null;
        identifyPattern();
    }

    @Override
    protected void setFunType() {}

    public Keywords.FX_ACCESS getParamType(){
        return paramType;
    }

    public int getAccessMod(){
        return accessMod;
    }

    private void identifyPattern(){
        Keywords.FX_ACCESS[] parTypes = Keywords.FX_ACCESS.values();
        for(int pari = 0; pari < parTypes.length; pari++){
            matcher = parTypes[pari].pattern.matcher(mainText);
            if(matcher.find()){
                paramType = parTypes[pari];
//                System.out.println("identifyPattern: paramType:" + paramType);
//                System.out.println("                  datatype:" + paramType.datatype);
                switch(paramType.datatype){
                    case FX_C:
                        break;
                    case FX_N:
                        intValues = new int[]{
                            Integer.parseInt(matcher.replaceAll("$1"))
                        };
                        break;
                    case FX_R:
                        intValues = new int[]{
                                Integer.parseInt(matcher.replaceAll("$1")),
                                Integer.parseInt(matcher.replaceAll("$2"))
                        };
                        if(intValues[0] >= intValues[1]){
                            Erlog.get(this).set("Expected range in ascending order", mainText);
                        }
                        break;
                    default:
                        DevErr.get(this).kill("Developer", mainText);
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
