package erlog;

import compile.basics.CompileInitializer;
import toksource.Base_TextSource;
import toksource.interfaces.ChangeListener;
import toksource.interfaces.ChangeNotifier;
import toksource.interfaces.ITextStatus;

/** Decorator pattern for ErlogCore: adds per-instance caller class name
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
        erlogCore = ErlogCore.getCurrentInstance(); 
    }
    public static Erlog get(){
        return new Erlog("Erlog");
    }
    public static Erlog get(Object object){// same erasure for string and object
        if(object instanceof String){
            return new Erlog((String)object);
        }
        else{
            return new Erlog(object.getClass().getSimpleName());
        }
    }
    
    public static void initErlog(int setBehavior){
        ErlogCore.init(setBehavior);
    }

    protected ErlogCore erlogCore;
    protected String className;
    
    /*=====Public API=========================================================*/
    
    /**Set error, let current instance of ITextSource provide line, col number
     * @param message describes the error */
    public void set( String message ){
        erlogCore.set( message, className );
    }
    
    /**Set error, let current instance of ITextSource provide line, col number
     * @param message describes the error 
     * @param text text that caused the error */
    public void set( String message, String text ){
        erlogCore.set( message + ": " + text, className );
    }
    
    /**Writes accumulated errors to file */
    public void finish(){
        erlogCore.finish();
    }
    
    /**Displays accumulated errors */
    public void disp(){
        erlogCore.disp();
    }
    
    /** @param textStatus provides line, col status for error report */
    public void setTextStatusReporter(ITextStatus textStatus){
        erlogCore.setTextStatusReporter(textStatus);
    }
    
    /** @return object to report parsing status, if any */
    public ITextStatus getTextStatusReporter(){
        return erlogCore.getTextStatusReporter();
    }
    
    /** No line, column status in error report */
    public void clearTextStatusReporter(){
        erlogCore.clearTextStatusReporter();
    }

    @Override
    public void onTextSourceChange(ITextStatus textStatus, ChangeNotifier caller) {
        erlogCore.setTextStatusReporter(textStatus);
    }
}
