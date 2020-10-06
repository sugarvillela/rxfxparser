
package commons;

//import java.lang.Math;

/**
 * A suite of bitwise functions for bit access, counting and display
 *
 * @author Dave Swanson
 */
public class BIT {
    
    public static String str( int n ){
        return BIT.str( n, "0000_0000_0000_0000_0000_0000_0000_0000" );
    }
    public static String str( int n, int w ){
        return BIT.str( n, BIT.binaryFormatString(w) );
    }
    /**
     * Get number as binary string; underscore spacers for readability
     *
     * @author Dave Swanson
     * @param n
     * @param formatString
     * @return String
     */
    public static String str( int n, String formatString ){
        /* Format string can include underscores for spacing */
        char[] fstring = formatString.toCharArray();
        for( int i=fstring.length-1; i>=0; i--){
            if( fstring[i]=='_' ){
               continue; 
            }
            if( (n & 1)>0 ){
                fstring[i]='1';
            }
            n=n>>1;
        }
        return String.valueOf(fstring);
    }
    public static void disp( int n ){
        System.out.printf( "%s \t %08X \n", BIT.str( n, "0000_0000_0000_0000_0000_0000_0000_0000" ), n );
    }
    public static void disp( int n, String formatString ){
        System.out.println( BIT.str( n, formatString ) );
        System.out.printf( "%s \t %08X \n", BIT.str( n, formatString ), n );
    }
    public static void disp( int[] n, String formatString ){
        for( int i=0; i<n.length; i++ )
            System.out.println( BIT.str( n[i], formatString ) );
    }
    /**
     * Get number of bits required to hold the given number
     *
     * @author Dave Swanson
     * @param n
     * @return 
     */
    public static int logCeil(int n ){
        /* Returns the number of bits required to hold the given number */
        return (n==0)? 1 : (n<0)? 32 : 1 + (int)(Math.log(n)/Math.log(2));
    }
    /**
     * First non-zero bit; count from lsb-msb
     *
     * @author Dave Swanson
     * @param n
     * @return 
     */
    public static int first1( int n ){//not called
        // find first non-zero bit from lsb-msb; return number
        int wordLen=32;
        for( int i=0; i<wordLen; i++ ){
            if((n&1)!=0){
                return i;
            }
            n = n>>1;
        }
        return wordLen;
    }
    public static int first0( int n ){//not called
        // find first zero bit from lsb-msb; return number
        int wordLen=32;
        for( int i=0; i<wordLen; i++ ){
            if((n&1)==0){
                return i;
            }
            n = n>>1;
        }
        return wordLen;
    }
    /**
     * Mask of 1's; count from lsb-msb; See code for variations
     *
     * @author Dave Swanson
     * @param width
     * @return (1<<width)-1;
     */
    public static int mask( int width ){
        return (1<<width)-1;
    }
    public static int mask( int width, int start ){
        return ((1<<width)-1)<<start;
    }
    public static int notMask( int width, int start ){
        return ~BIT.mask( width, start );
    }
    public static int notMask( int width ){
        return ~BIT.mask( width );
    }
    /**
     * Quantizes a value < width bits wide to a mask of width bits
     * 0 < val < 2^width
     *
     * @author Dave Swanson
     * @param val 0 < val < 2^width, at position within mask
     * @param width the width of the mask in bits
     * @return (1<<(w+width))-(1<<w) where w = (BIT.first1(val)/width)*width
     */
    public static int maskVal( int val, int width ){//don't pass 0
        int w = (BIT.first1(val)/width)*width;
        return (1<<(w+width))-(1<<w);
    }
    public static int maskRound( int val, int width ){
        return (BIT.first1(val)/width)*width;
    }
    /**
     * Substring for integers, same parameters as PHP's string version
     *
     * @author Dave Swanson
     * @param n
     * @param start
     * @return 
     */
    public static int subint( int n, int start ){
        /* Keep in mind ints are backward (assuming LSB on the right) 
           Start is inclusive, end non-inclusive: [start, end)
        */
        return (n>>start)&BIT.mask(32-start);
    }
    public static int subint( int n, int start, int end ){
        /* Keep in mind ints are backward (assuming LSB on the right) 
           Start is inclusive, end non-inclusive: [start, end)
        */
        return (n>>start) & BIT.mask( end-start );
    }
    /**
     * Concatenate integers: pass number|width pairs (even number of args!)
     *
     * @author Dave Swanson
     */
    public static int concint( int ...args ){//pass even number of args: int number, int width
        /* Variable arguments (always pass an even number of them) 
         * i is the number; i+1 is width (how many bits hold the number).
         * 
         * Option: Pass width=null to pack into minimum width (this is bad for 
         * uniform formatting as it ignores leading zeroes) */
        int out=0;
        for ( int i = 0; i < args.length-1; i+=2){
            int w = ( args[i+1]!=0 )? args[i+1] : BIT.logCeil( args[i] );
            out = out << w;
            out |= BIT.subint( args[i], 0, w );
        }
        return out;
    }
    /**
     * Same as concint but packs in the opposite order: msb-lsb
     *
     * @author Dave Swanson
     */
    public static int concintr( int ...args ){//pass even number of args
        int out=0;
        int shift=0;
        for ( int i = 0; i < args.length-1; i+=2){
            int w = ( args[i+1]!=0 )? args[i+1] : BIT.logCeil( args[i] );
            out |= BIT.subint( args[i], 0, w ) << shift;
            shift += w;
        }
        return out;
    }
    /**
     * Counts number of 1's in a binary number
     *
     * @author Dave Swanson
     */
    public static int numBits( int n ){
        int wordLen=32;
        int ones=0;
        for( int i=0; i<wordLen; i++ ){
            ones+=(n & 1);
            n=n>>1;
        }
        return ones;
    }
    public static boolean uGT( int a, int b ){
        if( a<0 ){
            if( b<0 ){  // a(31)=1  b(31)=1 
                return ( (a&0x7FFFFFF)>(b&0x7FFFFFF) );
            }
            else{       // a(31)=1 b(31)=0 
                return true;
            }
        }
        else if( b<0 ){
            if( a<0 ){  // a(31)=1  b(31)=0 
                return ( (a&0x7FFFFFF)>(b&0x7FFFFFF) );
            }
            else{       // b(31)=1 a(31)=0 
                return false;
            }
        }
        else{           // a(31)=0  b(31)=0 
            return ( a>b );
        }
    }
    public static int uMax( int a, int b ){
        return (BIT.uGT( a, b ))? a : b;
    }
    public static int uMin( int a, int b ){
        return (BIT.uGT( a, b ))? b : a;
    }
    /* Helper function for BIT.str */
    public static String binaryFormatString( int w ){//nonzero width
        /* generate format string like this: 
         * 0000_0000_0000_0000_0000_0000_0000_0000 for w = 4 
         * 00_000_000_000_000_000_000_000_000_000_000 for w = 3
         * Leftovers to the left.
         * Important: Don't pass w=0 or bad things will happen.
         */
        if(w<2){
            w=4;
        }
        int numScores = (int)Math.ceil( 32.0/w )-1;
        char[] fstring = new char[32+numScores];
        for (int i = 0; i < fstring.length; i++){
            fstring[i] = '0';
        }
        w++;
        for (int i = fstring.length-1, j=1; i >0; i--, j++){
            if( (j)%w==0 ){
                fstring[i] = '_';
            }
        }
        return String.valueOf(fstring);
    } 
    /* Math */
    public static int log2( int n ){
        return (int)(Math.log(n)/0.69314718);
    }

    public static int binStrToInt(String binStr){
        //System.out.println(binStr);
        int out = 0;
        for(int i = 0; i < binStr.length(); i++){
            char ch = binStr.charAt(i);
            if(ch != '_'){
                out <<= 1;
                if(ch == '1'){
                    out++;
                }

            }
        }
        return out;
    }
}
