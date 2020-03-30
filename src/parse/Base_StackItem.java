package parse;

/**Abstract base class for handlers
 *
 * @author Dave Swanson
 */
public abstract class Base_StackItem implements IParse{
    protected Base_Stack P;
    protected Base_StackItem above, below;
    protected H h;
    public String name;

    //public Handler_base(){}
    public Base_StackItem(){
        this.above=null;                // for linked stack
        this.below=null;                // for linked stack
        name = this.getClass().getSimpleName();
    }
    // each handler must implement pushPop
    public abstract void pushPop( String text );
    
    @Override
    public void push( Base_StackItem nuTop ){
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
    public void disp(){
        System.out.println( "My name is "+this.name ); 
        if(this.below==null){
            System.out.println( "display done" ); 
        }
        else{
            this.below.disp();
        }
    }
}
