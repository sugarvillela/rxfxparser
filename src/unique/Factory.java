/* Notes on Random UQ sequence
 * Prevents duplicates by logging previous. 
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
//Dev.b( "", );
import commons.BIT;
import commons.Commons;
import commons.Dev;
import java.util.Iterator;

/**Builds unique sequence generators
 *
 * @author Dave Swanson
 */
public abstract class Factory {
    public static final int INT_SIZE = 32;
    static String formatPattern = "%08X";
    
    public static void setPrefix(int strPad, String setPrefix){
        formatPattern = setPrefix+"%0"+strPad+"d";
    }
    
    /*=====Factory methods====================================================*/
    public static UQSequence getRandom(int setMax){
        return new Random(setMax, formatPattern);
    }
    public static UQSequence getSequential(){
        return new Sequential(1, 0, 0x7FFFFFFF, formatPattern);
    }
    public static UQSequence getSequential(int setIncrement, int setInitial, int SetFinal){
        return new Sequential(setIncrement, setInitial, SetFinal, formatPattern);
    }
    public static UQSequence getShift(){//set final -1 for all 32 iterations
        return new Shift(1, 1, -1, formatPattern);
    }
    public static UQSequence getShift(int setIncrement, int setInitial, int SetFinal){
        return new Shift(setIncrement, setInitial, SetFinal, formatPattern);
    }
    public static UQSequence getMask(int wcol, int initialCol, int finalCol){
        return new Mask(wcol, initialCol, finalCol);
    }
    public static UQSequence getDiscrete(int wcol, int setInitial){
        return new Discrete(wcol, setInitial, formatPattern);
    }
    public static UQSequence getEnubGen(int wrow, int initialEnub, int finalEnub){
        return new EnubGen(wrow, initialEnub, finalEnub, formatPattern);
    }
    public static UQSequence getEnudGen(int wrow, int wcol, int initialEnub, int finalEnub){
        return new EnudGen(wrow, wcol, initialEnub, finalEnub, formatPattern);
    }

    /*=====Unique sequence generator base class===============================*/
    public static abstract class UQSequence implements Iterable<Integer>{
        protected int inc, valInit, valCurr, valNext, valFin;
        protected String pattern;
        
        public UQSequence(){}
        
        // Abstracts to implement
        public abstract int next();
        public abstract boolean hasNext();

        // Optional to implement: Lifecycle events for enum generators
        public void onFirst(){}
        public void onSubsequent(){}
        public void onLast(){}
        public void newCol(){}
        public void newRow(){}

        // Already implemented
        public void rewind(){
            Dev.b( "inc, valInit, valFin", inc, valInit, valFin);
            valCurr = valNext = valInit;
        }
        public void disp(){
            System.out.println( BIT.str( valCurr ) );
        }

        // Extras implemented
        @Override
        public String toString(){
            return String.format( pattern, valCurr );
        }
        @Override
        public Iterator<Integer> iterator() { 
            return new UQitr(this); 
        }
    }
    
    public static class Sequential extends UQSequence{
        public Sequential(){}
        public Sequential(int setIncrement, int setInitial, int SetFinal, String setPattern){
            inc = setIncrement;
            valInit = setInitial;
            valFin = SetFinal; 
            pattern = setPattern;
            super.rewind();
        }
        @Override
        public int next() {
            valCurr = valNext;
            valNext += inc;
            return valCurr;
        }
        @Override
        public boolean hasNext() {
            return valCurr < valFin;
        }
    }
    
    public static class Random extends UQSequence{
        private int[] log;      // prevent duplicates by logging output
        private int logIndex;         // dest index in log
        
