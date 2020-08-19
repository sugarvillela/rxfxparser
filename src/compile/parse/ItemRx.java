package compile.parse;

import compile.basics.Factory_Node;
//import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.KWORD;


public class ItemRx extends Base_ParseItem{
    protected String low, high;
    
    public ItemRx(Factory_Node.ScanNode node){
        super(node);
    }
    
    @Override
    public void onPush() {
        ((Base_ParseItem)below).addTo(node.h, KWORD.ABOVE, node.h.toString());
    }

    @Override
    public void onPop() {}
}
