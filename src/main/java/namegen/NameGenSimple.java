package namegen;

import commons.Util_string;
import uq.Uq;

public class NameGenSimple {
    private static NameGenSimple instance;

    public static NameGenSimple init(){
        return (instance == null)? (instance = new NameGenSimple()) : instance;
    }

    private Uq uq;
    String projName;

    private NameGenSimple(){
        uq = new Uq();
    }

    public String raw(String identifier){
        return String.format("%s", identifier);
    }

    public String className(String identifier){
        return Util_string.toPascalCase(raw(identifier));
    }
//    public String classNameGen(String identifier){
//        return Util_string.toPascalCase(rawGen(identifier));
//    }
    public String functionName(String identifier){
        return Util_string.toCamelCase(raw(identifier.toString()));
    }

    public String globalVar(String identifier){
        return Util_string.toScreamingSnake(raw(identifier));
    }
}
