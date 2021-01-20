package translators.ut;

import codegen.interfaces.IWidget;

import java.util.ArrayList;

public class FormatUtil {
    private final ArrayList<String> content;
    protected int indent, tab, margin;
    private final Accumulator accumulator;

    public FormatUtil() {
        this.content = new ArrayList<>();
        margin = 70;
        tab = 4;
        indent = 0;
        accumulator = new Accumulator();
    }
    public void setTab(int tab){
        this.tab = tab;
    }
    public void setMargin(int margin){
        this.margin = margin;
    }

    public final String tab(String text){
        return new String(new char[indent * tab]).replace('\0', ' ') + text;
    }
    public final void clear(){this.indent = 0;}
    public final void inc(){this.indent++;}
    public final void dec(){
        this.indent = (this.indent > 0)? this.indent -1 : 0;
    }

    private int nextSpace( String text, int i ){
        while( i<text.length()-1 && text.charAt(i) != ' '){
            i++;
        }
        return i+1;
    }

    /** To add content as-is, no formatting or splitting ling line
     * @param text exact text, no indent */
    public final void add( String text ){
        this.content.add( text );
    }

    /**The primary way to add content; takes care of formatting and line splitting
     * @param text any length */
    public final void addLine(String text ){
        int i = 0;
        while((text = addLineSegment(text)) != null && i++ < 10){}
    }

    /**Stores a single line up to MARGIN length
     * @param text any length
     * @return the remaining text not added, null if finished */
    public final String addLineSegment(String text ){
        //System.out.println("===" + text + "===");
        text=tab(text);
        if(text.length()<= margin){
            this.content.add( text );
            return null;
        }

        // text too long for margin: break line
        int len = nextSpace( text, margin);
        this.content.add( text.substring( 0, len ) );
        text = text.substring( len );

        return (text.isEmpty())? null : text;
    }

    /**Add multiple lines directly from IWidget object
     * @param widgets nested objects containing widgets or text */
    public final void addLines(ArrayList<IWidget> widgets){
        for(IWidget widget : widgets){
            widget.finish(this);
        }
    }

    /**Same as addLines() but indent the content
     * @param widgets nested objects containing widgets or text */
    public final void addTabLines(ArrayList<IWidget> widgets){
        inc();
        for(IWidget widget : widgets){
            widget.finish(this);
        }
        dec();
    }

    public final void accumulate(String word){
        accumulator.add(word);
    }

    public final void finishAccumulate(){
        accumulator.finish();
    }
    /** @return the final result of all operations: element = line */
    public ArrayList<String> finish(){
        return content;
    }



    private class Accumulator{
        private final ArrayList<String> acc;
        private int currIndent, currLen;

        public Accumulator() {
            this.acc = new ArrayList<>();
            currLen = currIndent = indent * tab;
        }
        public void add(String text){
            acc.add(text);
            currLen += text.length() + 1;
            if(currLen > margin){
                this.dump();
            }
        }
        public void finish(){
            if(acc.size() > 0){
                this.dump();
            }
        }
        private void dump(){
            content.add(tab(String.join(" ", acc)));
            currLen = currIndent;
            acc.clear();
        }
    }
}
