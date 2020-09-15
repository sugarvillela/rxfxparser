package compile.symboltable;

import compile.basics.Keywords;
import erlog.Erlog;

import java.util.ArrayList;

import static compile.basics.Keywords.*;

public class SymbolTest {
    public static final int NONE = 0, UDEF = 1, UDEF_OPEN = 2,
            CL = 3, CL_TEXT = 4, TEXT_CL = 5,
            OPEN = 6, OP_TEXT = 7, TEXT_OP = 8;
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

    //unused
    public int classifyText(String text){
        if(isUserDef(text)){
            return (text.endsWith(ITEM_OPEN))? UDEF_OPEN : UDEF;
        }
        if(text.startsWith(ITEM_CLOSE)){
            return (text.length() == ITEM_CLOSE.length())? CL : CL_TEXT;
        }
        if(text.endsWith(ITEM_CLOSE)){
            return (text.length() == ITEM_CLOSE.length())? CL : TEXT_CL;
        }
        if(text.startsWith(ITEM_OPEN)){
            return (text.length() == ITEM_OPEN.length())? OPEN : OP_TEXT;
        }
        if(text.endsWith(ITEM_OPEN)){
            return (text.length() == ITEM_OPEN.length())? OPEN : TEXT_OP;
        }
        return NONE;
    }
    public String strip(String text, int classification){
        switch(classification){//            CL_TEXT = 4, TEXT_CL = 5, OP = 6, OP_TEXT = 7, TEXT_OP = 8;
            case UDEF:
                return text.substring(USERDEF_OPEN.length());
            case UDEF_OPEN:
                return text.substring(USERDEF_OPEN.length(), text.length() - ITEM_OPEN.length());
            case OP_TEXT:
                return text.substring(ITEM_OPEN.length());
            case TEXT_OP:
                return text.substring(0, text.length() - ITEM_OPEN.length());
            case CL_TEXT:
                return text.substring(ITEM_CLOSE.length());
            case TEXT_CL:
                return text.substring(0, text.length() - ITEM_CLOSE.length());
            default:
                return text;
        }
    }
    public Tuple getTuple(String text){
        int result = classifyText(text);
        if(result == NONE){
            return null;
        }
        return new Tuple(result, strip(text, result));
    }
    public static class Tuple{
        public final int result;
        public final String stripped;

        public Tuple(int result, String stripped){
            this.result = result;
            this.stripped = stripped;
        }

    }
}
