package itr_struct;

import java.util.Iterator;

/**
 *
 * @author Dave Swanson
 */
public abstract class Iterable_int implements Iterable<Integer>{
    public abstract void rewind();
    public abstract boolean hasNext(); 
    public abstract Integer next();
    // return Iterator instance 
    @Override
    public Iterator<Integer> iterator() { 
        return new Itr_int(this); 
    }
}
