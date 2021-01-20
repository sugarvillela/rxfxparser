package compile.scan.ut;

import langdef.Keywords;
import erlog.Erlog;

import static langdef.Keywords.DATATYPE.*;

public class IfElseUtil {
    protected Keywords.DATATYPE toggle;

    public IfElseUtil(){
        toggle = ELSE;
    }
    public final boolean assertToggle(Keywords.DATATYPE datatype){
        if(ELSE.equals(datatype) && !IF.equals(toggle)){
            Erlog.get(this).set("ELSE without IF");
            return false;
        }
        toggle = datatype;
        return true;
    }
}
