package compile.parse;


import compile.basics.Factory_Node;
import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.DATATYPE;
import static compile.basics.Keywords.FIELD;
import static compile.basics.Keywords.DATATYPE.RX_WORD;

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
                defName = val;
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
