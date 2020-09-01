/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itr_struct;

import commons.BIT;
import java.util.Iterator;
import unique.Enum_itr;

/**
 *
 * @author admin
 */
public class Enumstore implements Iterable<Integer>{
    public int[] table;                     // Holds packed values
    protected int wordLen;                  // combined width of value fields
    protected int wrow;                     // width of single field
    protected int wordMask;                 // mask to get value fields
    protected int rowMask;                  // right-justified masks
    protected int rowa;                     // row number for access methods
    protected int initi, fini;              // 1, highest enum set in array
    protected int rowOffset;                // Enums don't have to start at 0
    protected String name;                  // for dev
    protected boolean itrEnum;              // Return enum or value

    public Enumstore(){}
    public Enumstore( int wrowSet ){
        this( wrowSet, 1<<wrowSet );
    }
    public Enumstore( int wrowSet, int len ){
        this.name="Enumstore constructor";
        this.wrow = wrowSet;
        this.table = new int[len];
        this.wordLen = 32 - this.wrow;
        this.rowMask=(1<<this.wrow)-1;
        this.wordMask=(1<<this.wordLen)-1;
//        BIT.disp( this.rowMask );
//        BIT.disp( this.wordMask );
        /* Custom start and end points for iterating a range of enums */
        this.initi = 1;
        this.fini = 1;
        this.itrEnum = true;                // iterates enums by default
        this.rowOffset = 0;
    }
    /* Access */
    protected boolean seekRow(int index){
        //System.out.println("seekRow start");
        this.rowa = (index>>this.wordLen) & this.rowMask;
        return this.table.length > this.rowa && this.rowa >=0;
    }
    public void set( int index ){
        if( seekRow(index) ){
            int mask = index & this.wordMask;
            this.table[this.rowa] &= ~mask;
            this.table[this.rowa] |= index;
            this.fini = BIT.uMax( index, this.fini );
        }
    }
    public int get( int index ){
        return (
            seekRow(index) &&
            ( ( this.table[this.rowa] & index & this.wordMask ) != 0 )
        )? 1 : 0;
    }
    public void drop( int index ){
        if( seekRow(index) ){
            int mask = index & this.wordMask;
            this.table[this.rowa] &= ~mask;
            if( this.rowa == ( (this.fini>>this.wordLen) & this.rowMask ) ){
                this.fini = BIT.uMin( this.table[this.rowa], this.fini );
            }
        }
    }
    public boolean exists(int index){
        return ( seekRow(index) );
    }
    
    public int get( int mask, int pos, int index ){
        return( this.itrEnum )? getEnum( mask, index ) : getBool( mask, index );
    }
    public int getEnum( int mask, int index ){
        return (( this.table[index] & mask ) == 0) ? 0 : 
            this.table[index] & ( ~this.wordMask | mask );
    }
    public int getBool( int mask, int index ){
        return ( ( this.table[index] & mask  ) == 0 )? 0 : 1;
    }
    public int getVal( int pos, int index ){
        return ( ( this.table[index] ) >> pos ) & 1 ;
    }

    /* iterator */
    public void setInitial( int init ){
        this.initi = init;
        System.out.println( this.name+": setInitial: "+init );
    }
    /* iterator */
    public void setFinal( int fin ){
        this.fini = fin;
        //System.out.println( this.name+": setFinal: "+fin );
    }
    public void setInitialRow( int row ){//row as unshifted number
        setInitial( (row<<this.wordLen ) + 1);
    }
    public int getInitial(){
        return this.initi;
    }
    /* iterator */
    public int getFinal(){
        return this.fini;
        //System.out.println( this.name+": setFinal: "+fin );
    }
    public void setItrEnum( boolean enum_or_val ){
        this.itrEnum = enum_or_val;
    }
    @Override
    public Iterator<Integer> iterator() { 
        return new Enum_itr(
            this,
            this.wrow, 1,
            this.initi, this.fini 
        ); 
    }
    
    public void disp(){
        System.out.println();
        System.out.println("Display:");
        for(int n : this.table){
            System.out.println(BIT.str(n)); 
        }
        System.out.println();
    }
}
