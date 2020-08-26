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

import commons.Commons;
import java.util.ArrayList;
import java.util.Stack;
import static toktools.TK.DELIMIN;
import static toktools.TK.IGNORESKIP;
import static toktools.TK.SYMBOUT;

/**
 *
 * @author Dave Swanson
 */
public class Tokens_special  implements Tokens{
    protected  String delims;     // input text, list of delimiters text, 
    protected char[] oMap, cMap;        // matched open/close skip char arrays
    protected ArrayList<String> tokens; // output
    protected Stack<Character> cSymb;               // Closing symbol (replace with stack?)
    protected int symbIn;               // leave open/close chars in if 1
    protected boolean delimIn;          // keep delims, skips to separate list
    protected boolean ignoreSkip;       // 
    
    public Tokens_special( String delims, String skips, int flags ) {
        setDelims( delims );
        setMap( skips );
        setFlags( flags );
    }
    // initializers before parsing  
    @Override
    public final void setDelims( String delims ){
        this.delims = delims;
    }
    @Override
    public final void setDelims( char delim ){
        this.delims = String.valueOf(delim);
    }
    @Override
    public final void setMap( String skips ){
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
        //Commons.disp(oMap, "oMap");
        //Commons.disp(cMap, "cMap");
    }
    @Override
    public final void setFlags( int flags ){
        // ignores skip out and holdover flags
        symbIn = ( ( flags & SYMBOUT )==0 )? 1 : 0;   // integer for adding index
        delimIn = ( ( flags & DELIMIN )!=0 );
        ignoreSkip = ( ( flags & IGNORESKIP )!=0 );//IGNORESKIP
    }
    
    // utility for better readability in parse()
    protected final boolean isDelim( char symb ){
        //System.out.printf("\nisDelim symb=%c\n", symb);
        return ( delims.indexOf( symb )!= -1 );
    }
    protected final boolean setHolding( char symb ){
        // Set closer to match opener, or null if not an opener
        for(int i=0; i<oMap.length; i++){
            if( symb == oMap[i] ){
                this.cSymb.push(cMap[i]);// = cMap[i];// important side effect
                //System.out.println("\nsetHolding: "+symb);
                //System.out.println(cSymb);
                return true;
            }
        }
        return false;
    }
    protected final boolean clearHolding(){
        //System.out.println("\nclearHolding: "+cSymb.peek());
        cSymb.pop();
        //System.out.println(cSymb);
        //System.out.println(cSymb.empty());
        return cSymb.empty();
    }
    // override these two for more complex behavior in Tokens_wSkipHold
    protected void initParse(){
        cSymb = new Stack<>();
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
            //System.out.println(i + ": " + text.charAt(i));
            if( isHolding() ){                     // in skip area            
                if( cSymb.peek().equals(text.charAt(i))){ // found closing skip symbol
                    if( clearHolding() && !ignoreSkip && i != start ){               // if prev wasn't a delim, dump
                        // if symbIn==1, will keep current symbol, else lose it
                        addToTokens( text.substring( start, i+symbIn ) );
                        start=i+1;                    // reset for next token
                    }

                }
                else if( setHolding( text.charAt(i) ) ){// opener
                }
            }
            else if( setHolding( text.charAt(i) ) ){// opener
                if( !ignoreSkip && i != start ){               // if prev wasn't a delim, dump
                    addToTokens( text.substring( start, i ) );
                    start=i;
                    if(symbIn == 0){                // lose the current symbol
                        start += 1;
                    }
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
        return !cSymb.isEmpty();
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
