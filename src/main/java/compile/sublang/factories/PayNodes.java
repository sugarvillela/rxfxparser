package compile.sublang.factories;

import commons.Commons;
import compile.basics.Keywords;
import compile.sublang.ut.FxParamUtil;
import compile.sublang.ut.ParamUtil;
import compile.sublang.ut.RxParamUtil;
import compile.symboltable.ListTable;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.DATATYPE.RAW_TEXT;
import static compile.basics.Keywords.DATATYPE.RX;
import static compile.basics.Keywords.PAR.*;
import static compile.basics.Keywords.PRIM.NULL;

public abstract class PayNodes {
    public static PayNodeFactory getFactory(Keywords.DATATYPE datatype){
        return (RX.equals(datatype))? new RxPayNodeFactory() : new FxPayNodeFactory();
    }

    public static abstract class PayNodeFactory{
        protected static final Tokens_special T = new Tokens_special(",", "'", TK.IGNORESKIP );
        protected ListTable listTable;
        protected ArrayList<PayNode> payNodes;

        public PayNodeFactory(){
            listTable = ListTable.getInstance();
            payNodes = new ArrayList<>();
        }

        public ArrayList<PayNode> getPayNodes(){
            return payNodes;
        }
        public void clear(){
            payNodes = new ArrayList<>();
        }

        public abstract void addPayNode(String text);
        public abstract PayNode payNodeFromScanNode(String scanNodeText);
    }
    public static class RxPayNodeFactory extends PayNodeFactory{

        protected static final int NUM_FIELDS = 9;
        protected RxParamUtil paramUtil;

        public RxPayNodeFactory(){
            paramUtil = (RxParamUtil)ParamUtil.getParamUtil(RX);
        }

        @Override
        public void addPayNode(String text){
            Keywords.PAR paramType = paramUtil.getParamType();
            switch(paramType.datatype){
                case FUN:
                    Keywords.RX_FUN funType = paramUtil.getFunType();
                    payNodes.add( new RxPayNode(
                            funType.caller,
                            paramType,
                            funType.outType,
                            funType,
                            paramUtil.getMainText(),
                            paramUtil.getBracketText(),
                            null,
                            null,
                            1
                    ));
                    break;
                case NUM_TEXT:
                    payNodes.add( new RxPayNode(
                            NULL,
                            paramType,
                            Keywords.PRIM.NUMBER,
                            null,
                            text,
                            null,
                            null,
                            null,
                            Integer.parseInt(text)
                    ));
                    break;
                case BOOL_TEXT:
                    payNodes.add( new RxPayNode(
                            NULL,
                            paramType,
                            Keywords.PRIM.BOOLEAN,
                            null,
                            text,
                            null,
                            null,
                            null,
                            Boolean.parseBoolean(text)? 1 : 0
                    ));
                    break;
                case RAW_TEXT:
                    payNodes.add( new RxPayNode(
                            NULL,
                            TEST_TEXT,
                            RAW_TEXT.outType,
                            null,
                            text,
                            null,
                            null,
                            null,
                            1
                    ));
                    break;
                case LIST:
                    String category = paramUtil.makeNotUserDef(paramUtil.getMainText());
                    String item = paramUtil.getBracketText();
                    if(!category.equals(listTable.getCategory(item))){
                        Erlog.get(this).set(item + " not an item in " + category, text);
                    }
                    Keywords.DATATYPE listSource = listTable.getDataType(category);
                    payNodes.add( new RxPayNode(
                            NULL,
                            CATEGORY_ITEM,
                            listSource.outType,
                            null,
                            item,
                            null,
                            category,
                            listSource,
                            1
                    ));
                    break;
                default:
                    Erlog.get(this).set("Unknown parameter type", text);
            }
        }

        @Override
        public PayNode payNodeFromScanNode(String scanNodeText){
            String[] tok = T.toArr(scanNodeText);
            if(tok.length != NUM_FIELDS){
                Erlog.get(this).set("Bad scan node text size", scanNodeText);
                return null;
            }
            return new RxPayNode(
                    Keywords.PRIM.fromString(tok[0]),
                    Keywords.PAR.fromString(tok[1]),
                    Keywords.PRIM.fromString(tok[2]),
                    (NULL_TEXT.equals(tok[3]))? null : Keywords.RX_FUN.fromString(tok[3]),
                    (NULL_TEXT.equals(tok[4]))? null : tok[4],
                    (NULL_TEXT.equals(tok[5]))? null : tok[5],
                    (NULL_TEXT.equals(tok[6]))? null : tok[6],
                    (NULL_TEXT.equals(tok[7]))? null : Keywords.DATATYPE.fromString(tok[7]),
                    Integer.parseInt(tok[8])
            );
        }
    }
    public static class FxPayNodeFactory extends PayNodeFactory{
        protected static final int NUM_FIELDS = 9;
        protected FxParamUtil paramUtil;

        public FxPayNodeFactory(){
            paramUtil = FxParamUtil.getInstance();
        }
        @Override
        public void addPayNode(String text) {

        }

        @Override
        public PayNode payNodeFromScanNode(String scanNodeText) {
            String[] tok = T.toArr(scanNodeText);
            if(tok.length != NUM_FIELDS){
                Erlog.get(this).set("Bad scan node text size", scanNodeText);
                return null;
            }
            return null;
        }
    }


    public interface PayNode {
        String readableContent();
    }
    public static class RxPayNode implements PayNode {
        public final Keywords.PRIM callerType;
        public final Keywords.PAR paramType;
        public final Keywords.PRIM outType;
        public final Keywords.RX_FUN funType;
        public final String mainText;
        public final String bracketText;
        public final String uDefCategory;
        public final Keywords.DATATYPE listSource;
        public final int value;

        public RxPayNode(Keywords.PRIM callerType, Keywords.PAR paramType, Keywords.PRIM outType, Keywords.RX_FUN funType, String mainText, String bracketText, String uDefCategory, Keywords.DATATYPE listSource, int value) {
            this.callerType = callerType;
            this.paramType = paramType;
            this.outType = outType;
            this.funType = funType;
            this.mainText = mainText;
            this.bracketText = bracketText;
            this.uDefCategory = uDefCategory;
            this.listSource = listSource;
            this.value = value;
        }

        @Override
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
