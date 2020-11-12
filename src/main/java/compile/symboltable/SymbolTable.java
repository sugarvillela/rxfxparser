package compile.symboltable;

import runstate.RunState;
import compile.basics.Keywords;
import erlog.Erlog;
import toksource.Base_TextSource;
import toksource.interfaces.ChangeListener;
import toksource.interfaces.ChangeNotifier;
import toksource.interfaces.ITextStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Saves named text to an iterable node to use as text source during scanning */
public class SymbolTable implements ChangeListener {
    private static final SymbolTest SYMBOL_TEST = SymbolTest.getInstance();
    private static SymbolTable instance;

    private SymbolTable() {
        symbolTableMap = new HashMap<>();
        busy = false;
    }

    public static SymbolTable getInstance(){
        return (instance == null)? (instance = new SymbolTable()) : instance;
    }
    public static void killInstance(){
        RunState.getInstance().removeChangeListener(instance);
        instance = null;
    }

    private Map<String, Base_TextNode> symbolTableMap;
    //        nodeName   textNode with loader and iterator

    private Base_TextNode currNode;
    ITextStatus textStatus;
    private boolean busy;

    //=====Methods to populate nodes=====================================

    public void startTextNode(Keywords.DATATYPE type){
        if(busy){
            //System.out.println("Don't nest named definitions: " + type.toString());
            Erlog.get(this).set("Don't nest named definitions", type.toString());
        }
        else{
            //System.out.println("startTextNode: " + type.toString());
            currNode = new TextNode(this.textStatus);
            currNode.setType(type);
            busy = true;
        }
    }

    public void setTextName(String textName){
        if(symbolTableMap.containsKey(textName)){
            Erlog.get(this).set("Identifier already exists", textName);
        }
        else{
            //System.out.println("setTextName: " + textName);
            currNode.setName(textName);
        }
    }

    public void addWord(String text){
        //System.out.println("addWord: " + text);
        currNode.addWord(text);
    }

    public void back(){
        if(currNode != null){
            currNode.back();
        }
    }
    public void finishTextNode(){
        currNode.finishTextNode();
        symbolTableMap.put(currNode.getName(), currNode);
        busy = false;
//        System.out.println("finishTextNode: name: " + currNode.getName());
//        System.out.println(currNode);
//        testItr();
//        System.out.println("end finishTextNode: name: " + currNode.getName());
        currNode = null;
    }

    //=====Methods used by Scanner to iterate node data=====================================

    public Base_TextNode readVar(String text){
        if(SYMBOL_TEST.isUserDef(text)){
            return symbolTableMap.get(SYMBOL_TEST.stripUserDef(text));
        }
        return null;
    }
    public boolean isTextNode(String textNodeName){
        return symbolTableMap.containsKey(textNodeName);
    }

    public Base_TextNode getTextNode(String textNodeName){
        return symbolTableMap.get(textNodeName);
    }

    @Override
    public String toString(){
        System.out.println("SymbolTable toString: ");
        ArrayList<String> out = new ArrayList<>();
        for (Map.Entry<String, Base_TextNode> entry : symbolTableMap.entrySet()) {
            System.out.println("toString: text node: " + entry.getKey());
            out.add(entry.getValue().toString());
        }
        return String.join("\n", out);
    }
    public void testItr(){
        for (Map.Entry<String, Base_TextNode> entry : symbolTableMap.entrySet()) {
            System.out.println("testItr: text node: " + entry.getKey());
            entry.getValue().testItr();
        }
    }

    @Override
    public void onTextSourceChange(ITextStatus textStatus, ChangeNotifier caller) {
        this.textStatus = textStatus;
        if(currNode != null){
            currNode.onTextSourceChange(this.textStatus, caller);
        }
    }

    /** Takes care of loading the object with data, endLine logging */
    public static abstract class Base_TextNode extends Base_TextSource implements ISymbolNode, ChangeListener{
        protected final ArrayList<Integer> endLines;
        protected ArrayList<String> words;
        protected ITextStatus textStatus;
        protected String name;
        protected Keywords.DATATYPE type;
        protected int initialRow, index;

        public Base_TextNode(ITextStatus textStatus){
            this.textStatus = textStatus;
            endLines = new ArrayList<>();
        }

        public void addWord(String text){
            if(words == null){// initialize at first line of relevant data
                words = new ArrayList<>();
                this.initialRow = textStatus.getRow();
                this.index = 0;
            }
            words.add(text);
            index ++;
            if(textStatus.isEndLine()){// keep track of line endings
                endLines.add(index);
            }
            //Commons.disp(endLines, String.format("Factory_textNode.addWord() %s_____%s, %d, size", text, words.get(index-1), index));
        }
        public void back(){
            if(!words.isEmpty()){

                //Commons.disp(endLines, String.format("Factory_textNode.back() %s, %d, size", words.get(index-1), index));

                endLines.remove(Integer.valueOf(index));
                index--;
                words.remove(index);
                //Commons.disp(endLines, "after");
            }
        }
        @Override
        public boolean isEndLine(){
            return endLines.indexOf(index) != -1;
        }

        public void finishTextNode(){
            textStatus = null;
            rewind();
        }
        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setType(Keywords.DATATYPE type) {
            this.type = type;
        }

        @Override
        public Keywords.DATATYPE getType() {
            return type;
        }

        @Override
        public void onTextSourceChange(ITextStatus textStatus, ChangeNotifier caller) {
            this.textStatus = textStatus;
        }
        public abstract void testItr();
    }

    /** Takes care of Token Source iteration */
    public static class TextNode extends Base_TextNode {

        protected final String inName;
        protected int rowStart;
        protected boolean newLine;

        public TextNode(ITextStatus textStatus) {
            super(textStatus);
            this.inName = textStatus.getFileName();
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
            index ++;
            if(this.isEndLine()){
                newLine = true;
            }
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
        public String toString(){
            ArrayList<String> out = new ArrayList<>();
            this.rewind();
            while(this.hasNext()){
                String next = this.next();
                out.add(next);
                if(isEndLine()){
                    out.add("\n");
                }
            }
            return String.join(" ", out);
        }

        @Override
        public void testItr(){
            this.rewind();
            while(this.hasNext()){
                String next = this.next();
                System.out.printf("%s: %b, %s\n", this.loggableStatus(), isEndLine(), next);
            }
        }
    }
}
