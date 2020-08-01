package commons;

/**Defeatable static logger for development.
 *
 * @author Dave Swanson
 */
public class Dev {
    /*==========Defeatable static logger======================================*/
    
    private static boolean displayOn = true;
    private static boolean stackTraceOn = true;
    public static final String TAB = "\t|";
    public static final int TRACE_SKIPS = 4;
    public static final int TRACE_READS = 4;
    
    public static void dispOn(){ displayOn = true; }
    public static void dispOff(){ displayOn = false; }
    public static void traceOn(){ stackTraceOn = true; }
    public static void traceOff(){ stackTraceOn = false; }
    
    public static String s(Object o){
        return (o == null)? "NULL" : o.toString();
    }
    public static void d(){
        if(!displayOn){ return; }
        print_before();
        trace();
        print_after();
    }
    public static void d(Object... args){
        if(!displayOn){ return; }
        print_before();
        trace();
        println( args[0] + ":");
        for(int i=1; i<args.length; i++){
            println( args[i] + ", ");
        }
        print_after();
    }
//    public static void d(String label, int... args){
//        if(!displayOn){ return; }
//        print_before();
//        trace();
//        println( label + ":");
//        for(int i : args){
//            println( i + ", ");
//        }
//        print_after();
//    }
    public static void h(String label, int... args){
        if(!displayOn){ return; }
        print_before();
        trace();
        println( label + ":");
        for(int i : args){
            println( String.format("%08X, ", i) );
        }
        print_after();
    }
    public static void b(String label, int... args){
        if(!displayOn){ return; }
        print_before();
        trace();
        println( label + ":");
        for(int i : args){
            println(BIT.str(i));
        }
        print_after();
    } 
    
    /*==========Abridged stack trace==========================================*/
    
    public static void trace(){
        if(!stackTraceOn){ return; }
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if( trace==null){
            println("No stack trace" );
            return;
        }
        else{
            int len = Math.min( trace.length, TRACE_SKIPS + TRACE_READS);
            for(int i = TRACE_SKIPS; i<len; i++){
                println( "\t" + trace[i] );
            }
            println();
        }
    }
    
    /*==========Wrapper method to add tab=====================================*/
    
    private static void print( String text ){
        System.out.print( TAB + text  );
    }
    private static void println(){
        System.out.println( TAB  );
    }
    private static void println( String text ){
        System.out.println( TAB + text  );
    }
    private static void print_before(){
        System.out.println( "\n" + TAB + "===========================================================" );
    }
    private static void print_after(){
        System.out.println( TAB + "===========================================================\n" );
    }

    /*==========Below: defeat the defeat======================================*/
    
    public static void now(){
        displayOn = true;
        stackTraceOn = true;
        d();
        displayOn = false;
        stackTraceOn = false;
    }
    public static void now(Object... args){
        displayOn = true;
        stackTraceOn = true;
        d(args);
        displayOn = false;
        stackTraceOn = false;
    }
    public static void now(String label, int... args){
        displayOn = true;
        stackTraceOn = true;
        d(label, args);
        displayOn = false;
        stackTraceOn = false;
    }
    public static void hnow(String label, int... args){
        displayOn = true;
        stackTraceOn = true;
        h(label, args);
        displayOn = false;
        stackTraceOn = false;
    }
    public static void bnow(String label, int... args){
        displayOn = true;
        stackTraceOn = true;
        b(label, args);
        displayOn = false;
        stackTraceOn = false;
    }
}
