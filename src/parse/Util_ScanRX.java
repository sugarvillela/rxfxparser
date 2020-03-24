package parse;

import commons.Util_string;
import itr_struct.Itr_noFile;
import java.util.ArrayList;
import toktools.TK;

/**Sub-scanner utilities for RX pattern language
 *
 * @author Dave Swanson
 */
public class Util_ScanRX{
    protected static String name;
    protected static Range range;
    protected static PatternItr patternItr;

    public static Range getInstance_Range(){
        return (range==null)? ( range = new Range() ) : range ;
    }
    public static PatternItr getInstance_PatternItr(){
        return (patternItr==null)? ( patternItr = new PatternItr() ) : patternItr ;
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
                Class_Scanner.getInstance().setEr("Bad number format: " + pattern );
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
        public static String DELIMS = "=()'&|";
        protected TK tk;
        protected Itr_noFile words;
        
        public PatternItr(){
            tk = TK.getInstance();
            tk.setDelims(DELIMS); 
            tk.setMap(""); 
            tk.setFlags(TK.DELIMIN);
        }
        public void init( Base_Stack P, String text){
            tk.setText(text);
            tk.parse();
            ArrayList<Object> temp = new ArrayList<>();
            for( String obj : tk.get() ){
                temp.add(obj);
            }
            words = new Itr_noFile( temp );
            if(!words.hasFile()){
                P.setEr("Err at:"+text);
            }
        }
        public boolean hasNext(){
            return words.hasNext();
        }
        public String next(){
            return (String)words.next();
        }
    }
}
