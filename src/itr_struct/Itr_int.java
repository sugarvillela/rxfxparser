package itr_struct;

import java.util.Iterator;
/**
 *
 * @author Dave Swanson
 */
public class Itr_int implements Iterator<Integer>{
    Iterable_int table; //current

    public Itr_int( Iterable_int setTable ) { 
        this.table=setTable;
        //this.table.rewind();
    } 
    @Override
    public boolean hasNext() { 
        return this.table.hasNext();
    } 
    @Override
    public Integer next() { 
        return this.table.next();
    } 
    @Override
    public void remove(){} 
}