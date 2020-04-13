package toksource;

import commons.Erlog;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**One of a set of passive iterators that implement TextSource
 * Note: 1-index row instead of 0-index as you'd expect from an array
 * @author Dave Swanson
 */
public class TextSource_list extends TextSource_base{
    java.util.AbstractList<String> content;
    
    public TextSource_list(java.util.AbstractList<String> setContent){
        this.content = setContent;
        onCreate();
    }
    @Override
    public final void onCreate(){
        this.row = 0;
        this.text = "";
        
        if(this.content == null || this.content.isEmpty()){
            this.good = false;
            this.done = true;
            this.er.set("TextSource_list: null or empty text source");
        }
        else{
            this.good = true;
            this.done = false;
        }
    }
    @Override
    public final void onQuit(){ // call when program/task is finished  
        this.content = null;
    } 
    @Override
    public void rewind(){
        this.onCreate();
    }
    @Override
    public String next(){
        row++;
        this.done = row >= content.size();
        return content.get(row-1);
    }
    
    /**Writes or overwrites fileName with list content
     * @param fileName valid path
     * @param param a list reference, the data to write to file
     */
    public static void convert(String fileName, ArrayList<String> param ){
        TextSource_list f = new TextSource_list(param);
        if(f.hasData()){
            try( 
                BufferedWriter file = new BufferedWriter(new FileWriter(fileName)) 
            ){
                while( f.hasNext() ){
                    file.write( f.next() );
                    file.newLine();
                }
                file.close();

            }
            catch(IOException e){
                Erlog.getInstance().set(e.getMessage());
            }
        }
    }
}
