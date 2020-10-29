package demos;

import codegen.Widget;
import compile.basics.Keywords;
import compile.parse.importtable.ImportTable;
import compile.parse.importtable.NameGen;

public class NameGen_ {
    public static void demo(){
        NameGen.init("semantic");
        NameGen nameGen = NameGen.getInstance();

        System.out.println(nameGen.functionName(Keywords.DATATYPE.TARGLANG_INSERT));
        System.out.println(nameGen.className(Keywords.DATATYPE.RX_WORD));
        System.out.println(nameGen.globalVar(Keywords.DATATYPE.FX));
    }
    public static void importTable(){
        Widget.setDefaultLanguage(Widget.JAVA);
        ImportTable.init();
        ImportTable importTable = ImportTable.getInstance();

        String name = importTable.add(Keywords.DATATYPE.TARGLANG_INSERT);
        System.out.println(name);
        System.out.println(importTable.unpackType(name));
    }
}
