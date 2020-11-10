package codegen.ut;

import commons.Util_string;
import uq.Uq;

public class NameGen {
    private static NameGen instance;

    public static void init(String projName){
        instance = new NameGen(projName);
    }
    public static NameGen getInstance(){
        return instance;
    }

    private Uq uq;
    String projName;

    private NameGen(String projName){
        this.projName = projName;
        uq = new Uq();
    }

    public String rawGen(String identifier){
        return String.format("%s_%s_%s", projName, identifier, uq);
    }
    public String raw(String identifier){
        return String.format("%s", identifier);
    }

    public String className(String identifier){
        return Util_string.toPascalCase(raw(identifier));
    }
    public String classNameGen(String identifier){
        return Util_string.toPascalCase(rawGen(identifier));
    }
    public String functionName(String identifier){
        return Util_string.toCamelCase(raw(identifier.toString()));
    }

    public String globalVar(String identifier){
        return Util_string.toScreamingSnake(raw(identifier));
    }
}
