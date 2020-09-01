/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itr_struct;
import java.util.Iterator;

/**
 *
 * @author newAdmin
 */
class PayloadIterator<T> implements Iterator<T> { 
    ListNode<T> table; //current

    public PayloadIterator(ListNode<T> list) { 
        this.table=list;
        this.table.rewind();
    } 
    @Override
    public boolean hasNext() { 
        return this.table.hasNext();
    } 
    @Override
    public T next() { 
        return this.table.next();
    } 
    @Override
    public void remove(){} 
    
} 
