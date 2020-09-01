package codegen;
import commons.Commons;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.OutputStreamWriter;
import java.util.ArrayList; // Widget needs expandable array
import java.util.Stack;
//import parse.*;
/**
 * Simple code generator: use base class as a static instance manager
 * @author Dave Swanson
 */
public abstract class Widget {
    //Static constants and methods to manage default language
    public static final int JAVA = 1;
    public static final int PHP = 2;
    public static final int PYTHON = 3;
    public static final int CPP = 4;
    public static final int CPPH = 5;
    
    private static int defLang;
    
    public static void setDefaultLanguage( int setDefLang ){
        defLang = setDefLang;
    }
    public static Widget getNewWidget(){
        System.out.println("defLang="+defLang);
        switch (defLang){
            case JAVA:
                return new Widget_java();
            case PHP:
                return new Widget_PHP();
            case PYTHON:
                return new Widget_python();
            case CPP:
                return new Widget_cpp();
            case CPPH:
                return new Widget_cpp_h();
            default:
                return null;
        }
    }
    
    //==========================================================================

    //Below: Base class utilities and overridable methods
    static final int MARGIN = 70;           // Constant formatting value
    protected String commentSymbol;         // "#" for python, "//" elsewhere 
    protected String closingSymbol;         // blank for python, "}" elsewhere 
    protected String fileExtension;         // set by each child class
    
    protected ArrayList<String> content;    // Accumulate generated code
    protected Stack<String> type;           // For closing symbol identification
    protected int inClass;                  // inside class if > 0
    protected String className;             // current class being generated
    protected String filename;              // Empty aborts file write
    protected String fileErr;               // in case of error writing file
    //protected Erlog err;
    protected int tab;                      // formatting value for indent
    
    public Widget(){
        this.commentSymbol="//";
        this.closingSymbol="}";
        this.content = new ArrayList<>();   
        this.type = new Stack<>();          // keep track of block types
        this.inClass = 0;                   // for function/method or err 
        this.className = "";
        this.filename = null;
        this.fileErr = null;
        this.tab=0;
        //this.err = new Erlog(Erlog.DISRUPT);
    }
    protected final String joinNotNull( String[] arr ){
        StringBuilder out = new StringBuilder(); 
        for( String notNull : arr ){
            if( notNull != null ){
                out.append( notNull );
                out.append( ' ' );
            }
        }
        return out.toString();
    }
    
    // utilities for adding content and finishing file
    public final void merge( String[] newContent ){//add content from another source
        for( String text : newContent ){
            line(text);
        }
    }
    public final void save( String filename ){
        //System.out.println("widget save "+filename );
        try( 
            BufferedWriter file = new BufferedWriter(new FileWriter(this.filename)) 
        ){
            for (String line: this.content) {
                //System.out.println("LINE:"+line );
                file.write(line);
                file.newLine();
            }
            file.close();
            this.fileErr = null;
        }
        catch(IOException e){
            this.fileErr = e.getMessage();
        }
    }
    public final String getFileErr(){
        return this.fileErr;
    }
    public final String finish(){
        if( this.filename == null ){
            return "No file name";
        }
        if( this.content.isEmpty() ){
            return "No Content";
        }
        this.save(this.filename);
        this.filename = null;
        this.content.clear();
        return this.fileErr;
    }
    public final void disp(){
        for( String line : this.content ){
            System.out.println(line);
        }
    }
    public final String getClassName(){//the last class name detected
        return this.className;
    }
    public void setFileName( String f ){
        this.filename = Commons.assertFileExt(f, fileExtension);
    }

