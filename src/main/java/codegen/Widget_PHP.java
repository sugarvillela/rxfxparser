
package codegen;


/**
 *
 * @author Dave Swanson
 */
public class Widget_PHP extends Widget{
    public Widget_PHP(){
        fileExtension = "php";
    }
    
    @Override
    protected String classAttr( String text ){//stub
        return null;
    }
    @Override
    protected String funAttr( String text ){
        return text;
    }
    // Abstracts
    @Override
    public void import_(String name){
        line( "include('"+name+"');" );
    }
    @Override
    public void import_(String name, String comment){
        line( "include('"+name+"');", comment );
    }
    @Override
    public void class_( String info ){
        this.type.push("class");
        this.inClass++;
        line( info + " {" );
        tabinc();
    }
    @Override
    public void class_( String info, String comment ){
        this.type.push("class");
        System.out.println("widget class_ type.size = "+type.size());
        this.inClass++;
        line( info + " {", comment );
        tabinc();
    }
    @Override
    public void construct_(){
        function_("__construct");
    }
    @Override
    public void construct_(String comment, String paramList){
        function_("__construct",comment, paramList);
    }
    // for function, just pass info; keyword function will be inserted
    // "public myFunct" will generate "public function myFunct"
    @Override
    public void function_( String info ){
        function_( info, null, "" );
    }
    @Override
    public void function_( String info, String comment ){
        function_( info, comment, "" );
    }
    @Override
    public void function_( String info, String comment, String paramList ){
        String public_ = (this.inClass>0)? "public " : "";
        this.type.push("function");
        String[] arr = info.split(" ");
        String name = arr[arr.length-1];
        arr[arr.length-1]="function";
        if(comment==null){
            line( public_ + String.join(" ", arr)+" "+name+" ("+paramList+"){" );
        }
        else{
            line( public_ + String.join(" ", arr)+" "+name+"("+paramList+"){", comment );
        }
        tabinc();
    }
    @Override
    public void for_( String start, String end ){
        this.type.push("loop");
        line( "for ($i = "+start+"; $i < "+end+"; $i++){" );
        tabinc();
    }
    @Override
    public void for_( String start, String end, String comment ){
        this.type.push("loop");
        line( "for ($i = "+start+"; $i < "+end+"; $i++){", comment );
        tabinc();
    }
    @Override
    public void foreach_( String name, String comment ){
        this.type.push("loop");
        line( "foreach ("+name+" as $key => $value){", comment );
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
        line( name+" = array("+list+");", comment );
    }
    @Override
    public void array_( String type, String name, String arrContent ){}

    @Override
    public IGen getSpecializedCodeGenerator() {
        return new Gen_PHP();
    }
}
