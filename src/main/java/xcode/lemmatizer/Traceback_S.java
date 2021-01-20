package xcode.lemmatizer;

import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public class Traceback_S extends Suffix {
    protected String targetRoot;
    public boolean traceback( String text, ArrayList<String> curr, int end ){
        /* See comments in suffix->sRoots */
        //System.out.printf("text=%s, len=%d, end=%d\n", text, text.length(), end );
        if( end < REF.MINWORDLEN ){
            return false;
        }
        if( text.equals(targetRoot) ){
            roots = curr;
            return true;
        }
        String test, root;
        for (int len = Math.min( REF.MAXSUFLEN, end ); len >0; len--) {
            test = text.substring( end-len, end );
            if( REF.isSuffix(test) ){
                curr.add(test);
                root = text.substring(0, end-len );
                if( root.equals(targetRoot) ){
                    roots = curr;
                    return true;
                }
                if( 
                        endsWell( root ) &&
                        !dup( root ) &&
                        traceback( text, curr, end-len )
                    ){
                    return true;
                }
                for( Rules rule : rules ) {
                    if( rule.traceback( this, curr, root, test, end ) ){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Override
    public void set( String origText ){//suffix
        roots = new ArrayList<>();
        traceback( origText, new ArrayList<>() , origText.length() );
        ArrayList<String> noEmpties = new ArrayList<>();
        for(String value : roots){
            if( !value.isEmpty() ){
                noEmpties.add(value);
            }
        }
        roots = noEmpties;
    }
    public void setTargetRoot( String rootText ){
        targetRoot = rootText;
    }
}
