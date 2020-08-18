
package compile.parse;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.KWORD.DEF_NAME;
import compile.basics.Keywords;
import compile.basics.Keywords.KWORD;
import erlog.Erlog;

public class ItemUserDefList extends Base_ParseItem{
    protected String defName;
    
    public ItemUserDefList(Keywords.HANDLER h, String defName){
        this.h = h;
        this.debugName = h.toString();
        this.defName = defName;
        er = Erlog.get(this);
    }
    @Override
    public void addTo(Keywords.HANDLER handler, Object object) {
        ((Base_ParseItem)below).addTo(handler, object);
    }

    @Override
    public void setAttrib(KWORD key, String val) {}

    @Override
    public void onPush() {
        if(below == null){
            er.set("Developer: below == null");
        }
        else if(NULL_TEXT.equals(defName)){
            er.set("No list name");
        }
        else{
            ((Base_ParseItem)below).setAttrib(DEF_NAME, defName);
            below.onBeginStep(); // tell ENUM handler to start this list
        }
    }

    @Override
    public void onPop() {
        below.onEndStep(); // tell ENUM handler to finish this list
    }
    
}
