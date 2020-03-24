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
 * Three types of static method:
 * 1. Simple tokenizers without multi-delimiters and skip areas
 *  a. Return arrayList or array
 * 2. Complex tokenizers with all features
 *  a. Return arrayList or array
 * 3. Complex tokenizers that return instance of self with arrays initialized
 *  a. no arg getInstance() returns uninitialized instance (singleton pattern)
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
    
    protected  String text, delims;     // input text, list of delimiters
    protected ArrayList<String> tokens; // output
    protected ArrayList<String> skips;  // removed strings if SKIPOUT else null
    protected char[] oMap, cMap;        // matched open/close skip char arrays
    
    protected int symbIn;               // leave open/close chars in if 1
    protected boolean delimIn;//, skipOut; // keep delims, skips to separate list
    //protected boolean holdOver;         // carry over skip area to next parse
    //protected String holdText;          // To support holdOver
    private SkipBehavior skipper;
    private HoldBehavior holder;
    
    private TK() {
        symbIn = 1;
        //skipOut = false;
        delimIn = false;
        skipper = new Skip_false();
        holder = new Hold_false();
        
    }
    private static TK tk=null;
    
    // initializers before parsing
    public void setText( String text ){
        this.text = text;
    }    
    public void setDelims( String delims ){
        this.delims = delims;
    }
    public void setMap( String skips ){
        // map openers to closers, using symbols from arg
        // if you want different symbols, edit this or add a strategy pattern
        oMap =  new char[skips.length()];
        cMap =  new char[skips.length()];
        char[] openers = new char[]{'(','{','[','<','"','\''};
        char[] closers = new char[]{')','}',']','>','"','\''};
        int to = 0;
        for ( int i = 0; i < openers.length; i++) {
            if( skips.indexOf(openers[i])!=-1){
                oMap[to]=openers[i];
                cMap[to]=closers[i];
                to++;
            }
        }
    }
    public void setFlags( int flags ){
        symbIn = ( ( flags & SYMBOUT )==0 )? 1 : 0;   // integer for adding index
        skipper = ( ( flags & SKIPOUT )==0 )? new Skip_false() : new Skip_true();
        holder = ( ( flags & HOLDOVER )==0 )? new Hold_false() : new Hold_true();
        //skipOut = ( ( flags & SKIPOUT )!=0 );
        delimIn = ( ( flags & DELIMIN )!=0 );

    }
    
    // utility for better readability in parse()
    protected boolean isDelim( char symb ){
        return ( delims.indexOf( symb )!= -1 );
    }
    public boolean isHolding(){
        return holder.isHolding();
    }

    // utilities, not needed because dup prevention works
    // Then again, you may want to keep delims but lose spaces, so here you go.
    // Or set the delims, run parse() then set other delims to get rid of
    public void trimDelims(){
        /* Make sure no empty or all-delimiter strings are included */
        ArrayList<String> trimmed = new ArrayList<>();
        for ( String tok : tokens ){
            for ( int i = 0; i < tok.length(); i++){
                if( !isDelim( tok.charAt(i) ) ){
                    trimmed.add( tok );
                    break;
                }
            }
        }
        tokens = trimmed;
    }
    public void trimSpaces(){
        /* Make sure no empty strings are included */
        ArrayList<String> trimmed = new ArrayList<>();
        for ( String tok : tokens ){
            tok = tok.trim();
            if( tok.length() != 0 ){
                trimmed.add( tok );
            }
        }
        tokens = trimmed;
    }
    
    // main method
    public void parse( String text ){ // save a step if repeating parse
        setText( text );
        parse();
    }
    public void parse(){
        //System.out.printf("\nParse: holding=%b, text=%s\n", holder.isHolding(), text);
        holder.initParse();        // close symbol, matches opening symbol
        //holder.newList();  // to put skips in

        int start=0;        // beginning of substring
        int i;              // for current char being checked
        for ( i = 0; i < text.length(); i++) {
            if( holder.isHolding() ){                     // in skip area            
                if( holder.isClosing( text.charAt(i) ) ){ // found closing skip symbol
                    holder.clearCSymb();                  // leaving skip area
                    // if symbIn==1, will keep current symbol, else lose it
                    skipper.add( text.substring( start, i+symbIn ) );
                    start=i+1;                    // reset for next token
                }
            }
            else if( holder.isOpening( text.charAt(i) ) ){// opener
                if( i != start ){               // if prev wasn't a delim, dump
                    tokens.add( text.substring( start, i ) );
                    start=i;
                }
                if(symbIn == 0){                // lose the current symbol
                    start += 1;
                }
            }
            else if( isDelim( text.charAt(i) ) ){//delimiter
                if( i!=start ){                 // if text, dump
                    tokens.add( text.substring( start, i ) );
                }
                if( delimIn ){                  // give delim its own element
                    tokens.add( text.substring(i, i+1) );
                }
                start=i+1;                      // reset for next token
            }
        }
        if( i!=start ){                         // final dump if needed
            tokens.add( text.substring( start, i ) );
        }
    }
    
    // get result
    public ArrayList<String> get(){
        return tokens;
    }
    public ArrayList<String> getTokens(){
        return tokens;
    }
    public ArrayList<String> getSkips(){
        return skips;
    }
    private interface SkipBehavior{
        
        public void newList();
        public void add( String skipText );
    }
    private class Skip_false implements SkipBehavior{
        @Override
        public void newList(){
            skips = null;
        }
        @Override
        public void add( String skipText ){
            tokens.add( skipText );
        }
    }
    private class Skip_true implements SkipBehavior{
        @Override
        public void newList(){
            skips = new ArrayList<>();
        }
        @Override
        public void add( String skipText ){
            skips.add( skipText );
        }
    }
    private abstract class HoldBehavior{
        protected char cSymb;           // Closing symbol (replace with stack?)
        public HoldBehavior(){
            cSymb = 0;                  // clear closing symbol used for parse()
        }
        public final boolean isOpening( char symb ){
            // Set closer to match opener, or null if not an opener
            for(int i=0; i<oMap.length; i++){
                if( symb == oMap[i] ){
                    cSymb = cMap[i];    // important side effect
                    return true;
                }
            }
            return false;
        }
        public final boolean isHolding(){
            return cSymb != 0;
        }
        public final boolean isClosing( char symb ){// ((cSymb = getMappedSymb(text.charAt(i))) != 0)
            return symb == cSymb;
        }
        public final void clearCSymb(){
            cSymb = 0;
        }
        public abstract void initParse();
        public abstract void newList();
        public abstract void add( String skipText );
    }
    private class Hold_false extends HoldBehavior{
        @Override
        public void initParse(){
            cSymb = 0;
            tokens = new ArrayList<>();     // for main tokenized output
            skipper.newList();              // to put skips in
        }

        @Override
        public void newList(){

        }
        @Override
        public void add( String skipText ){
            skips.add( skipText );
        }
    }
    private class Hold_true extends HoldBehavior{
        @Override
        public void initParse(){
            if(!isHolding()){
                cSymb = 0;
                tokens = new ArrayList<>();     // for main tokenized output
                skipper.newList();              // to put skips in
            }
        }

        @Override
        public void newList(){
            skips = new ArrayList<>();
        }
        @Override
        public void add( String skipText ){
            skips.add( skipText );
        }
    }
    /*========================================================================*/
    // Static methods to run tokenizer: this one returns tokenizer instance so
    // you can use get() methods for the return array you want
    public static TK getInstance(){
        /* Returns uninitialized instance */
        return (tk==null)? ( tk = new TK() ) : tk;
    }
    public static TK getInstance( String delims, String text, String skips ){
        /* One stop shop: sets, runs and returns with no flags */
        return getInstance( delims, text, skips, 0 );
    }
    public static TK getInstance( String delims, String text, String skips, int flags ){
        /* One stop shop: sets, runs and returns */
        if (tk==null){
            tk = new TK();
        }
        tk.setText(text);
        tk.setDelims(delims); 
        tk.setMap(skips); 
        tk.setFlags(flags);
        tk.parse();
        return tk;  // initialized
    }
    
    /*========================================================================*/
    // Complex tokenize methods with multi-delims and skip symbols (see above)
    public static ArrayList<String> toList( String delims, String text, String skips ){
        /* One stop shop: sets, runs and returns with no flags */
        return TK.toList( delims, text, skips, 0 );
    }
    public static ArrayList<String> toList( String delims, String text, String skips, int flags ){
        /* One stop shop: sets, runs and returns */
        return TK.toList( getInstance(), delims, text, skips, flags );
    }
    public static String[] toArr( String delims, String text, String skips ){
        return TK.toList( delims, text, skips, 0 ).toArray(new String[0]);
    }
    public static String[] toArr( String delims, String text, String skips, int flags ){
        return TK.toList( delims, text, skips, flags ).toArray(new String[0]);
    }

    /*========================================================================*/
    // As above, but use an existing instance
    public static ArrayList<String> toList( TK instance, String delims, String text, String skips ){
        /* One stop shop: sets, runs and returns with no flags */
        return TK.toList( instance, delims, text, skips, 0 );
    }
    public static ArrayList<String> toList( TK instance, String delims, String text, String skips, int flags ){
        /* One stop shop: sets, runs and returns */
        instance.setText(text);
        instance.setDelims(delims); 
        instance.setMap(skips); 
        instance.setFlags(flags);
        instance.parse();
        return instance.get();
    }
    public static String[] toArr( TK instance, String delims, String text, String skips ){
        return TK.toList( instance, delims, text, skips, 0 ).toArray(new String[0]);
    }
    public static String[] toArr( TK instance, String delims, String text, String skips, int flags ){
        return TK.toList( instance, delims, text, skips, flags ).toArray(new String[0]);
    }
    /*========================================================================*/
    // Simple tokenize methods with empty-element prevention (see above)
    public static ArrayList<String> toList( char delim, String text ){
        return TK.toList( delim, text, 0x7FFFFFFF );
    }
    public static ArrayList<String> toList( char delim, String text, int limit ){
        ArrayList<String> out = new ArrayList<>();
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
        return out;
    }
    public static String[] toArr( char delim, String text ){
        return toArr( delim, text, 0x7FFFFFFF );
    }
    public static String[] toArr( char delim, String text, int limit ){
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
}
