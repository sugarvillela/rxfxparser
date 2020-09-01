package tinymaps;

/**
 *
 * @author Dave Swanson
 */
public class TinyStack {
    protected Object[] vals;
    protected Object def;
    protected int used;
    protected int point;

    public TinyStack(){
        this(4, null);
    }
    public TinyStack( int size ){//specify size and fill later
        this(size, null);
    }
    public TinyStack( Object defaultReturn ){//default return sets type
        this(4, defaultReturn);
    }
    public TinyStack( int size, Object defaultReturn ){//specify size and fill later
        used = 0;
        point = 0;
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
    
    // access
    public void push( Object value ){
        if(used == vals.length){
            resize(used*2);
        }
        vals[used] = value;
        used++;
    }
    public Object pop( ){
        if(used <= 0){
            return def;
        }
        return vals[-1 + used--];
    }
    public Object top( ){
        if(used <= 0){
            return def;
        }
        return vals[used-1];
    }
    public Object peek(){
        return top();
    }
    public void clear(){
        used = 0;
    }
    // get implementation array
    public Object[] values(){
        return vals;
    } 
    
    // state
    public int size(){
        return used;
    }
    public int capacity(){
        return vals.length;
    }
    public boolean isEmpty(){
        return used==0;
    }
    public boolean equals(TinyStack s){
        if( s.size() != used ){
            return false;
        }
        Object[] v = s.values();
        for(int i=0; i<used; i++){
            if(!vals[i].equals(v[i])){
                return false;
            }
        }
        return true;
    }

    public void disp(){
        System.out.println("\nDisplay: Capacity "+vals.length+", used: "+used);
        for(int i=0; i<used; i++){
            System.out.println(i + ": " + vals[i]);
        }
        System.out.println("...\n");
    }
 
    // protected utilities
    protected void resize(int newSize){
        Object[] v = setType(newSize, vals[0]);
        System.arraycopy(vals, 0, v, 0, used);
        vals = v;
    } 
}
