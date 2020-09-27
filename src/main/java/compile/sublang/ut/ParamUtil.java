package compile.sublang.ut;

import commons.RangeUtil;
import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;

import java.util.regex.Matcher;

import static compile.basics.Keywords.DATATYPE.RX;
import static compile.basics.Keywords.USERDEF_OPEN;

public abstract class ParamUtil {
    protected static final int MAX = 1024;
    protected final RangeUtil rangeUtil;
    protected ListTable listTable;
    protected String mainText, bracketText;
    protected String uDefCategory, item;    // null unless text describes a list item
    protected Keywords.DATATYPE listSource; // null unless text describes a list item
    protected int intValues[];
    protected Matcher matcher;

    protected ParamUtil(){
        rangeUtil = new RangeUtil();
    }

    public abstract void findAndSetParam(TreeFactory.TreeNode leaf, String text);

//    public final String getMainText(){
//        return mainText;
//    }
//
//    public final String getBracketText(){
//        return bracketText;
//    }

    public String getItem(){
        return item;
    }

    public int[] getIntValues() {
        return intValues;
    }

    public final String makeUserDef(String text){
        return (text == null)? "" :
                (text.startsWith(USERDEF_OPEN))? text : USERDEF_OPEN + text;
    }
    public final String makeNotUserDef(String text){
        return (text == null)? "" :
                (text.startsWith(USERDEF_OPEN))? text.substring(USERDEF_OPEN.length()) : text;
    }

    /** Expects funName(parameter) to be split: mainText = funName, bracketText = parameter
     * @return rebuilt function call with constant decoded */
    protected final String readConstant(String mainText_, String bracketText_){
        String read = ConstantTable.getInstance().readConstant(bracketText_);
        if(read == null){
            Erlog.get(this).set("Undefined constant", bracketText_);
        }
        return String.format("%s(%s)", mainText_, read);
    }
}