        public Random(int setMax, String setPattern){
            inc = setMax;
            rewind();
        }
        @Override
        public int next() {
            boolean isDup;
            do{
                isDup = false;
                valCurr = (int)(Math.random()*inc);
                for ( int i = 0; i < log.length; i++ ){
                    if( log[i] == valCurr ){
                        isDup = true;
                        break;
                    }
                }
            } while( isDup );
            log[ logIndex ] = valCurr;
            logIndex++;
            return valCurr;
        }
        @Override
        public boolean hasNext() { // runs max/2 times
            return logIndex < log.length;
        }
        @Override
        public final void rewind(){
            logIndex = 0;
            log = new int[(int)Math.ceil(inc/2.0)];
        }
    }
    
    public static class Shift extends UQSequence{
        protected ShiftCompare compare;
        
        public Shift(){}
        public Shift(int setIncrement, int setInitial, int SetFinal, String setPattern){
            inc = setIncrement;
            valInit = (setInitial == 0)? 1 : setInitial;
            valFin = SetFinal;
            compare = getCompare(valFin);
            pattern = setPattern;
            super.rewind();
        }
        @Override
        public int next() {
            valCurr = valNext;
            valNext <<= inc;
            return valCurr;
        }
        @Override
        public boolean hasNext() { 
            Dev.b( "inc, valInit, valFin", inc, valInit, valFin);
            return compare.hasNext( inc, valCurr, valFin );
        }
        public static ShiftCompare getCompare(int val){
            if( val < 0 ){                  // 0x80000000 -> 0xFFFFFFFF
                return new ShiftCompare(){// Stops At Neg
                    @Override
                    public boolean hasNext(int inc, int curr, int fin){
                        Dev.b( "curr, fin", curr, fin);
                        return curr > 0;
                    }
                };
            }
            else if( (val << 1) < 0 ){      // 0x40000000 -> 0x4FFFFFFF
                return new ShiftCompare(){// Stops At Last Positive
                    @Override
                    public boolean hasNext(int inc, int curr, int fin){
                        Dev.b( "curr, fin", curr, fin);
                        return (curr<<inc) > 0;
                    }
                };
            }
            else{                           // 0x00000001 -> 0x20000000
                return new ShiftCompare(){// Stops At GTE; valFin=0 won't run at all
                    @Override
                    public boolean hasNext(int inc, int curr, int fin){
                        Dev.b( "curr, fin", curr, fin);
                        return (curr<<inc)>0 && (curr<<inc) <= fin;
                    }
                };
            }
        }
    }
    
    public static class Mask extends UQSequence{
        protected UQSequence uq;
        public Mask(int wcol, int initialCol, int finalCol){
            int mask = (1 << wcol )-1;
            valInit = mask << (initialCol*wcol);
            valFin = mask << (finalCol*wcol);
            inc = wcol;
            Dev.b( "mask, valInit, valFin", mask, valInit, valFin);
            uq = Factory.getShift( inc, valInit, valFin );
        }
        @Override
        public int next() {
            return ( valCurr = uq.next() );
        }
        @Override
        public boolean hasNext() { 
            return uq.hasNext();
        }
        @Override
        public void rewind(){//
            uq = Factory.getShift( inc, valInit, valFin );
        }
        @Override
        public String toString(){
            return uq.toString();
        }
    }
    
    public static class Discrete extends UQSequence{
        protected UQSequence uq;
        public Discrete(int wcol, int setInitial, String setPattern){
            valInit = setInitial;
            inc = (valInit==0)? 1 : valInit;
            valFin = (inc << wcol) - inc; 
            Dev.b( "inc, valInit, valFin",inc, valInit, valFin );
            uq = Factory.getSequential( inc, valInit, valFin );
            
            pattern = setPattern;
        }
        @Override
        public int next() {
            return ( valCurr = uq.next() );
        }
        @Override
        public boolean hasNext() { 
            return uq.hasNext();
        }
        @Override
        public void rewind(){//
            uq = Factory.getSequential( inc, valInit, valFin );
        }
    }
    
