package unique;
/*
    Simple incrementer: sample patterns:
      0,1,2,3... or 1,2,3,4... or 0,2,4,6... or 20,18,16,14... (pass -2 for inc)
      Outputs padded string for formatting:  001,002,003,004...
      Overrides toString(): String id = "myid" + uq -> myid001, myid002...
      Use this and Unique_random for any non-repeating sequence task
*/
import java.util.Iterator;
import itr_struct.*;

/**Call n to increment; call nLast() to get same num again
 *
 * @author Dave Swanson
 */
public class Unique extends Iterable_int{
    protected int c, strPad;
    protected int initi, inci, fini;
    public String name;
    
    public Unique(){
        this.name="unique constructor"; // for dev
        this.inci=1;                    // amount to increment by
        this.initi = 0;                 // initial value on start and on rewind
        this.c = -1;                    // current value, inc before out
        this.fini = 0x40000000;         // stop here; pretty much never
        this.strPad=3;                  // 001, 002, 003...
    }
    void setStrPad( int pad ){
        this.strPad = ( pad> 0 )? pad : this.strPad;
    }
    public int n(){//increment and get
        //System.out.println( this.name+": A: "+this.c );
        this.c += this.inci;
        //System.out.println( this.name+": B: "+this.c );
        return this.c;
    }
    public String s(){//increment and get
        return String.format( "%0"+this.strPad+"d", n() );
    }
    public int nPrev(){//get, no increment
        return this.c;
    }
    public String sPrev(){//increment and get
        return String.format( "%0"+this.strPad+"d", nPrev() );
    }
    @Override
    public String toString(){//auto-convert; increments
        return s();
    }
    /* iterator */
    /* Must setInc() before setInitial */
    public void setInc( int inc ){
        this.inci=inc;
        this.rewind();
        //System.out.println( this.name+": setInc: "+this.inci );
    }
    /* iterator */
    public void setInitial( int init ){
        //System.out.println( this.name+": setInitial: "+init );
        this.initi = init;
        this.c = this.initi-this.inci;
    }
    /* iterator */
    public void setFinal( int fin ){
        this.fini = fin;
        //System.out.println( this.name+": setFinal: "+fin );
    }
    @Override
    public boolean hasNext() { 
        return ( this.c + this.inci ) <= this.fini;
    }
    @Override
    public Integer next() { 
        //System.out.println("unique next");
        return this.n();
    }
    @Override
    public void rewind(){//reset incrementer to initial value
        this.c = this.initi - this.inci;
    }
    @Override
    public Iterator<Integer> iterator() { 
        return new Itr_int(this); 
    }
}
