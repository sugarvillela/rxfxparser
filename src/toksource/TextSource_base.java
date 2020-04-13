package toksource;

import commons.Erlog;
/**
 *
 * @author Dave Swanson
 */
public abstract class TextSource_base implements TextSource{
    protected Erlog er;
    protected String text;
    protected boolean good;
    protected boolean done;
    protected int row;
    
    public TextSource_base(){
        this.er = Erlog.getInstance();
    }
    // abstracts 
    @Override
    public abstract void rewind();
    @Override
    public abstract String next();
    
    // empty implementions
    @Override
    public void onCreate(){}
    @Override
    public void onQuit(){}
    
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
}
