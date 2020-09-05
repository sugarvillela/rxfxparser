package compile.basics;

import erlog.Erlog;
import interfaces.ILifeCycle;
import compile.basics.IStackComponent;
import toksource.interfaces.ITextSource;

/**Abstract base class for Scanner and Parser classes
 *
 * @author Dave Swanson
 */
public abstract class Base_Stack implements ILifeCycle, IStackComponent{//
    protected String debugName;
    protected Base_StackItem top;  // stack; handlers are linked nodes
    protected int stackSize;     // changes on push, pop
    protected ITextSource fin;  // file to be parsed
    protected String title;      // outFile name = title_handler.extension
    protected Erlog er;    // logs, notifies, quits or all 3

    
    public Base_Stack(){
        er = Erlog.get(this);
    }
    
    /* IStackComponent methods */
    @Override
    public void push( Base_StackItem nuTop ){
        if(top==null){
            top = nuTop;
            stackSize=1;
        }
        else{
            top.push( nuTop );
            stackSize++;
        }
        top.onPush();
    }
    @Override
    public void pop(){

        if(top == null || stackSize < 1){
            stackSize=0;
            er.set("Blame developer: Stack empty"); 
        }
        else{
            top.onPop();
            top.pop();
            stackSize--;
        }
    }
    @Override
    public Base_StackItem getTop(){
        return top;
    }
    @Override
    public int getStackSize(){
        return this.stackSize;
    }

    public ITextSource getTokenSource(){
        return fin;
    }
    @Override
    public void disp(){
        System.out.println("Base_Stack disp(): "+stackSize);
        if( top != null ){
            top.disp();
        }
        System.out.println("Base_Stack disp done");
    }
    @Override
    public String getDebugName(){
        return this.getClass().getSimpleName() + ": " + debugName;
    }
    
    // utilities
    public void popAllSource(){
        while( stackSize > 1 ){
            pop();
        }
        fin.setLineGetter();
    }

    public final void setWordGetter(){
        fin.setWordGetter();
    }
    public final void setLineGetter(){
        fin.setLineGetter();
    }

    
    // Empty ILifeCycle implementions
    @Override
    public void onCreate(){}
    @Override
    public void onPush(){}
    @Override
    public void onBeginStep(){}
    @Override
    public void onEndStep(){}
    @Override
    public void onPop(){}
    @Override
    public void onQuit(){
        er.clearTextStatusReporter();
    }
}
