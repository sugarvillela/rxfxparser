
package commons.tinymaps;

/**A TinyMap with String-oriented constructors, for CSV or varArg
 *
 * @author admin
 */
public class CSVMap extends TinyMap{
    public CSVMap( int size ){//defaults to string key and value
        super(size, null, null);
    }
    /* Specialized constructors for string keys and values */
    public CSVMap( String ...args ){//initialize map with csv, varArgs or array
        // Parses comma-separated string. (Pass only one arg as csv string)
        //   Set values like this:  CSVMap("a=b,c=d")
        //   Set a default return like this:  CSVMap("a=b,c=d,default")
        //   Any item without = sign sets default
        // Parses array input or variable-length args
        //   Set values like this: CSVMap("a","b","c","d")
        //   Set a default like this: CSVMap("a","b","c","d","default")
        // Default is null if not specified
        used = 0;
        point = 0;
        int len = args.length;
        if( len == 1 ){             // Case: comma-separated string
            this.parseCSL(args[0]);
        }
        else{                       // Case: varArgs or array
            if( len%2 == 0 ){
                def = null;
            }
            else{
                len--;
                def = args[len];
            }
            keys = new String[len/2];
            vals = new String[len/2];
            for(int i=1; i<len; i+=2){
                this.push( args[i-1], args[i] );
            }
        }
    }
    protected final void parseCSL( String csl ){//bad format breaks this
        String[] pairs = csl.split(",");
        keys = new String[pairs.length];
        vals = new String[pairs.length];
        def = null;
        for( String pair : pairs ){
            String[] keyVal = pair.split("=");
            if( keyVal.length == 2 ){   // key value pair found
                this.push( keyVal[0].trim(), keyVal[1].trim() );
            }
            else{                       // no = sets default
                def = keyVal[0].trim();
            }
        }
    }
}
