package compile.scan.ut;

import compile.basics.Keywords;
import static compile.basics.Keywords.HANDLER.RX;
import static compile.basics.Keywords.HANDLER.FX;
import erlog.Erlog;

/**
 *
 * @author newAdmin
 */
public class RxFxUtil {
    protected Keywords.HANDLER toggle;
    
    public RxFxUtil(){
        toggle = FX;
    }
    public final boolean assertToggle(Keywords.HANDLER handler){
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
        toggle = handler;
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
