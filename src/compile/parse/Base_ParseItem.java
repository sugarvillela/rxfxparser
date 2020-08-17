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
import compile.basics.Keywords;
import compile.basics.Base_StackItem;
import compile.basics.IParseItem;
import compile.basics.Keywords.HANDLER;

public abstract class Base_ParseItem extends Base_StackItem{// implements IParseItem
    protected HANDLER h;
    
    public Base_ParseItem(){
        P = Class_Parser.getInstance();
    }
    public void setHandler(Keywords.HANDLER h){
        this.h = h;
    }
    
    public Keywords.HANDLER getHandler(){
        return this.h;
    }
    
    //@Override
    public void addTo(Object object){}
    
    //@Override
    public void setAttrib(Object key, Object val){}
    
}
