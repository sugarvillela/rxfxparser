package itr_struct;
import java.util.ArrayList;
//import unique.*;
/**
 *
 * @author newAdmin
 */
public class Itr_noFile implements Itr_CallNext{// implements Iterator, Iterable
    ArrayList<Object> content;
    boolean good, done;
    protected int row;
    
    public Itr_noFile(ArrayList<Object> setContent){
        this.init(setContent);
    }
    public final void init(ArrayList<Object> setContent){
        this.good = setContent != null && setContent.size() > 0;
        this.content = setContent;
        this.row = 0;
        this.done = false;
    }
    
    @Override
    public int getRow(){//row number
        return this.row;
    }
    @Override
    public int getCol(){//column number
        return 0;
    }
    @Override
    public String next(){
        this.row++;
        if(row >= this.content.size()){
            this.done = true;
            return "";
        }
        return this.content.get(row-1).toString();
    } 
    @Override
    public boolean hasFile(){ return this.good; }
    @Override
    public boolean hasNext(){ return !this.done; }
    @Override
    public void rewind(){
        this.good = this.content != null && this.content.size() > 0;
        this.row = 0;
        this.done = false;
    }
    
    @Override
    public void setLineGetter(){}
    @Override
    public void setWordGetter(){}
    @Override
    public boolean isLineGetter(){ return true; }
    @Override
    public boolean isWordGetter(){ return false; }
}
