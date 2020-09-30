package compile.parse.factories;


import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import erlog.Erlog;
import compile.parse.Base_ParseItem;

import compile.basics.Factory_Node.ScanNode;
import unique.Enum_itr;
import unique.Uq_enumgen;

import static compile.basics.Keywords.*;
import static compile.basics.Keywords.DATATYPE.*;

public abstract class Factory_ParseItem {
    public static Base_ParseItem get(ScanNode node){
        //System.out.println("====Base_ParseItem.get()====" + node.h.toString());
        DATATYPE h = node.h;
        switch(h){
            case LIST_BOOLEAN:
                //return new ItemENUB(node);
            case LIST_DISCRETE:
                //return new ItemENUD(node);
            case SRCLANG:
                //return new ParseItem(node);
            case RXFX:
                //return new ParseItem(node);
            case RX_WORD:
                //return new ItemRxWord(node);
            case RX:
                //return new ParseItem(node);
            case FX:
                //return new ParseItem(node);
            case TARGLANG_BASE:
                //return new ItemTargLangBase(node);
            case TARGLANG_INSERT:
                //return new ItemTargLangInsert(node);
            case COMMENT:
                //return new ParseItem(node);
            case ATTRIB:
                //return new ParseItem(node);
            case USER_DEF_LIST:
                //return new ItemUserDefList(node);
            case USER_DEF_VAR:
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
        public void addTo(DATATYPE datatype, FIELD key, String val) {}

        @Override
        /** This is the bottom of the stack, so it is the final stop for parse-time attributes.
         * Put all general enough attributes here */
        public void setAttrib(DATATYPE datatype, FIELD key, String val) {
            switch(key){
                default:
                    er.set("Unknown keyword", key.toString());
            }
        }
    }

    public static class ItemTargLangInsert extends Base_ParseItem{
        public ItemTargLangInsert(Factory_Node.ScanNode node){
            super(node);
        }

        @Override
        public void onPush() {
            System.out.println("ItemTargLangInsert onPush");
        }

        @Override
        public void onPop() {
            System.out.println("ItemTargLangInsert onPop");
        }

        @Override
        public void addTo(DATATYPE datatype, FIELD key, String val) {
            if(NULL_TEXT.equals(val)){
                er.set("No ItemTargLangInsert", val);
            }
            System.out.printf("Add to ItemTargLangInsert: %s\n", val);
        }
    }
    public static class ItemENUB extends Base_ParseItem{
        protected Enum_itr itr;
        protected int count;

        public ItemENUB(Factory_Node.ScanNode node){
            super(node);
            if(node.h == DATATYPE.LIST_BOOLEAN){// ENUD extends ENUB with different itr
                itr = (Enum_itr)(new Uq_enumgen(CompileInitializer.getInstance().getWRow())).iterator();
            }

            count = 0;
        }
        @Override
        public void addTo(DATATYPE datatype, FIELD key, String val) {
            if(NULL_TEXT.equals(val)){
                er.set("No variable name", val);
            }
            int cur = itr.next();
            System.out.printf("%s = 0x%x;\n", val, cur);
            System.out.println(commons.BIT.str(cur));
        }

        @Override
        public void onPush() {
            System.out.println("ENUB onPush");
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
        @Override
        public void onPop() {
            System.out.println("ENUB onPop");
        }
    }
    public static class ItemENUD extends Base_ParseItem{//extends ItemENUB {
        public ItemENUD(Factory_Node.ScanNode node){
            super(node);
//            itr = (Enum_itr)(
//                    new Uq_enumgen(
//                            CompileInitializer.getInstance().getWRow(),
//                            CompileInitializer.getInstance().getWVal()
//                    )
//            ).iterator();
        }
        @Override
        public void onPush() {
            System.out.println("ENUD onPush");
        }
        @Override
        public void onBeginStep(){
//            if(count > 0){
//                itr.newCol();
//            }
//            count++;
            System.out.println("ENUD onBeginStep: name = " + node.data);
        }
        @Override
        public void onEndStep(){
            System.out.println("ENUD onEndStep: name = " + node.data);
        }
        @Override
        public void onPop() {
            System.out.println("ENUD onPop");
        }
    }
    public static class ItemRxFx extends Base_ParseItem{
        protected DATATYPE toggle;

        public ItemRxFx(Factory_Node.ScanNode node){
            super(node);
        }
        private void setErr(){
            er.set(
                    String.format(
                            "Matched pairs required: %s precedes %s as cause precedes effect",
                            RX.toString(),
                            FX.toString())
            );
        }
        @Override
        public void addTo(DATATYPE datatype, FIELD key, String val) {
//        if(key == FIELD.ABOVE){
//            switch(datatype){
//                case RX:
//                    if(toggle != FX){
//                        setErr();
//                    }
//                    break;
//                case FX:
//                    if(toggle != RX){
//                        setErr();
//                    }
//                    break;
//            }
//        }
//        else{
//            er.set("Err in " + node.h.toString());
//        }
        }

        @Override
        public void onPush() {
            toggle = RX;
        }

        @Override
        public void onPop() {
            if(toggle != FX){
                setErr();
            }
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
        public void addTo(DATATYPE datatype, FIELD key, String val) {
            if(RX_WORD != datatype){
                er.set("Dev error", datatype.toString());
            }
            if(NULL_TEXT.equals(val)){
                er.set("No rxPattern", val);
            }
            node.data = val;
            System.out.printf("Add to RxWord: %s\n", val);
        }

        @Override
        public void setAttrib(DATATYPE datatype, FIELD key, String val) {
            switch(key){
                case DEF_NAME:
                    //defName = val;
                    break;
                case LO:
                    low = val;
                    break;
                case HI:
                    high = val;
                    break;//PROJ_NAME
                default:
                    ((Base_ParseItem)below).setAttrib(datatype, key, val);
            }
        }
    }
}
