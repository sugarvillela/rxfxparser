package compile.scan.ut;

import compile.basics.Keywords;
import erlog.Erlog;

import static compile.basics.Keywords.HANDLER.*;

public class IfElseUtil {
    protected Keywords.HANDLER toggle;

    public IfElseUtil(){
        toggle = ELSE;
    }
    public final boolean assertToggle(Keywords.HANDLER handler){
        if(ELSE.equals(handler) && !IF.equals(toggle)){
            Erlog.get(this).set("ELSE without IF");
            return false;
        }
        toggle = handler;
        return true;
    }
}
