package codegen.genjava;

import codegen.interfaces.*;
import translators.ut.FormatUtil;
import runstate.RunState;
import erlog.DevErr;

import java.util.ArrayList;

import static codegen.interfaces.enums.ENDL;
import static codegen.interfaces.enums.SEMICOLON;

public class ClassJava implements IClass {
    private final ArrayList<IWidget> content;
    private enums.VISIBILITY visibility;
    private boolean abstract_;
    private boolean static_;
    private boolean inner;          // denotes inner/nested class
    private String name;
    private String extends_;
    private String[] pathPackages;
    private String[] implements_;
    private IImport[] imports;

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
        if(!inner){
            genPackage(formatUtil);
            genImports(formatUtil);
        }
        genHeader(formatUtil);
        formatUtil.addTabLines(content);
        formatUtil.add("}");
        return this;
    }

    private void genPackage(FormatUtil formatUtil){
        formatUtil.add("package " + RunState.getInstance().getGenPackage(pathPackages) + SEMICOLON + ENDL);
    }
    private void genImports(FormatUtil formatUtil){
        if(imports != null){
            for(IImport import_ : imports){
                import_.finish(formatUtil);
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
        public IClassBuilder setInner() {
            built.inner = true;
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
        public IClassBuilder setPathPackages(String... pathPackages) {
            built.pathPackages = pathPackages;
            return this;
        }

        @Override
        public IClassBuilder setImports(IImport... imports) {
            built.imports = imports;
            return this;
        }

        @Override
        public IClass build() {
            return built;
        }
    }

    public static class ImportJava implements IImport{
        private final ArrayList<IWidget> content;
        private boolean wildcard;
        private boolean static_;
        private String name;
        private String[] pathPackages;

        public ImportJava() {
            content = new ArrayList<>();
        }

        @Override
        public IWidget finish(FormatUtil formatUtil) {
            ArrayList<String> header = new ArrayList<>();
            header.add("import");
            if(static_){
                header.add("static");
            }
            header.add(genDotSep());
            formatUtil.add(String.join(" ", header) + SEMICOLON);
            return this;
        }

        private String genDotSep(){
            ArrayList<String> dotSeparated = new ArrayList<>();
//            if(pathPackages != null){
//                for(String package_ : pathPackages){
//                    dotSeparated.add(package_);
//                }
//            }
            dotSeparated.add(RunState.getInstance().getGenPackage(pathPackages));

            if(name == null){
                DevErr.get(this).kill("import name field is required");
            }
            else{
                dotSeparated.add(name);
            }
            if(wildcard){
                dotSeparated.add("*");
            }
            return String.join(".", dotSeparated);
        }
    }
    public static class ImportBuilder implements IImportBuilder{
        private final ImportJava built;

        public ImportBuilder() {
            built = new ImportJava();
        }

        @Override
        public IImportBuilder setPathPackages(String... pathPackages) {
            built.pathPackages = pathPackages;
            return this;
        }

        @Override
        public IImportBuilder setName(String name) {
            built.name = name;
            return this;
        }

        @Override
        public IImportBuilder setStatic() {
            built.static_ = true;
            return this;
        }

        @Override
        public IImportBuilder setWildcard() {
            built.wildcard = true;
            return this;
        }

        @Override
        public IImport build() {
            return built;
        }
    }
}
