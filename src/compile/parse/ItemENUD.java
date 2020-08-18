package compile.parse;

import compile.basics.CompileInitializer;
import compile.basics.Keywords;
import erlog.Erlog;
import unique.Enum_itr;
import unique.Uq_enumgen;

public class ItemENUD extends ItemENUB{
    public ItemENUD(Keywords.HANDLER h){
        this.h = h;
        this.debugName = h.toString();
        itr = (Enum_itr)(
            new Uq_enumgen(
                CompileInitializer.getWRow(), 
                CompileInitializer.getWVal()
            )
        ).iterator();
        er = Erlog.get(this);
    }
    @Override
    public void onPush() {
        System.out.println("ENUD onPush");
    }
    @Override
    public void onBeginStep(){
        if(count > 0){
            itr.newCol();
        }
        count++;
        System.out.println("ENUD onBeginStep: name = " + defName);
    }
    @Override
    public void onEndStep(){
        System.out.println("ENUD onEndStep: name = " + defName);
    }
    @Override
    public void onPop() {
        System.out.println("ENUD onPop");
    }
}
