package compile.basics;
import compile.parse.Class_Parser;
import erlog.Erlog;
import interfaces.ILifeCycle;

/**For separation of concerns, this class handles the self-stacking of items
 * @author Dave Swanson
 */
public abstract class Base_StackItem implements IStackComponent, ILifeCycle{
    protected String debugName;
    protected Erlog er;
    protected Base_Stack P;                 // containing stack
    protected Base_StackItem above, below;  // stack links

    public Base_StackItem(){
        this.above=null;                // for linked stack
        this.below=null;                // for linked stack
        P = CompileInitializer.getInstance().getCurrParserStack();
        er = Erlog.get(this);
    }
    
    /*=====IStackComponent methods============================================*/
    
    @Override
    public void push( Base_StackItem nuTop ){
//        System.out.printf(
//            "\n Push... %s -> %s\n",
//                this.getDebugName(),
//                nuTop.getDebugName()
//        );
        
        nuTop.below = this;
        this.above = nuTop;
        P.top = nuTop;
    }
    @Override
    public void pop(){
//        String name = this.below == null? "NULL" : this.below.getDebugName();
//        System.out.printf(
//            "\n Popping... %s -> %s\n",
//                this.getDebugName(),
//                name
//        );
        this.above = null;
        //System.out.println(P);
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
        System.out.println( "        "+this.getDebugName() ); 
        if(this.below != null){
            this.below.disp();
        }
    }
    @Override
    public String getDebugName(){
        return (debugName == null)?
                this.getClass().getSimpleName() :
                this.getClass().getSimpleName() + ": " + debugName;
    }

    /*======Empty ILifeCycle methods==========================================*/
    
    @Override
    public void onCreate(){}
    @Override
    public void onBeginStep(){}
    @Override
    public void onEndStep(){}
    @Override
    public void onQuit(){}
    
    public Base_StackItem getAbove(){
        return above;
    }
    public Base_StackItem getBelow(){
        return below;
    }
}
