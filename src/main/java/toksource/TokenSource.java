package toksource;

import erlog.Erlog;

import java.util.ArrayList;

import toktools.*;

/** Word/line iterator; changes modes on the fly; doesn't preserve half-lines
 *
 * @author Dave Swanson
 */
public class TokenSource extends Base_TextSource {
    /*  For text file input;
        Returns one word or line with each next() call.
        isEndLn() true if word mode, end line reached and no extension '...'
        hasFile() true if file opened
        hasNext() false when iterator runs out of words or lines
        static function readAll dumps file to array list
    */
    protected Erlog er;  
    protected Tokens tk;
    private boolean endLn;
    protected Base_TextSource getter;// current getter
    protected Base_TextSource gLine;
    protected Base_TextSource gWord;
    
    /**Token source with default tokenize on space
     * @param lineGetter list or file */
    public TokenSource( Base_TextSource lineGetter ){
        this.er = Erlog.get(this);
        this.gLine = lineGetter;// need this whether line or word mode
        this.tk = TK.getInstance(" ", "\"", TK.IGNORESKIP);
        this.onCreate();
    }
    
    /**Token source with custom tokenize
     * @param lineGetter list or file
     * @param setTokenizer initialize tokenizer to set tokenize behavior
     */
    public TokenSource( Base_TextSource lineGetter, Tokens setTokenizer ){
        this.er = Erlog.get(this);
        this.gLine = lineGetter;// need this whether line or word mode
        this.tk = setTokenizer;
        this.onCreate();
    }

    @Override
    public final void onCreate(){
        this.gWord = this.getter = new Getter_word();      
        this.rewind();
    }
    @Override
    public void onPush(){}

    @Override
    public void onPop(){}
    @Override
    public void onQuit(){
        this.gLine.onQuit();
        this.gWord.onQuit();
    }
    
    /* Output mode: Word trims; line keeps tabs, spaces, newlines */
    @Override
    public void setLineGetter(){
        //System.out.println("\n setLineGetter");
        this.getter = this.gLine;
        this.endLn = false;
    }
    @Override
    public void setWordGetter(){
        //System.out.println("\n setWordGetter");
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
        return this.gWord.getCol();
    }
    @Override
    public boolean isEndLine(){ 
        return this.endLn;
    }

    @Override
    public String getFileName() {
        return gLine.getFileName();
    }
    @Override
    public String next(){
        return this.getter.next();
    }

    public class Getter_word extends Base_TextSource{
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
        public int getCol(){
            return this.col;
        }
        @Override
        public void rewind() {
            this.tokens = null;
            this.col = 0;
        }
        @Override
        public String getFileName() {
            return null;
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
