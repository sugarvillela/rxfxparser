package parse;

import commons.Commons;
import erlog.Erlog;
import toksource.TextSource_list;
import java.util.ArrayList;
import toktools.TK;
import toktools.Tokens;

/**Sub-scanner utilities for RX pattern language
 *
 * @author Dave Swanson
 */
public class Util_ScanRX{
    protected static String name;
    protected static Range range;           // parses range on RX patterns
    protected static PatternItr patternItr; // take itr build out of nested
    protected static PairMinder pairMinder;
    
    public static Range getInstance_Range(){
        return (range==null)? ( range = new Range() ) : range ;
    }
    public static PatternItr getInstance_PatternItr(){
        return (patternItr==null)? ( patternItr = new PatternItr() ) : patternItr ;
    }
    public static PairMinder getInstance_ParMinder(){
        return (pairMinder==null)? ( pairMinder = new PairMinder() ) : pairMinder ;
    }
    
    public static class Range{
        private final int MAX = 1024;
        private int lo,  hi;
        private String pattern;
        
        public void init(String text) {
            pattern = text;
            //System.out.printf( "\ngetRange %s\n", pattern );
            char lastChar = pattern.charAt(pattern.length()-1);
            switch ( lastChar ){
                case '*':
                    trunc();
                    this.lo = 0;
                    this.hi = MAX;
                    break;
                case '+':
                    trunc();
                    this.lo=1;
                    this.hi = MAX;
                    break;
                case '?':
                    trunc();
                    this.lo=0;
                    this.hi=1;
                    break;
                case '}':
                    getRangeFromCurlys();
                    break;
                default:
                    this.lo=1;
                    this.hi=1;
                    break;
            }
        }
        public int getLo(){ return lo; }
        public int getHi(){ return hi; }
        public String getPattern(){ return pattern; }
        
        private void trunc(){
            pattern = pattern.substring( 0, pattern.length()-1 );
        }
        private int getNumberInString( int start, int end ){
            try{
                return Integer.parseInt( pattern.substring(start, end) );
            }
            catch(NumberFormatException e){
                Erlog.get(this).set("Bad number format", pattern );
                return 0;
            }
        }
        private void getRangeFromCurlys(){
            //System.out.printf( "\nparseRange %s\n", pattern );
            int i, j;
            for(i=0; i<pattern.length()-1; i++){
                if(pattern.charAt(i)=='{'){
                    //System.out.printf( "found { at %d\n", i );
                    break;
                }
            }
            for(j=i+1; j<pattern.length()-1; j++){
                if(pattern.charAt(j)=='-'){
                    lo = getNumberInString( i+1, j );
                    hi = getNumberInString( j+1, pattern.length()-1 );
                    pattern = pattern.substring(0, i);
                    return;
                }
            }
            lo = hi = getNumberInString( i+1, pattern.length()-1 );
            pattern = pattern.substring(0, i);
        }
    }
    public static class PatternItr{
        public static String DELIMS = "=~()&|";
        protected Tokens tk;
        protected TextSource_list words;
        
        public PatternItr(){
            tk = TK.getInstance(DELIMS, "'", TK.DELIMIN);
        }
        public void init( String text){
            words = new TextSource_list( tk.toList(text) );
            if(!words.hasData()){
                Erlog.get(this).set("Error", text);
            }
        }
        public boolean hasNext(){
            return words.hasNext();
        }
        public String next(){
            return (String)words.next();
        }
    }
    public static class PairMinder{
        public String trimSurrounding(String text){
            //System.out.println("trimSurrounding: "+text);
            if(text.charAt(0) != '(' || text.charAt(text.length()-1) != ')'){
                //System.out.println("trimSurrounding first return: "+text);
                return text;
            }
            int stackLevel = 1;
            for(int i=1; i<text.length()-1; i++){
                switch(text.charAt(i)){
                    case '(':
                        stackLevel++;
                        break;
                    case ')':
                        stackLevel--;
                        if(stackLevel == 0){
                            //System.out.println("trimSurrounding loop return: "+text);
                            return text;
                        }
                        break;
                }
            }
            //System.out.println("trimSurrounding final return: "+text);
            return text.substring( 1, text.length()-1 );
        }
        public boolean validParenth( String text ){// even ( ) ratio
            int stackLevel = 0;
            for(int i=0; i<text.length(); i++){
                switch(text.charAt(i)){
                    case '(':
                        stackLevel++;
                        break;
                    case ')':
                        stackLevel--;
                        break;
                    default:
                        break;
                }
                //System.out.printf("%c %d \n", text.charAt(i), stackLevel);
            }
            return stackLevel == 0;
        }
        public boolean validQuotes( String text ){// even ( ) ratio
            int count = 0;
            for(int i=0; i<text.length(); i++){
                if('\'' == text.charAt(i)){
                    count++;
                }
            }
            return count%2 == 0;
        }
        public char disallowedChar( String disallow, String text ){
            boolean ignore = false;
            for(int i=1; i<text.length(); i++){
                char c = text.charAt(i);
                if( '\'' == c ){
                    ignore = !ignore;
                }
                else if( !ignore && disallow.indexOf(c) != -1){
                    return c;
                }
            }
            return '\0';
        }
        public String encodeFunction( String text ){// something useful
            char[] chars = text.toCharArray();
            char last = chars[0];
            boolean ignore = true, changed = false;
            for(int i=1; i<chars.length; i++){
                if('\'' == chars[i]){
                    ignore = !ignore;
                }
                else if( !ignore && ')' == chars[i] && '(' == last){
                    chars[i-1]='\0';
                    chars[i]='~';
                    changed = true;
                }
            }
            return changed? new String(Commons.copyNonNull(chars)) : text;
        }
    }
}
