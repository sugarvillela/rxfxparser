package compile.parse;

import compile.basics.Factory_Node;
import static compile.basics.Keywords.DATATYPE;
import static compile.basics.Keywords.FIELD;
import static compile.basics.Keywords.DATATYPE.RX;
import static compile.basics.Keywords.DATATYPE.FX;

public class ItemRxFx extends Base_ParseItem{
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
