/* Used by Uq_enumgen (discrete generator only) */
package unique;

import commons.BIT;
import itr_struct.Enumstore;

/**Shifting number generator with variable initial and final points
 * newCol() and newRow() for Uq_enumgen row/col skipping.
 * 
 * @author Dave Swanson
 */
public class Enum_itr_discrete extends Enum_itr{
    protected Uq_repeater val;
    protected boolean done2, done3;
    public Enum_itr_discrete(
        Enumstore setTable, 
        int wrow, int wval,
        int initi, int fini 
    ){
        this.table=setTable;

        int wordLen = 32-wrow;
        int wordMask = ((1<<wordLen)-1);
        int rowMask = ((1<<wrow)-1);
        int valMask = ((1<<wval)-1);
        
        int initRow = (initi>>wordLen) & rowMask;
        int finRow =  ( fini>>wordLen) & rowMask;
        
        int initPos = BIT.maskRound( ( initi & wordMask ), wval );
        int finPos =  BIT.maskRound( (  fini & wordMask ), wval );
        
        int initVal = (initi>>initPos) & valMask;
        int finVal =  (fini>>finPos)   & valMask;
        System.out.println("=====Enum_itr_discrete Construct======");
        
        System.out.println("wordLen = "+wordLen);
        System.out.printf(
            " initRow=%d, finRow=%d\n initPos=%d, finPos=%d\n initVal=%d, finVal=%d\n",
                initRow, finRow, initPos, finPos, initVal, finVal
        );//
        //initRow=%d, finRow=%d\n initPos=%d, finPos=%d\n initVal=%d, finVal=%d\n
        /* Custom start and end points for iterating a range of enums */
        System.out.println("fini = "+fini);
        this.row = new Unique();
        this.row.name="row";
        this.row.setInitial( initRow );
        this.row.setFinal(   finRow  );
        this.row.n();
        
        this.pos = new Uq_repeater();
        this.pos.name="pos";
        this.pos.setInc(wval);
        this.pos.setInitial(initPos);
        this.pos.setFinal(finPos);
        this.pos.setBreakPoint( wordLen );
        this.pos.setResumePoint(0);
        this.pos.n();
        
        this.val = new Uq_repeater();
        this.val.name="val";
        this.val.setInitial(initVal);
        this.val.setFinal(finVal);
        this.val.setBreakPoint( (1<<wval) - 1);
        this.val.setResumePoint(1);
        this.done=false;
    }
    @Override
    public boolean hasNext() { 
        return !this.done;
    } 
    @Override
    public Integer next() { 
        //System.out.println( BIT.str( this.mask.n(),this.wval ) +", pos="+this.pos.n()+", row="+this.row.nPrev() );
        int posi = this.pos.nPrev();
        int vali = this.val.n()<<posi;
        
        int rowi = this.row.nPrev();
        //System.out.println( vali +", pos="+posi+", row="+rowi );
        int out = this.table.get( vali, posi, rowi );//>>posi; &maski
        
        if( this.val.signal() ){
            System.out.println("val signal");
            this.pos.n();
            this.val.resume();
        }
        if( this.pos.signal() ){
            System.out.println("pos signal");
            this.row.n();
            System.out.println("\nrow="+rowi+", pos="+(posi/3)+", row="+this.row.hasNext()+", pos="+this.pos.hasNext());
            if( !this.row.hasNext() && !this.pos.hasNext()){
                System.out.println("Done!!");
                this.done=true;
                return out;
            }
            this.pos.resume();
            this.pos.n();
        }
        return out;//this.table.next();
    } 
    @Override
    public void newRow(){
        System.out.println("Enum_itr newRow");
        this.row.n();
        if(!this.row.hasNext() && !this.mask.hasNext()){//
            this.done=true;
        }
        this.val.resume();
        this.pos.resume();
        this.pos.n();
    }
    @Override
    public void newCol(){
        System.out.println("Enum_itr newCol");
        this.val.resume();
        this.pos.n();
        if( this.pos.signal() ){
            System.out.println("pos signal");
            this.row.n();
            //System.out.println("\nrow="+rowi+", pos="+(posi/3)+", row="+this.row.hasNext()+", pos="+this.pos.hasNext());
            if( !this.row.hasNext() && !this.pos.hasNext()){
                System.out.println("Done!!");
                this.done=true;
            }
            this.pos.resume();
            this.pos.n();
        }
    }
}
