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

public abstract class Base_ParseItem extends Base_StackItem implements IParseItem{
    protected ScanNode node;
    protected String outFileName;
    protected String defName;
        
    public Base_ParseItem(Factory_Node.ScanNode node){
        this.node = node;
        this.debugName = node.datatype.toString();
    }
    
    public ScanNode getNode(){
        return this.node;
    }

    @Override
    public void addTo(Factory_Node.ScanNode node) {}

    @Override
    public void setAttrib(Factory_Node.ScanNode node) {
        switch (node.field){
            case DEF_NAME:
                defName = node.data;
                break;
            default:
                System.out.println(node.field.toString() + " in Base_ParseItem setAttrib: " + node.data);
                //((Base_ParseItem)below).setAttrib(node);
        }
    }

    @Override
    public void onPush() {
        //System.out.println("ItemTargLang onPush");
    }

    @Override
    public void onPop() {
        //System.out.println("ItemTargLang onPop");
    }

    public String getOutFileName(){
        return outFileName;
    }
    
}
