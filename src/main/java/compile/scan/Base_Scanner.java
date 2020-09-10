package compile.scan;

import compile.basics.Base_Stack;
import compile.basics.CompileInitializer;
import toksource.Base_TextSource;

import java.util.Stack;

public class Base_Scanner  extends Base_Stack {
    protected final String inName;
    protected final Stack<Base_TextSource> fileStack;

    public Base_Scanner(Base_TextSource fin) {
        this.inName = CompileInitializer.getInstance().getInName();
        fileStack = new Stack<>();
        this.fin = fin;
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
