package commons;

import erlog.Erlog;

/**Defeatable static logger for development.
 *
 * @author Dave Swanson
 */
public class Dev {
    /*==========Defeatable static logger======================================*/
    public static final String TAB = "\t|";

    private static boolean displayOn = true;
    private static boolean stackTraceOn = true;
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
        for (Object arg : args) {
            println(arg);
        }
        print_after();
    }
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
        }
        else{
//            int len = Math.min( trace.length, TRACE_SKIPS + TRACE_READS);
//            for(int i = TRACE_SKIPS; i<len; i++){
//                println( "\t" + trace[i] );
//            }

            for(int i = 0; i<trace.length; i++){
                String traceStr = trace[i].toString();
                if(traceStr != null && traceStr.contains("Erlog.")){
                    for(int j = i+1; j<trace.length; j++){
                        println( "\t" + trace[j] );
                    }
                    break;
                }
            }
            System.out.println();
        }
    }
    
    /*==========Wrapper method to add tab=====================================*/
    
    private static void println(Object obj){
        System.out.println(TAB + s(obj));
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
