package toktools;

import java.util.ArrayList;

/**Tokenizers of various levels of complexity
 *
 * @author Dave Swanson
 */
public interface Tokens {
    // main method
    public void parse( String text );
    
    // get result
    public ArrayList<String> getTokens();
    public ArrayList<String> getSkips();
    // get state: in a skip area or not
    public boolean isHolding();
    
    // Simple tokenize methods implemented by Tokens_simple
    // Complex tokenize methods with multi-delims and skip symbols
    public ArrayList<String> toList( String text );
    public String[] toArr( String text );
    
    // initializers before parsing  
    public void setDelims( String delims );
    public void setDelims( char delim );
    public void setMap( String skips );
    public void setFlags( int flags );
}
