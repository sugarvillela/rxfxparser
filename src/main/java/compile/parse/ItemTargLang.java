package compile.parse;


import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.KWORD;

public class ItemTargLang extends Base_ParseItem{
    
    public ItemTargLang(Factory_Node.ScanNode node){
        super(node);
    }

    @Override
    public void addTo(HANDLER handler, KWORD key, String val) {}

    @Override
    public void setAttrib(KWORD key, String val) {
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
