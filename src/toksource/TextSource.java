package toksource;

/**Interface for base class -> file and list implementations
 *
 * @author Dave Swanson
 */
public interface TextSource {
    public void rewind();
    public String next();
    public boolean hasNext();
    public boolean hasData();
    public int getRow();
    
    public void onCreate();
    public void onQuit();
    
}
