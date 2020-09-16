package compile.scan.ut;

import compile.basics.Keywords;
import erlog.Erlog;

import static compile.basics.Keywords.DATATYPE.*;

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
