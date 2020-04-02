package itr_struct;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;
import commons.*;
import toktools.TK;
/** Word/line iterator; changes modes on the fly; doesn't preserve half-lines
 *
 * @author Dave Swanson
 */
public class StringSource_file implements StringSource{
    /*  For text file input;
        Returns one word or line with each next() call.
        isEndLn() true if word mode, end line reached and no extension '...'
        hasFile() true if file opened
        hasNext() false when iterator runs out of words or lines
        static function readAll dumps file to array list
    */
    public Scanner input;
    protected Erlog log;
    //protected String line, word;
    protected ArrayList<String> tok;
    protected TK tk;
    protected int row, col;
    protected boolean good;
    protected boolean done;
    protected Getter getter;// current getter
    protected Getter gLine;
    protected Getter gWord;
    protected String ignoreEndLn;// force isEndLine() false by adding at end of line
    public String name;
    public static final int LINE = 1;
    public static final int WORD = 2;
    
    public StringSource_file( String fileName ){
        init( fileName, WORD );// default word output with ext pattern
    }
    public StringSource_file( String fileName, int defBehavior ){
        init( fileName, defBehavior );// choose behavior, no pattern
    }
    public final void init(String fileName, int defBehavior){
        this.log = Erlog.getInstance();
        openFile( fileName );
        this.gLine = new Getter_line();// need this whether line or word mode

        switch(defBehavior){
            case LINE:
                this.getter = this.gLine;
                break;
            case WORD:
                this.gWord = new Getter_word();
                this.getter = this.gWord;
                break;
            default:
                log.set("ItrFile: set default behavior");
                break;
        }
        // init tokenizer
        this.tk = TK.getInstance();     
        this.tk.setDelims(" ");
        this.tk.setMap("\"");
        this.tk.setFlags(0); // 
        this.name="Itr_file";
    }
    /* Initialize or re-initialize */
    public final void openFile( String fileName ){
        this.row = 0;
        this.col = -1;
        this.tok = null;
        try{
            this.input = new Scanner( new File(fileName) );
            this.good = true;
            this.done = false;
        }
        catch ( FileNotFoundException e ){
            this.good = false;
            this.done = true;
            //this.log.set(eeee.getMessage());
        }
    }
    public void finish(){
        this.input.close();
    }
    /* Output mode: Word trims; line keeps tabs, spaces, newlines */
    @Override
    public void setLineGetter(){
        System.out.println("\n setLineGetter");
        this.col = 0;
        this.tok = null;
        this.getter = this.gLine;
    }
    @Override
    public void setWordGetter(){
        System.out.println("\n setWordGetter");
        this.col = 0;
        this.tok = null;
        this.getter = this.gWord;
    }
    /* Iterator */
    @Override
    public int getRow(){//row number
        return this.row;
    }
    @Override
    public int getCol(){//column number
        return this.col;
    }
    @Override
    public boolean isEndLine(){ 
        return getter.isEndLine(); 
    }
    @Override
    public String next(){
        //String s =this.getter.next();
        //System.out.println( "StringSource_FromFile: "+s ); 
        return this.getter.next();
    }
    public abstract class Getter{
        protected String text;
        public abstract String next();
        public abstract boolean isEndLine();
    }
    public class Getter_line extends Getter{
        public Getter_line(){}
        public Getter_line(String discard){}
        @Override
        public String next(){
            try{
                this.text = input.nextLine();
                row++;
                return this.text;
            }
            catch ( NoSuchElementException | IllegalStateException e ){
                //System.out.println("Getter_line: Bazooka: "+eeee);
                done = true;
                return "";
            }
        }
        @Override
        public boolean isEndLine(){ return false; }
    }
    public class Getter_word extends Getter{
        private boolean endLn;
        
        public Getter_word(){
            endLn = true;
        }
        @Override
        public String next(){
            if( endLn ){//first or refresh
//                do{
                    this.text = "";
                    while( this.text.isEmpty()){
                        if(done){
                            //System.out.println("Getter_word: line getter says done" );
                            col=0;
                            tok = null;
                            return "";
                        }
                        this.text = gLine.next().trim();
                        //System.out.println("text from line getter = " + this.text );
                    }
                    //System.out.println("line = "+this.line);
                    tk.parse( this.text );
//                }while(tk.isHolding());
                
                tok = tk.get();
                col=-1;
                endLn = false;
                //Commons.disp(tok, "====GETTER_WORD=====");
            }
            col++;
            this.text = tok.get(col).trim();
            endLn = (col >= tok.size() - 1);
            return this.text;
        }
        @Override
        public boolean isEndLine(){
            return endLn;
        }
    }
    @Override
    public void rewind(){
        this.good = false;
        this.done = false;
    }
    /* State booleans */
    @Override
    public boolean hasFile(){ return this.good; }
    @Override
    public boolean hasNext(){ return !this.done; }
    @Override
    public boolean isLineGetter(){ return getter == gLine; }
    @Override
    public boolean isWordGetter(){ return getter == gWord; }
    public ArrayList<String> readAll(){//return array of file contents
        ArrayList<String> arr;
        arr = new ArrayList<>();
        String text;
        try{
            while ( ( text = this.input.nextLine() ) != null ){
                text = text.trim();
                if( !text.isEmpty()){
                    arr.add(text);
                }
            }
        }
        catch ( NoSuchElementException | IllegalStateException eeee ){
            this.good = false;
            this.log.set(eeee.getMessage());
        }
        return arr;
    }
}
