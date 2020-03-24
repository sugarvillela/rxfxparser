package itr_struct;
import java.util.Iterator;

enum list_role{ list, node };
/**
 *
 * @author newAdmin
 * @param <T>
 */
public class ListNode<T> implements Iterable<T> { 
    protected list_role role;               // list or node               
    // Node Role
    public ListNode<T> prev, next;             // for node role
    public T val; 
    
    // List/Iterator Role
    protected ListNode<T> head, tail;          // for doubly linked list role
    protected ListNode<T> curr;                // for accessor and iterator
    protected int top;//                        // top index = length-1
    protected int start, end, inci, rowi;       // itr vals
    protected boolean sizeChanged;              // trigger clear range on rewind

    public ListNode() {                        // for doubly linked list role
        this.role = list_role.list;
	this.head = this.tail = null;
        this.curr=null;
        this.top = -1;
        this.rowi=0;
        this.inci = 1;
        this.start=0;
        this.end=0;
        this.sizeChanged=true;
    }
    public ListNode( T val ) {                 // for node role
        this.role = list_role.node;
        this.val = val; 
        this.prev = null;
        this.next = null; 
    }
    // List functions: bookkeeping
    public void incTop(){//use this to keep itrRange updated
        this.top++;
        this.sizeChanged=true;
    }
    public void incTop( int inc ){//use this to keep itrRange updated
        this.top+=inc;
        this.sizeChanged=true;
    }
    public void decTop(){//use this to keep itrRange updated
        this.top--;
        this.sizeChanged=true;
    }
    public int negIndex( int end, int index ){
        /* For this and child classes: end is max index + 1  */
        return ( index< 0 )? end+index : index;
    }
    // List functions: seek
    public boolean seek( int index ){
        System.out.printf("seek index=%d\n", index );
        if( index>this.top ){
            return false;
        }
        if( index==this.rowi ){
            return true;
        }
        if( index==this.rowi-1 ){
            this.rowi=index;
            this.curr=this.curr.prev;
        }
        else if( index==this.rowi+1 ){
            this.rowi=index;
            this.curr=this.curr.next;
        }
        else if( (this.top + 1 - index) < index ){
            this.seekBack(index);
        }
        else{
            this.seekFront(index);
        }
        return true;
    }
    public void seekFront( int index ){
        for ( 
            this.curr=this.head, this.rowi=0; 
            this.rowi<index; 
            this.curr=this.curr.next, this.rowi++ 
        ){}
    }
    public void seekBack( int index ){
        for ( 
            this.curr=this.tail, this.rowi=this.top; 
            this.rowi>index; 
            this.curr=this.curr.prev, this.rowi-- 
        ){}
    }
    /* Accessors */
    public T getVal( int index ) {      
        return ( this.seek( this.negIndex( this.top+1, index ) ) )? 
            this.curr.val : null;
    }
    public ListNode<T> getNode( int index ) {      
        return ( this.seek( this.negIndex( this.top+1, index ) ) )? 
            this.curr : null;
    }
    /* Mutators */
    public void pushFront( T val){
        ListNode<T> temp = new ListNode(val);
        temp.val = val;
        temp.next = this.head;
        if(this.top < 0){
            this.tail = temp;
        }
        this.head = temp;
        this.incTop();
        /* keep iter/access valid */
        this.curr=this.head;
    }
    public void pushBack ( T val){
        if(this.top < 0){
            this.pushFront(val);
        }
        else{
            ListNode<T> temp = new ListNode(val);
            temp.val = val;
            temp.prev = this.tail;
            this.tail.next = temp;
            this.tail = temp;
            this.incTop();
            /* keep iter/access valid */
            this.curr=this.head;
            this.sizeChanged=true;
        }
    }
    public T popFront(){
        if( this.top >= 0 ){
            T ret = this.head.val;
            ListNode<T> victim = this.head;
            this.head = victim.next;
            this.head.prev=null;
            victim.next = null;
            victim = null;
            this.decTop();
            this.rowi=0;
            this.curr=this.head;
            return ret;
        }
        else{
            return null;
        }
    }
    public T popBack(){
        if( this.top >= 0 ){
            T ret = this.tail.val;
            ListNode<T> victim = this.tail;
            this.tail = victim.prev;
            this.tail.next=null;
            victim.prev = null;
            victim = null;
            this.decTop();
            this.rowi=this.top+1;
            this.curr=this.tail;
            return ret;
        }
        else{
            return null;
        }
    }
    public void insert( int index, T val ){
        this.insert(index, new ListNode( val ));
    }
    public void insert( int index, ListNode<T> node ){
        if( !this.seek( index )){
            return;
        }
        node.prev = this.curr.prev;
        node.prev.next=node;
        node.next = this.curr;
        this.curr.prev = node;
        this.curr=node;
        this.rowi++;
        this.incTop();
    }
    public void offsetSet( int index, T val ) {
        int i = 0;
        ListNode<T> cur;
        for ( cur = this.head; i<index; cur=cur.next, i++ ){
            if(cur.next==null){
                return;
            }
        }
        cur.val=val;
    }
    public void offsetUnset( int index) {
        if( index==0 ){
            this.popFront();
        }
        else if( index==this.top ){
            this.popBack();
        }
        else if( this.seek( index )){
            prev=this.curr.prev;
            next=this.curr.next;
            prev.next = next;
            next.prev = prev;
            this.curr=next;
            this.decTop();
        }
        else{
            return;
        }
        this.decTop();
    }
    /* Iterator: go forward or back */
    public void setItrForward(){
        /* Inc 1 for regular itr; Any integer for skipping */
        this.inci = 1;
    }
    public void setItrBack(){
        this.inci = -1;
    }
    /* Iterator: four functions to set any range */
    public void setItrStart( int startIndex ){
        this.start=startIndex;
        this.sizeChanged=false;//to preserve settings on rewind
    }
    public void setItrEnd( int endIndex ){
        this.end=endIndex;
        this.sizeChanged=false;//to preserve settings on rewind
    }
    public void setItrRange( int startIndex, int endIndex ){
        /* [start,end) start is inclusive, end is non-inclusive */
        this.start=startIndex;
        this.end=endIndex;
        this.sizeChanged=false;//to preserve settings on rewind
    }
    public void clearItrRange(){//manually trigger clear itr range on rewind
        this.sizeChanged=true;
    }
    /* Iterator: manage pointer */
    public void rewind(){
        if( this.sizeChanged ){//clears iter range if size changed
            System.out.println("sizeChanged");
            this.start=0;
            this.end=this.top+1;
            this.sizeChanged=false;
        }
        System.out.printf("top=%d, start=%d, end=%d, inci=%d", top, start, end, inci);
        System.out.println();
        this.seek((this.inci<0)? this.end-1 : this.start);
    }
    protected void incCur(){
        if(this.inci<0){
            this.curr = this.curr.prev; 
            this.rowi--;
        }
        else{
            this.curr = this.curr.next;
            this.rowi++;
        }
        
    }
    public int key(){//iterator index
        return this.rowi-this.inci;//assume this is called after incCur()
    }
    public boolean hasNext() { 
        System.out.printf("hasNext=%b\n", this.curr != null);
        return this.end > this.rowi && 
                this.rowi >= this.start && this.curr != null; 
    } 
    public T next() { 
        T data = this.curr.val; 
        System.out.printf("next: rowi=%d\n", this.rowi );
        incCur();
        return data; 
    }
    // return Iterator instance 
    @Override
    public Iterator<T> iterator() { 
        return new PayloadIterator<>(this); 
    }
    
} 
