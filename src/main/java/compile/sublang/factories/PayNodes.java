package compile.sublang.factories;

import commons.Commons;
import compile.basics.Keywords;
import compile.sublang.ut.FxAccessUtil;
import compile.sublang.ut.FxParamUtil;
import compile.sublang.ut.RxParamUtil;
import erlog.Erlog;
import interfaces.DataNode;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static compile.basics.Keywords.NULL_TEXT;
import static compile.basics.Keywords.DATATYPE.*;

public abstract class PayNodes {
    public static PayNodeFactory getFactory(Keywords.DATATYPE datatype){
        return (RX.equals(datatype))? new RxPayNodeFactory() : new FxPayNodeFactory();
    }

    public static abstract class PayNodeFactory{
        protected static final Tokens_special T = new Tokens_special(",", "'", TK.IGNORESKIP );
        //protected ListTableScanLoader listTable;
        protected ArrayList<DataNode> payNodes;

        public PayNodeFactory(){
            //listTable = ListTableScanLoader.getInstance();
        }

        public ArrayList<DataNode> getPayNodes(){
            return payNodes;
        }

        public abstract void clear();
        public abstract void addPayNode();
        public abstract DataNode payNodeFromScanNode(String scanNodeText);
    }

    public static class RxPayNodeFactory extends PayNodeFactory{
        private RxParamUtil paramUtil;

        public RxPayNodeFactory(){
            paramUtil = RxParamUtil.getInstance();
        }

        @Override
        public void clear(){
            payNodes = new ArrayList<>();
        }

        @Override
        public void addPayNode(){
            payNodes.add( new RxPayNode(
                    paramUtil.getCallerType(),
                    paramUtil.getParamType(),
                    paramUtil.getOutType(),
                    paramUtil.getFunType(),
                    paramUtil.getItem(),
                    paramUtil.getUDefCategory(),
                    paramUtil.getListSource(),
                    paramUtil.getIntValues()
            ));
        }

        @Override
        public DataNode payNodeFromScanNode(String scanNodeText){
            String[] tok = T.toArr(scanNodeText);
            // Commons.disp(tok, "RxPayNodeFactory");
            if(tok.length != RxPayNode.NUM_FIELDS){
                Erlog.get(this).set("Bad scan node text size" + tok.length, scanNodeText);
                return null;
            }
            return new RxPayNode(
                    Keywords.PRIM.fromString(tok[0]),
                    Keywords.RX_PAR.fromString(tok[1]),
                    Keywords.PRIM.fromString(tok[2]),
                    (NULL_TEXT.equals(tok[3]))? null : Keywords.RX_FUN.fromString(tok[3]),
                    (NULL_TEXT.equals(tok[4]))? null : tok[4],
                    (NULL_TEXT.equals(tok[5]))? null : tok[5],
                    (NULL_TEXT.equals(tok[6]))? null : Keywords.DATATYPE.fromString(tok[6]),
                    Commons.undoNullSafe_intArray(tok[7])
            );
        }
    }

    public static class FxPayNodeFactory extends PayNodeFactory{

        private FxParamUtil funUtil;
        private FxAccessUtil accessUtil;
        boolean state;

        public FxPayNodeFactory(){
            funUtil = FxParamUtil.getInstance();
            accessUtil = FxAccessUtil.getInstance();
        }

        @Override
        public void clear(){
            payNodes = new ArrayList<>();
            state = false;
        }

        @Override
        public void addPayNode() {
            if(!state){
                payNodes.add( new FxPayNode(
                        accessUtil.getParamType(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        accessUtil.getIntValues(),
                        accessUtil.getAccessMod()
                ));
            }
            else{
                payNodes.add( new FxPayNode(
                        null,
                        funUtil.getParamType(),
                        funUtil.getFunType(),
                        funUtil.getItems(),
                        funUtil.getCategories(),
                        funUtil.getListSources(),
                        funUtil.getIntValues(),
                        0
                ));
            }
            state = !state;
        }

        @Override
        public DataNode payNodeFromScanNode(String scanNodeText) {
            String[] tok = T.toArr(scanNodeText);
            //Commons.disp(tok, "FxPayNodeFactory");
            if(tok.length != FxPayNode.NUM_FIELDS){
                Erlog.get(this).set("Bad scan node text size: " + tok.length, scanNodeText);
                return null;
            }
            return new FxPayNode(
                    Keywords.FX_ACCESS.fromString(tok[0]),
                    Keywords.FX_PAR.fromString(tok[1]),
                    Keywords.FX_FUN.fromString(tok[2]),
                    Commons.undoNullSafe_stringArray(tok[3]),
                    Commons.undoNullSafe_stringArray(tok[4]),
                    Keywords.DATATYPE.fromStrings(tok[5]),
                    Commons.undoNullSafe_intArray(tok[6]),
                    Integer.parseInt(tok[7])
            );
        }
    }

