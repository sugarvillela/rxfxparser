package codegen.genjava;

import codegen.interfaces.*;
import codegen.ut.FormatUtil;
import codegen.ut.PathUtil;
import commons.Commons;
import erlog.DevErr;

import java.util.ArrayList;

import static codegen.interfaces.enums.ENDL;
import static codegen.interfaces.enums.SEMICOLON;
import static codegen.interfaces.enums.VISIBILITY.PROTECTED_;
import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;
import static codegen.genjava.CommentJava.*;
import static codegen.genjava.MethodJava.*;
import static codegen.genjava.VarJava.*;
import static codegen.genjava.SwitchJava.*;
import static codegen.genjava.ArrayJava.*;

public class ClassJava implements IClass {
    private final ArrayList<IWidget> content;
    private enums.VISIBILITY visibility;
    private boolean abstract_;
    private boolean static_;
    private String name;
    private String extends_;
    private String[] subPackages;
    private String[] implements_;
    private String[] imports;

    public ClassJava(){
        content = new ArrayList<>();
    }

    @Override
    public IClass add(IWidget... widget) {
        for(IWidget w : widget){
            content.add(w);
        }
        return this;
    }

    @Override
    public IWidget finish(FormatUtil formatUtil) {
        genPackage(formatUtil);
        genImports(formatUtil);
        genHeader(formatUtil);
        formatUtil.addTabLines(content);
        formatUtil.add("}");
        return this;
    }


    private void genPackage(FormatUtil formatUtil){
        formatUtil.add("package " + PathUtil.getInstance().fixPackage(subPackages) + SEMICOLON + ENDL);
    }
    private void genImports(FormatUtil formatUtil){
        if(imports != null){
            for(String import_ : imports){
                formatUtil.add("import " + import_ + SEMICOLON);
            }
            formatUtil.add("");
        }
    }
    private void genHeader(FormatUtil formatUtil){
        ArrayList<String> header = new ArrayList<>();
        if(visibility == null){
            header.add("public");
        }
        else{
            header.add(visibility.toString());
        }
        if(abstract_){
            header.add("abstract");
        }
        if(static_){
            header.add("static");
        }
        if(name == null){
            DevErr.get(this).kill("name field is required");
        }
        else{
            header.add(name);
        }
        if(extends_ != null){
            header.add("extends " + extends_);
        }
        if(implements_ != null){
            header.add("implements " + String.join(", ", implements_));
        }
        header.add("{");
        formatUtil.add(String.join(" ", header));
    }
    private void genContent(FormatUtil formatUtil){
        formatUtil.inc();
        for(IWidget widget : content){
            widget.finish(formatUtil);
        }
        formatUtil.dec();
    }
    @Override
    public String toString(){
        return name;
    }
    public static class ClassJavaBuilder implements IClassBuilder{
        private ClassJava built;

        public ClassJavaBuilder() {
            built = new ClassJava();
        }

        @Override
        public IClassBuilder setVisibility(enums.VISIBILITY visibility) {
            built.visibility = visibility;
            return this;
        }

        @Override
        public IClassBuilder setAbstract() {
            built.abstract_ = true;
            return this;
        }

        @Override
        public IClassBuilder setStatic() {
            built.static_ = true;
            return this;
        }

        @Override
        public IClassBuilder setName(String name) {
            built.name = name;
            return this;
        }

        @Override
        public IClassBuilder setExtends(String extends_) {
            built.extends_ = extends_;
            return this;
        }

        @Override
        public IClassBuilder setImplements(String... implements_) {
            built.implements_ = implements_;
            return this;
        }

        @Override
        public IClassBuilder setSubPackages(String... subPackages) {
            built.subPackages = subPackages;
            return this;
        }

        @Override
        public IClassBuilder setImports(String... imports) {
            built.imports = imports;
            return this;
        }

        @Override
        public IClass build() {
            return built;
        }
    }

    public static void demo(){
        String[] demo1 = Commons.randomContent(20);
        String[] demo2 = Commons.randomContent(25);
        FormatUtil formatUtil = new FormatUtil();
        IWidget classJava = new ClassJavaBuilder().setName("MyClass").setVisibility(PUBLIC_).setAbstract().
                setExtends("MyParentClass").setImplements("CrazyInterface", "BadInterface").
                setImports("Munchkin","FeeFoo").setSubPackages("subby").build().
                add(
                        new CommentBuilder().setLong().build().add("This is a short part and this is a much much longer part with lots of extra words and it should definitely be split into two lines!"),
                        new ArrayBuilder().setName("myArray").setType("String").setSplit().build(),
                    new MethodBuilder().setName("MyClass").setIsConstructor().build().add(
                            new CommentBuilder().build().add("Inside the method"),
                            new ArrayBuilder().setName("myArray").setType("String").setSplit().build().add(demo1)
                    ),
                    new MethodBuilder().setName("doSomething").setFinal().build().add(
                            new CommentBuilder().build().add("Inside the method: doSomething"),
                            new ArrayBuilder().setName("myArray").setType("String").build().add(demo2)
                    ),
                    new MethodBuilder().setName("doSomethingElse").setVisibility(PROTECTED_).setFinal().setReturnType("int").build().add(
                            new CommentBuilder().build().add("Inside the method: doSomethingElse"),
                            new VarBuilder().setType("int").setName("myVar").seValue("7").build(),
                            new SwitchBuilder().setTestObject("myVar").build().
                                startCase("6").add(
                                    new SimpleText.SimpleTextBuilder().build().add("System.out.println(\"Foo Boo\");")
                                ).finishCase().startCase("7").add(
                                    new SimpleText.SimpleTextBuilder().build().add("System.out.println(\"Meh\");"),
                                    new AssignmentBuilder().setName("myVar").seValue("22").build()
                            ).finishCase()
                    )
        ).finish(formatUtil);
        Commons.disp(formatUtil.finish(), "Classy");
    }
}
