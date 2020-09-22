package compile.rx.factories;

import commons.Commons;
import compile.basics.Keywords;
import compile.rx.ut.RxParamUtil;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;

import java.util.ArrayList;
import java.util.regex.Matcher;

import static compile.basics.Keywords.DATATYPE.RAW_TEXT;
import static compile.basics.Keywords.PAR.CATEGORY_ITEM;
import static compile.basics.Keywords.PAR.TEST_TEXT;
import static compile.basics.Keywords.PRIM.NULL;
import static compile.basics.Keywords.USERDEF_OPEN;

public class Factory_PayNode {
    private final int CHECK_ALL = 0, CHECK_LIST_SOURCE = 1, CHECK_CATEGORY = 2, CHECK_TEXT = 3;
    private RxParamUtil paramUtil;
    private ListTable listTable;
    private ArrayList<Factory_PayNode.PayNode> payNodes;
    private Keywords.DATATYPE currListSource;
    private String currCategory;
    private int state;
    //public static String bodyText;

    public Factory_PayNode(){
        paramUtil = RxParamUtil.getInstance();
        listTable = ListTable.getInstance();
        payNodes = new ArrayList<>();
        currListSource = null;
        currCategory = null;
        state = CHECK_ALL;
    }
    public void finishNode(PayNode newNode){
        payNodes.add(newNode);
        currListSource = null;
        currCategory = null;
        state = CHECK_ALL;
    }
    public ArrayList<Factory_PayNode.PayNode> getPayNodes(){
        return payNodes;
    }
    public void clear(){
        payNodes = new ArrayList<>();
    }

    public void add(String text){
        Keywords.PAR paramType = paramUtil.getParamType();
        PayNode node;
        switch(paramType.datatype){
            case FUN:
                node = new PayNodeFun();
                node.funType = paramUtil.getFunType();
                node.paramType = paramType;
                node.caller = node.funType.caller;
                node.bodyText = paramUtil.getMainText();
                node.paramText = paramUtil.getBracketText();
                node.outType = node.funType.outType;
                finishNode(node);
                break;
            case NUM_TEXT:
                node = new PayNodeNumTest();
                node.paramType = paramType;
                node.bodyText = text;
                node.value = Integer.parseInt(text);
                finishNode(node);
                break;
            case BOOL_TEXT:
                node = new PayNodeBoolTest();
                node.paramType = paramType;
                node.bodyText = text;
                node.value = Boolean.parseBoolean(text)? 1 : 0;
                finishNode(node);
                break;
            case RAW_TEXT:
                node = new PayNodeRawText();
                node.paramType = TEST_TEXT;
                node.bodyText = text;
                node.outType = RAW_TEXT.outType;
                finishNode(node);
                break;
            case LIST:
                String category = paramUtil.makeNotUserDef(paramUtil.getMainText());
                String item = paramUtil.getBracketText();
                if(!category.equals(listTable.getCategory(item))){
                    Erlog.get(this).set(item + " not an item in " + category, text);
                }
                Keywords.DATATYPE listSource = listTable.getDataType(category);
                node = new PayNodeListItem();
                node.paramType = CATEGORY_ITEM;
                node.outType = listSource.outType;
                node.listSource = listSource;
                node.uDefCategory = category;
                node.bodyText = item;
                finishNode(node);
        }
    }

    public static abstract class PayNode{
        public Keywords.PRIM caller;
        public Keywords.PAR paramType;
        public Keywords.PRIM outType;
        public Keywords.RX_FUN funType;
        public String paramText;
        public Keywords.DATATYPE listSource;
        public String uDefCategory;
        public String bodyText;
        public int value;
    }
    public static class PayNodeFun extends PayNode{
        @Override
        public String toString(){
            return String.format(
                "paramType=%s, outType=%s, funType=%s, paramText=%s",
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(outType),
                    Commons.nullSafe(funType),
                    Commons.nullSafe(paramText)
            );
        }
    }
    public static class PayNodeListItem extends PayNode{
        public PayNodeListItem(){
            caller = NULL;
        }
        @Override
        public String toString(){
            return String.format(
                    "paramType=%s, outType=%s, listSource=%s, category=%s, bodyText=%s",
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(outType),
                    Commons.nullSafe(listSource),
                    Commons.nullSafe(uDefCategory),
                    Commons.nullSafe(bodyText)
            );
        }
    }
    public static class PayNodeRawText extends PayNode{
        public PayNodeRawText(){
            caller = NULL;
            outType = Keywords.PRIM.STRING;
        }
        @Override
        public String toString(){
            return String.format(
                    "paramType=%s, outType=%s, bodyText=%s",
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(outType),
                    Commons.nullSafe(bodyText)
            );
        }
    }
    public static class PayNodeBoolTest extends PayNode{
        public PayNodeBoolTest(){
            caller = NULL;
            outType = Keywords.PRIM.BOOLEAN;
        }

        @Override
        public String toString(){
            return String.format(
                    "paramType=%s, outType=%s, bodyText=%s",
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(outType),
                    Commons.nullSafe(bodyText)
            );
        }
    }
    public static class PayNodeNumTest extends PayNode{
        public PayNodeNumTest(){
            caller = NULL;
            outType = Keywords.PRIM.NUMBER;
        }
        @Override
        public String toString(){
            return String.format(
                    "paramType=%s, outType=%s, bodyText=%s",
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(outType),
                    Commons.nullSafe(bodyText)
            );
        }
    }
}
