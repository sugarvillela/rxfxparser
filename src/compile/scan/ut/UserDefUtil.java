package compile.scan.ut;

import static compile.basics.Keywords.USERDEF_OPEN;
import java.util.ArrayList;

/**
 *
 * @author newAdmin
 */
public class UserDefUtil {
    private final ArrayList<String> list;
    
    public UserDefUtil(){
        list = new ArrayList<>();
    }
    public boolean isUserDef(String text){
        return (text.startsWith(USERDEF_OPEN) && text.length() > 1);
    }
    public boolean addIfNew(String text){
        if(list.indexOf(text) == -1){
            list.add(text);
            return true;
        }
        return false;
    }
    public boolean isNewUserDef(String text){
        return isUserDef(text) && addIfNew(text);
    }
    public boolean isOldUserDef(String text){
        return isUserDef(text) && !addIfNew(text);
    }
}
