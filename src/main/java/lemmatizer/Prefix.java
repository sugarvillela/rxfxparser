package lemmatizer;

/**
 *
 * @author Dave Swanson
 */
public class Prefix extends Affix{
    public void pRoots( String text, int start ){
        System.out.printf("text=%s, len=%d, start=%d\n", text, text.length(), start );
        if( start >= text.length() - REF.MINWORDLEN -1 ){
            /* If we're here, this word can't possibly have a prefix */
            return;
        }
        /* Start with longest prefix, strip that length from front of text
         * and see if it's a prefix from the list. If it is, see if the 
         * remaining text is a valid word, then recurse the remaining text. 
         */
        String root, prefix;
        for (int len = findLen( text.length(), start ); len >0; len--) { 
            prefix = text.substring( start, start + len );
            //System.out.printf("%s, len=%d\n", prefix, len );
            /* Valid prefix and valid root word */
            if( REF.isPrefix(prefix) && this.startsWell( root = text.substring( start+len ) ) ){
                System.out.printf("%s | %s, pos=%d\n", prefix, root, start + len );
                /* Save info in the array field to keep from having to make a
                 * table. start+len is the string pos where the current root
                 * word begins */
                this.roots.add(root);
                this.pRoots( text, start+len );
            }
        }
    }
    @Override
    public void set( String text ){
        this.pRoots( text, 0 );
    }
    public int findLen(int textLen, int start ){
        int len = textLen - start - REF.MINWORDLEN;
        return (len <= 0)? 0 : Math.min(REF.MAXPREFLEN, len);
    }
}
