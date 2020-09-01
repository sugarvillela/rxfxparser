/* Prevents duplicates by logging previous. 
 * 
 * To prevent endless loop during n() selection, log length cannot be 
 * greater than max. Here we use max/2 or smaller.
 * 
 * For reliable uniqueness, set log size to the number of times 
 * n() might be called in the program
 * 
 * Get n random numbers: setInitial(n)
 * Use foreach iterator; stops after n iterations
 *     
 * Make a random number of random numbers: setFinal()
 * Use foreach iterator; stops after max/2 iterations, +- randomness
 */
package unique;
/**
 * Generate non-repeating random numbers, integer or formatted string
 * @author Dave Swanson
 */
public class Unique_random extends Unique{
    protected int[] log;                // prevent duplicates by logging output
    protected int max, logIndex;        // max value; dest index in log
    protected int counter, stopOn;      // stop on n iterations or stop on value
    
    public Unique_random( int setMax ){
        /* log size will set to setMax/2
           String pad will set to log_10(setMax)  */
        this( setMax, 0x40000000, ( ""+setMax ).length() );
    }
    public Unique_random( int setMax, int setLogSize, int setStrPad ) {
        this.c = 0;
        this.strPad=setStrPad;
        this.max=setMax;
        this.log=new int[Math.min( (int)Math.ceil(setMax/2.0), setLogSize )];
        this.logIndex=-1;
        this.counter=0x40000000;
        this.stopOn = -1;
    }
    @Override
    public int n(){//this function feeds the others in the class
        boolean in;
        do{
            in = false;
            this.c = (int)(Math.random()*this.max);
            for ( int i = 0; i < this.log.length; i++ ){
                if( this.log[i] == this.c ){
                    in = true;
                    break;
                }
            }
        } while( in );
        this.logIndex++;
        log[ this.logIndex%this.log.length ] = this.c;
        return this.c;
    }
    public int[] getLog(){// for dev
        return log;
    }
    @Override
    public void setInitial( int itrLength ){//make a counter
        this.counter = itrLength;
    }
    public void setFinal(){// choose an arbitrary value to stop on
        this.stopOn = this.max-1;
    }
    public void setFinal( int stopOnValue ){// set valid value (no protection)
        this.stopOn = stopOnValue;
    }
    @Override
    public boolean hasNext() { 
        return ( 0 < this.counter-- ) && ( this.nPrev() != this.stopOn );
    } 
    @Override
    public void rewind(){}    
}
