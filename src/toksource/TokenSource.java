package toksource;

/**Interface for Word/Line iterator; set output to word, line or a strategy/state
 * @author Dave Swanson
 */
public interface TokenSource extends StringSource{
    public void setLineGetter();
    public void setWordGetter();
    public boolean isLineGetter();
    public boolean isWordGetter();
    public boolean isEndLine();
    public int getCol();
    
}
