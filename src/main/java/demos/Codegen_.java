package demos;
import codegen.*;

/**
 *
 * @author Dave Swanson
 */
public class Codegen_ {
    public static void assertFileExt(){
        {
            Widget w = new Widget_java();
            w.setFileName("codegen");
            w.setFileName("codegen.java");
        }
    
    }
    public static void classGrabber(){
        String[] classes={
            "MyClass",
            "static private public nonsense MyClass",
            "static public MyClass extends YourClass implements TheirClass, thatClass",
            "public implements TheirClass, ThatClass extends YourClass abstract MyClass",
            "MyClass abstract"
                
        };
        for(String text:classes){
            System.out.println("_____________________________");
            Widget widget = new Widget_java();
            widget.class_(text, "class definition");
            System.out.println("Class name = " + widget.getClassName());
            widget.disp();
        }

    }
    public static void funGrabber(){
        String[] funs={
            "myFun",
            "static private public nonsense myFun",
            "static public myFun",
            "myFun final"
                
        };
        for(String text:funs){
            System.out.println("_____________________________");
            Widget widget = new Widget_java();
            widget.function_(text, "function definition");
            System.out.println("function name = " + widget.getClassName());
            widget.disp();
        }

    }
    public static void widget_java(){
        Widget widget = new Widget_java();
        //widget.line("This is a short line that got a lot longer once I started writing more words in it so now it's long");
        //widget.line("This is a short line","This is a much much longer comment with lots of extra words and it should definitely be split into two lines!");
        widget.class_("MyClass", "class definition");
            widget.construct_();
            widget.blank();
            widget.close();
            widget.function_("int myfunct", "this is a comment", "int param1, int param2");
                widget.var_("int newVar", "param1 + param2");
                widget.array_("String", "myArr", "10" );
                widget.array_("String", "myArr2", new String[]{"a","b","c","d","e","f"} );
                widget.foreach_( "String", "myArr2");
                widget.close();
                widget.line("return newVar;");
            widget.close();
        widget.close();
        widget.disp();
    }
    public static void widget_php(){
        Widget widget = new Widget_PHP();
        //widget.line("This is a short line that got a lot longer once I started writing more words in it so now it's long");
        //widget.line("This is a short line","This is a much much longer comment with lots of extra words and it should definitely be split into two lines!");
        widget.function_("soloFunct", "function, not method");
            widget.blank();
        widget.close();
        widget.class_("class MyClass", "class definition");
            widget.construct_();
                widget.blank();
            widget.close();
            widget.function_("myfunct", "method, not function", "$param1, $param2");
                widget.var_("$newVar", "$param1 + $param2");
                widget.array_("$myArr1", "numeric", new String[]{"22","45","7","226","14","12"} );
                widget.array_("$myArr2", "string", new String[]{"a","b","c","d","e","f"} );
                widget.foreach_("$myArr2","");
                    widget.line("echo $value.'<br>';");
                widget.close();
                widget.line("return $newVar;");
            widget.close();
        widget.close();
        widget.disp();
    }
    public static void widget_python(){
        Widget widget = new Widget_python();
        //widget.line("This is a short line that got a lot longer once I started writing more words in it so now it's long");
        //widget.line("This is a short line","This is a much much longer comment with lots of extra words and it should definitely be split into two lines!");
        widget.function_("soloFunct", "function, not method");
            widget.blank();
        widget.close();
        widget.class_("class MyClass", "class definition");
            widget.construct_();
                widget.blank();
            widget.close();
            widget.function_("myfunct", "method, not function", "param1, param2");
                widget.var_("newVar", "param1 + param2");
                widget.array_("myArr1", "numeric", new String[]{"22","45","7","226","14","12"} );
                widget.array_("myArr2", "string", new String[]{"a","b","c","d","e","f"} );
                widget.foreach_("myArr2","");
                    widget.line("print(value)");
                widget.close();
                widget.line("return newVar");
            widget.close();
        widget.close();
        widget.disp();
    }
    public static void widget_cpp(){
        Widget_cpp_h widget = new Widget_cpp_h();
        //widget.line("This is a short line that got a lot longer once I started writing more words in it so now it's long");
        //widget.line("This is a short line","This is a much much longer comment with lots of extra words and it should definitely be split into two lines!");
        widget.pragma_();
        widget.blank();
        widget.class_("MyClass", "class definition");
            widget.pubPriv_("");
            widget.construct_();
            widget.function_("int myfunct", "this is a comment", "int param1, int param2");
            widget.pubPriv_("private");
            widget.var_("int", "classField");
        widget.close();
        widget.disp();
        
        widget = new Widget_cpp("myClass");
        widget.blank();
        widget.import_("\"MyClass.h\"");
        widget.blank();
        widget.forwardDec("void doSomething", "forward declaration");
        widget.blank();
        widget.construct_();
            widget.line("classField = 2;");
        widget.close();
        widget.function_("int myfunct", "this is a comment", "int param1, int param2");
            widget.var_("int newVar", "param1 + param2");
            widget.array_("string", "myArr", "10" );
            widget.array_("string", "myArr2", new String[]{"a","b","c","d","e","f"} );
            widget.for_( "0", "myArr2.length");
                widget.line("cout << myArr2[i] << endl;");
            widget.close();
            widget.line("return newVar;");
        widget.close();
        widget.disp();
        
        widget = new Widget_cpp();
        widget.blank();
        widget.function_("void doSomething", "the forward declaration above");
            widget.line("cout << \"I did something\"; << endl;");
        widget.close();
        widget.disp();
    }
}
