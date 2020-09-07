package compile.symboltable;

import commons.Commons;
import compile.basics.CompileInitializer;
import erlog.Erlog;
import toksource.Base_TextSource;
import toksource.interfaces.ITextSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static compile.basics.Keywords.STATUS_FORMAT;

public class SymbolTable_Fun {
    private static SymbolTable_Fun instance;

    public SymbolTable_Fun() {
        symbolTable = new HashMap<>();
    }

    public static SymbolTable_Fun getInstance(){
        return (instance == null)? (instance = new SymbolTable_Fun()) : instance;
    }

    private Map<String, Base_FunNode> symbolTable;
    //        funName   FunNode with RawTextNodes

    private Base_FunNode currFun;
    private String currName;



    public void newFun(Base_TextSource textSource){
        currFun = new FunNode(textSource);
    }

    public void setFunName(String funName){
        if(symbolTable.containsKey(currName)){
            Erlog.get(this).set("Duplicate function name");
        }
        else{
            currName = funName;
        }
    }

    public void addWord(String text){
        currFun.addWord(text);
    }

    public void endFun(){
        currFun.endFun();
        symbolTable.put(currName, currFun);
    }

    public boolean isFun(String funName){
        return symbolTable.containsKey(funName);
    }
    public Base_FunNode getFun(String funName){
        return symbolTable.get(funName);
    }

    @Override
    public String toString(){
        ArrayList<String> out = new ArrayList<>();
        for (Map.Entry<String, Base_FunNode> entry : symbolTable.entrySet()) {
            System.out.println("function name: " + entry.getKey());
            out.add(entry.getValue().toString());
        }
        return String.join("\n", out);
    }
    public void testItr(){
        currFun.testItr();
    }

    /** Takes care of loading the object with data, endLine logging */
    public static abstract class Base_FunNode extends Base_TextSource{
        protected final ArrayList<Integer> endLines;
        protected ArrayList<String> words;
        protected Base_TextSource fin;
        protected int initialRow, index;

        public Base_FunNode(Base_TextSource textSource){
            fin = textSource;
            endLines = new ArrayList<>();
        }

        public void addWord(String text){
            if(words == null){// initialize at first line of relevant data
                words = new ArrayList<>();
                this.initialRow = fin.getRow();
                this.index = 0;
            }
            if(fin.isEndLine()){// keep track of line endings
                endLines.add(index);
            }
            words.add(text);
            index ++;
        }

        @Override
        public boolean isEndLine(){
            return endLines.indexOf(index) != -1;
        }

        @Override
        public String toString(){
            return String.join(" ", words);
        }
        public void endFun(){
            //Commons.disp(endLines, "endFun()");
            fin = null;
            rewind();
        }
        public abstract void testItr();
    }

    /** Takes care of Token Source iteration */
    public static class FunNode extends Base_FunNode{

        protected final String inName;
        protected int rowStart;
        protected boolean newLine;

        public FunNode(Base_TextSource textSource) {
            super(textSource);
            this.inName = fin.getFileName();
        }

        @Override
        public final void rewind() {
            index = rowStart = 0;
            row = initialRow;
            newLine = false;
            done = false;
        }

        @Override
        public String next() {
            if(newLine){
                newLine = false;
                row ++;
                rowStart = index;
            }
            if(this.isEndLine()){
                newLine = true;
            }
            String s = words.get(index);
            //System.out.printf("     %s, newLine=%b, index=%d, rowStart=%d, row=%d\n", s, isEndLine(), index, rowStart, row);
            index ++;
            this.done = !(index < words.size());
            return words.get(index -1);
        }
        @Override
        public boolean hasData(){
            return !words.isEmpty();
        }

        @Override
        public int getCol(){
            return index - rowStart - 1;
        }

        @Override
        public String getFileName() {
            return inName;
        }

        @Override
        public void testItr(){
            System.out.println("\n===testItr===");
            this.rewind();
            int i = 0;
            while(this.hasNext() && 10 > i++){
                String next = this.next();
                System.out.printf("%s: %b, %s\n", this.readableStatus(), isEndLine(), next);
            }
        }

    }

    public static abstract class Base_FunNode1  implements ITextSource{
        protected int row, col, index, initRow;
        protected String fileName;
        protected Base_TextSource fin;

        public Base_FunNode1(){
            initRow = 0;
            rewind();
        }

        public Base_FunNode1(String fileName, Base_TextSource textSource){
            this.fileName = fileName;
            this.fin = textSource;
            this.initRow = fin.getRow();
            rewind();
        }
        @Override
        public final void rewind() {
            index = 0;
            row = initRow;
            col = 0;
        }

        @Override
        public int getRow() {
            return row;
        }

        @Override
        public String readableStatus() {
            return String.format(STATUS_FORMAT, fileName, this.getRow(), this.getRow());
        }
    }

}
