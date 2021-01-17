package demos;

import codegen.Widget;
import compile.basics.Keywords;
import compile.parse.importtable.ImportTable;
import codegen.namegen.NameGenSimple;

public class NameGen_ {
    public static void demo(){
        NameGenSimple.init("semantic");
        NameGenSimple nameGen = NameGenSimple.getInstance();

        System.out.println(nameGen.functionName(Keywords.DATATYPE.TARGLANG_INSERT.toString()));
        System.out.println(nameGen.className(Keywords.DATATYPE.RX_WORD.toString()));
        System.out.println(nameGen.globalVar(Keywords.DATATYPE.FX.toString()));
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