    public static class RxPayNode extends DataNode {
        public static final int NUM_FIELDS = 8;

        public final Keywords.PRIM callerType;
        public final Keywords.RX_PAR paramType;
        public final Keywords.PRIM outType;
        public final Keywords.RX_FUN funType;
        public final String item;
        public final String uDefCategory;
        public final Keywords.DATATYPE listSource;
        public final int[] values;

        public RxPayNode(Keywords.PRIM callerType, Keywords.RX_PAR paramType, Keywords.PRIM outType, Keywords.RX_FUN funType, String item, String uDefCategory, Keywords.DATATYPE listSource, int[] values) {
            this.callerType = callerType;
            this.paramType = paramType;
            this.outType = outType;
            this.funType = funType;
            this.item = item;
            this.uDefCategory = uDefCategory;
            this.listSource = listSource;
            this.values = values;
        }

        @Override
        public String readableContent(){
            ArrayList<String> out = new ArrayList<>();
            if(callerType != null)  {out.add("callerType: " +   callerType.toString());}
            if(paramType != null)   {out.add("paramType: " +    paramType.toString());}
            if(outType != null)     {out.add("outType: " +      outType.toString());}
            if(funType != null)     {out.add("funType: " +      funType.toString());}
            if(item != null)        {out.add("item: " +         item);}
            if(uDefCategory != null){out.add("uDefCategory: " + uDefCategory);}
            if(listSource != null)  {out.add("listSource: " +   listSource.toString());}
            if(values != null)      {out.add("values: " +       Commons.nullSafe(values));}
            return String.join(", ", out);
        }

        @Override
        public String toString(){
            return String.format(
                    "%s,%s,%s,%s,%s,%s,%s,%s",
                    Commons.nullSafe(callerType),
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(outType),
                    Commons.nullSafe(funType),
                    Commons.nullSafe(item),
                    Commons.nullSafe(uDefCategory),
                    Commons.nullSafe(listSource),
                    Commons.nullSafe(values)
            );
        }
    }
    public static class FxPayNode extends DataNode {
        public static final int NUM_FIELDS = 8;

        public final Keywords.FX_ACCESS accessType;
        public final Keywords.FX_PAR paramType;
        public final Keywords.FX_FUN funType;
        public final String[] items;
        public final String[] uDefCategories;
        public final Keywords.DATATYPE[] listSources;
        public final int[] values;
        public final int accessMod;

        public FxPayNode(Keywords.FX_ACCESS accessType, Keywords.FX_PAR paramType, Keywords.FX_FUN funType, String[] items, String[] uDefCategories, Keywords.DATATYPE[] listSources, int[] values, int accessMod) {
            this.accessType = accessType;
            this.paramType = paramType;
            this.funType = funType;
            this.items =  items;
            this.uDefCategories = uDefCategories;
            this.listSources = listSources;
            this.values = values;
            this.accessMod = accessMod;
        }

        @Override
        public String readableContent(){
            ArrayList<String> out = new ArrayList<>();
            if(accessType != null)      {out.add("accessType: " +    accessType.toString());}
            if(paramType != null)      {out.add("paramType: " +      paramType.toString());}
            if(funType != null)        {out.add("funType: " +        funType.toString());}
            if(items != null)          {out.add("items: " +          String.join( ", ", items));}
            if(uDefCategories != null) {out.add("uDefCategories: " + String.join( ", ", uDefCategories));}
            if(listSources != null)    {out.add("listSources: " +    Commons.join(", ", listSources));}
            if(values != null)         {out.add("values: " +         Commons.join(", ", values));}
            if(accessMod != 0)         {out.add("accessMod: " +      accessMod);}
            return String.join(", ", out);
        }

        @Override
        public String toString(){
            return String.format(
                    "%s,%s,%s,%s,%s,%s,%s,%d",
                    Commons.nullSafe(accessType),
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(funType),
                    Commons.nullSafe(items),
                    Commons.nullSafe(uDefCategories),
                    Commons.nullSafe(listSources),
                    Commons.nullSafe(values),
                    accessMod
            );
        }

    }
}
