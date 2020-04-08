/*
 */
package toktools;

import java.util.ArrayList;

/**Custom tokenizers for grabbing specific patterns in a string
 * Three types of static method:
 * 1. map1(): tokenizes and keeps array you can refer to later
 *  a. Pass a string: returns first word; pass an integer i: returns ith word
 * 2. map2() and map3(): tokenize and return index based on string map.
 *      You can specify complex behavior in a map like this |1*0 (see code)
 * 
 * @author Dave Swanson
 */
public class Mapper {
    public static String[] toks = null;

    public static String map1( int i ){// get index from prev split
        return ( toks != null && i<toks.length)? toks[i] : null;
    }
    public static String map1( String text ){// split on space, return 0
        return map1( ' ', text, 0 );
    }
    public static String map1( String text, int i ){// split on space
        return map1( ' ', text, i );
    }
    public static String map1( char ch, String text ){// split on char, return 0
        return map1( ch, text, 0 );
    }
    public static String map1( char ch, String text, int i ){
        // Keep local copy of tokenized to refer to it later
        toks = TK.toArr( ch, text );
        return (i<toks.length)? toks[i] : null;
    }
    public static String[] map2( String text, String map ){
        return Mapper.map2( text, map, 0x7FFFFFFF, 0 );
    }
    public static String[] map2( String text, String map, int limit ){
        return Mapper.map2( text, map, limit, 0 );
    }
    public static String[] map2( String text, String map, int limit, int i ){
        /* Returns an array, size depending on even or odd map length 
         * Map gives split symbol and index for the resulting array.
         * Function splits on symbol and keeps indexed item.
         * If function encounters split symbol without index, it returns the 
         * array (see Commons_.map2).
         * Don't pass empty strings. */
        //System.out.printf( "map i = %s\n", map.substring(i, i+1) );
        String[] tok = TK.toArr( map.charAt(i), limit, text );
        i++;
        if( i >= map.length() || ( map.charAt(i)-'0' ) >= tok.length){
            return tok;
        }
        else if( (i+1)>=map.length() ){
            return new String[]{tok[( map.charAt(i)-'0' )]};
        }
        else{
            return Mapper.map2( tok[( map.charAt(i)-'0' )], map, limit, i+1 );
        }
    }
    public static void map3( ArrayList<String> returnme, String text, String map ){
        map3( returnme, text, map, 0x7FFFFFFF, 0 );
    }
    public static void map3( ArrayList<String> returnme, String text, String map, int limit ){
        map3( returnme, text, map, limit, 0 );
    }
    public static void map3( ArrayList<String> returnme, String text, String map, int limit, int i ){
        /* See map2(), above.  Similar, except map controls more options.
         * You can grab things along the way and add them to a return array. 
         * First symbol is delimiter
         * Second symbol is which index to save to array  
         *   '*' means all, '_' means none 
         * Third symbol is which index of the tok array to split next
        */
        /* Tokenize on first symbol */
        ArrayList<String> tok = TK.toList( map.charAt(i), text );
        i++;
        /* Store the item specified by the second symbol */
        if( i>=map.length() ){
            returnme.addAll(tok);
            return;
        }
        else if( map.charAt(i)!='_' ){
            if( map.charAt(i)=='*' || ( map.charAt(i)-'0' ) >= tok.size() ){
                returnme.addAll(tok);
                return;
            }
            else{
                returnme.add(tok.get(map.charAt(i)-'0'));
            }
        }
        i++;
        /* Follow the path specified by the third symbol */
        if( i < map.length() && ( map.charAt(i)-'0' ) < tok.size()){
            Mapper.map3( returnme, tok.get(map.charAt(i)-'0'), map, limit, i+1 );
        }
    }
}
