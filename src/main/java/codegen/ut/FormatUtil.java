package codegen.ut;

import codegen.interfaces.IWidget;
import commons.Commons;

import java.util.ArrayList;

public class FormatUtil {
    static final int MARGIN = 70;           // Constant formatting value

    private final ArrayList<String> content;
    protected int tab;                      // formatting value for indent

    public FormatUtil() {
        this.content = new ArrayList<>();
        tab = 0;
    }

    public final String tab( String text ){
        switch (this.tab){
            case 0:
                return text;
            case 1:
                return "    "+text;
            case 2:
                return "        "+text;
            case 3:
                return "            "+text;
            case 4:
                return "                "+text;
            default:
                return "                    "+text;
        }
    }
    public final void clear(){this.tab=0;}
    public final void inc(){this.tab++;}
    public final void dec(){
        this.tab=(this.tab>0)? this.tab-1 : 0;
    }

    private int nextSpace( String text, int i ){
        while( i<text.length()-1 && text.charAt(i) != ' '){
            i++;
        }
        return i+1;
    }


    // To add content as-is, no formatting
    public final void add( String text ){
        this.content.add( text );
    }

    // The primary way to add content; takes care of formatting and line splitting
    public final void addLine(String text ){
        int i = 0;
        while((text = addLineSegment(text)) != null && i++ < 10){}
    }

    public final String addLineSegment(String text ){
        System.out.println("===" + text + "===");
        text=tab( text );
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

    public final void addLines(ArrayList<IWidget> widgets){
        for(IWidget widget : widgets){
            widget.finish(this);
        }
    }

    public final void addTabLines(ArrayList<IWidget> widgets){
        inc();
        for(IWidget widget : widgets){
            widget.finish(this);
        }
        dec();
    }

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
