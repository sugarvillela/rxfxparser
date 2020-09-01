/* 
 * Map is a convenient Java structure.  But if you know your dataset is tiny,
 * it might make sense to use a simple array-based version instead.
 * Big-O is really just a measure of how much the time complexity changes with
 * array size. There is a significant constant-complexity that is ignored 
 * because it is dwarfed by the n-complexity. There is a break-even point where 
 * n-complexity and constant-complexity are equal.  For large arrays, the "better"
 * algorithm makes sense. With tiny arrays (many use cases) the time of a linear 
 * search is less than the constant time of the better algorithm.
 * This is a map that implements some of the features of Java map.
 * Supports String, Integer, Double and Character by passing the appropriate
 * types to the constructor. For other object types, use the custom array
 * constructor (pass in object arrays).
 * Also supports key=value comma-separated-list and varArg string constructor.
*/
package tinymaps;

import java.util.HashMap;

/**
 * A tiny data-set, limited feature implementation of java map
 * Additional custom features like csv and counting set functionality.
 * @author Dave Swanson
 */
public class  TinyMap{
    protected Object[] keys, vals;
    protected Object def;
    protected int used;
    protected int point;
    
    // initialization:
    
    protected TinyMap(){}
    public TinyMap( int size ){//specify size and fill later
        this(size, null, null);
    }
    public TinyMap( int size, Object defaultReturn ){//specify size and fill later
        this(size, null, defaultReturn);
    }
    
    /* Detailed constructor: set key and value type; default sets value type */
    public TinyMap( int size, Object keyType, Object defaultReturn ){//set default non-null
        used = 0;
        point = 0;
        keys = setType(size, keyType);
        vals = setType(size, defaultReturn);
        def = defaultReturn;
    }
    protected final Object[] setType( int size, Object type ){
        if(type == null || type instanceof String ){//default
            return new String[size];
        }
        else if(type instanceof Integer ){
            return new Integer[size];
        }
        else if(type instanceof Double ){
            return new Double[size];
        }
        else if(type instanceof Character ){
            return new Character[size];
        }
        else{
            return null;//fail loudly so programmer will read the instructions
        }
    }
    
    /* For key/val object types not supported by setType, pass in custom arrays
        Make sure resize() is not triggered, because it will break
        Make sure defaultReturn is null or same class as setValues
    */
    public TinyMap( Object[] setKeys, Object[] setValues ){//set default non-null
        this(setKeys, setValues, null);
    }
    public TinyMap( Object[] setKeys, Object[] setValues, Object defaultReturn ){//set default non-null
        used = 0;
        point = 0;
        keys = setKeys;
        vals = setValues;
        def = defaultReturn;
    }
    
    
    
    // state
    public int size(){
        return used;
    }
    public int capacity(){
        return keys.length;
    }
    public boolean isEmpty(){
        return used==0;
    }
    public int findKey(Object key){//numeric key for object key
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                return i;
            }
        }
        return -1;
    }
    public int findValue(Object value){
        for(int i=0; i<used; i++){
            if(vals[i].equals(value)){
                return i;
            }
        }
        return -1;
    }
    public boolean equals(TinyMap m){
        if( m.size() != used ){
            return false;
        }
        Object[] k = m.keys();
        Object[] v = m.values();
        for(int i=0; i<used; i++){
            if(!keys[i].equals(k[i]) || !vals[i].equals(v[i])){
                return false;
            }
        }
        return true;
    }
    
    // access
    public Object get(Object key){
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                return vals[i];
            }
        }
        return def;
    }
    public void put( Object key, Object value ){//replace or add to end of array
        for(int i=0; i<used; i++){
            if(keys[i].equals(key)){
                vals[i] = value;
                return;
            }
        }
        push(key, value);
    }
    public void clear(){
        used = 0;
    }
    // Remove value at specified key if key exists
    public void remove(Object key){
        if(used==0){
            return;
        }
        int i;
        for(i=0; i<used; i++){
            if(keys[i].equals(key)){
                break;
            }
        }
        if(i==used){
            return;
        }
        for(int j=i+1; j<used; j++){
            keys[j-1] = keys[j];
            vals[j-1] = vals[j];
        }
        used--;
    }
    public void disp(){
        System.out.println("\nDisplay: Capacity "+keys.length+", used: "+used);
        if(vals == null ){
            for(int i=0; i<used; i++){
                System.out.println(keys[i]);
            }
        }
        else{
            for(int i=0; i<used; i++){
                System.out.println(keys[i] + ": " + vals[i]);
            }
        }
        System.out.println("...\n");
    }
    // get implementation arrays
    public Object[] keys(){
        return keys;
    }
    public Object[] values(){
        return vals;
    }  
    // protected utilities
    protected final void push( Object key, Object value ){
        if(used == keys.length){
            resize(used*2);
        }
        keys[used] = key;
        vals[used] = value;
        used++;
    }
    protected void resize(int newSize){
        Object[] k = setType(newSize, keys[0]);
        Object[] v = setType(newSize, vals[0]);
        for(int i=0; i<used; i++){
            k[i] = keys[i];
            v[i] = vals[i];
        }
        keys = k;
        vals = v;
    }
}


