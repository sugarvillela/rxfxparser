package compile.parse.factories;


import compile.basics.Factory_Node;
import erlog.Erlog;
import compile.parse.Base_ParseItem;
import static compile.basics.Keywords.FIELD;
import static compile.basics.Keywords.HANDLER;

import compile.basics.Factory_Node.ScanNode;

import compile.parse.ItemENUB;
import compile.parse.ItemENUD;
import compile.parse.ItemRxWord;
import compile.parse.ItemTargLang;
import compile.parse.ItemTargLangInsert;
import compile.parse.ItemUserDefList;

public class Factory_ParseItem {
    public static Base_ParseItem get(ScanNode node){
        //System.out.println("====Base_ParseItem.get()====" + node.h.toString());
        HANDLER h = node.h;
        switch(h){
            case ENUB:
                return new ItemENUB(node);
            case ENUD:
                return new ItemENUD(node);
            case SRCLANG:
                return new ParseItem(node);
            case RXFX:
                return new ParseItem(node);
            case RX_WORD:
                return new ItemRxWord(node);
            case RX:
                return new ParseItem(node);
            case FX:
                return new ParseItem(node);
            case TARGLANG_BASE:
                return new ItemTargLang(node);
            case TARGLANG_INSERT:
                return new ItemTargLangInsert(node);
            case COMMENT:
                return new ParseItem(node);
            case ATTRIB:
                return new ParseItem(node);
            case USER_DEF_LIST:
                return new ItemUserDefList(node);
            case USER_DEF_VAR:
                return new ParseItem(node); 
            //========To implement=====================
            case SCOPE:
                return null;//new Scope(node);
            default:
                Erlog.get("Factory_cxs").set("Developer error in get(handler)", node.toString());
                return null;
        }
    }
    public static class ParseItem extends Base_ParseItem{

        public ParseItem(Factory_Node.ScanNode node){
            super(node);
        }
        @Override
        public void onPush() {}
        
        @Override
        public void addTo(HANDLER handler, FIELD key, String val){
        }

//        @Override
//        public void setAttrib(FIELD key, String val){}
        
        @Override
        public void onPop() {}
    }
}
