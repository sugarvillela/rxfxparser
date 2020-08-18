package compile.parse.factories;

import com.sun.istack.internal.NotNull;
import erlog.Erlog;
import compile.parse.Base_ParseItem;
import static compile.basics.Keywords.KWORD;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.HANDLER.ATTRIB;
import static compile.basics.Keywords.HANDLER.ENUB;
import static compile.basics.Keywords.HANDLER.ENUD;
import static compile.basics.Keywords.HANDLER.FX;
import static compile.basics.Keywords.HANDLER.RX;
import static compile.basics.Keywords.HANDLER.USER_DEF_LIST;
import static compile.basics.Keywords.HANDLER.USER_DEF_VAR;
import compile.basics.Factory_Node.ScanNode;

import compile.parse.ItemENUB;
import compile.parse.ItemENUD;
import compile.parse.ItemUserDefList;

public class Factory_ParseItem {
    public static Base_ParseItem get(@NotNull ScanNode node){
        //System.out.println("====Base_ParseItem.get()====" + node.h.toString());
        HANDLER h = node.h;
        switch(h){
            case ENUB:
                return new ItemENUB(h);
            case ENUD:
                return new ItemENUD(h);
            case SRCLANG:
                return new ParseItem(h);
            case RXFX:
                return new ParseItem(h);
            case RX_WORD:
                return new ParseItem(h);
            case RX:
                return new ParseItem(h);
            case FX:
                return new ParseItem(h);
            case TARGLANG_BASE:
                return new ParseItem(h);
            case TARGLANG_INSERT:
                return new ParseItem(h);
            case COMMENT:
                return new ParseItem(h);
            case ATTRIB:
                return new ParseItem(h);
            case USER_DEF_LIST:
                return new ItemUserDefList(h, node.data);
            case USER_DEF_VAR:
                return new ParseItem(h); 
            //========To implement=====================
            case SCOPE:
                return null;//new Scope(h);
            default:
                Erlog.get("Factory_cxs").set("Developer error in get(handler)", node.toString());
                return null;
        }
    }
    public static class ParseItem extends Base_ParseItem{

        public ParseItem(HANDLER h){
            this.h = h;
            this.debugName = h.toString();
        }
        @Override
        public void onPush() {}
        
        @Override
        public void addTo(HANDLER handler, Object object){
        }

        @Override
        public void setAttrib(KWORD key, String val){}
        
        @Override
        public void onPop() {}
    }
}
