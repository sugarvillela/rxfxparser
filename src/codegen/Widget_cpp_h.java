package codegen;
/**
 *
 * @author Dave Swanson
 */
public class Widget_cpp_h extends Widget{
    public Widget_cpp_h(){
        fileExtension = "h";
    }
    // C++ boilerplate stuff 
    public void pragma_(){
        line( "#pragma once" );
    }
    public void using_(){
        line( "#pragma once" );
    }
    public void pubPriv_( String pubPrivProtected ){
        tabdec();
        switch( pubPrivProtected.length()){
            case 7:
                line( "private:" );
                break;
            case 9:
                line( "protected:" );
                break;
            default:
                line( "public:" );
                break;
        }
        tabinc();
    }
    @Override
    public void close(){// Closing parentheses for class needs semicolon
        tabdec();
        String name = this.type.pop();
        if( name.equals("class") ){
            this.inClass--;
            line( this.closingSymbol+";", "end " + name );
        }
        else{
            line( this.closingSymbol, "end " + name );
        }
    }
    // Usable for header and cpp file
    @Override
    public void var_( String dataType, String name ){
        line( dataType+" "+name+";" );
    }
    @Override
    public void var_( String dataType, String name, String comment ){
        line( dataType+" "+name+";",comment );
    }
    public void virtual_( String info ){
        line( "virtual "+info+"()=0;" );
    }
    public void virtual_( String info, String comment ){
        line( "virtual "+info+"()=0;", comment );
    }
    public void virtual_( String info, String comment, String paramList ){
        line( "virtual "+info+"("+paramList+")=0;", comment );
    }
    public void forwardDec( String info ){
        line( info+"();" );
    }
    public void forwardDec( String info, String comment ){
        line( info+"();", comment );
    }
    public void forwardDec( String info, String comment, String paramList ){
        line( info+"("+paramList+");", comment );
    }
    @Override
    public void import_(String name){
        line( "#include \""+name+"\";" );
    }
    @Override
    public void import_(String name, String comment){
        line( "#include \""+name+"\";", comment );
    }
    @Override
    protected String classAttr( String text ){
        String[] in = text.split(" ");
        String[] words = 
                    {"class",":","public","private","protected"};
        int[] map = {0,      2,  3,       3,         3};
        String[] out = {"class",null,null,null, null};
        for( int i=0; i<in.length; i++){
            boolean found = false;
            for( int j=0; j<words.length; j++){
                if( in[i].equals(words[j]) ){
                    found = true;
                    out[map[j]] = words[j]; 
                    break;
                }
            }
            if(!found){
                if(out[1]==null){
                    out[1]=in[i];
                    this.className = in[i];
                }
                else{
                    out[4]=in[i];
                }
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
        forwardDec(this.className);
    }
    @Override
    public void construct_(String comment, String paramList){
        forwardDec(this.className,comment, paramList);
    }
    @Override
    public void function_( String name ){
        forwardDec( name );
    }
    @Override
    public void function_( String name, String comment ){
        forwardDec( name, comment );
    }
    @Override
    public void function_( String name, String comment, String paramList ){
        forwardDec( name, comment, paramList );
    }
    // Stubs 
    @Override
    public void array_( String type, String name, String len ){}
    @Override
    public void array_( String type, String name, String[] arrContent ){}
    @Override
    // Java and C++11
    public void foreach_( String type, String name ){}
    @Override
    public void foreach_( String type, String name, String comment ){} 
    
    @Override
    public IGen getSpecializedCodeGenerator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
