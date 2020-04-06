package parse;

import commons.Erlog;
import itr_struct.StringSource_file;
import java.util.ArrayList;
import itr_struct.StringSource;

/**Abstract base class for Scanner and Parser classes
 *
 * @author Dave Swanson
 */
public abstract class Base_Stack implements IParse{
    protected Base_StackItem top;  // stack; handlers are linked nodes
    protected int stackSize;     // changes on push, pop
    protected StringSource fin;  // file to be parsed
    protected String title;      // outFile name = title_handler.extension
    protected final Erlog er;    // logs, notifies, quits or all 3
    protected String backText;   // repeat lines
    
    public Base_Stack(){
        er = Erlog.getInstance();
    }
    
    @Override
    public void push( Base_StackItem nuTop ){
        System.out.println( "Pushing "+nuTop.getClass().getSimpleName()+", stackSize = "+stackSize );
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
        if(top==null){
            stackSize=0;
            setEr("Blame developer: Stack empty"); 
            //finish();
            //System.exit(0);
        }
        else{
            System.out.println( "Popping "+top.getClass().getSimpleName()+", stackSize = "+stackSize );
            top.onPop();
            top.pop();
            stackSize--;
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
    public void notify(Keywords.KWORD k){}
    
    @Override
    public ArrayList<ScanNode> getScanNodeList(){
        commons.Erlog.getInstance().set(
            "Developer: no getScanNodeList() implementation in "
                    + this.getClass().getSimpleName()
        );
        return new ArrayList<>();
    }
    @Override
    public Base_StackItem getTop(){
        return top;
    }
    @Override
    public StringSource getStringSource(){
        return fin;
    }
    @Override
    public void disp(){
        if( top != null ){
            top.disp();
        }
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
    public int getStackSize(){
        return this.stackSize;
    }
    public final void setFile(String filename, String ext){
        if(!checkAndSetTitle(filename, ext)){
            er.set( "Not a ." + ext + " file: " + filename );
            return;
        }
        fin = new StringSource_file( filename );
        if( !fin.hasFile() ){
            er.set( "Bad input file name: "+filename );
        }
    }
    private boolean checkAndSetTitle(String f, String ext){
        if( f.length() < ext.length() + 2 || !f.endsWith( "." + ext) ){
            return false;
        }
        title = f.substring(0, f.length() - ext.length() - 1 );
        //System.out.println( "title... " + title );
        return true;
    }
    public final void setEr( String text ){
        // handlers don't need to know line number; just call parser to set
        er.set( text, fin.getRow(), fin.getCol() );
    }
    public final void setWordGetter(){
        fin.setWordGetter();
    }
    public final void setLineGetter(){
        fin.setLineGetter();
    }
    public void back( String repeatThis ){// if backText not null, use it 
        backText = repeatThis;
    }

}