    /*=====Specialized Applications of the basic Classes======================*/
    private static class EnuRowSequential extends Sequential{
        public EnuRowSequential(int wrow, int initialEnub, int finalEnub, String setPattern){
            int wval = INT_SIZE - wrow;
            int rowMask = ~((1 << wval)-1);
            inc = (1 << wval);
            valInit = initialEnub & rowMask;
            valFin = finalEnub & rowMask; 
            pattern = setPattern;
            super.rewind();
            Dev.b( "initialEnub, finalEnub", initialEnub, finalEnub );
        }
    }
    private static class EnuColShift extends Shift{
        protected int valInit_param, valFin_param;
        protected boolean first;// workaround for init==fin && rowInit != rowFin
        //protected boolean finalWasFixed, lastIteration;
        public EnuColShift(){}
        public EnuColShift(int wrow, int initialEnub, int finalEnub, String setPattern){
            int wval = INT_SIZE - wrow;
            int valMask = (1 << wval)-1;
            
            /* Keep given init and fin parameters (see onFirst(), onLast() */
            valInit_param = initialEnub & valMask;
            valFin_param = finalEnub & valMask;
            if(valInit_param == 0){
                valInit_param = 1;
            }
            if(valFin_param == 0){
                valFin_param = 1;
            }
            
            if(initialEnub == finalEnub){
                valFin = 0;
                first = false;
            }
            else{
                valFin = 1 << (wval-1);
                first = true; // workaround for init==fin && rowInit != rowFin
            }
            compare = getCompare(valFin);
            inc = 1;
            pattern = setPattern;
            onFirst();
            Dev.b( "valMask, valInit_param, valFin_param", valMask, valInit_param, valFin_param);
        }
        @Override
        public int next() {
            first = false;
            return super.next();
        }
        @Override
        public boolean hasNext() { 
            Dev.b( "inc, valCurr, valFin, first",inc, valCurr, valFin, Commons.boolInt(first) );
            return compare.hasNext( inc, valCurr, valFin ) || first;
        }
        @Override
        public final void onFirst(){
            /* First iteration uses given init, default fin */
            Dev.b( "valInit_param", valInit_param);
            valInit = valInit_param;
            rewind();
        }
        @Override
        public final void onSubsequent(){
            /* Subsequent iteration uses default init, default fin */
            Dev.d();
            valInit = 1;
            rewind();
        }
        @Override
        public final void onLast(){
            /* Last iteration uses default init, given fin */
            Dev.b( "valFin_param", valFin_param);
            valInit = 1;
            valFin = valFin_param;
            compare = getCompare(valFin);
            rewind();
        }
    }
    private static class EnuColSequential extends EnuColShift{
        protected UQSequence colGen;
        protected int currColInit, currColFin, wcol;
        
