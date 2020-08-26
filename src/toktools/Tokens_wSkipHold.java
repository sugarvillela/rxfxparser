package toktools;

import java.util.ArrayList;
import java.util.Stack;
import static toktools.TK.DELIMIN;
import static toktools.TK.HOLDOVER;
import static toktools.TK.SKIPOUT;
import static toktools.TK.SYMBOUT;
/**
 *
 * @author Dave Swanson
 */
public class Tokens_wSkipHold extends Tokens_special{
    private SkipBehavior skipBehavior;
    private HoldBehavior holdBehavior;
    
    public Tokens_wSkipHold( String delims, String skips, int flags ) {
        super( delims, skips, flags );
        setMoreFlags(flags);
    }
    
    protected final void setMoreFlags( int flags ){
        skipBehavior = ( ( flags & SKIPOUT )==0 )? new Skip_false() : new Skip_true();
        holdBehavior = ( ( flags & HOLDOVER )==0 )? new Hold_false() : new Hold_true();
    }
    @Override
    public ArrayList<String> getSkips(){
        return this.skipBehavior.getSkips();
    } 
    // overriding these two for more complex behavior
    @Override
    protected void initParse(){
        this.holdBehavior.initParse();// maybe init
    }
    @Override
    protected void addToTokens(String txt){
        holdBehavior.add(txt);
    }
    
    protected abstract class SkipBehavior{
        public SkipBehavior(){}
        public abstract void newList();
        public abstract void add( String skipText );
        public abstract ArrayList<String> getSkips();
    }
    protected class Skip_false extends SkipBehavior{
        // This one ignores command to add to skips; adds to tokens instead
        @Override
        public void newList(){}
        @Override
        public void add( String skipText ){
            tokens.add( skipText );
        }
        @Override
        public ArrayList<String> getSkips(){
            return null;
        }
    }
    protected class Skip_true extends SkipBehavior{
        protected ArrayList<String> skips;
        // this one adds to skips when commanded
        @Override
        public void newList(){
            skips = new ArrayList<>();
        }
        @Override
        public void add( String skipText ){
            if(cSymb.empty()){
                tokens.add( skipText );
            }
            else{
                skips.add( skipText );
            }
        }
        @Override
        public ArrayList<String> getSkips(){
            return skips;
        }
    }
    protected abstract class HoldBehavior{

        public abstract void initParse();
        public abstract void add( String skipText );
    }
    protected class Hold_false extends HoldBehavior{
        // always resets
        @Override
        public void initParse(){
            cSymb = new Stack<>();
            tokens = new ArrayList<>();    // for main tokenized output
            skipBehavior.newList();        // to put skips in
        }
        @Override
        public void add( String skipText ){
            skipBehavior.add( skipText );
        }
    }
    protected class Hold_true extends HoldBehavior{
        protected ArrayList<String> holdingTokens;
        
        public Hold_true(){
            holdingTokens = new ArrayList<>();
        }
        @Override
        public void initParse(){
            if(!isHolding()){
                cSymb = new Stack<>();
                tokens = new ArrayList<>();     // for main tokenized output
                skipBehavior.newList();        // to put skips in
            }
        }
        @Override
        public void add( String skipText ){
            if(isHolding()){
                holdingTokens.add( skipText );
            }
            else if( !holdingTokens.isEmpty() ){
                skipBehavior.add( String.join( " ", holdingTokens ) );
                skipBehavior.add( skipText );
                holdingTokens = new ArrayList<>();
            }
            else{
                skipBehavior.add( skipText );
            }
            
        }
    }
}
