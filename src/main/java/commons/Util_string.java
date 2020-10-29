package commons;

import static commons.Commons.copyNonNull;
import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public class Util_string {
    public static boolean isAllCap( String text ){
        for(int i=0; i<text.length(); i++){
            char c = text.charAt(i);
            if( c != '_' && Character.isLowerCase(c) ){
                return false;
            }
        }
        return true;
    }
    public static String assertFileExt( String f, String ext ){
        
        if(ext.charAt(0) == '.'){
            ext = ext.substring(1);
        }
        int fLast = f.length()-1;
        int extLen = ext.length();
        System.out.println("filename... " + f.substring(fLast-extLen) );
        System.out.println("fLast... " + fLast );
        System.out.println("extLen... " + extLen );
        if( f.substring(fLast-extLen).equals( '.'+ext) ){
            return f;
        }
        return f+"."+ext;
    }

    public static String toPascalCase( String text ){
        return fromSnakeCase(text, true).trim();
    }
    public static String toCamelCase( String text ){
        return fromSnakeCase(text, false).trim();
    }
    private static String fromSnakeCase( String text, boolean capFirst ){
        char[] out = new char[text.length()+1];
        boolean forceCap = capFirst, lastCap = true;
        for(int i=0, j=0; i<text.length(); i++){
            char c = text.charAt(i);
            if( c == '_' ){
                forceCap = true;
            }
            else if(forceCap){
               out[j++] = Character.toUpperCase(c); 
               forceCap = false;
               lastCap = true;
            }
            else if(lastCap){
               out[j++] = Character.toLowerCase(c);
               lastCap = Character.isUpperCase(c);
            }
            else{
               out[j++] = c;
               lastCap = Character.isUpperCase(c);
            }
        }
        return new String(out);
    }
    public static String toSnakeCase( String text ){
        // on all cap: lowercase all and abort
        if(isAllCap(text)){
            return text.toLowerCase();
        }
        
        // not all cap: how many caps?
        int numCaps = 0;
        boolean underscoreLast = false;
        for(int i=1; i<text.length(); i++){
            char c = text.charAt(i);
            if( c == '_' ){
                underscoreLast = true;
            }
            else{
                if( !underscoreLast && Character.isUpperCase(c) ){
                    numCaps++;
                }
                underscoreLast = false;
            }
        }
        
        // set out array size and populate; cap inserts underscore, no dups
        char[] out = new char[text.length()+numCaps+1];
        underscoreLast = true;
        for(int i=0, j=0; i<text.length(); i++){
            char c = text.charAt(i);
            if( c == '_' ){
                if( !underscoreLast ){
                    out[j++] = c;
                }
                underscoreLast = true;
            }
            else{
                if( !underscoreLast && Character.isUpperCase(c) ){
                    out[j++] = '_';
                }
                out[j++] = Character.toLowerCase(c);
                underscoreLast = false;
            }
        }
        return new String(out).trim();
    }
    public static String toScreamingSnake( String text ){
        return toSnakeCase(text).toUpperCase();
    }
    /**Returns trimmed string if first and last chars equal openClose
     * @param openClose single or double quote
     * @param text original text
     * @return trimmed string or original
     * @author Dave Swanson
     */
    public static String trimSurrounding( char openClose, String text ){
        return trimSurrounding( openClose, openClose, text );
    }
    
    /**Returns trimmed string if first and last chars equal open and close
     * @param open opening bracket or parentheses
     * @param close counterpart bracket or parentheses
     * @param text original text
     * @return trimmed string or original
     * @author Dave Swanson
     */
    public static String trimSurrounding( char open, char close, String text ){
        int len = text.length();
        return ( len > 2 && text.charAt(0) == open && text.charAt(len-1) == close)?
            text.substring( 1, len-1 ) : text;
    }
    
    /**Returns trimmed string if first and last chars equal any given pairs
     * @param openList char array as string, contains opening characters
     * @param closeList same length as openList, contains counterpart characters
     * @param text original text
     * @return trimmed string or original
     * @author Dave Swanson
     */
    public static String trimSurrounding( String openList, String closeList, String text ){
        if( text.length() > 2 ){
            char open = text.charAt(0), close = text.charAt(text.length()-1);
            for(int i=0; i<openList.length(); i++){
                if(openList.charAt(i) == open && closeList.charAt(i) == close){
                    return text.substring( 1, text.length()-1 );
                }
            }
        }
        return text;
    }
    /**same as String.trim() but you can pass a non-space char
     * @param kill char to remove from either end
     * @param text original text
     * @return trimmed string or original
     * @author Dave Swanson
     */
    public static String trim( char kill, String text ){     
        int start = -1, end = text.length();
        do{
            start++;
            if( start == text.length() ){
                return "";
            }
        }while( text.charAt(start)==kill );
        
        do{
            end--;
        }while( end >= 0 && text.charAt(end)==kill );
        return text.substring(start, end+1);
    }
    /**removes any contiguous killList chars from front or back
     * @param killList chars to remove from either end
     * @param text original text
     * @return trimmed string or original
     * @author Dave Swanson
     */
    public static String trim( String killList, String text ){
        // same as String.trim() but you can pass a list of chars to trim 
        int start = -1, end = text.length();
        do{
            start++;
            if( start == text.length() ){
                return "";
            }
        }while( killList.indexOf( text.charAt(start) ) != -1 );
        
        do{
            end--;
        }while( killList.indexOf( text.charAt(end) ) != -1 );
        
        return text.substring(start, end+1);
    }
    /**same as trim with killList but applies to array. Removes empty lines
     * @param killList chars to remove from either end
     * @param arr original string array
     * @return array with trimmed; may be shorter or empty if empties removed
     * @author Dave Swanson
     */
    public static String[] trim( String killList, String[] arr ){
        // same as array map trim but empty elements are deleted
        for (int i=0; i<arr.length; i++){
            if( ( arr[i] = trim( killList, arr[i] ) ).length() == 0 ){
                arr[i] = null;
            }
        }
        return copyNonNull( arr );
    }
    public static ArrayList<String> trim( String killList, ArrayList<String> arr ){
        ArrayList<String> trimmed = new ArrayList<>();
        for ( String text : arr ){
            if( ( text = trim( killList, text ) ).length() != 0 ){
                trimmed.add( text );
            }
        }
        return trimmed;
    }
}
