package itr_struct;

/**Interface for Itr_file, Itr_noFile
 *
 * @author Dave Swanson
 */
public interface StringSource {
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
    public boolean isEndLine();
}
