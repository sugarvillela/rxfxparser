package xcode.lemmatizer;

import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public abstract class Affix {//parent for prefix, suffix; grandparent for pTraceback, sTraceback
    protected ArrayList<String> roots;
    
    public Affix() {
        this.roots = new ArrayList<>();
    }
    
    public abstract void set( String text );
    
    public ArrayList<String> get(){
        return this.roots;
    }
    public boolean haveAffix(){
        return this.roots.size() > 0;
    }
    public boolean dup( String root ){
        return roots.indexOf(root) != -1;
    }
    public boolean pushUQ( String root ){
        /* Only add unique items to roots array 
         * If item exists in roots, cancel and return false
         * If item new, add to roots and return true */
        return roots.indexOf(root) == -1 && roots.add(root);
    }
    public boolean vowel( char c ){
        // simple check; use for first letter of word
        switch ( c ) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return true;
            default:
                return false;
        }
    }
    public boolean vowel( char prev, char c ){
        // borrowed from porter stemmer
        // 2nd letter=y can be vowel if first letter is consonant
        switch ( c ) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return true;
            case 'y':
                return prev!='\0' && !vowel(prev, '\0');
            default:
                return false;
        }
    }
    public boolean hardConsonant( char c ){// b,p, t,d, k and g used by stemmer
        switch ( c ) {
            case 'b':
            case 'c':
            case 'd':
            case 'g':
            case 'k':
            case 'p':
            case 't':
                return true;
            default:
                return false;
        }
    }
    public boolean startsWell( String root ){//Commons.binarySearch( REF.STARTS, root.substring(0, 2) )!=-1;
        /* Keeps prefix stripper from making gibberish word */
        return root.length() >=REF.MINWORDLEN    &&
            (   
                vowel( root.charAt(0))        || 
                vowel( root.charAt(0),root.charAt(1))        || 
                REF.inStarts(root.substring(0, 2))
            );
    }
    public boolean endsWell( String root ){
        int len = root.length();
        if( len < REF.MINWORDLEN ){ return false; }
        char minus0 = root.charAt(len-1);
        char minus1 = root.charAt(len-2);
        char minus2 = (len>2)? root.charAt(len-3) : '\0';

        return (  
                minus1 == minus0              ||
                vowel( minus1, minus0 )       || 
                vowel( minus2, minus1 )     || 
                REF.inEnds(root.substring( root.length()-2 ))
            );
    }
    public String substr(String text, int start, int end){
        return (end>=text.length())? text.substring( start ) : text.substring( start, end );
    }
    
}
