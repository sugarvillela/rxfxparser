
package tinymaps;

/**
 * A tiny data-set, limited feature implementation of java set
 * Additional feature: counts number of times set.
 * @author Dave Swanson
 */
public class CountingSet extends TinyMap{
    
    protected CountingSet(){}
    
    public CountingSet( int size ){//specify size and fill later
        this(size, null);
    }
    
    /* Detailed constructor: set key and value type; keyType sets key type */
    public CountingSet( int size, Object keyType ){//set default non-null
        used = 0;
        point = 0;
        keys = setType(size, keyType);
        vals = new Integer[size];
    }
    
    /* Pass custom object array for keys...
        Make sure keys are unique and make sure resize() is not triggered */
    public CountingSet( Object[] setKeys ){//set default non-null
        used = 0;
        point = 0;
        keys = setKeys;
        vals = new Integer[setKeys.length];
        for(int i=0; i<setKeys.length; i++){
            vals[i]=1;
        }
    }
    
    /**
     * @param key String, integer, double or char
     * @return 0 if key not in set; > 0 if key in set
     * @author Dave Swanson
     */
    @Override
    public Integer get(Object key){
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                return (Integer)vals[i];
            }
        }
        return 0;
    }
    
    /**Keeps count of how many times same key set
     * @param key String, integer, double or char
     * @return 1 if key not previously in set; > 1 if key previously in set
     * @author Dave Swanson
     */
    public int put( Object key ){//replace or add to end of array
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                vals[i] = ((Integer)(vals[i])) + 1;
                return (Integer)vals[i];
            }
        }
        push(key);
        return 1;
    }
    
    /**Two parameter version could break things; override with placebo method
     * @param key dummy
     * @param value dummy
     * @author Dave Swanson
     */   
    @Override
    public void put( Object key, Object value ){}
    
    protected void push( Object key ){
        if(used == keys.length){
            resize(used*2);
        }
        keys[used] = key;
        vals[used] = 1;
        used++;
    }
}
