package compile.symboltable;

import compile.parse.Base_ParseItem;
import erlog.Erlog;
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

    private Map<String, FunNode> symbolTable;
    //        funName   FunNode with RawTextNodes

    private FunNode currFun;
    private String currName;

    public boolean isFun(String funName){
        return symbolTable.containsKey(funName);
    }

    public void newFun(String fileName, int row){
        currFun = new FunNode(fileName, row);
    }

    public void setName(String funName){
        if(symbolTable.containsKey(currName)){
            Erlog.get(this).set("Duplicate function name");
        }
        else{
            currName = funName;
        }
    }

    public void addWord(int row, String text){
        currFun.addWord(row, text);
    }

    public void endFun(){
        if(currName == null || currFun == null){
            Erlog.get(this).set("Expected function identifier");
        }
        else{
            symbolTable.put(currName, currFun);
            currFun = null;
            currName = null;
        }
    }

    @Override
    public String toString(){
        ArrayList<String> out = new ArrayList<>();
        for (Map.Entry<String, FunNode> entry : symbolTable.entrySet()) {
            System.out.println("function name: " + entry.getKey());
            out.add(entry.getValue().toString());
        }
        return String.join("\n", out);
    }

    public static abstract class Base_FunNode  implements ITextSource{
        protected int row, col, index, initRow;
        protected String fileName;

        public Base_FunNode(){
            initRow = 0;
            rewind();
        }

        public Base_FunNode(String fileName, int initRow){
            this.fileName = fileName;
            this.initRow = initRow;
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

    public static class FunNode extends Base_FunNode{
        private ArrayList<RawTextNode> lines;
        private RawTextNode currLine;
        private int prevRow;

        public FunNode(String fileName, int row){
            lines = new ArrayList<>();
            prevRow = -1;
        }
        public void addWord(int row, String text){
            if(row != prevRow){
                currLine = new RawTextNode(row);
                lines.add(currLine);
                prevRow = row;
            }
            currLine.addWord(text);
        }
        public String toString(){
            ArrayList<String> out = new ArrayList<>();
            for(RawTextNode line : lines){
                out.add(line.toString());
            }
            return String.join("\n", out);
        }

        @Override
        public String next() {
            if(!lines.get(index).hasNext()){
                index++;
                lines.get(index).rewind();
            }
            return lines.get(index).next();
        }

        @Override
        public boolean hasNext() {
            return index < lines.size()-1 || lines.get(index).hasNext();
        }

        @Override
        public boolean hasData() {
            return !lines.isEmpty();
        }

        @Override
        public boolean isEndLine() {
            return lines.get(index).isEndLine();
        }
        @Override
        public int getCol() {
            return lines.get(index).getCol();
        }
    }

    public static class RawTextNode extends Base_FunNode{
        private final ArrayList<String> line;
        private final int row;

        public RawTextNode(int row) {
            this.row = row;
            line = new ArrayList<>();
        }

        public void addWord(String text) {
            line.add(text);
        }

        @Override
        public String toString(){
            return String.format("row %d: %s", row, String.join(" ", line));
        }

        public void disp(){
            System.out.println(this.toString());
        }

        @Override
        public String next() {
            col++;
            return line.get(col - 1);
        }

        @Override
        public boolean hasNext() {
            return col < line.size();
        }

        @Override
        public boolean hasData() {
            return !line.isEmpty();
        }

        @Override
        public boolean isEndLine() {
            return col == line.size();
        }

        @Override
        public int getCol() {
            return col;
        }
    }
}
