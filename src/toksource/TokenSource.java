package toksource;

/**Interface for Word/Line iterator; set output to word, line or a strategy/state
 * @author Dave Swanson
 */
public interface TokenSource extends TextSource{
    /* Inherited:
    
    public void rewind();
    public String next();
    public boolean hasNext();
    public boolean hasData();
    public int getRow();

    public void onCreate();
    public void onQuit();
    
    */
    public void setLineGetter();
    public void setWordGetter();
    public boolean isLineGetter();
    public boolean isWordGetter();
    public boolean isEndLine();
    public int getCol();
    
}
