package compile.symboltable;

import commons.Commons;
import compile.basics.CompileInitializer;
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
public class Factory_TextNode implements ChangeListener {
    private static Factory_TextNode instance;

    private Factory_TextNode() {
        symbolTable = new HashMap<>();
        busy = false;
    }

    public static Factory_TextNode getInstance(){
        return (instance == null)? (instance = new Factory_TextNode()) : instance;
    }
    public static void killInstance(){
        CompileInitializer.getInstance().removeChangeListener(instance);
        instance = null;
    }

    private Map<String, Base_TextNode> symbolTable;
    //        nodeName   textNode with loader and iterator

    private Base_TextNode currNode;
    ITextStatus textStatus;
    private boolean busy;

    //=====Methods to populate nodes=====================================

    public void startTextNode(Keywords.HANDLER type){
        if(busy){
            Erlog.get().set("Don't nest named definitions", type.toString());
        }
        else{
            currNode = new TextNode(this.textStatus);
            currNode.setType(type);
            busy = true;
        }
    }

    public void setTextName(String textName){
        if(symbolTable.containsKey(textName)){
            Erlog.get(this).set("Duplicate identifier");
        }
        else{
            currNode.setName(textName);
        }
    }

    public void addWord(String text){
        currNode.addWord(text);
    }

    public void back(){
        if(currNode != null){
            currNode.back();
        }
    }
    public void finishTextNode(){
        currNode.finishTextNode();
        symbolTable.put(currNode.getName(), currNode);
        currNode = null;
        busy = false;
        //System.out.println("finishTextNode: name: " + currNode.getName());
        //System.out.println(currNode);
        //testItr();
        //System.out.println("end finishTextNode: name: " + currNode.getName());
    }

    //=====Methods used by Scanner to iterate node data=====================================

    public boolean isTextNode(String textNodeName){
        return symbolTable.containsKey(textNodeName);
    }

    public Base_TextNode getTextNode(String textNodeName){
        return symbolTable.get(textNodeName);
    }

    public Base_TextNode getTextNode(String textNodeName, Keywords.HANDLER type){
        Base_TextNode node = symbolTable.get(textNodeName);
        if(node != null && node.getType().equals(type)){
            return node;
        }
        return null;
    }

    @Override
    public String toString(){
        ArrayList<String> out = new ArrayList<>();
        for (Map.Entry<String, Base_TextNode> entry : symbolTable.entrySet()) {
            System.out.println("toString: text node: " + entry.getKey());
            out.add(entry.getValue().toString());
        }
        return String.join("\n", out);
    }
    public void testItr(){
        for (Map.Entry<String, Base_TextNode> entry : symbolTable.entrySet()) {
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
        protected Keywords.HANDLER type;
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
            Commons.disp(endLines, String.format("Factory_textNode.addWord() %s_____%s, %d, size", text, words.get(index-1), index));
        }
        public void back(){
            if(!words.isEmpty()){

                Commons.disp(endLines, String.format("Factory_textNode.back() %s, %d, size", words.get(index-1), index));

                endLines.remove(Integer.valueOf(index));
                index--;
                words.remove(index);
                Commons.disp(endLines, "after");
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
        public void setType(Keywords.HANDLER type) {
            this.type = type;
        }

        @Override
        public Keywords.HANDLER getType() {
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
                System.out.printf("%s: %b, %s\n", this.readableStatus(), isEndLine(), next);
            }
        }
    }
}
