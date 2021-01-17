package compile.parse.importtable;

import codegen.Widget;
import codegen.namegen.NameGenSimple;
import commons.Util_string;
import compile.basics.Keywords;

import java.util.HashMap;
import java.util.Map;

import static compile.basics.Keywords.UQ_FORMAT_LEN;

public class ImportTable {
    private static ImportTable instance;

    public static void init(){
        instance = new ImportTable();
    }
    public static ImportTable getInstance(){
        return instance;
    }

    private final NameGenSimple nameGen;
    private final Map<String, String> importTableMap;

    private ImportTable(){
        String projName = "semantic"; // RunState.getInstance().getProjName();
        //NameGen.init(projName);
        nameGen = NameGenSimple.getInstance();
        importTableMap = new HashMap<>();
    }

    public String add(Keywords.DATATYPE datatype){
        String name = nameGen.className(datatype.toString());
        return name;
    }
    public Keywords.DATATYPE unpackType(String name){
        String projName = "semantic"; // RunState.getInstance().getProjName();
        int start = projName.length();
        int end = name.length() - UQ_FORMAT_LEN - Widget.getFileExt().length() - 1;
        return Keywords.DATATYPE.fromString(
            Util_string.toScreamingSnake(name.substring(start, end))
        );
    }
}
