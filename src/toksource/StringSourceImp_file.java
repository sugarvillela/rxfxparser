package toksource;

import commons.Erlog;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**One of a set of passive iterators that implement StringSource
 * @author Dave Swanson
 */
public class StringSourceImp_file extends StringSourceBase{
    protected String fileName, prev;
    protected Scanner input;
    
    public StringSourceImp_file( String fileName ){
        this.er = Erlog.getInstance();
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
            this.er.set(e.getMessage());
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
    /**Fills empty list with file contents
     * @param fileName valid path (no action if bad file name)
     * @param param a list reference, instantiated and empty
     */
    public static void convert(String fileName, ArrayList<String> param ){
        StringSourceImp_file f = new StringSourceImp_file(fileName);
        if(f.hasData()){
            while( f.hasNext() ){
                param.add( f.next() );
            }
        }
    }

}
