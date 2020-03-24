package itr_struct;

/**
 *
 * @author Dave Swanson
 */
public interface Itr_CallNext {
    public int getRow();
    public int getCol();
    public String next();
    public boolean hasFile();
    public boolean hasNext();
    public void rewind();
    
    public void setLineGetter();
    public void setWordGetter();
    public boolean isLineGetter();
    public boolean isWordGetter();
}