    // utilities for formatting
    public final String tab( String text ){
        switch (this.tab){
            case 0:
                return text;
            case 4:
                return "    "+text;
            case 8:
                return "        "+text;
            case 12:
                return "            "+text;
            default:
                return "                "+text;
        }
    }
    public final void tabzero(){this.tab=0;}
    public final void tabinc(){this.tab+=4;}
    public final void tabdec(){
        this.tab=(this.tab>=4)? this.tab-4 : 0;
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
    public final void line( String text ){
        text=tab( text );
        if(text.length()<=MARGIN){
            this.content.add( text );
        }
        else{
            /* text too long for margin: break line */
            /* if comment, put a // at the beginning of each broken line */
            int len;
            do{
                len=nextSpace( text, MARGIN );
                this.content.add( text.substring( 0, len ) );
                text=tab( text.substring( len ) );
            }
            while( text.length()>MARGIN );
            if( text.length()>this.tab ){
                this.content.add( text );
            } 
        }
    }
    public final void line( String text, String comment ){
        /* simple case first: text fits in margin */
        String textCopy = tab( text+" "+this.commentSymbol+" "+comment );
        if(textCopy.length()<=MARGIN){
            this.content.add( textCopy );
            return;
        }
        lineComment( comment );
        line( text );

    }
    // Formatted comments
    public final void lineComment( String comment ){
        /* simple case first: text fits in margin */
        comment=tab( this.commentSymbol+""+comment );
        if(comment.length()<=MARGIN){
            this.content.add( comment );
        }
        else{
            /* text too long for margin: break line */
            do{
                int len=nextSpace( comment, MARGIN );
                this.content.add( comment.substring( 0, len ) );
                comment=tab( this.commentSymbol+""+comment.substring( len ) );
            }
            while( comment.length()>MARGIN );
            if( comment.length()>this.tab ){
                this.content.add( comment );
            } 
        }
    }
    public final void comment_long( String text ){
        line( "/*" );
        tabinc();
        line( text );
        tabdec();
        line( "*/" );
    }
    // Closing parentheses and white space
    public void close(){
        tabdec();
        //System.out.println("widget close type.size = "+type.size());
        String name = this.type.pop();
        if( name.equals("class") ){
            this.inClass--;
        }
        line( this.closingSymbol, "end " + name );
    }
    public final void blank(){
        this.content.add(" ");
    }
    // Common code widgets
    public void var_( String info, String value ){
        line( info+" = "+value+";" );
    }
    public void var_( String info, String value, String comment ){
        line( info+" = "+value+";", comment );
    }
    public void if_( String condition ){
        this.type.push("if");
        line( "if( "+condition+" ){" );
        tabinc();
    }
    public void if_( String condition, String comment ){//needs close if no elif or else
        this.type.push("if");
        line( "if( "+condition+" ){", comment );
        tabinc();
    }
    public void elif_( String condition ){//needs close if no else
        this.type.push("elif");
        close();
        line( "else if( "+condition+" ){" );
        tabinc();
    }
    public void elif_( String condition, String comment ){//needs close if no else
        close();
        this.type.push("elif");
        line( "else if( "+condition+" ){", comment );
        tabinc();
    }
    public void else_(){
        close();
        this.type.push("else");
        line( "else {" );
        tabinc();
    }
    public void else_( String comment ){
        close();
        this.type.push("else");
        line( "else {", comment );
        tabinc();
    }
    public void switch_( String name ){
        this.type.push("switch");
        line( "switch ("+name+"){" );
        tabinc();
    }
    public void switch_( String name, String comment ){
        this.type.push("switch");
        line( "switch ("+name+"){", comment );
        tabinc();
    }
    public void case_( String value, String[] code ){
        line( "case "+value+":" );
        tabinc();
        for( String text : code ){
            line( text );
        }
        line( "break;" );
        tabdec();
    }
    public void case_( String value, String[] code, String comment ){
        line( "case "+value+":", comment );
        tabinc();
        for( String text : code ){
            line( text );
        }
        line( "break;" );
        tabdec();
    }
    public void switch_close( String[] code ){
        line( "default:" );
        tabinc();
        for( String text : code ){
            line( text );
        }
        line( "break;" );
        tabdec();
        close();
    }
    public void switch_close( String[] code, String comment ){
        line( "default:", comment );
        tabinc();
        for( String text : code ){
            line( text );
        }
        line( "break;" );
        tabdec();
        close();
    }
    public void for_( String start, String end ){
        this.type.push("loop");
        line( "for (int i = "+start+"; i < "+end+"; i++){" );
        tabinc();
    }
    public void for_( String start, String end, String comment ){
        this.type.push("loop");
        line( "for (int i = "+start+"; i < "+end+"; i++){", comment );
        tabinc();
    }
    public void while_( String condition ){
        this.type.push("while loop");
        line( "while( "+condition+" ){" );
        tabinc();
    }
    public void while_( String condition, String comment ){//needs close if no elif or else
        this.type.push("while loop");
        line( "while( "+condition+" ){", comment );
        tabinc();
    }
    public void do_(){
        this.type.push("");
        line( "do{" );
        tabinc();
    }
    public void do_( String comment ){//needs close if no elif or else
        this.type.push("");
        line( "do{", comment );
        tabinc();
    }
    public void closeDoWhile( String condition ){
        this.type.push("");
        close();
        line( "while("+condition+");" );
    }
    // abstracts
    
    public abstract IGen getSpecializedCodeGenerator();
    protected abstract String classAttr( String text );
    protected abstract String funAttr( String text );
    public abstract void import_(String name);
    public abstract void import_(String name, String more);
    public abstract void class_( String info );
    public abstract void class_( String info, String comment );
    public abstract void construct_();
    public abstract void construct_(String comment, String paramList);
    public abstract void function_( String name );
    public abstract void function_( String name, String comment );
    public abstract void function_( String name, String comment, String paramList );
    public abstract void array_( String type, String name, String len );
    public abstract void array_( String type, String name, String[] arrContent );
    // Java and C++11
    public abstract void foreach_( String type, String name );
    public abstract void foreach_( String type, String name, String comment );
}
// 254
