package xcode.lemmatizer;

import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public class Traceback_P extends Prefix {
    public boolean traceback( String text, ArrayList<String> curr, int start ){
        /* See comments in prefix->pRoots */
        if( start>=text.length()){
            return false;
        }
        int remaining = text.length() - start;
        for (int len = Math.min( REF.MAXPREFLEN, remaining ); len >0; len--) {
            String test=text.substring( start, start+len );
            if( REF.isPrefix(test) ){
                curr.add(test);
                if( start+len==text.length() ){
                    roots = curr;
                    return true;
                }
                if( traceback( text, curr, start+len ) ){
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void set( String affixText ){
        traceback( affixText, new ArrayList<>(), 0 );
    } 
}
