/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toktools;

/**
 *
 * @author admin
 */
public class Tokens_byGroup extends Tokens_special{
    private String[] groups;
    
    public Tokens_byGroup( String[] grps, String skips, int flags ){
        super( "", skips, flags );
        setGroups(grps);
    }
    public final void setGroups(String[] grps){
        groups = grps;
    }
    private int groupNumber(char needle ){
        for( int i=0; i<groups.length; i++ ){
            if( groups[i].indexOf(needle) != -1 ){
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public void parse( String text ){
        //System.out.printf("\nParse: holding=%b, text=%s\n", cSymb!=0, text);
        initParse();
        int start=0;        // beginning of substring
        int i;              // for current char being checked
        int currGroup, lastGroup = groupNumber( text.charAt(0) );// && i!=start 
        
        for ( i = 1; i < text.length(); i++) {
            if( isHolding() ){                     // in skip area            
                if( text.charAt(i) == cSymb.peek() && clearHolding() ){ // found closing skip symbol
                    addToTokens( text.substring( start, i+symbIn ) );
                    start=i;                    // reset for next token
                    lastGroup = -2;
                }
            }
            else if( setHolding( text.charAt(i) ) ){// opener
                if( i != start ){               // if prev wasn't a delim, dump
                    addToTokens( text.substring( start, i ) );
                    start=i;
                }
                if(symbIn == 0){                // lose the current symbol
                    start += 1;
                }
            }
            else {
                currGroup=groupNumber( text.charAt(i) );
                if( currGroup != lastGroup ){
                    System.out.printf("newGroup %c\n", text.charAt(i) );
                    addToTokens( text.substring( start, i ) );
                    start=i;                      // reset for next token
                }
                lastGroup = currGroup;
            }
        }
        if( i!=start ){                         // final dump if needed
            addToTokens( text.substring( start, i ) );
        }
    }
}
