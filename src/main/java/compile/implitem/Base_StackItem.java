package compile.implitem;
import compile.implstack.Base_Stack;
import compile.interfaces.Debuggable;
import compile.interfaces.IStackItem;
import runstate.RunState;

/**For separation of concerns, this class handles the self-stacking of items
 * @author Dave Swanson
 */
public abstract class Base_StackItem implements IStackItem, Debuggable {
    protected String debugName;
    protected Base_Stack P;                 // containing stack
    protected Base_StackItem above, below;  // stack links

    public Base_StackItem(){
        this.above=null;                // for linked stack
        this.below=null;                // for linked stack
        P = RunState.getInstance().getActiveParserStack();
    }
    
    /*=====IStackItem methods============================================*/
    
    @Override
    public void push( Base_StackItem nuTop ){
//        System.out.printf(
//            "\n Push... %s -> %s\n",
//                this.getDebugName(),
//                nuTop.getDebugName()
//        );
        
        nuTop.below = this;
        this.above = nuTop;
        P.setTop(nuTop);
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
        P.setTop(this.below);
        if( this.below != null){
            this.below.above = null;
            this.below = null;
        }
    }

    @Override
    public Base_StackItem getAbove(){
        return above;
    }

    @Override
    public Base_StackItem getBelow(){
        return below;
    }

    /*======Debuggable========================================================*/

    @Override
    public String getDebugName(){
        return (debugName == null)?
                this.getClass().getSimpleName() :
                this.getClass().getSimpleName() + ": " + debugName;
    }

    @Override
    public void disp(){
        System.out.println( "        "+this.getDebugName() );
        if(this.below != null){
            this.below.disp();
        }
    }
}
