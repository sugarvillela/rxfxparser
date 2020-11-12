package demos;

import codegen.genjava.ClassJava;
import codegen.genjava.ConditionJava;
import codegen.genjava.TextFileJava;
import codegen.genjava.TextJava;
import codegen.interfaces.IWidget;
import codegen.ut.FormatUtil;
import commons.Commons;

import static codegen.interfaces.enums.VISIBILITY.PROTECTED_;
import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;
import static codegen.genjava.CommentJava.*;
import static codegen.genjava.MethodJava.*;
import static codegen.genjava.VarJava.*;
import static codegen.genjava.SwitchJava.*;
import static codegen.genjava.ArrayJava.*;
import static codegen.genjava.ControlJava.*;
import static codegen.genjava.ConditionJava.*;
import static codegen.genjava.ConditionJava.CONNECTOR.*;

/**
 *
 * @author Dave Swanson
 */
public class Codegen_ {
    public static void widget_java(){
        String[] demo1 = Commons.randomContent(20);
        String[] demo2 = Commons.randomContent(25);
        FormatUtil formatUtil = new FormatUtil();
        IWidget classJava = new ClassJava.ClassJavaBuilder().setName("MyClass").setVisibility(PUBLIC_).setAbstract().
                //setImports("Munchkin","Frito").setPathPackages("subby").build().
                setExtends("MyParentClass").setImplements("CrazyInterface", "BadInterface").build().add(
                        new CommentBuilder().setLong().build().add("This is a short part and this is a much much longer part with lots of extra words and it should definitely be split into two lines!"),
                        new ArrayBuilder().setName("myArray").setType("String").setSplit().build(),
                        new MethodBuilder().setName("MyClass").setIsConstructor().build().add(
                                new CommentBuilder().build().add("Inside the method"),
                                new ArrayBuilder().setName("myArray").setType("String").setSplit().build().add(demo1),
                                new ControlBuilder().setForEach("String","item","myArray").build().
                                        add(
                                                new CommentBuilder().build().add("in the loop")
                                        )
                        ),
                        new MethodBuilder().setName("doSomething").setFinal().build().add(
                                new CommentBuilder().build().add("Inside the method: doSomething"),
                                new ArrayBuilder().setName("myArray").setType("String").build().add(demo2),
                                new ControlBuilder().setIf(
                                        new ConditionBuilder().setNot().build().add(
                                                ConditionJava.set("myArray.length"),
                                                GT_,
                                                ConditionJava.set("7"),
                                                AND_,
                                                ConditionJava.set("myArray[0]"),
                                                EQUALS_,
                                                ConditionJava.set("\"Frank\"")
                                        )
                                ).build().add(
                                        TextJava.set("break;")
                                ),
                                new ControlBuilder().setElse().build().add(
                                        new TextFileJava.TextFileBuilder().setFile("file01.txt").build(),
                                        TextJava.set("doSomethingElse()")
                                )
                        ),
                        new MethodBuilder().setName("doSomethingElse").setVisibility(PROTECTED_).setFinal().setReturnType("int").build().add(
                                new CommentBuilder().build().add("Inside the method: doSomethingElse"),
                                new VarBuilder().setType("int").setName("myVar").seValue("7").build(),
                                new SwitchBuilder().setTestObject("myVar").build().
                                        startCase("6").add(
                                        "System.out.println(\"Foo Boo\");"
                                ).finishCase().startCase("7").add(
                                        TextJava.set("System.out.println(\"Meh\");"),
                                        new AssignmentBuilder().setName("myVar").seValue("22").build()
                                ).finishCase(),
                                new ControlBuilder().setFor("25").build().
                                        add(
                                                new CommentBuilder().build().add("in the loop")
                                        ).
                                        add(
                                                "System.out.println(\"Meh\" + i);"
                                        )

                        )
                ).finish(formatUtil);
        Commons.disp(formatUtil.finish(), "Classy");
    }
}
