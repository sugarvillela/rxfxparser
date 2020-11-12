package compile.basics;

import erlog.Erlog;
import interfaces.ILifeCycle;
import runstate.RunState;
import toksource.Base_TextSource;
import toksource.interfaces.ChangeNotifier;
import toksource.interfaces.ITextStatus;

/**Abstract base class for Scanner and Parser classes
 *
 * @author Dave Swanson
 */
public abstract class Base_Stack implements ILifeCycle, IStackComponent, ChangeNotifier {
    protected String debugName;
    protected Base_StackItem top;  // stack; datatypes are linked nodes
    protected int stackSize;     // changes on push, pop
    protected Base_TextSource fin;  // file to be parsed
    //protected String title;
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
            String name = top == null? "NULLY" : top.getDebugName();
            //System.out.println("pop: stackSize="+stackSize+", top="+name);
            top.onPop();
            String name2 = top == null? "NULLY" : top.getDebugName();
            //System.out.println("pop: stackSize="+stackSize+", top="+name2);
            top.pop();
            stackSize--;
        }
    }
    @Override
    public Base_StackItem getTop(){
        return top;
    }

    @Override
    public void setTop(Base_StackItem nuTop){
        top = nuTop;
    }

    @Override
    public int getStackSize(){
        return this.stackSize;
    }

    public Base_TextSource getTokenSource(){
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
        return (debugName == null)?
            this.getClass().getSimpleName() :
            this.getClass().getSimpleName() + ": " + debugName;
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
        Erlog.clearTextStatusReporter();
    }

    // ChangeNotifier implementation
    @Override
    public void onTextSourceChange(ITextStatus textStatus) {
        RunState.getInstance().onTextSourceChange(textStatus, this);
    }
}
