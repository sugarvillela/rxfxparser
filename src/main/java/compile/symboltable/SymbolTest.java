package compile.symboltable;

import erlog.Erlog;

import java.util.ArrayList;

import static langdef.Keywords.*;

public class SymbolTest {
    public static final int NONE = 0, UDEF = 1, UDEF_OPEN = 2,
            CL = 3, CL_TEXT = 4, TEXT_CL = 5,
            OPEN = 6, OP_TEXT = 7, TEXT_OP = 8;
    private static SymbolTest instance;

    public static SymbolTest init(){
        return (instance == null)? instance = new SymbolTest() : instance;
    }

    private final ArrayList<String> list;

    private SymbolTest(){
        list = new ArrayList<>();
    }

    public boolean isUserDef(String text){
        return text.startsWith(USERDEF_OPEN) && text.length() > 1;
    }

    public String stripUserDef(String text){
        return text.substring(USERDEF_OPEN.length());
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
