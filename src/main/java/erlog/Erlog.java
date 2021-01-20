package erlog;

import compile.interfaces.Debuggable;
import toksource.interfaces.ChangeListener;
import toksource.interfaces.ChangeNotifier;
import toksource.interfaces.ITextStatus;

/** Error reporting for writer of rxfx source code, not java implementation.
 *  Decorator pattern for ErlogCore: adds per-instance caller class name
 * @author Dave Swanson */
public class Erlog implements ChangeListener {
    
    /*=====Behavior Options: you can OR the non-exclusive ones================*/
    
    public static final int IGNORE = 0;         // Ignore all errors
    public static final int LOG = 1;            // Quietly log to file
    public static final int NOTIFY = 2;         // Log and display immediately
    public static final int DISRUPT = 3;        // Log, display and kill program
    
    public static final int USESYSOUT = 0x10;   // OR this for black text display
      
    /*=====Care and Feeding===================================================*/
    
    private Erlog(String setClassName){
        this.className = setClassName;
        //erlogCore = ErlogCore.getInstance();
    }

    public static Erlog get(String text){// same erasure for string and object
        return new Erlog(text);
    }
    public static Erlog get(Debuggable debuggable){// same erasure for string and object
        return new Erlog(debuggable.getDebugName());
    }
    public static Erlog get(Object object){// same erasure for string and object
        return new Erlog(object.getClass().getSimpleName());
    }
    public static void initErlog(int setBehavior){
        ErlogCore.init(setBehavior);
    }

    //protected ErlogCore erlogCore;
    protected String className;
    
    /*=====Public API=========================================================*/
    
    /**Set error, let current instance of ITextSource provide line, col number
     * @param message describes the error */
    public void set( String message ){
        ErlogCore.set( message, className );
    }
    
    /**Set error, let current instance of ITextSource provide line, col number
     * @param message describes the error 
     * @param at text that caused the error */
    public void set( String message, String at ){
        ErlogCore.set( message, at, className );
    }
    
    /**Writes accumulated errors to file */
    public static void finish(){
        ErlogCore.finish();
    }
    
    /**Displays accumulated errors */
    public static void disp(){
        ErlogCore.disp();
    }
    
    /** @param textStatus provides line, col status for error report */
    public static void setTextStatusReporter(ITextStatus textStatus){
        ErlogCore.setTextStatusReporter(textStatus);
    }
    
    /** @return object to report parsing status, if any */
    public static ITextStatus getTextStatusReporter(){
        return ErlogCore.getTextStatusReporter();
    }
    
    /** No line, column status in error report */
    public static void clearTextStatusReporter(){
        ErlogCore.clearTextStatusReporter();
    }

    @Override
    public void onTextSourceChange(ITextStatus textStatus, ChangeNotifier caller) {
        ErlogCore.setTextStatusReporter(textStatus);
    }
}
