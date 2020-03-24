package codegen;

/**
 *
 * @author Dave Swanson
 */
public class Widget_python extends Widget{
    public Widget_python(){
        this.commentSymbol="#";
        this.closingSymbol="";
        fileExtension = "py";
    }
    // Common code widgets
    @Override
    public void var_( String info, String value ){
        line( info+" = "+value );
    }
    @Override
    public void var_( String info, String value, String comment ){
        line( info+" = "+value, comment );
    }
    @Override
    public void if_( String condition ){
        this.type.push("if");
        line( "if "+condition+":" );
        tabinc();
    }
    @Override
    public void if_( String condition, String comment ){//needs close if no elif or else
        this.type.push("if");
        line( "if "+condition+":", comment );
        tabinc();
    }
    @Override
    public void elif_( String condition ){//needs close if no else
        this.type.push("elif");
        close();
        line( "elif "+condition+":" );
        tabinc();
    }
    @Override
    public void elif_( String condition, String comment ){//needs close if no else
        close();
        this.type.push("elif");
        line( "elif "+condition+":", comment );
        tabinc();
    }
    @Override
    public void else_(){
        close();
        this.type.push("else");
        line( "else:" );
        tabinc();
    }
    @Override
    public void else_( String comment ){
        close();
        this.type.push("else");
        line( "else:", comment );
        tabinc();
    }
    @Override
    public void switch_( String name ){}
    @Override
    public void switch_( String name, String comment ){}
    @Override
    public void case_( String value, String[] code ){}
    @Override
    public void case_( String value, String[] code, String comment ){}
    @Override
    public void switch_close( String[] code ){}
    @Override
    public void switch_close( String[] code, String comment ){}
    @Override
    public void for_( String start, String end ){
        this.type.push("loop");
        line( "for i in range("+start+", "+end+":" );
        tabinc();
    }
    @Override
    public void for_( String start, String end, String comment ){
        this.type.push("loop");
        line( "for i in range("+start+", "+end+":", comment );
        tabinc();
    }
    @Override
    public void while_( String condition ){
        this.type.push("while loop");
        line( "while "+condition+":" );
        tabinc();
    }
    @Override
    public void while_( String condition, String comment ){//needs close if no elif or else
        this.type.push("while loop");
        line( "while "+condition+":", comment );
        tabinc();
    }
    @Override
    public void do_(){}
    public void do_( String comment ){}
    @Override
    public void closeDoWhile( String condition ){}
    // Abstracts
    @Override
    public void import_(String name){
        line( "import "+name );
    }
    @Override
    public void import_(String name, String as){
        line( "import "+name+" " + as );
    }
    @Override
    protected String classAttr( String text ){
        String[] in = text.split(" ");
        String[] out = {"class",null};
        for (String word : in) {
            if (!word.equals("class")) {
                out[1] = word;
                this.className = word;
                break;
            }
        }
        return joinNotNull( out );
    }
    @Override
    protected String funAttr( String text ){
        return text;
    }    
    @Override
    public void class_( String info ){
        this.type.push("class");
        this.inClass++;
        line( classAttr(info) + ":" );
        tabinc();
    }
    @Override
    public void class_( String info, String comment ){
        this.type.push("class");
        this.inClass++;
        line( classAttr(info) + ":", comment );
        tabinc();
    }
    @Override
    public void construct_(){
        this.type.push("constructor");
        line( "def __init__(self):" );
        tabinc();
    }
    @Override
    public void construct_(String comment, String paramList){
        this.type.push("constructor");
        line( "def __init__(self, "+paramList+"):", comment );
        tabinc();
    }
    @Override
    public void function_( String name ){
        if(this.inClass>0){//Java only allows this case, but other languages...
            this.type.push("method");
            line( "def " + name+"(self):" );
        }
        else{
            this.type.push("function");
            line( "def " + name+"():" );
        }
        tabinc();
    }
    @Override
    public void function_( String name, String comment ){
        if(this.inClass>0){//Java only allows this case, but other languages...
            this.type.push("method");
            line( "def " + name+"(self):", comment );
        }
        else{
            this.type.push("function");
            line( "def " + name+"():", comment );
        }
        tabinc();
    }
    @Override
    public void function_( String name, String comment, String paramList ){
        if(this.inClass>0){
            this.type.push("method");
            line( "def " + name+"(self, "+paramList+"):", comment );
        }
        else{
            this.type.push("function");
            line( "def " + name+"("+paramList+"):", comment );
        }
        tabinc();
    }
    @Override
    public void foreach_( String name, String comment ){
        this.type.push("loop");
        line( "for value in"+name+":", comment );
        tabinc();
    }
    @Override
    public void foreach_( String type, String name, String comment ){}
    // array
    @Override
    public void array_( String name, String comment, String[] arrContent ){
        boolean isInt = true;
        for( String item: arrContent ){
            if( !item.matches("[ ,0-9]+") ){
                isInt=false;
                break;
            }
        }
        String list = (isInt)? 
            String.join(", ", arrContent):
            "\"" + String.join("\", \"", arrContent) + "\"";
        line( name+" = ["+list+"]", comment );
    }
    @Override
    public void array_( String type, String name, String arrContent ){}
    
    @Override
    public IGen getSpecializedCodeGenerator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
