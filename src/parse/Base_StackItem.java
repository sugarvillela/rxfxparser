package parse;
import erlog.Erlog;
import interfaces.ILifeCycle;
import parse.interfaces.IStackComponent;

/**For separation of concerns, this class handles the self-stacking of items
 * @author Dave Swanson
 */
public abstract class Base_StackItem implements IStackComponent, ILifeCycle{
    protected Erlog er;
    protected Base_Stack P;                 // containing stack
    protected Base_StackItem above, below;  // stack links
    
    //public Handler_base(){}
    public Base_StackItem(){
        this.er = Erlog.getCurrentInstance();
        this.above=null;                // for linked stack
        this.below=null;                // for linked stack
    }
    
    /*=====IStackComponent methods============================================*/
    
    @Override
    public void push( Base_StackItem nuTop ){
        System.out.printf(
            "\n Push... oldTop: %s, nuTop: %s\n",
                this.getClass().getSimpleName(),
                nuTop.getClass().getSimpleName()
        );
        
        nuTop.below = this;
        this.above = nuTop;
        P.top = nuTop;
    }
    @Override
    public void pop(){
        String name = this.below == null? "NULL" : this.below.getClass().getSimpleName();
        System.out.printf(
            "\n Popping... oldTop: %s, nuTop: %s\n",
                this.getClass().getSimpleName(),
                name
        );
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
    
    /*======Empty ILifeCycle methods==========================================*/
    
    @Override
    public void onCreate(){}
    @Override
    public void onQuit(){}
}
