package lemmatizer;

import commons.Util_string;
import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public class Lemmatizer extends Affix{
    private final Prefix prefix;
    private final Suffix suffix;
    private final Traceback_P tracebackP;
    private final Traceback_S tracebackS;
    
    public Lemmatizer() {
        this.prefix = new Prefix(); 
        this.suffix = new Suffix();
        tracebackP = new Traceback_P(); 
        tracebackS = new Traceback_S();
    }
    private void merge(){
        /* Merge found root words into one list.
         * For each prefix root, a new word for eath suffix root must be 
         * created.  That multiplies the size of the list quickly. 
         * Duplicates are removed.
         */
        if( !this.prefix.haveAffix() ){
            this.roots = this.suffix.get();
            return;
        }
        if( !this.suffix.haveAffix() ){
            this.roots = this.prefix.get();
            return;
        }
        String merged;
        for( String pRoot : prefix.get() ){
            roots.add(pRoot);
            for(String sRoot : suffix.get()){
                roots.add(sRoot);
                if( 
                    pRoot.length() < sRoot.length() &&
                    ( merged = sRoot.substring( pRoot.length() ) ).length() >= REF.MINWORDLEN &&
                    pushUQ(merged)
                ){}
            }
        }
    }
    private void filterLength(){
        ArrayList<String> nu = new ArrayList<>();
        for ( String root : this.roots ) {
            if( root.length() <= REF.MAXWORDLEN ){
                nu.add(root);
            }
        }
        this.roots=nu;
    }
    @Override
    public void set( String text ){
        /* Clears every time set is called so this object can be reused */
        this.roots = new ArrayList<>();
        /* Create root lists independently */
        this.prefix.set( text );
        this.suffix.set( text );
//        /* Join the lists */
        merge();
        //Rthis.roots = Commons.getUQ( this.roots );
//        /* Delete words longer than any in the database. This step is done
//         * here because merged list constains roots with pref and suff stripped
//         */
//        filterLength();
//        /* Sort words longest to shortest for search precedence */
//        Collections.sort(roots, (String a, String b) -> a.length() - b.length());
    }
    public boolean traceback( String rootText, String origText ){
        /* Traceback initializes lists of the prefixes and suffixes stripped
         * to make rootText from origText. To do so it follows the same steps
         * as setRoots, looking for a thread that generates rootText */
        /* Clears every time set is called so this object can be reused */
//        tracebackP.init();
//        tracebackS.init();
        String findMe = rootText;
        int strpos = rootText.indexOf( origText );
        while( strpos == -1 ){
            /* If we're here, a suffix rule was invoked during setRoots, which 
             * changed the last letter or two of rootText.  To find where it
             * starts in origText, remove trailing letters until the effect of 
             * the rule is gone */
            findMe=findMe.substring( 0, findMe.length()-1 );
            if( findMe.length() == 0 ){
                /* If we're here, that word just aint in the original */
                return false;
            }
            strpos = rootText.indexOf( origText );
        }
        /* Have strpos, traceback prefix list */
        tracebackP.set( origText.substring( 0, strpos ) );
        /* Remove prefix from origText */
        origText=origText.substring( strpos );
        if( origText.equals(rootText) ){
            /* If we're here, no suffixes were stripped by setRoots */
            return true;
        }
        /* sTraceback needs the current root text.
         * Then traceback suffix list */
        tracebackS.setTargetRoot( rootText );
        tracebackS.set( origText );
        return true;//always true if some form of rootText is in origText
    }
    public ArrayList<String> getPref(){
        /* Returns list after traceback */
        return tracebackP.get();
    }
    public ArrayList<String> getSuff(){
        /* Returns list after traceback */
        return tracebackS.get();
    }
}
