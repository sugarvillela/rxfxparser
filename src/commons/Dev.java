package commons;

/**Defeatable static logger
 *
 * @author Dave Swanson
 */
public class Dev {
    /*==========Defeatable static logger======================================*/
    private static boolean displayOn = true;
    public static final int TRACE_SKIPS = 4;
    public static final int TRACE_READS = 4;
    
    public static void dispOn(){ displayOn = true; }
    public static void dispOff(){ displayOn = false; }
    
    public static void d(){
        if(!displayOn){ return; }
        System.out.println("\n===============================================");
        trace();
        System.out.println("\n===============================================");
    }
    public static void d(String... args){
        if(!displayOn){ return; }
        System.out.println("\n===============================================");
        trace();
        System.out.println( args[0] + ":");
        for(int i=1; i<args.length; i++){
            System.out.print( args[i] + ", ");
        }
        System.out.println("\n===============================================");
    }
    public static void d(String label, String text, int... args){
        if(!displayOn){ return; }
        System.out.println("\n===============================================");
        trace();
        System.out.println( label + ":");
        System.out.print( text + ", ");
        for(int i : args){
            System.out.print( i + ", ");
        }
        System.out.println("\n===============================================");
    }
    public static void d(String label, int... args){
        if(!displayOn){ return; }
        System.out.println("\n===============================================");
        trace();
        System.out.println( label + ":");
        for(int i : args){
            System.out.print( i + ", ");
        }
        System.out.println("\n===============================================");
    }
    public static void h(String label, int... args){
        if(!displayOn){ return; }
        System.out.println("\n===============================================");
        trace();
        System.out.println( label + ":");
        for(int i : args){
            System.out.printf( "%08X, ", i );
        }
        System.out.println("\n===============================================");
    }
    public static void b(String label, int... args){
        if(!displayOn){ return; }
        System.out.println("\n===============================================");
        trace();
        System.out.println( label + ":");
        for(int i : args){
            BIT.disp(i);
        }
        System.out.println("\n===============================================");
    } 
    
    public static <T> void trace(){
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if( trace==null){
            System.out.println("No stack trace" );
            return;
        }
        else{
            int len = Math.min( trace.length, TRACE_SKIPS + TRACE_READS);
            for(int i = TRACE_SKIPS; i<len; i++){
                System.out.print( trace[i] + "\t" );
            }
            System.out.println();
        }
    }

    /*==========Below: defeat the defeat======================================*/
    public static void now(){
        displayOn = true;
        d();
        displayOn = false;
    }
    public static void now(String... args){
        displayOn = true;
        d(args);
        displayOn = false;
    }
    public static void now(String label, String text, int... args){
        displayOn = true;
        d(label, text, args);
        displayOn = false;
    }
    public static void now(String label, int... args){
        displayOn = true;
        d(label, args);
        displayOn = false;
    }
    public static void hnow(String label, int... args){
        displayOn = true;
        h(label, args);
        displayOn = false;
    }
    public static void bnow(String label, int... args){
        displayOn = true;
        b(label, args);
        displayOn = false;
    }
}
