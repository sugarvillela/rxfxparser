/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codegen;

/**
 *
 * @author Dave Swanson
 */
public class Widget_cpp extends Widget_cpp_h{
    protected String classScope;
    
    public Widget_cpp(){
        this.className = "";
        this.classScope = "";
        fileExtension = "cpp";
    }
    public Widget_cpp( String setClassName ){
        this.className = setClassName;
        this.classScope = setClassName+"::";
        fileExtension = "cpp";
    }
    @Override
    public void construct_(){
        this.type.push("constructor");
        line( this.classScope + this.className+" (){" );
        tabinc();
    }
    @Override
    public void construct_(String comment, String paramList){
        this.type.push("constructor");
        line( this.classScope + this.className+" ("+paramList+"){", comment );
        tabinc();
    }
    @Override
    public void function_( String name ){
        this.type.push("function");
        line( this.classScope + name+" (){" );
        tabinc();
    }
    @Override
    public void function_( String name, String comment ){
        this.type.push("function");
        line( this.classScope + name+" (){", comment );
        tabinc();
    }
    @Override
    public void function_( String name, String comment, String paramList ){
        this.type.push("function");
        line( this.classScope + name+" ("+paramList+"){", comment );
        tabinc();
    }
    @Override
    public void var_( String info, String value ){
        line( info+" = "+value+";" );
    }
    @Override
    public void var_( String info, String value, String comment ){
        line( info+" = "+value+";", comment );
    }
    @Override
    public void array_( String type, String name, String len ){
        line( type+" "+name+"["+len+"];" );
    }
    @Override
    public void array_( String type, String name, String[] arrContent ){
        String list;
        switch (type) {
            case "string":
                list = "\"" + String.join("\", \"", arrContent) + "\"";
                break;
            case "char":
                list = "'" + String.join("', '", arrContent) + "'";
                break;
            default:
                list = String.join(", ", arrContent);
                break;
        }
        line( type+" "+name+"[] = "+"{ "+list+" };" );
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
}
