
package commons.tinymaps;

/**
 * A tiny data-set, limited feature implementation of java set
 * @author Dave Swanson
 */
public class TinySet extends CountingSet {
    protected TinySet(){}
    
    public TinySet( int size ){//specify size and fill later
        this(size, null);
    }
    
    /* Detailed constructor: set key and value type; keyType sets key type */
    public TinySet( int size, Object keyType ){//set default non-null
        used = 0;
        point = 0;
        keys = setType(size, keyType);
        vals = null;
    }
    
    /* Pass custom object array for keys...
       Make sure keys are unique and make sure resize() is not triggered */
    public TinySet( Object[] setKeys ){//set default non-null
        used = 0;
        point = 0;
        keys = setKeys;
        vals = null;
    }
    
    /**TinySet access methods patterned after Java set
     * Use contains() and add(), not get and put
     * @param key String, integer, double or char
     * @return 0 if key not in set; > 0 if key in set
     * @author Dave Swanson
     */
    public boolean contains(Object key){
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                return true;
            }
        }
        return false;
    }
      
    /**TinySet access methods patterned after Java set
     * Use contains() and add(), not get and put
     * @param key String, integer, double or char
     * @return 1 if key not previously in set; > 1 if key previously in set
     * @author Dave Swanson
     */
    public boolean add( Object key ){//replace or add to end of array
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                return true;
            }
        }
        push(key);
        return false;
    }
    /**Don't use get and put for tiny set; use contains() and add()
     * @param key String, integer, double or char
     * @return 0 if key not in set; 1 if key in set
     * @author Dave Swanson
     */
    @Override
    public Integer get(Object key){
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                return 1;
            }
        }
        return 0;
    }
    /**Don't use get and put for tiny set; use contains() and add()
     * @param key String, integer, double or char
     * @return 1 if key not previously in set; 2 if key previously in set
     * @author Dave Swanson
     */
    @Override
    public int put( Object key ){//replace or add to end of array
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                return 2;
            }
        }
        push(key);
        return 1;
    }

   
    // protected utilities
    @Override
    protected void push( Object key ){
        if(used == keys.length){
            resize(used*2);
        }
        keys[used] = key;
        used++;
    }
}
