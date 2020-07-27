package parse;
import parse.interfaces.IParse;
import parse.Keywords.HANDLER;
import parse.interfaces.ILifeCycle;
import parse.interfaces.IStackComponent;

/**Abstract base class for handlers
 *
 * @author Dave Swanson
 */
public abstract class Base_StackItem implements IParse, IStackComponent, ILifeCycle{
    protected Base_Stack P;                 // containing stack
    protected Base_StackItem above, below;  // stack links
    protected HANDLER h;                    // class's own enum
    protected HANDLER[] allowedHandlers;    // children handlers to instantiate

    //public Handler_base(){}
    public Base_StackItem(){
        this.above=null;                // for linked stack
        this.below=null;                // for linked stack
    }
    // utilities
    // careful: these two are opposite logic, one positive one negative
    protected boolean isAllowedHandler(HANDLER handler){
        for( HANDLER allowed : allowedHandlers ){
            if( handler == allowed ){
                return true;
            }
        }
        return false;
    }
    // sets a parse error
    protected boolean erOnBadHandler(HANDLER handler){
        if(isAllowedHandler(handler)){
            return false;
        }
        P.setEr( handler + " not allowed here");
        return true;
    }
    
    /* IStackComponent methods */
    @Override
    public void push( Base_StackItem nuTop ){
        System.out.println("\n Base stack item...");
        System.out.println("this: "+this.toString());
        System.out.println("P: "+P.toString());
        System.out.println("Nu top: "+nuTop.toString());
        nuTop.below = this;
        this.above = nuTop;
        P.top = nuTop;
        //P.top.start();
    }
    @Override
    public void pop(){
        //this.finish();
        this.above = null;
        P.top = this.below;
        if( this.below != null){
            this.below.above = null;
            this.below = null;
        }
    }
    @Override
    public Base_StackItem getTop(){
        return P.getTop();
    }
    @Override
    public int getStackSize(){
        return this.P.getStackSize();
    }
    
    /* ILifeCycle methods */
    @Override
    public void onCreate(){}
    @Override
    public void onPush(){}
    @Override
    public void onPop(){}
    @Override
    public void onQuit(){}
    
    @Override
    public void disp(){
        System.out.println( "My name is "+this.getClass().getSimpleName() ); 
        if(this.below==null){
            System.out.println( "display done" ); 
        }
        else{
            this.below.disp();
        }
    }
}
