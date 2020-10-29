package compile.parse.factories;


import compile.basics.Factory_Node;
import compile.basics.Factory_Node.ScanNode;
import compile.parse.Base_ParseItem;
import compile.parse.Class_Parser;
import compile.parse.ut.FlatTreeBuilder;
import compile.sublang.factories.TreeFactory;
import compile.sublang.ut.FlatTree;
import erlog.Erlog;

import static compile.basics.Keywords.DATATYPE;

public abstract class Factory_ParseItem {
    public static Base_ParseItem get(ScanNode node){
        //System.out.println("====Base_ParseItem.get()====" + node.h.toString());
        DATATYPE h = node.datatype;
        switch(h){
            case SRCLANG:
                return new ParseItem(node);
            case RX_WORD:
            case FX_WORD:
                return new ItemRxWord(node);
            case TARGLANG_BASE:
                //return new ItemTargLangBase(node);
            case TARGLANG_INSERT:
                //return new ItemTargLangInsert(node);
            case ATTRIB:
                //return new ParseItem(node);
            //========To implement=====================
            case SCOPE:
                //return null;//new Scope(node);
            default:

//                DevErr.get("Factory_cxs").kill("Developer error in get(datatype)", node.toString());
//                return null;
        }
        return new ParseItem(node);
    }
    public static class ParseItem extends Base_ParseItem{
        public ParseItem(ScanNode node) {
            super(node);
            System.out.println("ParseItem Constructor: " + node);
        }
    }

    public static class ItemTargLangBase extends Base_ParseItem{

        public ItemTargLangBase(Factory_Node.ScanNode node){
            super(node);
        }

        @Override
        public void addTo(Factory_Node.ScanNode node) {
            System.out.printf("Add to ItemTargLangBase: %s\n", node.data);
        }

        @Override
        /** This is the bottom of the stack, so it is the final stop for parse-time attributes.
         * Put general enough attributes here */
        public void setAttrib(Factory_Node.ScanNode node) {
            switch(node.field){
                default:
                    Erlog.get(this).set("Unknown keyword", node.field.toString());
            }
        }
    }

    public static class ItemTargLangInsert extends Base_ParseItem{
        public ItemTargLangInsert(Factory_Node.ScanNode node){
            super(node);
        }

        @Override
        public void addTo(Factory_Node.ScanNode node) {
            System.out.printf("Add to ItemTargLangInsert: %s\n", node.data);
        }
    }

    public static class ItemRxFx extends Base_ParseItem{

        public ItemRxFx(Factory_Node.ScanNode node){
            super(node);
        }

        @Override
        public void addTo(Factory_Node.ScanNode node) {

        }

    }

    public static abstract class RxFxWord extends Base_ParseItem {
        protected FlatTreeBuilder builder;
        protected FlatTree flatTree;

        public RxFxWord(ScanNode node) {
            super(node);
        }
    }
    public static class ItemRxWord extends RxFxWord{
        protected String low, high;


        public ItemRxWord(Factory_Node.ScanNode node){
            super(node);
        }

        @Override
        public void onPush() {
            System.out.println("RxFxWord onPush: datatype = " + node.datatype);
            builder = new FlatTreeBuilder(node.datatype, ((Class_Parser)P).getScanNodeSource(), this);
            builder.build();
            flatTree = builder.get();
            flatTree.disp();
            P.pop();
        }

        @Override
        public void onPop() {
            System.out.printf("RxWord onPop: %s {%s-%s}\n", node.data, low, high);
        }

        @Override
        public void addTo(Factory_Node.ScanNode node) {
            System.out.printf("Add to RxWord: %s\n", node.data);
        }

        @Override
        public void setAttrib(Factory_Node.ScanNode node) {
            switch(node.field){
                case LO:
                    low = node.data;
                    break;
                case HI:
                    high = node.data;
                    break;
                default:
                    ((Base_ParseItem)below).setAttrib(node);
            }
        }
    }
}
