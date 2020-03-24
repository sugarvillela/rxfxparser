/* Used by Enumstore (bool), Enumstore_discrete (masked values) and 
    Uq_enumgen (bool generator only) */
package unique;

import commons.BIT;
import itr_struct.Enumstore;
import java.util.Iterator;

/**Shift mask iterator with variable initial and final points.
 * newCol() and newRow() for Uq_enumgen row/col skipping.
 *  
 * @author Dave Swanson
 */
public class Enum_itr implements Iterator<Integer>{
    Enumstore table; //current
    protected Unique row;
    protected Uq_repeater pos;
    protected Uq_rshift mask;
    protected boolean done;

    public Enum_itr(){}
    public Enum_itr( 
        Enumstore setTable, 
        int wrow, int wval,
        int initi, int fini 
    ) { 
        this.table=setTable;

        int wordLen = 32-wrow;
        System.out.println("Enum_itr wordLen = "+wordLen);
        //BIT.disp( ((1<<wrow)-1) );
        System.out.println("fini = "+fini);
        this.row = new Unique();
        this.row.name="row";
        this.row.setInitial( (initi>>wordLen) & ((1<<wrow)-1) );
        this.row.setFinal(    (fini>>wordLen) & ((1<<wrow)-1));
        this.row.n();
        
        this.pos = new Uq_repeater();
        this.pos.name="pos";
        this.pos.setInc(wval);
        this.pos.setInitial(BIT.first1(initi));
        this.pos.setResumePoint(0);
        /* Custom start and end points for iterating a range of enums */
        this.mask = new Uq_rshift();
        this.mask.name="mask";
        this.mask.setInc(wval);
        this.mask.setInitial( initi & ( (1<<wordLen)-1 ) );//BIT.maskVal(fini, wval);
        fini &= (1<<wordLen)-1;
        this.mask.setFinal( BIT.maskVal(fini, wval) );
        /* Typical start and end points for repeating shift-iteration*/
        this.mask.setResumePoint( (1<<wval) - 1 );
        this.mask.setBreakPoint(1<<wordLen);
        this.done=false;
    } 
    @Override
    public boolean hasNext() { 
        return !this.done;
    } 
    @Override
    public Integer next() { 
        //System.out.println( BIT.str( this.mask.n(),this.wval ) +", pos="+this.pos.n()+", row="+this.row.nPrev() );
        int maski = this.mask.n();
        int posi = this.pos.n();
        int rowi = this.row.nPrev();
        //System.out.println( BIT.str( maski ) +", pos="+posi+", row="+rowi );
        int out = this.table.get( maski, posi, rowi );//>>posi; &maski
        //System.out.println("\nrow="+rowi+", pos="+(posi/3)+", row="+this.row.hasNext()+", mask="+this.mask.hasNext());
        if(!this.row.hasNext() && !this.mask.hasNext()){//
            //System.out.println("Done!!");
            this.done=true;
            return out;
        }
        if( this.mask.signal() ){
            //System.out.println("Signal: row="+this.row.hasNext()+", mask="+this.mask.hasNext());
            this.mask.resume();
            this.pos.resume();
            this.row.n();
        }
        
        return out;//this.table.next();
    } 
    public void newRow(){
        System.out.println("Enum_itr newRow");
        this.row.n();
        if(!this.row.hasNext() && !this.mask.hasNext()){//
            this.done=true;
        }
        this.pos.resume();
        this.mask.resume();
    }
    public void newCol(){
        this.mask.n();
        if( this.mask.signal() ){
            //System.out.println("Signal: row="+this.row.hasNext()+", mask="+this.mask.hasNext());
            this.mask.resume();
            this.pos.resume();
            this.row.n();
        }
    }
    public int getCurrRow(){
        return this.row.nPrev();//returns no shift row
    }

    @Override
    public void remove(){}  
    
    @Override
    public String toString(){//decimal number in string form
        return String.valueOf( next() );
    }
}
