package compile.scan;

import compile.basics.Base_Stack;
import compile.basics.CompileInitializer;
import toksource.Base_TextSource;
import toksource.TextSource_file;
import toksource.TokenSource;

import java.util.Stack;

import static compile.basics.Keywords.SOURCE_FILE_EXTENSION;

public class Base_Scanner  extends Base_Stack {
    protected final String inName;
    protected final Stack<Base_TextSource> fileStack;

    public Base_Scanner(Base_TextSource fin) {
        this.inName = CompileInitializer.getInstance().getInName();
        fileStack = new Stack<>();
        this.fin = fin;
    }

    public void include(String fileName){
        if(!fileName.endsWith(SOURCE_FILE_EXTENSION)){
            fileName += SOURCE_FILE_EXTENSION;
        }
        changeTextSource(new TokenSource(new TextSource_file(fileName)));
    }

    public void changeTextSource(Base_TextSource source){
        if(source == null){
            er.set("changeTextSource: NULL source");

        }
        else if(!source.hasData()){
            er.set("changeTextSource: bad source", source.toString());
        }
        else{
            System.out.println("changeTextSource: \n" + source);
            fileStack.push(fin);
            fin = source;
            fin.rewind();
            this.onTextSourceChange(fin);
        }
    }
    public boolean restoreTextSource(){
        if(fileStack.isEmpty()){
            return false;
        }
        fin = fileStack.pop();
        this.onTextSourceChange(fin);
        return true;
    }
}
