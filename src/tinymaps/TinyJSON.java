package tinymaps;

import erlog.Erlog;
import toksource.TokenSource;
import java.util.ArrayList;
import toksource.TextSource_file;
import toktools.TK;

/**Simple JSON parser: converts object and array notation to java map and array.
 * Uses TinyMap and TinySet for map and array.
 *
 * @author Dave Swanson
 */
public class TinyJSON {
    private final Erlog log;
    private final TextSource_file fin;
    private int lineNumber;
    private char opener;
    private String curr;
    private ArrayList<TinyMap> objects;
    
    public TinyJSON(String filename){
        // initialize
        fin = new TextSource_file( filename );
        log = Erlog.getCurrentInstance();
        log.setTextStatusReporter(fin);
        
        if( fin.hasData() ){
            parseLines();
        }
        else{
            log.set( "Bad input file name: "+filename );
        }
    }
    public final void parseLines(){
        objects = new ArrayList<>();
        int start = 0;
        lineNumber = -1;
        opener = '\0';
        while( fin.hasNext() ){
            curr = fin.next();
            if(curr == null || curr.isEmpty()){ continue; }
            lineNumber++;
            for(int i=0; i<curr.length(); i++){
                switch(curr.charAt(i)){
                    case '\0':
                        assertNull();
                        break;
                    case '{':
                    case '[':
                        assertNull();
                        start = i+1;
                        opener = curr.charAt(i);
                        break;
                    case '}':
                        assertOpener('{');
                        parseObj(start, i);
                        opener = '\0';
                        break;
                    case ']':
                        assertOpener('[');
                        parseArr(start, i);
                        opener = '\0';
                        break;
                    default:
                        break;
                }
            }
            if(lineNumber>2)
                break;
        }
    }
    public ArrayList<TinyMap> getAll(){//can return TinyMap or TinySet
        return objects;
    }
    public ArrayList<TinyMap> getJSONObjects(){//select only TinyMap instances
        ArrayList<TinyMap> out = new ArrayList<>();
        for( TinyMap obj : objects ){
            if(obj instanceof TinyMap ){
                out.add(obj);
            }
        }
        return out;
    }
    public ArrayList<TinyMap> getJSONArrays(){//select only TinySet instances
        ArrayList<TinyMap> out = new ArrayList<>();
        for( TinyMap obj : objects ){
            if(obj instanceof TinySet ){
                out.add(obj);
            }
        }
        return out;
    }
    public void disp(){
        for(TinyMap obj : objects){
            obj.disp();
        }
    }
    
    private String parseJTerm(String text){
        text = text.trim();
        return ( text.length() > 2 && !text.equals("null") )?  
            text.substring( 1, text.length()-1 ) : "";
    }
    private void parseObj(int start, int end){
        String[] toks = TK.toArr(',', curr.substring(start, end) );
        TinyMap currMap = new TinyMap( toks.length );
        for(String tok : toks){
            try{
                String[] keyval = tok.split(":");
                currMap.put( parseJTerm(keyval[0]), parseJTerm(keyval[1]) );
            }
            catch(ArrayIndexOutOfBoundsException e){
                log.set( "JSON format: missing semicolon at: " + tok);
            }
        }
        objects.add(currMap);
    }
    private void parseArr(int start, int end){
        String[] toks = TK.toArr(',', curr.substring(start, end) );
        TinySet currMap = new TinySet( toks.length );
        for(String tok : toks){
            currMap.add( parseJTerm(tok) );
        }
        objects.add(currMap);
    }

    private void assertNull(){
        if(opener != '\0'){
            log.set("Make sure JSON objects start and end on same line");
        }
    }
    private void assertOpener(char c){
        if(opener != c){
            log.set("This parser does not support nested JSON objects");
        }
    }
}
