package codegen;

/**
 *
 * @author Dave Swanson
 */
public class Widget_java extends Widget{
    public Widget_java(){
        fileExtension = "java";
    }
    
// Utility to format text and populate className field
    @Override
    protected String classAttr( String text ){
        String[] tokens = toktools.TK.toArr(' ', text);
        String[] words = 
            {"public","private","protected","static","final","abstract","class","extends","implements"};
        int[] map = {0, 0,       0,          1,       2,      3,         4,        6,          8};
        String[] out = new String[16];
        out[0] = "public";
        out[4] = "class";
        int iName = 4;
        for (String tok : tokens) {
            boolean isName = true;
            for (int j = 0; j<words.length; j++) {
                if (tok.equals(words[j])) {
                    isName = false;
                    out[map[j]] = words[j];
                    iName = map[j];
                    break;
                }
            }
            if (isName) {
                if (iName <= 4) {
                    this.className = tok;
                    iName=4;
                }
                iName++;
                out[iName] = tok;
            }
        }
        return joinNotNull( out );
    }    
    @Override
    protected String funAttr( String text ){
        String[] tokens = toktools.TK.toArr(' ', text);
        String[] words = 
            {"public","private","protected","static","final","abstract","void","int","double","float"};
        int[] map = {0, 0,       0,          1,       2,      3,        4,      4,    4,       4};
        String[] out = new String[16];
        out[0] = "public";
        for (String tok : tokens) {
            boolean isName = true;
            for (int j = 0; j<words.length; j++) {
                if (tok.equals(words[j])) {
                    isName = false;
                    out[map[j]] = words[j];
                    break;
                }
            }
            if (isName) {
                this.className = tok;
                out[4] = tok;
            }
        }
        return joinNotNull( out );
    }
// Abstracts
    @Override
    public void import_(String pkg){
        line( "import "+pkg+".*;" );
    }
    @Override
    public void import_(String pkg, String file){
        line( "import "+pkg+"."+file+";" );
    }
    @Override
    public void class_( String info ){
        this.type.push("class");
        this.inClass++;
        line( classAttr(info) + " {" );
        tabinc();
    }
    @Override
    public void class_( String info, String comment ){
        this.type.push("class");
        this.inClass++;
        line( classAttr(info) + " {", comment );
        tabinc();
    }
    @Override
    public void construct_(){
        this.type.push("constructor");
        line( "public " + this.className+" (){" );
        tabinc();
    }
    @Override
    public void construct_(String comment, String paramList){
        this.type.push("constructor");
        line( "public " + this.className+" ("+paramList+"){", comment );
        tabinc();
    }
    @Override
    public void function_( String name ){
        if(this.inClass>0){//Java only allows this case, but other languages...
            this.type.push("method");
            line( "public " + name+" (){" );
            tabinc();
        }
        else{
//            this.type.push("function");
//            line( name+" (){" );
            //err.set("Java language: methods must be declared in class context");
        }
        
    }
    @Override
    public void function_( String name, String comment ){
        name = funAttr( name );
        if(this.inClass>0){//Java only allows this case, but other languages...
            this.type.push("method");
            line( "public " + name+" (){", comment );
        }
        else{
            this.type.push("function");
            line( name+" (){", comment );
        }
        tabinc();
    }
    @Override
    public void function_( String name, String comment, String paramList ){
        if(this.inClass>0){//Java only allows this case, but other languages...
            this.type.push("method");
            line( "public " + name+" ("+paramList+"){", comment );
        }
        else{
            this.type.push("function");
            line( name+" ("+paramList+"){", comment );
        }
        tabinc();
    }
    @Override
    public void array_( String type, String name, String len ){
        line( type+"[] "+name+" = new "+type+"["+len+"];" );
    }
    @Override
    public void array_( String type, String name, String[] arrContent ){
        String list;
        if(type.equals("String")){
            list = "\"" + String.join("\", \"", arrContent) + "\"";
        }
        else if(type.equals("char")){
            list = "'" + String.join("', '", arrContent) + "'";
        }
        else{
            list = String.join(", ", arrContent);
        }
        line( type+"[] "+name+" = new "+type+"[]{ "+list+" };" );
    }
    // Java and C++11
    @Override
    public void foreach_( String type, String name ){
        this.type.push("loop");
        line( "for ("+type+" value : "+name+"){" );
        tabinc();
    }
    @Override
    public void foreach_( String type, String name, String comment ){
        this.type.push("loop");
        line( "for ("+type+" value : "+name+"){", comment );
        tabinc();
    }
    
    @Override
    public IGen getSpecializedCodeGenerator() {
        return new Gen_Java();
    }
}
