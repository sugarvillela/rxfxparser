package codegen.ut;

import codegen.interfaces.IWidget;
import commons.Commons;

import java.util.ArrayList;

public class FormatUtil {
    static final int MARGIN = 70;           // Constant formatting value
    static final int TAB = 4;

    private final ArrayList<String> content;
    protected int indent;                      // formatting value for indent

    public FormatUtil() {
        this.content = new ArrayList<>();
        indent = 0;
    }

    public final String tab(String text){
        return new String(new char[indent * TAB]).replace('\0', ' ') + text;
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
        if(text.length()<=MARGIN){
            this.content.add( text );
            return null;
        }

        // text too long for margin: break line
        int len = nextSpace( text, MARGIN );
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

    /** @return the final result of all operations: element = line */
    public ArrayList<String> finish(){
        return content;
    }

    public static void demo(){
        String text = "This is a short part and this is a much much longer part with lots of extra words and it should definitely be split into two lines!";
        FormatUtil ut = new FormatUtil();
        ut.addLine(text);
        Commons.disp(ut.finish(), text);
    }
}
