package toktools;
/* To run:
*  TokenTool instance = TK.getInstance();
*  instance.setText(text);
*  instance.setDelims(delims); 
*  instance.setMap(skips); 
*  instance.setFlags(flags);
*  instance.parse();
*  return instance.get();
*/

import java.util.ArrayList;
import static toktools.TK.DELIMIN;

import static toktools.TK.SYMBOUT;

/**
 *
 * @author Dave Swanson
 */
public class Tokens_special  implements Tokens{
    protected  String delims;     // input text, list of delimiters text, 
    protected char[] oMap, cMap;        // matched open/close skip char arrays
    protected ArrayList<String> tokens; // output
    protected char cSymb;               // Closing symbol (replace with stack?)
    protected int symbIn;               // leave open/close chars in if 1
    protected boolean delimIn;          // keep delims, skips to separate list
    
    public Tokens_special( String delims, String skips, int flags ) {
        setDelims( delims );
        setMap( skips );
        setFlags( flags );
    }
    // initializers before parsing  
    protected final void setDelims( String delims ){
        this.delims = delims;
    }
    protected final void setMap( String skips ){
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
    protected final void setFlags( int flags ){
        // ignores skip out and holdover flags
        symbIn = ( ( flags & SYMBOUT )==0 )? 1 : 0;   // integer for adding index
        delimIn = ( ( flags & DELIMIN )!=0 );
    }
    
    // utility for better readability in parse()
    protected final boolean isDelim( char symb ){
        //System.out.printf("\nisDelim symb=%c\n", symb);
        return ( delims.indexOf( symb )!= -1 );
    }
    protected final boolean isOpening( char symb ){
        // Set closer to match opener, or null if not an opener
        for(int i=0; i<oMap.length; i++){
            if( symb == oMap[i] ){
                this.cSymb = cMap[i];// important side effect
                return true;
            }
        }
        return false;
    }
    // override these two for more complex behavior in Tokens_wSkipHold
    protected void initParse(){
        cSymb = 0;
        this.tokens = new ArrayList<>();     // for main tokenized output
    }
    protected void addToTokens(String txt){
        tokens.add(txt);
    }
    
    // main method
    @Override
    public void parse( String text ){ // save a step if repeating parse
        //System.out.printf("\nParse: holding=%b, text=%s\n", cSymb!=0, text);
        initParse();

        int start=0;        // beginning of substring
        int i;              // for current char being checked
        for ( i = 0; i < text.length(); i++) {
            if( isHolding() ){                     // in skip area            
                if( text.charAt(i) == cSymb ){ // found closing skip symbol
                    
                    // if symbIn==1, will keep current symbol, else lose it
                    addToTokens( text.substring( start, i+symbIn ) );
                    cSymb = 0;                  // leaving skip area
                    start=i+1;                    // reset for next token
                }
            }
            else if( isOpening( text.charAt(i) ) ){// opener
                if( i != start ){               // if prev wasn't a delim, dump
                    addToTokens( text.substring( start, i ) );
                    start=i;
                }
                if(symbIn == 0){                // lose the current symbol
                    start += 1;
                }
            }
            else if( isDelim( text.charAt(i) ) ){//delimiter
                //System.out.printf("yes %c\n", text.charAt(i) );
                if( i!=start ){                 // if text, dump
                    addToTokens( text.substring( start, i ) );
                }
                if( delimIn ){                  // give delim its own element
                    addToTokens( text.substring(i, i+1) );
                }
                start=i+1;                      // reset for next token
            }
        }
        if( i!=start ){                         // final dump if needed
            addToTokens( text.substring( start, i ) );
        }
    }
    
    // get result
    @Override
    public ArrayList<String> getTokens(){
        return this.tokens;
    }
    
    // get state: in a skip area or not
    @Override
    public final boolean isHolding(){
        return cSymb != 0;
    }
    
    // Complex tokenize methods with multi-delims and skip symbols

    @Override
    public ArrayList<String> toList(String text ){
        /* One stop shop: sets, runs and returns */
        this.parse(text);
        return this.getTokens();
    }
    @Override
    public String[] toArr(String text ){
        return this.toList( text ).toArray(new String[0]);
    }
    
    // Unsupported because it's not a Tokens_wSkipHold
    @Override
    public ArrayList<String> getSkips() {
        throw new UnsupportedOperationException("Use Tokens_wSkipHold!");
    }
}