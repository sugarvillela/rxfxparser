package itr_struct;
/*
    The idea is to use these as enumerations and store their values in 
    an integer array to hold states, accessing them by their enum name

    =====================================================================
    Example calculation using default sizes: 5, 3, 3
    r = row number, c = column of interest, v = col holding a value

    Usage   r c v v v v v v v v
    Size    5|3|3|3|3|3|3|3|3|3 = 32

    32 - 5 - 3 leaves 24 bits for discretes
    wval of 3 allows discrete range 0-7
    24/3 gives 8 columns per row
    Total uniques: 2^5 * 8 = 256 small discrete numbers with value 0-7
    =====================================================================
    Same calculation with wval of 4:

    Usage   r c v v v v v v
    Size    5|3|4|4|4|4|4|4 = 32

    32 - 5 - 3 leaves 24 bits for discretes
    wval of 4 allows discrete range 0-15
    24/4 gives 6 columns per row
    Total uniques: 2^5 * 6 = 192 small discrete numbers with value 0-15
    =====================================================================
    No mechanism for resetting; max discretes determined by sizes chosen
    For aligning, you can skip rows/cols by calling newCol and newRow
 */
import commons.BIT;
import java.util.Iterator;
import unique.Enum_itr;
/*
    public int[] table;                     // Holds packed values
    protected int wordLen;                  // combined width of value fields
    protected int wrow;                     // width of single field
    protected int wordMask;                 // mask to get value fields
    protected int rowMask;                  // right-justified masks
    protected int initi, fini;              // 0, highest enum set in array
    protected String name;                  // for dev
    protected boolean itrEnum;              // Return enum or value
*/
/**
 *
 * @author Dave Swanson
 */
public class Enumstore_discrete extends Enumstore{
    protected int wval, valMask;
    protected int maska, posa;
    
    public Enumstore_discrete( int wrowSet, int wvalSet ){
        this( wrowSet, wvalSet, 1<<wrowSet );
    }
    public Enumstore_discrete( int wrowSet, int wvalSet, int len ){
        super( wrowSet, len );
        this.wval=wvalSet;
        this.valMask=this.initi=(1<<this.wval)-1;
    }
    public boolean seekPos(int index){
        //System.out.println("seekPos start");
        int cols = index & this.wordMask;
        //System.out.println("cols="+cols);
        for(
            int mask=this.valMask, i=0; 
            i<this.wordLen; 
            mask = mask<<this.wval, i+=this.wval 
        ){
            //System.out.println("search: i="+i+", mask="+mask);
            if( (cols & mask) != 0 ){
                this.maska=mask;
                this.posa=i;
                //System.out.println("good: i="+i);
                return true;
            }
        }
        //System.out.println("seekPos bad");
        return false;
    }
    @Override
    public void set( int index ){
        if( seekRow(index) && seekPos(index) ){
            this.table[this.rowa] &= ~this.maska;
            this.table[this.rowa] |= index;
            this.fini = BIT.uMax( index, this.fini );
        }
    }
    @Override
    public int get( int index ){
        return ( seekRow(index) && seekPos(index) )? 
            (this.table[this.rowa]>>this.posa) & this.valMask : 0;
    }
    @Override
    public void drop( int index ){
        if( seekRow(index) && seekPos(index) ){
            this.table[this.rowa] &= ~this.maska;
        }
    }
    
    @Override
    public int get( int mask, int pos, int index ){
        return( this.itrEnum )? getEnum( mask, index ) : getVal( pos, index );
    }
    @Override
    public int getVal( int pos, int index ){
        pos/=this.wval;
        //System.out.print(pos+": getVal: ");
        return ( ( this.table[index] ) >> pos*this.wval ) & this.valMask ;
    }
    @Override
    public Iterator<Integer> iterator() { 
        return new Enum_itr(
            this,
            this.wrow, this.wval,
            this.initi, this.fini 
        ); 
    }
    @Override
    public void disp(){
        System.out.println();
        System.out.println("Display:");
        for(int n : this.table){
            System.out.println(BIT.str(n, this.wval)); 
        }
        System.out.println();
    }
    @Override
    public void setInitialRow( int row ){//row as unshifted number
        setInitial( (row<<this.wordLen ) + this.valMask);
    }
}
