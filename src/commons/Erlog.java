package commons;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A quiet logger or a verbose quitter, depending on behavior chosen
 * @author Dave Swanson
 */
public class Erlog {
    protected Ignore log;               // behavior class nested within
    protected String fname;             // for log file
    
    // Behavior enums
    public static final int IGNORE = 0;
    public static final int LOG = 1;
    public static final int NOTIFY = 2;
    public static final int DISRUPT = 3;
    public static Erlog instance;
    
    // 4 constructors
    private Erlog(){
        this( LOG, "dlog.txt" );
    }
    private Erlog( int behavior ){
        this( behavior, "dlog.txt" );
    }
    private Erlog( String filename ){
        this( LOG, filename );
    }
    private Erlog( int behavior, String filename ){
        // Enum passed sets which behavior class gets instantiated
        this.fname=filename;
        switch(behavior){
            case IGNORE:
                this.log=new Ignore();
                break;
            case LOG:
                this.log=new Log();
                break;
            case NOTIFY:
                this.log=new Notify();
                break;
            case DISRUPT:
                this.log=new Disrupt();
                break;
            default:
                new Disrupt().set("Bad value ErLog.java", -1, -1);
                break;
        }
    }
    
    // logging calls for a singleton pattern; need config options, so...
    public static Erlog getInstance(){
        return (instance == null)? (instance = new Erlog()) : instance;
    }
    public static Erlog getInstance( int behavior ){
        return (instance == null)? (instance = new Erlog(behavior)) : instance;
    }
    public static Erlog getInstance( String filename ){
        return (instance == null)? (instance = new Erlog(filename)) : instance;
    }
    public static Erlog getInstance( int behavior, String filename ){
        return (instance == null)? (instance = new Erlog(behavior, filename)) : instance;
    }
    // main iterface: calls methods of instantiated behavior class
    
    /**
     * Set error, where line number not available
     * @author Dave Swanson
     * @param text describes the error, no line number
     */
    public void set( String text ){
        this.log.set( text, -1, -1 );
    }
    
    /**
     * Set error, where line number is available but not word number
     * @author Dave Swanson
     * @param text describes the error, no line number
     * @param line -1 skips; zero and above prints line number
     */
    public void set( String text, int line ){
        this.log.set( text, line, -1 );
    }
    
    /**
     * Set error, with line and word number
     * @author Dave Swanson
     * @param text describes the error, no line number
     * @param line -1 skips; zero and above prints line number
     * @param word count space-separated tokens, not cursor position
     */
    public void set( String text, int line, int word ){
        this.log.set( text, line, word );
    }
    
    /**
     * Writes accumulated errors to file
     * @author Dave Swanson
     */
    public void finish(){
        this.log.finish();
    }
    
    /**
     * Displays accumulated errors
     * @author Dave Swanson
     */
    public void disp(){
        this.log.disp();
    }
    
    // behavior classes
    /**
     * Ignore: all errors passed without log, notification or exit
     * @author Dave Swanson
     */
    public class Ignore{
        protected ArrayList<String> content;    // Accumulate messages
        public Ignore(){
            this.content = new ArrayList<>();
        }
        public void set( String text, int line, int word ){}
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
        protected String errText( String text, int line, int word ){
            if( line < 0 ){
                return "Error: " + text;
            }
            if( word < 0 ){
                return "Error: line " + line + ": " + text;
            }
            return "Error: line " + line + ", word " + word + ": " + text;
        }
    }
    
    /**
     * Log: quietly log errors, no notification or app exit. disp() shows errors
     * @author Dave Swanson
     */
    public class Log extends Ignore{        
        @Override
        public void set( String text, int line, int word ){
            this.content.add(this.errText(text, line, word));
        }
    }
    
    /**
     * Log: logs and prints errors to screen, no exit.
     * @author Dave Swanson
     */
    public class Notify extends Log{        
        @Override
        public void set( String text, int line, int word ){
            String errText = this.errText(text, line, word);
            this.content.add(errText);
            System.err.println(errText);
        }
    }
    
    /**
     * Log: logs and prints to screen; exits on first error.
     * @author Dave Swanson
     */
    public class Disrupt extends Notify{        
        @Override
        public void set( String text, int line, int word ){
            String errText = this.errText(text, line, word);
            this.content.add(errText);
            this.finish();
            System.err.println(errText);
            System.err.println("Exit...");
            System.exit(0); 
        }
    }
}
