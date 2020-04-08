package toktools;

import java.util.ArrayList;
/* To run:
*  TokenTool instance = TK.getInstance();
*  instance.setText(text);
*  instance.setDelims(delims); 
*  instance.setMap(skips); 
*  instance.setFlags(flags);
*  instance.parse();
*  return instance.get();
*/

/**
 * Tokenizer with multi-delimiters, empty-element prevention, special options.
 * Can define 'skip' areas not to tokenize, like quoted text etc.
 * Can leave 'skip' characters in or take them out
 * Can leave delimiters in or take them out.
 * Can leave skipped text in or put it into a separate list: skips
 * Can extend skip areas across two or more lines
 * 
 * Three types of static method:
 * 1. getInstance() for Tokens_simple, Tokens_special, Tokens_wSkipHold
 *  a. Returns appropriate instance for parameters passed
 *  b. Instance is initialized; just call toArr or toList with text to split
 *  c. Instances are always new; save instance locally and pass as first 
 *       parameter of subsequent calls
 * 2. Simple tokenizer without multi-delimiters and skip areas
 *  a. Return arrayList or array
 * 3. Complex tokenizers with all features
 *  a. Return arrayList or array

 * 
 * 
 * @author Dave Swanson
 */
public class TK {
    /* Enumerations for tokenizer flags (used by complex tokenizer) */
    
    /* Make delimiters their own token? default false.
     * Where delim normally removed, delim will be an element in tok array 
     * No dup prevention in this case: Multiple delims make multiple elements */
    public static final int DELIMIN = 0x01;
    
    /* Remove or keep brackets/quotes defining skipped area? default in. */
    public static final int SYMBOUT = 0x02;
    
    /* Remove or keep skipped text? default in.
     * If out, skipped text goes in separate array. Call getSkips() to access */
    public static final int SKIPOUT = 0x04;
    
    /* Let skip areas carry over to the next parse? default no.
     * This is useful for file parsing, where a skip area might continue
     * on the next line. Note: you need to get instance, set map, delim etc,
     * then setText() and parse() again. setMap() clears the holdover */
    public static final int HOLDOVER = 0x08;
    
    /*========================================================================*/
    // Static methods to run tokenizer: 

    /**Gets simple tokenizer with split on space
     * @return instance initialized with everything but text
     */
    public static Tokens getInstance(){
        return new Tokens_simple();  // initialized
    }
    
    /**Gets simple tokenizer with default limit (high)
     * @param delim single char delimiter like space etc
     * @return instance initialized with everything but text
     */
    public static Tokens getInstance( char delim ){
        return new Tokens_simple( delim );  // initialized
    }
    
    /**Gets simple tokenizer
     * @param delim single char delimiter like space etc
     * @param limit max number of splits allowed
     * @return instance initialized with everything but text
     */
    public static Tokens getInstance( char delim, int limit ){
        return new Tokens_simple( delim, limit );  // initialized
    }

    /**Gets special tokenizer with default settings
     * @param delims delimiters like space etc
     * @param skips symbols surround text you don't want split, like "() etc
     * @return instance initialized with everything but text
     */
    public static Tokens getInstance( String delims, String skips ){//, int flags
        return new Tokens_special( delims, skips, 0 );
    }
    /**Gets special tokenizer or skip-and-hold-enabled tokenizer
     * 
     * @param delims delimiters like space etc
     * @param skips symbols surround text you don't want split, like "() etc
     * @param flags constants enumerated in Tokens interface
     * @return instance initialized with everything but text
     */
    public static Tokens getInstance( String delims, String skips, int flags ){//, int flags
        return ( (flags & (TK.SKIPOUT|TK.HOLDOVER) ) != 0 )? 
            new Tokens_special( delims, skips, flags ) : 
            new Tokens_wSkipHold( delims, skips, flags );
    }
    
    public static Tokens getInstance( String[] grps, String skips, int flags ){
        return new Tokens_byGroup(grps, skips, flags);
    }
    
    /*========================================================================*/
    // Simple tokenize methods with empty-element prevention (see above)
    // List version
    public static ArrayList<String> toList( char delim, String text ){
        return TK.toList(
            TK.getInstance( delim ), 
            text
        );
    }
    public static ArrayList<String> toList( char delim, int limit, String text ){
        return TK.toList(
            TK.getInstance( delim, limit ), 
            text
        );
    }

    /*========================================================================*/
    // Simple tokenize methods with empty-element prevention (see above)
    // Array version
    public static String[] toArr( char delim, String text ){
        return TK.toArr(
            TK.getInstance( delim, 0x7FFFFFFF ), 
            text
        );
    }
    public static String[] toArr( char delim, int limit, String text ){
        return TK.toArr(
            TK.getInstance( delim, limit ), 
            text
        );
    }
    // Keep this: Tokens_simple has custom method for array output
    public static String[] toArr( Tokens_simple instance, String text ){
        return instance.toArr( text );
    }
    
    /*========================================================================*/
    // Complex tokenize methods with multi-delims and skip symbols (see above)
    // List version
    public static ArrayList<String> toList( String delims, String skips, String text ){
        /* One stop shop: sets, runs and returns with no flags */
        return TK.toList( delims, skips, 0, text );
    }
    public static ArrayList<String> toList( String delims, String skips, int flags, String text ){
        /* One stop shop: sets, runs and returns */
        return TK.toList( 
            getInstance( delims, skips, flags ),
            text
        );
    }
    public static ArrayList<String> toList( String[] grps, String skips, int flags, String text ){
        return TK.toList( 
            getInstance( grps, skips, flags ),
            text
        );
    }
    
    /*========================================================================*/
    // Complex tokenize methods with multi-delims and skip symbols (see above)
    // Array version
    public static String[] toArr( String delims, String skips, String text ){
        return TK.toArr( 
            getInstance( delims, skips, 0 ),
            text
        );
    }
    public static String[] toArr( String delims, String skips, int flags, String text ){
        return TK.toArr( 
            getInstance( delims, skips, flags ),
            text
        );
    }
    public static String[] toArr( String[] grps, String skips, int flags, String text ){
        return TK.toArr( 
            getInstance( grps, skips, flags ),
            text
        );
    }
    
    /*========================================================================*/
    // Runner: assumes passed instance is initialized

    public static ArrayList<String> toList( Tokens instance, String text ){
        return instance.toList(text);
    }
    public static String[] toArr( Tokens instance, String text ){
        return TK.toList( 
            instance,
            text
        ).toArray(new String[0]);
    }

    

}
