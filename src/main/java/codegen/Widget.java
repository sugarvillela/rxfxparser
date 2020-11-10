package codegen;
import codegen.interfaces.IWidget;

/**
 * Simple code generator: use base class as a static instance manager
 * @author Dave Swanson
 */
public abstract class Widget {

    public static final int JAVA = 1;
    public static final int PHP = 2;
    public static final int PYTHON = 3;
    public static final int CPP = 4;
    
    private static int defLang;
    
    public static void setDefaultLanguage( int setDefLang ){
        defLang = setDefLang;
    }
    public static IWidget getNewWidget(){
        //System.out.println("defLang="+defLang);
        switch (defLang){
            case JAVA:
                //return new Widget_java();
            case PHP:
            case PYTHON:
            case CPP:
            default:
                return null;
        }
    }
    public static String getFileExt(){
        //System.out.println("defLang="+defLang);
        switch (defLang){
            case JAVA:
                return "java";
            case PHP:
                return "php";
            case PYTHON:
                return "py";
            case CPP:
                return "cpp";
            default:
                return null;
        }
    }
}
// 254
