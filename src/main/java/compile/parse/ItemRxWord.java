package compile.parse;


import compile.basics.Factory_Node;
import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.FIELD;
import static compile.basics.Keywords.HANDLER.RX_WORD;

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
    public void addTo(HANDLER handler, FIELD key, String val) {
        if(RX_WORD != handler){
            er.set("Dev error", handler.toString());
        }
        if(NULL_TEXT.equals(val)){
            er.set("No rxPattern", val);
        }
        node.data = val;
        System.out.printf("Add to RxWord: %s\n", val);
    }

    @Override
    public void setAttrib(HANDLER handler, FIELD key, String val) {
        switch(key){
            case DEF_NAME:
                defName = val;
                break;
            case LO:
                low = val;
                break;
            case HI:
                high = val;
                break;//PROJ_NAME
            default:
                ((Base_ParseItem)below).setAttrib(handler, key, val);
        }
    }
}
