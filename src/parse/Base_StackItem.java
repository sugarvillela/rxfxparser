package parse;
import itr_struct.StringSource;
import java.util.ArrayList;
import parse.Keywords.HANDLER;
/**Abstract base class for handlers
 *
 * @author Dave Swanson
 */
public abstract class Base_StackItem implements IParse{
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
    // implementations
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

    // children must implement pushPop
    public abstract void pushPop( String text );
    
    // stubs: children may override to implement
    @Override
    public void add(Object obj){}
    @Override
    public void setAttrib(String key, Object value){}
    @Override
    public Object getAttrib(String key){
        return null;
    }
    @Override
    public void onCreate(){}
    @Override
    public void onPush(){}
    @Override
    public void onPop(){}
    @Override
    public void onQuit(){}
    @Override
    public void notify(Keywords.KWORD k){}
    
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
