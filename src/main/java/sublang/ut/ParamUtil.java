package sublang.ut;

import commons.RangeUtil;
import langdef.Keywords;
import compile.symboltable.ConstantTable;
import erlog.Erlog;
import sublang.treenode.TreeNodeBase;

import java.util.regex.Matcher;

public abstract class ParamUtil {
    protected static final int MAX = 1024;
    protected final RangeUtil rangeUtil;
    protected String mainText, bracketText;
    protected String uDefCategory, item;    // null unless text describes a list item
    protected Keywords.DATATYPE listSource; // null unless text describes a list item
    protected int intValues[];
    protected Matcher matcher;

    protected ParamUtil(){
        rangeUtil = new RangeUtil();
    }

    public abstract void findAndSetParam(TreeNodeBase leaf, String text);
    protected abstract void setFunType();

    public String getItem(){
        return item;
    }

    public int[] getIntValues() {
        return intValues;
    }

    /** Expects funName(parameter) to be split: mainText = funName, bracketText = parameter
     * @return rebuilt function call with constant decoded */
    protected final String readConstant(String mainText_, String bracketText_){
        String read = ConstantTable.init().getConstantValue(bracketText_);
        if(read == null){
            Erlog.get(this).set("Undefined constant", bracketText_);
        }
        return String.format("%s(%s)", mainText_, read);
    }
}
