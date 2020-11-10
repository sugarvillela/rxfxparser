package toktools;

import java.util.ArrayList;

/**Tokenizers of various levels of complexity
 *
 * @author Dave Swanson
 */
public interface Tokens {
    // main method
    void parse( String text );
    
    // get result
    ArrayList<String> getTokens();
    ArrayList<String> getSkips();
    // get state: in a skip area or not
    boolean isHolding();
    
    // Simple tokenize methods implemented by Tokens_simple
    // Complex tokenize methods with multi-delims and skip symbols
    ArrayList<String> toList( String text );
    String[] toArr( String text );
    
    // initializers before parsing  
    void setDelims( String delims );
    void setDelims( char delim );
    void setMap( String skips );
    void setFlags( int flags );
}
