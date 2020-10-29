package compile.parse.importtable;

import codegen.Widget;
import commons.Util_string;
import compile.basics.CompileInitializer;
import compile.basics.Keywords;
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

    public String raw(Keywords.DATATYPE datatype){
        return String.format("%s_%s_%s", projName, datatype.toString(), uq);
    }
    public String className(Keywords.DATATYPE datatype){
        return Util_string.toPascalCase(raw(datatype));
    }
    public String functionName(Keywords.DATATYPE datatype){
        return Util_string.toCamelCase(raw(datatype));
    }
    public String globalVar(Keywords.DATATYPE datatype){
        return Util_string.toScreamingSnake(raw(datatype));
    }
    public String fileName(Keywords.DATATYPE datatype){
        return className(datatype) + "." + Widget.getFileExt();
    }
}
