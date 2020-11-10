package codegen.genjava;

import codegen.interfaces.*;
import codegen.ut.FormatUtil;
import codegen.ut.PathUtil;
import commons.Commons;
import compile.basics.CompileInitializer;
import erlog.DevErr;

import java.util.ArrayList;

import static codegen.genjava.ConditionJava.CONNECTOR.AND_;
import static codegen.interfaces.enums.ENDL;
import static codegen.interfaces.enums.SEMICOLON;
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
    public IClass add(String... text) {
        content.add(new TextJava().add(text));
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
        formatUtil.add("package " + CompileInitializer.getInstance().getGenPackage(subPackages) + SEMICOLON + ENDL);
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
            header.add("class");
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
        private final ClassJava built;

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
}
