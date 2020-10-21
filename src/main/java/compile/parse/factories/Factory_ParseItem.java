package compile.parse.factories;


import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import erlog.Erlog;
import compile.parse.Base_ParseItem;

import compile.basics.Factory_Node.ScanNode;
import unique.Enum_itr;
import unique.Uq_enumgen;

import static compile.basics.Keywords.*;

public abstract class Factory_ParseItem {
    public static Base_ParseItem get(ScanNode node){
        //System.out.println("====Base_ParseItem.get()====" + node.h.toString());
        DATATYPE h = node.datatype;
        switch(h){
            case LIST_BOOLEAN:
                return new ItemListBoolean(node);
            case LIST_DISCRETE:
                return new ItemListDiscrete(node);
            case SRCLANG:
                //return new ParseItem(node);
            case RX_WORD:
                //return new ItemRxWord(node);
            case RX:
                //return new ParseItem(node);
            case FX:
                //return new ParseItem(node);
            case TARGLANG_BASE:
                return new ItemTargLangBase(node);
            case TARGLANG_INSERT:
                return new ItemTargLangInsert(node);
            case ATTRIB:
                //return new ParseItem(node);
            //========To implement=====================
            case SCOPE:
                //return null;//new Scope(node);
            default:
                Erlog.get("Factory_cxs").set("Developer error in get(datatype)", node.toString());
                return null;
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
    public static class ItemListBoolean extends Base_ParseItem{
        private Enum_itr itr;
        private int count;

        public ItemListBoolean(Factory_Node.ScanNode node){
            super(node);
            itr = (Enum_itr)(new Uq_enumgen(CompileInitializer.getInstance().getWRow())).iterator();
            count = 0;
        }
        @Override
        public void addTo(Factory_Node.ScanNode node) {
            int cur = itr.next();
            System.out.printf("%s = 0x%x;\n", node.data, cur);
            System.out.println(commons.BIT.str(cur));
        }

        @Override
        public void onBeginStep(){
            if(count > 0){
                itr.newRow();
            }
            count++;
            System.out.println("ENUB onBeginStep: name = " + node.data);
        }
        @Override
        public void onEndStep(){
            System.out.println("ENUB onEndStep: name = " + node.data);
        }
    }
    public static class ItemListDiscrete extends Base_ParseItem{//extends ItemENUB {
        private Enum_itr itr;
        private int count;
        public ItemListDiscrete(Factory_Node.ScanNode node){
            super(node);
            itr = (Enum_itr)(
                    new Uq_enumgen(
                            CompileInitializer.getInstance().getWRow(),
                            CompileInitializer.getInstance().getWVal()
                    )
            ).iterator();
        }
        @Override
        public void onBeginStep(){
            if(count > 0){
                itr.newCol();
            }
            count++;
            System.out.println("ItemListDiscrete onBeginStep: name = " + node.data);
        }
        @Override
        public void onEndStep(){
            System.out.println("ItemListDiscrete onEndStep: name = " + node.data);
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

    public static class ItemRx extends Base_ParseItem{
        protected String low, high;

        public ItemRx(Factory_Node.ScanNode node){
            super(node);
        }

        @Override
        public void onPush() {
            //((Base_ParseItem)below).addTo(node.h, FIELD.ABOVE, node.h.toString());
        }

        @Override
        public void onPop() {}
    }

    public class ItemRxWord extends Base_ParseItem{
        protected String low, high;

        public ItemRxWord(Factory_Node.ScanNode node){
            super(node);
        }

        @Override
        public void onPush() {
            System.out.println("RxWord onPush");
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
                    break;//PROJ_NAME
                default:
                    ((Base_ParseItem)below).setAttrib(node);
            }
        }
    }
}
