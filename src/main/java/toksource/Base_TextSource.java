package toksource;

import static compile.basics.Keywords.STATUS_FORMAT;
import toksource.interfaces.ITextSource;

/**
 *
 * @author Dave Swanson
 */
public abstract class Base_TextSource implements ITextSource{
    protected String text;
    protected boolean good;
    protected boolean done;
    protected int row;
    
    public Base_TextSource(){}
    // abstracts 
    @Override
    public abstract void rewind();
    @Override
    public abstract String next();
    

    
    // useful implementions
    @Override
    public boolean hasNext(){
        return !this.done;
    }
    @Override
    public boolean hasData(){
        return this.good;
    }
    @Override
    public int getRow(){
        return this.row;
    }
    @Override
    public int getCol(){
        return -1;
    }
    @Override
    public String readableStatus(){
        return String.format(STATUS_FORMAT, "Text", this.getRow(), 0);
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
    public void onQuit(){}
    
    // Empty ITextSource implementations
    @Override
    public void setLineGetter() {}
    @Override
    public void setWordGetter() {}

    @Override
    public boolean isLineGetter() {
        return true;
    }

    @Override
    public boolean isWordGetter() {
        return false;
    }

    @Override
    public boolean isEndLine() {
        return false;
    }
}
