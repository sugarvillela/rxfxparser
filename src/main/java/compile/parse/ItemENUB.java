
package compile.parse;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.FIELD;
import unique.Uq_enumgen;
import unique.Enum_itr;

public class ItemENUB extends Base_ParseItem{
    protected Enum_itr itr;
    protected int count;
    
    public ItemENUB(Factory_Node.ScanNode node){
        super(node);
        if(node.h == HANDLER.ENUB){// ENUD extends ENUB with different itr
            itr = (Enum_itr)(new Uq_enumgen(CompileInitializer.getInstance().getWRow())).iterator();
        }
        
        count = 0;
    }
    @Override
    public void addTo(HANDLER handler, FIELD key, String val) {
        if(NULL_TEXT.equals(val)){
            er.set("No variable name", val);
        }
        int cur = itr.next();
        System.out.printf("%s = 0x%x;\n", val, cur);
        System.out.println(commons.BIT.str(cur));
    }

    @Override
    public void onPush() {
        System.out.println("ENUB onPush");
    }
    @Override
    public void onBeginStep(){
        if(count > 0){
            itr.newRow();
        }
        count++;
        System.out.println("ENUB onBeginStep: name = " + node.data);
    }
    @Override
    public void onEndStep(){
        System.out.println("ENUB onEndStep: name = " + node.data);
    }
    @Override
    public void onPop() {
        System.out.println("ENUB onPop");
    }
}
