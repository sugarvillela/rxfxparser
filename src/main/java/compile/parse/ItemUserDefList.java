package compile.parse;

import compile.basics.Factory_Node;
import static compile.basics.Keywords.NULL_TEXT;
import static compile.basics.Keywords.FIELD.DEF_NAME;
import static compile.basics.Keywords.DATATYPE;
import static compile.basics.Keywords.FIELD;

public class ItemUserDefList extends Base_ParseItem{
    
    public ItemUserDefList(Factory_Node.ScanNode node){
        super(node);
        this.defName = node.data;
    }
    @Override
    public void addTo(DATATYPE datatype, FIELD key, String val) {
        ((Base_ParseItem)below).addTo(datatype, key, val);
    }

//    @Override
//    public void setAttrib(FIELD key, String val) {
//        ((Base_ParseItem)below).setAttrib(key, val);
//    }

    @Override
    public void onPush() {
        if(below == null){
            er.set("Developer: below == null");
        }
        else if(NULL_TEXT.equals(defName)){
            er.set("No list name");
        }
        else{
            ((Base_ParseItem)below).setAttrib(null, DEF_NAME, defName);
            below.onBeginStep(); // tell LIST_* datatype to start this list
        }
    }

    @Override
    public void onPop() {
        below.onEndStep(); // tell LIST_* datatype to finish this list
    }
    
}
