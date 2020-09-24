package compile.sublang.ut;

import commons.RangeUtil;
import compile.basics.Keywords;
import compile.basics.RxFxTreeFactory;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;

import java.util.regex.Matcher;

import static compile.basics.Keywords.DATATYPE.RX;
import static compile.basics.Keywords.USERDEF_OPEN;

public abstract class ParamUtil {

    protected final RangeUtil rangeUtil;
    protected int pari;
    protected ListTable listTable;
    protected Keywords.DATATYPE listSource;
    protected String mainText, bracketText;
    protected Keywords.PRIM outType;
    protected Matcher matcher;
    protected int low, high;

    protected ParamUtil(){
        rangeUtil = new RangeUtil();
    }

    public static ParamUtil getParamUtil(Keywords.DATATYPE datatype){
        return (RX.equals(datatype))? RxParamUtil.getInstance() : FxParamUtil.getInstance();
    }

    public abstract void findAndSetParam(RxFxTreeFactory.TreeNode leaf, String text);

    public final String getMainText(){
        return mainText;
    }

    public final String getBracketText(){
        return bracketText;
    }

    public final Keywords.PRIM getOutType(){
        return outType;
    }

    public final int getRangeLow(){
        return low;
    }
    public final int getRangeHigh(){
        return high;
    }
    public final String makeUserDef(String text){
        return (text == null)? "" :
                (text.startsWith(USERDEF_OPEN))? text : USERDEF_OPEN + text;
    }
    public final String makeNotUserDef(String text){
        return (text == null)? "" :
                (text.startsWith(USERDEF_OPEN))? text.substring(USERDEF_OPEN.length()) : text;
    }
    protected final String readConstant(){
        String read = ConstantTable.getInstance().readConstant(bracketText);
        if(read == null){
            Erlog.get(this).set("Undefined constant", bracketText);
        }
        System.out.println("param="+ bracketText + ", read="+read);
        return String.format("%s(%s)", mainText, read);
    }
}
