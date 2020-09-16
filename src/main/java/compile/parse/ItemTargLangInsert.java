package compile.parse;


import compile.basics.Factory_Node;
import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.DATATYPE;
import static compile.basics.Keywords.FIELD;

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
    public void addTo(DATATYPE datatype, FIELD key, String val) {
        if(NULL_TEXT.equals(val)){
            er.set("No ItemTargLangInsert", val);
        }
        System.out.printf("Add to ItemTargLangInsert: %s\n", val);
    }
}
