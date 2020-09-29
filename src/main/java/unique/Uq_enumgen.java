package unique;

import itr_struct.Enumstore;
import java.util.Iterator;

/**Shifting boolean/discrete generator with variable initial and final points.
 * newCol() and newRow() for row/col skipping when generating enumerations.
 * See Enumstore, Enumstore_discrete classes
 *
 * @author Dave Swanson
 */
public class Uq_enumgen extends Enumstore{
    protected Enum_itr itr;
    protected int wval;
    //protected int currPos;
    
    public Uq_enumgen( int wrowSet ){//call this constructor for boolean enums
        this(wrowSet, 1);
    }
    public Uq_enumgen( int wrowSet, int wvalSet ){//call this for discrete enums
        this.name="unique constructor"; // for dev
        this.wrow = wrowSet;
        this.wval = wvalSet;
        this.wordLen=32-wrowSet;
        this.initi = 1;                 // initial value on start and on rewind
        this.fini = 0x40000000;         // stop here; pretty much never
        //this.currPos=0;
    }
    @Override
    public int get( int mask, int pos, int index ){
        //this.currPos=pos;//for skipping 
        return ( index<<this.wordLen ) | mask;
    }
    public void newRow(){
        //System.out.println("Uq_enumgen newRow");
        this.itr.newRow();
    }
    public void newCol(){
        //System.out.println("Uq_enumgen newCol");
        this.itr.newCol();
    }
    @Override
    public Iterator<Integer> iterator() {
        this.itr = ( this.wval>1)? 
            new Enum_itr_discrete(
                this,
                this.wrow, this.wval,
                this.initi, this.fini 
            )
                :
            new Enum_itr(
                this,
                this.wrow, this.wval,
                this.initi, this.fini 
            ); 
        return this.itr;
    }
    public int getCurrRow(){
        return (this.itr==null)? 0: this.itr.getCurrRow();//returns no shift row
    }
}
