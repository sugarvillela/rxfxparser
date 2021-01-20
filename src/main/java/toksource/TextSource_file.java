package toksource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**One of a set of passive iterators that implement TextSource
 * @author Dave Swanson
 */
public class TextSource_file extends Base_TextSource{
    protected String fileName, prev;
    protected Scanner input;
    
    public TextSource_file( String fileName ){
        this.fileName = fileName;
        onCreate();
    }
    @Override
    public final void onCreate(){
        this.row = -1;
        this.text = "";
        this.prev = "";
        try{
            this.input = new Scanner( new File(fileName) );
            this.good = true;
            this.done = false;
            this.next();             // internal state one row ahead of output
        }
        catch ( FileNotFoundException e ){
            this.good = false;
            this.done = true;
        }
    }
    @Override
    public final void onQuit(){ // call when program/task is finished  
        this.input.close();
    } 
    @Override
    public void rewind(){
        this.onCreate();
    }
    @Override
    public String next(){
        this.prev = this.text;
        row++;
        try{
            this.text = input.nextLine();
        }
        catch ( NoSuchElementException | IllegalStateException e ){
            done = true;
        }
        return this.prev;
    }

    @Override
    public String getFileName() {
         return fileName;
    }

    @Override
    public String toString(){
        return this.fileName;
    }
    
    /**Fills empty list with file contents
     * @param fileName valid path (no action if bad file name)
     * @param param a list reference, instantiated and empty
     */
    public static void convert(String fileName, ArrayList<String> param ){
        TextSource_file f = new TextSource_file(fileName);
        if(f.hasData()){
            while( f.hasNext() ){
                param.add( f.next() );
            }
        }
    }
}
