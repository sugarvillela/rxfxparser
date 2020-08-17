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
    protected String backText;   // repeat lines
    
    public Base_Stack(){}
    
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
        System.out.println("Push: StackSize = " + this.getStackSize());
    }
    @Override
    public void pop(){
        if(top==null){
            stackSize=0;
            er.set("Blame developer: Stack empty"); 
            //finish();
            //System.exit(0);
        }
        else{
            top.onPop();
            top.pop();
            stackSize--;
        }
        System.out.println("Pop: StackSize = " + this.getStackSize());
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
        System.out.println("pop all source: "+stackSize);
        while( stackSize > 1 ){
            pop();
            //System.out.println("after: "+stackSize);
        }
        fin.setLineGetter();
    }

//    public final void setFile(String filename, String ext){
//        if(!checkAndSetTitle(filename, ext)){
//            er.set( "Not a ." + ext + " file", filename );
//            return;
//        }
//        fin = new TokenSource( new TextSource_file(filename) );
//        if( !fin.hasData() ){
//            er.set( "Bad input file name", filename );
//        }
//        er.setTextStatusReporter(fin);
//    }
//    private boolean checkAndSetTitle(String f, String ext){
//        if( f.length() < ext.length() + 2 || !f.endsWith( "." + ext) ){
//            return false;
//        }
//        title = f.substring(0, f.length() - ext.length() - 1 );
//        //System.out.println( "title... " + title );
//        return true;
//    }
    public final void setWordGetter(){
        fin.setWordGetter();
    }
    public final void setLineGetter(){
        fin.setLineGetter();
    }
    public void back( String repeatThis ){// if backText not null, use it 
        backText = repeatThis;
    }
    
    // Empty ILifeCycle implementions
    @Override
    public void onCreate(){}
    @Override
    public void onPush(){}
    @Override
    public void onPop(){}
    @Override
    public void onQuit(){
        er.clearTextStatusReporter();
    }
}
