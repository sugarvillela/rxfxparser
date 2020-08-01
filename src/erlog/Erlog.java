package erlog;

import commons.Dev;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import toksource.NullSource;
import toksource.interfaces.ITextSource;
import toksource.interfaces.ITextStatus;

/**A quiet logger or a verbose quitter, depending on behavior chosen.
 * @author Dave Swanson*/
public class Erlog {
    
    /*=====Behavior Options: you can OR the non-exclusive ones================*/
    
    public static final int IGNORE = 0;         // Ignore all errors
    public static final int LOG = 1;            // Quietly log to file
    public static final int NOTIFY = 2;         // Log and display immediately
    public static final int DISRUPT = 3;        // Log, display and kill program
    
    public static final int USESYSOUT = 0x10;   // OR this for black text display
      
    /*=====Care and Feeding===================================================*/
    
    protected Behavior behavior;                // behavior class nested within
    protected Printer printer;                  // print red or black
    protected String fname;                     // for log output file
    protected ITextStatus nullStatus, textStatus;
    
    protected Erlog(int setBehavior){
        textStatus = nullStatus = new NullSource();
        this.setBehavior(setBehavior);
    }
    protected final void setBehavior( int setBehavior ){// Specify both
        this.fname = "dlog.txt";
        
        if( (USESYSOUT & setBehavior) != 0 ){   // Print red or black
            printer = new Black();
            setBehavior &= 0xF;                 // Clear USESYSOUT flag
        }
        else{
            printer = new Red();
        }
        
        switch(setBehavior){
            case IGNORE:
                this.behavior=new Ignore();
                break;
            case LOG:
                this.behavior=new Log();
                break;
            case NOTIFY:
                this.behavior=new Notify();
                break;
            case DISRUPT:
                this.behavior=new Disrupt();
                break;
            default:
                new Disrupt().set("Bad value ErLog.java");
                break;
        }
    }
        
    /*=====Public API=========================================================*/
    
    /**Set error, let current instance of ITextSource provide line, col number
     * @param text describes the error */
    public void set( String text ){
        this.behavior.set( text );
    }
    
    /**Writes accumulated errors to file */
    public void finish(){
        this.behavior.finish();
    }
    
    /**Displays accumulated errors */
    public void disp(){
        this.behavior.disp();
    }
    
    /** @param textStatus provides line, col status for error report */
    public void setTextStatusReporter(ITextStatus textStatus){
        this.textStatus = textStatus;
    }
    
    /** No line, column status in error report */
    public void clearTextStatusReporter(){
        textStatus = nullStatus;
    }
    
    /*=====Singleton Pattern Implementation===================================*/

    public static Erlog instance;
    
    public static void initErlog(int setBehavior){
        instance = new Erlog(setBehavior);
    }
    public static Erlog getCurrentInstance(){
        if(instance == null){
            throw new IllegalStateException(
                "Erlog must be initialized before use"
            );
        }
        return instance;
    }
    
    /*=====Behavior strategy implementations==================================*/
    
    /**Abstract base class handles background work */
    protected abstract class Behavior{
        protected ArrayList<String> content;    // Accumulate messages
        
        public Behavior(){
            this.content = new ArrayList<>();
        }
        public abstract void set( String text );
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
        protected String errText( String text ){
            return String.format("Error: %s %s", textStatus.readableStatus(), text);
        }
    }
    /**Ignore: all errors passed without log, notification or exit */
    protected class Ignore extends Behavior{
        @Override
        public void set( String text ){}
    }  
    
    /**Quietly log errors, no notification or exit. disp() shows errors */
    protected class Log extends Behavior{        
        @Override
        public void set( String text ){
            this.content.add(this.errText(text));
        }
    }
    
    /**Logs and prints errors to screen when set, no exit. */
    protected class Notify extends Log{        
        @Override
        public void set( String text ){
            String errText = this.errText(text);
            this.content.add(errText);
            printer.print(errText);
        }
    }
    
    /**logs and prints to screen; exits on first error. */
    protected class Disrupt extends Notify{        
        @Override
        public void set( String text ){
            String errText = this.errText(text);
            this.content.add(errText);
            this.finish();
            printer.print(errText + "\nExit...");
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
