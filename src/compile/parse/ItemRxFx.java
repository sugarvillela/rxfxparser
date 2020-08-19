package compile.parse;

import compile.basics.Factory_Node;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.KWORD;
import static compile.basics.Keywords.HANDLER.RX;
import static compile.basics.Keywords.HANDLER.FX;

public class ItemRxFx extends Base_ParseItem{
    protected HANDLER toggle;
    protected int count;
    
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
    public void addTo(HANDLER handler, KWORD key, String val) {
        count++;
        if(key == KWORD.ABOVE){
            switch(handler){
                case RX:
                    if(toggle != FX){
                        setErr();
                    }
                    break;
                case FX:
                    if(toggle != RX){
                        setErr();
                    }
                    break;
            }
        }
        else{
            er.set("Err in " + node.h.toString());
        }
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
