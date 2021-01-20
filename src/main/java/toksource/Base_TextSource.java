package toksource;

import static langdef.Keywords.LOGGABLE_FORMAT;
import static langdef.Keywords.STATUS_FORMAT;

import interfaces.ILifeCycle;
import toksource.interfaces.ITextSource;
import toksource.interfaces.ITextWordOrLine;

/**
 *
 * @author Dave Swanson
 */
public abstract class Base_TextSource implements ITextSource, ITextWordOrLine, ILifeCycle {
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
    public String loggableStatus(){
        return String.format(LOGGABLE_FORMAT, this.getFileName(), this.getRow(), this.getCol());
    }
    @Override
    public String readableStatus(){
        return String.format(STATUS_FORMAT, this.getFileName(), this.getRow(), this.getCol());
    }
    
    // Empty ILifeCycle implementions
    @Override
    public void onCreate(){}
    @Override
    public void onPush(){}

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
