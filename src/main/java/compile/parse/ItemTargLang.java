package compile.parse;


import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import static compile.basics.Keywords.DATATYPE;
import static compile.basics.Keywords.FIELD;

public class ItemTargLang extends Base_ParseItem{
    
    public ItemTargLang(Factory_Node.ScanNode node){
        super(node);
    }

    @Override
    public void addTo(DATATYPE datatype, FIELD key, String val) {}

    @Override
    public void setAttrib(DATATYPE datatype, FIELD key, String val) {
        switch(key){
            case PROJ_NAME:
                CompileInitializer.getInstance().setProjName(val);
                break;
            default:
                er.set("Unknown keyword", key.toString());
        }
    }
    
    @Override
    public void onPush() {
        System.out.println("ItemTargLang onPush");
    }

    @Override
    public void onPop() {
        System.out.println("ItemTargLang onPop");
    }
}