        public EnuColSequential(int wrow, int wcolSet, int initialEnub, int finalEnub, String setPattern){
            wcol = wcolSet;
            int wval = INT_SIZE - wrow;
            wval -= (wval % wcol);      // correct for bad fit
            int valMask = (1 << wval)-1;

            /* Keep given init and fin parameters (see onFirst(), onLast() */
            valInit_param = initialEnub & valMask;
            valFin_param = finalEnub & valMask;
            if(valInit_param == 0){
                valInit_param = 1;
            }
            if(valFin_param == 0){
                valFin_param = (1 << wcol)-1;
            }
            
            if(initialEnub == finalEnub){
                valFin = 0;
                first = false;
            }
            else{
                valFin = ( 1 << wval ) - (1 << (wval-wcol));// last column
                first = true; // workaround for init==fin && rowInit != rowFin
            }
            currColInit = valInit = valInit_param;
            currColFin = currColInit + ((1 << wcol)-1);
            currColFin -= (currColFin % wcol);      // correct for bad fit
            currColFin = Math.min(currColFin-1, valFin);
            Dev.b("wcol, currColInit, currColFin", wcol, currColInit, currColFin);
            Dev.b( "valMask, alInit_param, valFin_param, valFin", valMask, valInit_param, valFin_param, valFin);
            colGen = Factory.getSequential(1, currColInit, currColFin );
            pattern = setPattern;

        }
        @Override
        public int next() {
            first = false;
            valCurr = colGen.next();
            Dev.b( "valCurr", valCurr);
            if(!colGen.hasNext()){
                newCol();
            }
            return valCurr;
        }
        @Override
        public boolean hasNext() { 
            Dev.b( "inc, valCurr, valFin, first",inc, valCurr, valFin, Commons.boolInt(first) );
            return colGen.hasNext() || valCurr < valFin || first;//valCurr < valFin;
        }
        @Override
        public void newCol(){
            if( valCurr < valFin ){
                currColInit = currColFin+1;
                currColFin = (currColInit << wcol) - currColInit;
                Dev.b( "wcol, currColInit, currColFin", wcol, currColInit, currColFin);
                colGen = Factory.getSequential(currColInit, currColInit, currColFin );
            }
        }
        @Override
        public void rewind(){
            currColInit = valCurr = valNext = valInit;
            currColFin = currColInit + ((1 << wcol)-1);
            currColFin--;
            Dev.b( "wcol, currColInit, currColFin", wcol, currColInit, currColFin);
            colGen = Factory.getSequential(1, currColInit, currColFin );
        }
    }
    public static class EnubGen extends UQSequence{
        protected UQSequence colGen, rowGen;
        protected int valRowCurr;
        
        public EnubGen(){}
        public EnubGen(int wrow, int initialEnub, int finalEnub, String setPattern){
            rowGen = new EnuRowSequential( wrow, initialEnub, finalEnub, setPattern );
            colGen = new EnuColShift( wrow, initialEnub, finalEnub, setPattern );
            valRowCurr = rowGen.next();
            if(!rowGen.hasNext()){
                colGen.newCol();
            }
            pattern = setPattern;
        }
        @Override
        public int next() {
            int valCol;
            if( colGen.hasNext() ){
                valCol = colGen.next();
            }
            else {
                valRowCurr = rowGen.next();
                //valCol = 0;
                if( rowGen.hasNext() ){
                    //valRowCurr = rowGen.next();
                    colGen.onSubsequent();
                    valCol = colGen.next();
                }
                else{
                    colGen.onLast();
                    valCol = colGen.next();
                }
            }

            valCurr = valRowCurr | valCol;
            return valCurr;
        }
        @Override
        public boolean hasNext() { 
            return rowGen.hasNext() || colGen.hasNext();
        }
        @Override
        public void rewind(){//
            super.rewind();
            colGen.rewind();
            rowGen.rewind();
            valRowCurr = rowGen.next();
            //uq = Factory.getShift( inc, valInit, valFin );
        }
    }
    
    public static class EnudGen extends EnubGen{
        protected int valFin_param, wcol;//valRowCurr, 
        
        public EnudGen(int wrow, int wcol, int initialEnub, int finalEnub, String setPattern){
            rowGen = new EnuRowSequential( wrow, initialEnub, finalEnub, setPattern );
            colGen = new EnuColSequential(wrow, wcol, initialEnub, finalEnub, setPattern); 
            valRowCurr = rowGen.next();            
            pattern = setPattern;
            //Dev.b( "", );

        }
        @Override
        public void rewind(){//
            super.rewind();
            colGen.rewind();
            rowGen.rewind();
            valRowCurr = rowGen.next();
        }
    }
    
    /*=====Utils to help Shift class stop on time=============================*/
    public interface ShiftCompare{
        public boolean hasNext(int inc, int curr, int fin );
    }
    
    /*=====Detachable iterator to use with foreach loops======================*/
    public static class UQitr implements Iterator{
        private final UQSequence seq;
        
        public UQitr( UQSequence setSequence ){
            seq = setSequence;
        }
        @Override
        public boolean hasNext() {
            return seq.hasNext();
        }
        @Override
        public Object next() {
            return seq.next();
        }
        @Override
        public void remove(){}
    }
}
