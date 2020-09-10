package compile.symboltable;

import compile.basics.Keywords;
import erlog.Erlog;

import java.util.ArrayList;

import static compile.basics.Keywords.USERDEF_OPEN;

public class SymbolTest {
    private static SymbolTest instance;

    public static SymbolTest getInstance(){
        return (instance == null)? instance = new SymbolTest() : instance;
    }
    public static void killInstance(){
        instance = null;
    }

    private final ArrayList<String> list;

    private SymbolTest(){
        list = new ArrayList<>();
    }

    public boolean isUserDef(String text){
        return text.startsWith(USERDEF_OPEN) && text.length() > 1;
    }

    public boolean isNew(String identifier){
        if(list.indexOf(identifier) == -1){
            list.add(identifier);
            return true;
        }
        return false;
    }

    public boolean assertNew(String identifier){
        if(this.isNew(identifier)){
            return true;
        }
        Erlog.get(this).set("Identifier already exists", identifier);
        return false;
    }
}
