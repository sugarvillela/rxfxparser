package compile.rx.factories;

import commons.Commons;
import compile.basics.Keywords;
import compile.rx.ut.RxParamUtil;
import compile.symboltable.ListTable;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.DATATYPE.NONE;
import static compile.basics.Keywords.DATATYPE.RAW_TEXT;
import static compile.basics.Keywords.PAR.*;
import static compile.basics.Keywords.PRIM.NULL;

public class Factory_PayNode {
    private static final Tokens_special T = new Tokens_special(",", "'", TK.IGNORESKIP );
    private static final int NUM_PAYNODE_FIELDS = 9;
    private RxParamUtil paramUtil;
    private ListTable listTable;
    private ArrayList<Factory_PayNode.PayNode> payNodes;

    public Factory_PayNode(){
        paramUtil = RxParamUtil.getInstance();
        listTable = ListTable.getInstance();
        payNodes = new ArrayList<>();
    }
    public void finishNode(PayNode newNode){
        payNodes.add(newNode);
    }
    public ArrayList<Factory_PayNode.PayNode> getPayNodes(){
        return payNodes;
    }
    public void clear(){
        payNodes = new ArrayList<>();
    }

    public void add(String text){
        Keywords.PAR paramType = paramUtil.getParamType();
        PayNode node = new PayNode();
        switch(paramType.datatype){
            case FUN:
                Keywords.RX_FUN funType = paramUtil.getFunType();

                node.callerType = funType.caller;
                node.paramType = paramType;
                node.outType = funType.outType;
                node.funType = funType;
                node.mainText = paramUtil.getMainText();
                node.bracketText = paramUtil.getBracketText();
                node.bracketText = null;
                node.uDefCategory = null;
                node.listSource = null;
                node.value = 1;
                break;
            case NUM_TEXT:
                node.callerType = NULL;
                node.paramType = paramType;
                node.outType = Keywords.PRIM.NUMBER;
                node.funType = null;
                node.mainText = text;
                node.bracketText = null;
                node.uDefCategory = null;
                node.listSource = null;
                node.value = Integer.parseInt(text);
                break;
            case BOOL_TEXT:
                node.callerType = NULL;
                node.paramType = paramType;
                node.outType = Keywords.PRIM.BOOLEAN;
                node.funType = null;
                node.mainText = text;
                node.bracketText = null;
                node.uDefCategory = null;
                node.listSource = null;
                node.value = Boolean.parseBoolean(text)? 1 : 0;
                break;
            case RAW_TEXT:
                node.callerType = NULL;
                node.paramType = TEST_TEXT;
                node.outType = RAW_TEXT.outType;
                node.funType = null;
                node.mainText = text;
                node.bracketText = null;
                node.uDefCategory = null;
                node.listSource = null;
                node.value = 1;
                break;
            case LIST:
                String category = paramUtil.makeNotUserDef(paramUtil.getMainText());
                String item = paramUtil.getBracketText();
                if(!category.equals(listTable.getCategory(item))){
                    Erlog.get(this).set(item + " not an item in " + category, text);
                }
                Keywords.DATATYPE listSource = listTable.getDataType(category);

                node.callerType = NULL;
                node.paramType = CATEGORY_ITEM;
                node.outType = listSource.outType;
                node.funType = null;
                node.mainText = item;
                node.bracketText = null;
                node.uDefCategory = category;
                node.listSource = listSource;
                node.value = 1;
                break;
            default:
                Erlog.get(this).set("Unknown parameter type", text);
        }
        finishNode(node);
    }

    public PayNode payNodeFromScanNode(String scanNodeText){
        String[] tok = T.toArr(scanNodeText);
        if(tok.length != NUM_PAYNODE_FIELDS){
            Erlog.get(this).set("Bad scan node text size", scanNodeText);
            return null;
        }
        PayNode node = new PayNode();
        node.callerType =   Keywords.PRIM.fromString(tok[0]);
        node.paramType =    Keywords.PAR.fromString(tok[1]);
        node.outType =      Keywords.PRIM.fromString(tok[2]);
        node.funType =      (NULL_TEXT.equals(tok[3]))? null : Keywords.RX_FUN.fromString(tok[3]);
        node.mainText =     (NULL_TEXT.equals(tok[4]))? null : tok[4];
        node.bracketText =  (NULL_TEXT.equals(tok[5]))? null : tok[5];;
        node.uDefCategory = (NULL_TEXT.equals(tok[6]))? null : tok[6];;
        node.listSource =   (NULL_TEXT.equals(tok[7]))? null : Keywords.DATATYPE.fromString(tok[7]);;
        node.value =        Integer.parseInt(tok[8]);
        return  node;
    }
    public static class PayNode{
        public Keywords.PRIM callerType;
        public Keywords.PAR paramType;
        public Keywords.PRIM outType;
        public Keywords.RX_FUN funType;
        public String mainText;
        public String bracketText;
        public String uDefCategory;
        public Keywords.DATATYPE listSource;
        public int value;

        public String readableContent(){
            ArrayList<String> out = new ArrayList<>();
            if(callerType != null)  {out.add("callerType: " +   callerType.toString());}
            if(paramType != null)   {out.add("paramType: " +    paramType.toString());}
            if(outType != null)     {out.add("outType: " +      outType.toString());}
            if(funType != null)     {out.add("funType: " +      funType.toString());}
            if(mainText != null)    {out.add("mainText: " +     mainText);}
            if(bracketText != null) {out.add("bracketText: " +  bracketText);}
            if(uDefCategory != null){out.add("uDefCategory: " + uDefCategory);}
            if(listSource != null)  {out.add("listSource: " +   listSource.toString());}
            if(paramType != null){
                switch(paramType.datatype){
                    case BOOL_TEST:
                        out.add("value: " + ((value == 1)? "TRUE" : "FALSE"));
                    case NUM_TEXT:
                        out.add("value: " + value);
                }
            }
            return String.join(", ", out);
        }

        @Override
        public String toString(){
            return String.format(
                    "%s,%s,%s,%s,%s,%s,%s,%s,%d",
                    Commons.nullSafe(callerType),
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(outType),
                    Commons.nullSafe(funType),
                    Commons.nullSafe(mainText),
                    Commons.nullSafe(bracketText),
                    Commons.nullSafe(uDefCategory),
                    Commons.nullSafe(listSource),
                    value
            );
        }
    }
}
