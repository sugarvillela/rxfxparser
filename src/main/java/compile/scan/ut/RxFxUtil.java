package compile.scan.ut;

import compile.basics.Keywords;
import static compile.basics.Keywords.DATATYPE.RX;
import static compile.basics.Keywords.DATATYPE.FX;
import erlog.Erlog;

/**
 *
 * @author Dave Swanson
 */
public class RxFxUtil {
    protected Keywords.DATATYPE toggle;
    
    public RxFxUtil(){
        toggle = FX;
    }
    public final boolean assertToggle(Keywords.DATATYPE datatype){
        switch(datatype){
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
        toggle = datatype;
        return true;
    }
    public final boolean errOnPop(){
        if(toggle != FX){
            setErr();
            return true;
        }
        return false;
    }
    private void setErr(){
        Erlog.get(this).set(
            String.format(
                "Matched pairs required: %s precedes %s as cause precedes effect", 
                RX.toString(), 
                FX.toString())
        );
    }
    
}
