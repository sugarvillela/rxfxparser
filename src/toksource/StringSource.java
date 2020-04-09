package toksource;

/**Interface for Itr_file, Itr_noFile
 *
 * @author Dave Swanson
 */
public interface StringSource {
    public void rewind();
    public String next();
    public boolean hasNext();
    public boolean hasData();
    public int getRow();
    
    public void onCreate();
    public void onQuit();
    
}
