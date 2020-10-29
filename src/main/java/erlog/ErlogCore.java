package erlog;

import commons.Dev;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import toksource.NullSource;
import toksource.interfaces.ITextStatus;

/** Package-private class to provide core functions for logging, notifying and terminating program
 *  A quiet logger or a verbose quitter, depending on behavior chosen.
 * @author Dave Swanson
 */
class ErlogCore {//package private class
      
    /*=====Care and Feeding===================================================*/
    
    protected Behavior behavior;                // behavior class nested within
    protected Printer printer;                  // print red or black
    protected String fname;                     // for log output file
    protected ITextStatus nullStatus, textStatus;
    
    protected ErlogCore(int setBehavior){
        textStatus = nullStatus = new NullSource();
        this.setBehavior(setBehavior);
    }
    protected final void setBehavior( int setBehavior ){// Specify both
        this.fname = "dlog.txt";
        
        if( (Erlog.USESYSOUT & setBehavior) != 0 ){   // Print red or black
            printer = new Black();
            setBehavior &= 0xF;                 // Clear USESYSOUT flag
        }
        else{
            printer = new Red();
        }
        
        switch(setBehavior){
            case Erlog.IGNORE:
                this.behavior=new Ignore();
                break;
            case Erlog.LOG:
                this.behavior=new Log();
                break;
            case Erlog.NOTIFY:
                this.behavior=new Notify();
                break;
            case Erlog.DISRUPT:
                this.behavior=new Disrupt();
                break;
            default:
                new Disrupt().set("Bad value ErLog.java", "ErlogCore");
                break;
        }
    }
        
    /*=====Public API=========================================================*/
    
    /**Set error, let current instance of ITextSource provide line, col number
     * @param text describes the error */
    public static void set( String text, String className ){
        instance.behavior.set( text, className );
    }

    public static void set( String text, String at, String className ){
        instance.behavior.set( text, at, className );
    }
    
    /**Writes accumulated errors to file */
    public static void finish(){
        instance.behavior.finish();
    }
    
    /**Displays accumulated errors */
    public static void disp(){
        instance.behavior.disp();
    }
    
    /** @param textStatus provides line, col status for error report */
    public static void setTextStatusReporter(ITextStatus textStatus){
        if(textStatus != null){
            instance.textStatus = textStatus;
        }
    }
    
    /** @return object to report parsing status, if any */
    public static ITextStatus getTextStatusReporter(){
        return instance.textStatus;
    }
    
    /** No line, column status in error report */
    public static void clearTextStatusReporter(){
        instance.textStatus = instance.nullStatus;
    }
    
    /*=====Singleton Pattern Implementation===================================*/

    public static ErlogCore instance;
    
    public static void init(int setBehavior){
        instance = new ErlogCore(setBehavior);
    }
//    public static ErlogCore getInstance(){
//        if(instance == null){
//            throw new IllegalStateException(
//                "Erlog must be initialized before use"
//            );
//        }
//        return instance;
//    }
    
    /*=====Behavior strategy implementations==================================*/
    
    /**Abstract base class handles background work */
    protected abstract class Behavior{
        protected ArrayList<String> content;    // Accumulate messages
        
        public Behavior(){
            this.content = new ArrayList<>();
        }

        public abstract void set( String text, String className );
        public abstract void set( String text, String at, String className );

        public void finish(){
            try( 
                BufferedWriter file = new BufferedWriter(new FileWriter(fname));    
            ){
                for (String text: this.content) {
                    file.write(text);
                    file.newLine();
                }
                file.close();
            }
            catch(IOException e){
                System.err.println(e);
            }
        }
        
        public void disp(){
            for(String text : this.content ){
                System.err.println( text );
            }
        }
        
        // Utility for adding to error text if position info supplied
        protected String errText( String text, String className ){
            return String.format(
                "Error: %s: %s: %s",
                textStatus.readableStatus(),
                className,
                text
            );
        }
        protected String errText( String text, String at, String className ){
            //System.out.println(textStatus);
            return String.format(
                    "Error: %s: %s: %s at %s",
                    textStatus.readableStatus(),
                    className,
                    text,
                    at
            );
        }
    }
    /**Ignore: all errors passed without log, notification or exit */
    protected class Ignore extends Behavior{
        @Override
        public void set( String text, String className ){}
        @Override
        public void set( String text, String at, String className ){}
    }  
    
    /**Quietly log errors, no notification or exit. disp() shows errors */
    protected class Log extends Behavior{        
        @Override
        public void set( String text, String className ){
            this.content.add(this.errText(text, className));
        }
        @Override
        public void set( String text, String at, String className ){
            this.content.add(this.errText(text, at, className));
        }
    }
    
    /**Logs and prints errors to screen when set, no exit. */
    protected class Notify extends Log{        
        @Override
        public void set( String text, String className ){
            String errText = this.errText(text, className);
            this.content.add(errText);
            printer.print(errText);
        }
        @Override
        public void set( String text, String at, String className ){
            String errText = this.errText(text, at, className);
            this.content.add(errText);
            printer.print(errText);
        }
    }
    
    /**logs and prints to screen; exits on first error. */
    protected class Disrupt extends Notify{        
        @Override
        public void set( String text, String className ){
            String errText = this.errText(text, className );
            this.content.add(errText);
            printer.print(errText);
            this.finish();
            System.exit(0); 
        }
        @Override
        public void set( String text, String at, String className ){
            String errText = this.errText(text, at, className );
            this.content.add(errText);
            printer.print(errText);
            this.finish();
            System.exit(0);
        }
    }
    
    /** Print red or print prominently in black */
    protected abstract class Printer{
        public abstract void print(String errText);
    }
    protected class Red extends Printer{
        @Override
        public void print(String errText){
            System.err.println(errText);
        }
    }
    protected class Black extends Printer{
        @Override
        public void print(String errText){
            Dev.now(errText, "");
        }
    }
}
