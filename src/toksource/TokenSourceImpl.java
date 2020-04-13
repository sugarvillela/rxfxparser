package toksource;

import java.util.ArrayList;
import commons.*;
import toktools.*;

/** Word/line iterator; changes modes on the fly; doesn't preserve half-lines
 *
 * @author Dave Swanson
 */
public class TokenSourceImpl implements TokenSource{
    /*  For text file input;
        Returns one word or line with each next() call.
        isEndLn() true if word mode, end line reached and no extension '...'
        hasFile() true if file opened
        hasNext() false when iterator runs out of words or lines
        static function readAll dumps file to array list
    */
    //public Scanner input;
    protected Erlog log;  
    protected Tokens tk;
    private boolean endLn;
    protected TextSource getter;// current getter
    protected TextSource gLine;
    protected TextSource gWord;
    public String name;
    public static final int LINE = 0x10;
    public static final int WORD = 0x20;
    
    public TokenSourceImpl( TextSource lineGetter ){
        init( lineGetter, WORD, TK.getInstance(" ", "\"'", 0) );// default word output with ext pattern
    }
    public TokenSourceImpl( TextSource lineGetter, int defBehavior ){
        init( lineGetter, defBehavior, TK.getInstance(" ", "\"'", defBehavior) );// choose behavior, no pattern
    }
    public TokenSourceImpl( TextSource lineGetter, Tokens setTokenizer ){
        init( lineGetter, WORD, setTokenizer );// choose behavior, no pattern
    }
    public TokenSourceImpl( TextSource lineGetter, int defBehavior, Tokens setTokenizer ){
        init( lineGetter, defBehavior, setTokenizer );// choose behavior, no pattern
    }
    public final void init(TextSource lineGetter, int defBehavior, Tokens setTokenizer ){
        this.log = Erlog.getInstance();
        this.gLine = lineGetter;// need this whether line or word mode
        
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
        this.tk = setTokenizer;
        this.name="Itr_file";
    }
    @Override
    public final void onCreate(){
    
    }
    @Override
    public void onQuit(){
        this.gLine.onQuit();
        this.gWord.onQuit();
    }
    /* Output mode: Word trims; line keeps tabs, spaces, newlines */
    @Override
    public void setLineGetter(){
        System.out.println("\n setLineGetter");
        this.getter = this.gLine;
        this.endLn = false;
    }
    @Override
    public void setWordGetter(){
        System.out.println("\n setWordGetter");
        this.getter = this.gWord;
        this.getter.rewind();
        this.endLn = true;
    }
    /* Iterator */
    @Override
    public int getRow(){//row number
        return this.gLine.getRow();
    }
    @Override
    public int getCol(){//column number
        return this.gWord.getRow();
    }
    @Override
    public boolean isEndLine(){ 
        return this.endLn;
    }
    @Override
    public String next(){
        //String s =this.getter.next();
        //System.out.println( "StringSource_FromFile: "+s ); 
        return this.getter.next();
    }

    public class Getter_word extends TextSource_base{
        protected ArrayList<String> tokens;
        private int col;
        public Getter_word(){
            endLn = true;
        }
        @Override
        public String next(){
            if( endLn ){//first or refresh
//                do{
                    this.text = "";
                    while( this.text.isEmpty()){
                        this.text = gLine.next().trim();
                        if(done){
                            //System.out.println("Getter_word: line getter says done" );
                            col=0;
                            this.tokens = null;
                            return this.text;
                        }
                        
                        //System.out.println("text from line getter = " + this.text );
                    }
                    //System.out.println("line = "+this.line);
                    tk.parse( this.text );
//                }while(tk.isHolding());
                
                this.tokens = tk.getTokens();
                col=-1;
                endLn = false;
                //Commons.disp(this.tokens, "====GETTER_WORD=====");
            }
            col++;
            this.text = this.tokens.get(col).trim();
            endLn = (col >= this.tokens.size() - 1);
            return this.text;
        }
        @Override
        public int getRow(){// misleading name
            return this.col;
        }
        @Override
        public void rewind() {
            this.tokens = null;
            this.col = 0;
        }
    }
    @Override
    public void rewind(){
        this.gLine.rewind();
        this.gWord.rewind();
    }
    /* State booleans */
    @Override
    public boolean hasData(){ 
        return this.gLine.hasData(); 
    }
    @Override
    public boolean hasNext(){ 
        return this.gLine.hasNext(); 
    }
    @Override
    public boolean isLineGetter(){ return getter == gLine; }
    @Override
    public boolean isWordGetter(){ return getter == gWord; }
    
}
