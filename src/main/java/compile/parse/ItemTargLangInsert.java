package compile.parse;


import compile.basics.Factory_Node;
import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.KWORD;

public class ItemTargLangInsert extends Base_ParseItem{
    public ItemTargLangInsert(Factory_Node.ScanNode node){
        super(node);
    } 

    @Override
    public void onPush() {
        System.out.println("ItemTargLangInsert onPush");
    }

    @Override
    public void onPop() {
        System.out.println("ItemTargLangInsert onPop");
    }

    @Override
    public void addTo(HANDLER handler, KWORD key, String val) {
        if(NULL_TEXT.equals(val)){
            er.set("No ItemTargLangInsert", val);
        }
        System.out.printf("Add to ItemTargLangInsert: %s\n", val);
    }
}
