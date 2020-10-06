package demos;

import commons.BIT;

/**
 * Test and demo commons.BIT
 * @author Dave Swanson
 */
public class BIT_ {
    public static void str(){
        System.out.println("===Demo BIT.str===");
        System.out.println("Binary display 129, -1, 0, 286331153, 1227133513");
        BIT.disp(129);
        BIT.disp(-1);
        BIT.disp(0);
        BIT.disp(286331153);
        BIT.disp(1227133513, "00_000_000_000_000_000_000_000_000_000_000");
    }
    public static void log_ceil(){
        int n=1;
        for ( int i = 0; i < 32; i++) {
            System.out.println( BIT.str(n-1)+", c="+BIT.logCeil(n-1) );
            n = n<<1;
        }
        System.out.println( BIT.str(-1)+", c="+BIT.logCeil(-1) );
    }
    public static void first1(){
        System.out.println("===Demo BIT.first1===");
        int n=1;
        for ( int i = 0; i < 32; i++) {
            System.out.println( BIT.str(n)+", c="+BIT.first1(n) );
            n = n<<1;
        }
        System.out.println("===Demo BIT.first0===");
        n=0x7FFFFFFF;
        for ( int i = 0; i < 32; i++) {
            System.out.println( BIT.str(n)+", c="+BIT.first0(n) );
            n = n>>1;
        }
    }
    public static void mask(){
        System.out.println("===Demo BIT.mask===");
        int start=1;
        int width=3;
        System.out.println( "start="+start+", width = "+width );
        BIT.disp( BIT.mask( width, start ) );
        
        System.out.println("========");
        System.out.println( "notMask: Same parameters inverted" );
        BIT.disp( BIT.notMask( width, start ) );
        
        System.out.println("========");
        start=6;
        width=24;
        System.out.println( "start="+start+", width = "+width );
        BIT.disp( BIT.mask( width, start ) );
        System.out.println("========");
        System.out.println( "start not passed, width = "+width );
        BIT.disp( BIT.mask( width ) );
    }
    public static void maskVal(){
        System.out.println("===Demo maskVal===");//
        System.out.println("===Width 3===");
        int width = 3;
        int val = 805306368;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 16384;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 4096;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 3072;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 16;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 5;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 1;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        System.out.println("===Width 4===");
        width=4;
        val = 805306368;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 16384;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 4096;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 3072;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 16;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 5;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 1;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        System.out.println("===Width 1===");
        width=1;
        val = 8388608;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 16384;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 32;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
        val = 1;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.str(BIT.maskVal(val, width), width));
        System.out.println( );
    }
    public static void maskRound(){
        System.out.println("===Demo maskRound===");//
        System.out.println("===Width 3===");
        int width = 3;
        int val = 805306368;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 16384;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 4096;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 3072;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 16;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 4;
        System.out.println("val="+val);
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 1;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        System.out.println("===Width 4===");
        width=4;
        val = 805306368;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 16384;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 4096;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 3072;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 16;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 5;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 1;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        System.out.println("===Width 1===");
        width=1;
        val = 8388608;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 16384;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 32;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
        val = 1;
        System.out.println(BIT.str(val, width));
        System.out.println(BIT.maskRound(val, width));
        System.out.println( );
    }
    public static void subint(){
        System.out.println("===Demo BIT.subint===");
        //int n = 0x87654321;
        //int n = 0x0F0F0F0F;
        int n = 0xAAAAAAAA;
        //int n = 0x0FFFFFFFF;
        System.out.println("orig number:");
        BIT.disp(n);
        System.out.println("subint 4, 12:");
        BIT.disp(BIT.subint( n, 4, 12 ) );
        
        System.out.println("========");
        n = 0x55555555;
        System.out.println("orig number:");
        BIT.disp(n);
        System.out.println("subint 4, 24:");
        BIT.disp(BIT.subint( n, 4, 24 ) );
        
        System.out.println("========");
        n = 0x0FFFFFFFF;
        System.out.println("orig number:");
        BIT.disp(n);
        System.out.println("subint 6, 26:");
        BIT.disp(BIT.subint( n, 6, 26 ) );
        
        System.out.println("========");
        System.out.println("subint start=6 (end not passed):");
        BIT.disp(BIT.subint( n, 6 ) );
    }
    public static void concint(){//pass even number of args: int number, int width
        System.out.println("===Demo BIT.concint===");
        System.out.println( "Auto-width with leading ones:Input: 8, 9, A, B, C" );
        int out=BIT.concint( 
            8, 0,
            9, 0, 
            10, 0,
            11, 0,
            12, 0
        );
        System.out.println( String.format( "%x", out ) );
        BIT.disp( out );
        System.out.println("========");
        System.out.println( "Auto-width ignores leading zeroes:Input: 1, 2, 3, 4, 5, 6, 7" );
        out=BIT.concint( 
            1, 0,
            2, 0, 
            3, 0,
            4, 0,
            5, 0,
            6, 0,
            7, 0
        );
        System.out.println( String.format( "%x", out ) );
        BIT.disp( out );
        System.out.println("========");
        System.out.println( "Manual-width:Input: 1, 2, 3, 4, 5, 6, 7");
        out=BIT.concint( 
            1, 4,
            2, 4, 
            3, 4,
            4, 4,
            5, 4,
            6, 4,
            7, 4
        );
        System.out.println( String.format( "%x", out ) );
        BIT.disp( out );
        System.out.println("========");
        System.out.println( "concintr orders right to left:Input: 1, 2, 3, 4, 5, 6, 7");
        out=BIT.concintr( 
            1, 4,
            2, 4, 
            3, 4,
            4, 4,
            5, 4,
            6, 4,
            7, 4
        );
        System.out.println( String.format( "%x", out ) );
        BIT.disp( out );
    }
    public static void numBits(){
        System.out.println("===Demo BIT.numBits===");
        int n=0x705A;
        for ( int i = 0; i < 16; i++) {
            System.out.println( BIT.str(n)+", c="+BIT.numBits(n) );
            n = n>>1;
        }
    }
    /* Helper function for BIT.str */
    public static void binaryFormatString(){//nonzero width
        String s;
        System.out.println("binaryFormatString: 16");
        s = BIT.binaryFormatString(16);
        System.out.println(s);
        System.out.println("========");
        System.out.println("binaryFormatString: 8");
        s = BIT.binaryFormatString(8);
        System.out.println(s);
        System.out.println("========");
        System.out.println("binaryFormatString: 7");
        s = BIT.binaryFormatString(7);
        System.out.println(s);
        System.out.println("========");
        System.out.println("binaryFormatString: 5");
        s = BIT.binaryFormatString(5);
        System.out.println(s);
        System.out.println("========");
        System.out.println("binaryFormatString: 4");
        s = BIT.binaryFormatString(4);
        System.out.println(s);
        System.out.println("========");
        System.out.println("binaryFormatString: 3");
        s = BIT.binaryFormatString(3);
        System.out.println(s);
        System.out.println("========");
        System.out.println("binaryFormatString: 2");
        s = BIT.binaryFormatString(2);
        System.out.println(s);
    } 
}
