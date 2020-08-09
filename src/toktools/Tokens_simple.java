package toktools;

import java.util.ArrayList;

/**Simple tokenizer methods with empty-element prevention
 *
 * @author Dave Swanson
 */
public class Tokens_simple implements Tokens{
    private ArrayList<String> out;
    private final char delim;
    private final int limit;
    
    public Tokens_simple(){
        this( ' ', 0x7FFFFFFF );
    }
    public Tokens_simple( char setDelim ){
        this( setDelim, 0x7FFFFFFF );
    }
    public Tokens_simple( char setDelim, int setLimit ){
        delim = setDelim;
        limit = setLimit;
    }
    
    @Override
    public void parse(String text) {
        out = new ArrayList<>();
        int i, j=0, start = 0;
        for( i=0; i<text.length(); i++ ){
            if( text.charAt(i) == delim ){
                if( i != start ){
                    if( j >= limit-1){
                        break;
                    }
                    out.add( text.substring(start, i) );
                    j++;

                }
                start=i+1;
            }
        }
        if( i != start ){
            out.add( text.substring(start) );
        }
    }
    @Override
    public ArrayList<String> getTokens() {
        return out;
    }
    
    @Override
    public ArrayList<String> toList( String text ){
        parse(text);
        return getTokens();
    }

    @Override
    public String[] toArr(String text){
        // Simple tokenizer with unlimited splits, no empty
        // Rehearse to get size
        int count = 0;
        int start = 0;
        int i, j = 0;
        for( i=0; i<text.length(); i++ ){
            if( text.charAt(i) == delim ){
                if( i != start ){
                    count++;
                    // Limit size, if limit passed
                    if( count == limit ){
                        i = start;
                        break;
                    }
                }
                start=i+1;
            }
        }
        if( i != start ){
            count++;
        }
        // Set array and run again to populate
        String[] out = new String[count];
        start = 0;
        for( i=0; i<text.length(); i++ ){
            if( text.charAt(i) == delim ){
                if( i != start ){
                    if( j >= limit-1){
                        break;
                    }
                    out[j] = text.substring(start, i);
                    j++;

                }
                start=i+1;
            }
        }
        if( i != start ){
            out[j] = text.substring(start);
        }
        return out;
    }

    // Unsupported, because it's simple
    @Override
    public boolean isHolding() {
        throw new UnsupportedOperationException("Use Tokens_special!");
    }
    // Unsupported because it's not a Tokens_wSkipHold
    @Override
    public ArrayList<String> getSkips() {
        throw new UnsupportedOperationException("Use Tokens_wSkipHold!");
    }
    @Override
    public void setDelims( String delims ){}
    @Override
    public void setDelims( char delim ){}
    @Override
    public void setMap( String skips ){}
    @Override
    public void setFlags( int flags ){}
}
