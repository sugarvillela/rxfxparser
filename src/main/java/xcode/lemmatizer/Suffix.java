package xcode.lemmatizer;

import commons.tinymaps.TinyMap;
import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public class Suffix extends Affix {
    
    protected Rules[] rules;
    public Suffix() {
        rules = new Rules[]{
            new S_ly_le(),
            new S_i_y(),
            new S_doubleCons(),
            new S_custom()
        };
    }

    private void sRoots( String text, int end ){
        //System.out.printf("text=%s, len=%d, end=%d\n", text, text.length(), end );
        if( end<=REF.MINWORDLEN+1 ){
            /* If we're here, this word can't possibly have a suffix. The 
             * presence of prefixes can allow this to overshoot */
            return;
        }
        /* This is like the prefix algo except everything is backwards.
         * The recursive calls can lead to duplicate entries. pushUQ handles 
         * that problem
         * The list of sRules algos handles suffix rules like i to y etc
         */
        String suffix, root;
        for (int len = Math.min( REF.MAXSUFLEN, end ); len >0; len--) {
            //System.out.printf("len=%d\n", len );
            suffix = text.substring( end-len, end );
            //System.out.printf("%s, len=%d\n", suffix, len );
            if( REF.isSuffix(suffix) ){
                root = text.substring( 0, end-len ); 
                //System.out.printf("%s | %s, pos=%d\n", root, suffix, end-len );
                if( endsWell( root ) && pushUQ( root ) ){
                    /* Invalid or duplicate word ends the thread */
                    sRoots( text, end-len );
                }
                for( Rules rule : rules ) {
                    rule.go( this, root, suffix );
                }
            }
        }
    }
    @Override
    public void set( String text ){
        /* Clears every time set is called so this object can be reused */
        roots = new ArrayList<>();
        sRoots( text, text.length() );
    }

    public interface Rules{
        public boolean go(Suffix suffix, String root, String s);
        public boolean traceback( Traceback_S suffix, ArrayList<String> curr, String root, String s, int end );
    }
    public class S_ly_le implements Rules{
        @Override
        public boolean go(Suffix suffix, String root, String s){
            /* Here, a word like simply becomes simple */
            int len = root.length();
            if( 
                    len >= 3 &&
                    s.equals("ly") && 
                    suffix.hardConsonant( root.charAt(len-1) ) && 
                    root.charAt(len-1) != root.charAt(len-2) 
                ){
                root += "le";
                if( suffix.pushUQ( root ) ){
                    suffix.sRoots( root, len );
                    return true;
                }
            }
            return false; 
        }
        @Override
        public boolean traceback( Traceback_S suffix, ArrayList<String> curr, String root, String s, int end ){
            /* Here, a word like simply becomes simple */
            int len=root.length();
            if( 
                    len >= 3 &&
                    s.equals("ly")  && 
                    suffix.hardConsonant( root.charAt(len-1) ) && 
                    root.charAt(len-1) != root.charAt(len-2) 
                ){
                root+="le";
                if( 
                    !suffix.dup( root ) &&
                    suffix.traceback( root, curr, root.length() )
                ){
                    return true;
                }
            }
            return false; 
        }
    }
    public class S_i_y implements Rules{
        @Override
        public boolean go(Suffix suffix, String root, String s){
            int len=root.length();
            if( len >= 3 && root.charAt(len-1) == 'i' ){
                root = root.substring(0, len-1) + 'y';
                if( suffix.pushUQ( root ) ){
                    suffix.sRoots( root, len );
                    return true; 
                }
            }
            return false; 
        }
        @Override
        public boolean traceback( Traceback_S suffix, ArrayList<String> curr, String root, String s, int end ){
            int len=root.length();
            if( len >= 3 && root.charAt(len-1) == 'i' ){
                root = root.substring(0, len-1) + 'y';
                if( 
                    !suffix.dup( root ) &&
                    suffix.traceback( root, curr, root.length() )
                ){
                    return true;
                }
            }
            return false; 
        }
    }
    public class S_doubleCons implements Rules{
        @Override
        public boolean go(Suffix suffix, String root, String s){
            int len=root.length();
            if(//doubled consonants
                    len >= 4 &&
                    !suffix.vowel( root.charAt(len-1) ) && 
                    root.charAt(len-1) == root.charAt(len-1)
            ){
                root = root.substring(0, len-1);
                if( suffix.pushUQ( root ) ){
                    suffix.sRoots( root, len-1 );
                    return true; 
                }
            }
            return false; 
        }
        @Override
        public boolean traceback( Traceback_S suffix, ArrayList<String> curr, String root, String s, int end ){
            int len=root.length();
            if(//doubled consonants
                    len >= 4 &&
                    !suffix.vowel( root.charAt(len-1) ) && 
                    root.charAt(len-1) == root.charAt(len-1)
            ){
                root = root.substring(0, len-1);
                if( 
                    !suffix.dup( root ) &&
                    suffix.traceback( root, curr, len-1 )
                ){
                    return true;
                }
            }
            return false; 
        }
    }
    public class S_custom implements Rules{
        @Override
        public boolean go(Suffix suffix, String root, String s){
            System.out.printf("root=%s, s=%s \n", root, s);
            // Suffix found by calling function
            // root and s are root text and suffix text
            // LiteMap is not null
            TinyMap map = REF.getSuffix();
            if( map.isEmpty() ){
                return false;
            }
            boolean vLast=suffix.vowel( root.charAt(root.length()-2 ), root.charAt(root.length()-1 ) );
            String[] addThis = (String[])map.keys();
            //String[] callItThat = map.values();
            for(int i=0; i<addThis.length; i++){
                System.out.printf("?? %s \n", addThis[i]);
                if( vLast!=suffix.vowel( root.charAt(root.length()-1 ), addThis[i].charAt(0) ) ){
                    System.out.printf("adding %s \n", addThis[i]);
                    String nuRoot = root + addThis[i];
                    if( suffix.pushUQ( nuRoot ) ){
                        suffix.sRoots( nuRoot, nuRoot.length() );
                    }
                }
            }
            return true;
        }
        @Override
        public boolean traceback( Traceback_S suffix, ArrayList<String> curr, String root, String s, int end ){
            TinyMap map = REF.getSuffix();
            if( map.isEmpty() ){
                return false;
            }
            boolean vLast=suffix.vowel( root.charAt(root.length()-2 ), root.charAt(root.length()-1 ) );
            String[] addThis = (String[])map.keys();
            String[] callItThat = (String[])map.values();
            
            for(int i=0; i<addThis.length; i++){
                if( vLast!=suffix.vowel( root.charAt(root.length()-1 ), addThis[i].charAt(0) ) ){
                    String nuRoot = root + addThis[i];
                    if( curr.size() > 0 ){
                        curr.set(curr.size()-1, callItThat[i]);
                    }
                    else{
                        curr.add(callItThat[i]);
                    }
                    if( 
                        !suffix.dup( nuRoot ) &&
                        suffix.traceback( nuRoot, curr, nuRoot.length() )
                    ){
                        //return true;
                    }
                }
            }
            return true;
        }
    }
}
