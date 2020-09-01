package compile.parse;


import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import unique.Enum_itr;
import unique.Uq_enumgen;

public class ItemENUD extends ItemENUB{
    public ItemENUD(Factory_Node.ScanNode node){
        super(node);
        itr = (Enum_itr)(
            new Uq_enumgen(
                CompileInitializer.getInstance().getWRow(), 
                CompileInitializer.getInstance().getWVal()
            )
        ).iterator();
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
        System.out.println("ENUD onBeginStep: name = " + node.data);
    }
    @Override
    public void onEndStep(){
        System.out.println("ENUD onEndStep: name = " + node.data);
    }
    @Override
    public void onPop() {
        System.out.println("ENUD onPop");
    }
}
