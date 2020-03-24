package unique;
/*

*/
import commons.BIT;
/** Extended Unique with variable increment patterns and stop condition
 *
 * @author Dave Swanson
 */
public class Uqx extends Unique{
    /* Choose pattern: pass 0 to set Default Sequential iterator */
    public static final int SHIFT =  0x02;  // Left Shift
    /* Choose stop criteria: c >= stopVal: pass 0 for equals */
    public static final int GT =     0x04;  // c > stopVal
    public static final int GTE =    0x08;  // c >= stopVal
    public static final int LAHEAD = 0x10;  // look ahead: Next >= stopVal
    public static final int NOP =    0x20;  // always initial and valid
    public String name;
    
    protected Incrementer incr;
    protected Ender endr;
    protected int initVal, initVal2;
    public Uqx(){
        this(0);
    }
    public Uqx( int config ){
        this.name="uqx constructor";
        if( (config & Uqx.NOP) != 0 ){
            this.incr = new NoInc();
            this.endr = new NoEnd();
            //this.c=0;
            this.incr.setValue(0);
            return;
        }
        // Set incrementer
        if( (config & Uqx.SHIFT) != 0 ){
            this.incr = new Shifter();
        }
        else{
            //System.out.println("sequential");
            this.incr = new Incrementer();
        }
        // Set stopper
        if( (config & Uqx.GT) != 0 ){
            this.endr = new GT();
        }
        else if( (config & Uqx.GTE) != 0 ){
            this.endr = new GTE();
        }
        else if( (config & Uqx.LAHEAD) != 0 ){
            this.endr = new LookAhead( this.incr );
        }
        else{
            this.endr = new Ender();
        }
        this.initVal=0;
        this.initVal2=0;
        this.incr.setValue(0);
        this.incr.setIncrement(1);
        
    }
    /* Initialize strategy objects */
    @Override
    public void setInitial( int val ){
        //System.out.println(name+"=name: uqx set initial: "+BIT.str(val, 3)+" = setInitial = " + val );
        this.initVal = val;
        this.incr.setValue( val );
    }
    public void setInitial2( int val ){
        // To start at some value other than the init value
        // setInitial sets outer class initVal and inner class value
        // setCurr sets outer and inner current values without changing initval
        this.initVal2 = val;
        this.incr.setValue( val );
    }
    public void setIncrement( int increment ){
        this.incr.setIncrement(increment);
    }
    public void setFinal( int val ){
        //System.out.println(BIT.str(val, 3)+" = setFinal");
        this.endr.setFinal(val);
    }
    public void setFinal2( int val ){
        this.endr.setFinal2(val);
    }
    /* Start, Stop & Restart */
    @Override
    public boolean hasNext(){// false when stopper reaches EQ, GT condition
        return this.endr.hasNext(this.c);
    }
    public boolean hasNext2(){// false when stopper reaches EQ, GT condition
        return this.endr.hasNext2(this.c);
    }
    @Override
    public void rewind(){//reset incrementer to initial value
        this.incr.setValue( this.initVal );
    }
    public void rewind2(){//reset incrementer to initial value 2
        this.incr.setValue( this.initVal2 );
    }
    
    /* Incrementer strategy */
    public class Incrementer{
        protected int c, inci;
        public void setValue( int initVal ){
            System.out.println( Uqx.this.name+": Incrementer seq: setValue: "+initVal);
            this.c = initVal;
        }
        void setIncrement( int increment ){
            this.inci=increment;
        }
        public int n(){
            int out = this.c;
            //System.out.println("Incrementer n: out="+out);
            this.c+=this.inci;
            return out;
        }
        public int lookAhead(){
            return this.c;
        }
    }
    public class Shifter extends Incrementer{
        @Override
        public void setValue( int initVal ){
            System.out.println(name+": Shifter: setValue: "+initVal);
            this.c = (initVal!=0)? initVal : 1;
            //System.out.println("Incshift setValue:" + this.c);
        }
        @Override
        public int n(){
            int out = this.c;
            this.c = this.c << this.inci;
            return out;
        }
        @Override
        public int lookAhead(){//c is already ahead
            return this.c;
        }
    }
    public class NoInc extends Incrementer{
        @Override
        public int n(){
            return this.c;
        }
    }
    /* Termination strategy */
    public class Ender{
        protected int stopVal, stopVal2;
        public Ender(){
            this.stopVal=this.stopVal2=0x40000000;// Unlimited until stopVal set
        }
        void setFinal( int val ){
            System.out.println(Uqx.this.name+": Ender or LookAhead setFinal: "+val);
            this.stopVal=val;
        }
        void setFinal2( int val ){
            this.stopVal2=val;
        }
        public boolean hasNext( int c ){
            return ( c != this.stopVal );
        }
        public boolean hasNext2( int c ){
            return ( c != this.stopVal2 );
        }
    }
    public class GT extends Ender{
        @Override
        public boolean hasNext( int c ){
            return ( c <= this.stopVal );
        }
        @Override
        public boolean hasNext2( int c ){
            return ( c <= this.stopVal2 );
        }
    }
    public class GTE extends Ender{
        @Override
        public boolean hasNext( int c ){
            return ( c < this.stopVal );
        }
        @Override
        public boolean hasNext2( int c ){
            return ( c < this.stopVal2 );
        }
    }
    public class LookAhead extends Ender{
        Incrementer incr;
        public LookAhead( Incrementer setIncr ){
            this.incr = setIncr;
        }
        @Override
        public boolean hasNext( int c ){
            //System.out.println(BIT.str(this.incr.lookAhead(), 3)+" = lookAhead");
            //System.out.println(BIT.str(this.stopVal, 3)+" = stopVal");
            return ( this.incr.lookAhead() < this.stopVal );
        }
        @Override
        public boolean hasNext2( int c ){
            return ( this.incr.lookAhead() < this.stopVal2 );
        }
    }
    public class NoEnd extends Ender{
        @Override
        public boolean hasNext( int c ){
            return true;
        }
        @Override
        public boolean hasNext2( int c ){
            return true;
        }
    }
    /* From class Unique */
    @Override
    public int n(){//increment and get
        this.c = this.incr.n();
        return this.c;
    }
    @Override
    public String s(){//increment and get
        return BIT.str( n() );
    }
    @Override
    public String sPrev(){//
        return BIT.str( nPrev() );
    }
    @Override
    void setStrPad( int pad ){}//not needed  
}
