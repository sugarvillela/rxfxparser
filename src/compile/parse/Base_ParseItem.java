package compile.parse;
    /*
    // IStackComponent
    @Override
    public void push(Base_StackItem nuTop) {}

    @Override
    public void pop() {}

    @Override
    public Base_StackItem getTop() {}

    @Override
    public int getStackSize() {}

    @Override
    public void disp() {}

    //ILifeCycle
    @Override
    public void onCreate() {}

    @Override
    public void onPush() {}

    @Override
    public void onPop() {}

    @Override
    public void onQuit() {}
    */
import compile.basics.Base_StackItem;
import compile.basics.Factory_Node;
import compile.basics.Factory_Node.ScanNode;
import compile.basics.IParseItem;
import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.KWORD;

public abstract class Base_ParseItem extends Base_StackItem implements IParseItem{
    protected ScanNode node;
    protected String defName;
        
    public Base_ParseItem(Factory_Node.ScanNode node){
        this.node = node;
        this.debugName = node.h.toString();
        P = Class_Parser.getInstance();
    }
    
    public ScanNode getNode(){
        return this.node;
    }
    
    @Override
    public void addTo(HANDLER handler, KWORD key, String val) {}
    
    @Override
    public void setAttrib(KWORD key, String val) {
        switch (key){
            case DEF_NAME:
                defName = val;
                break;
            default:
                //System.out.println(key.toString() + " in Base_ParseItem setAttrib: " + val);
                ((Base_ParseItem)below).setAttrib(key, val);
        }
    }
    
}