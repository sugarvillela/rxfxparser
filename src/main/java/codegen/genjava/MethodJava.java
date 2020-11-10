package codegen.genjava;

import codegen.interfaces.IMethod;
import codegen.interfaces.IWidget;
import codegen.interfaces.enums;
import codegen.ut.FormatUtil;
import erlog.DevErr;

import java.util.ArrayList;

import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;

public class MethodJava implements IMethod {
    private final ArrayList<IWidget> content;
    private enums.VISIBILITY visibility;
    private boolean isConstructor;
    private boolean abstract_;
    private boolean static_;
    private boolean final_;
    private String returnType;
    private String name;
    private String[] params;

    public MethodJava() {
        content = new ArrayList<>();
    }


    @Override
    public IMethod add(IWidget... widget) {
        for(IWidget w : widget){
            content.add(w);
        }
        return this;
    }

    @Override
    public IMethod add(String... text) {
        content.add(new TextJava().add(text));
        return this;
    }

    @Override
    public IWidget finish(FormatUtil formatUtil) {
        genHeader(formatUtil);
        formatUtil.addTabLines(content);
        formatUtil.addLine("}");
        return this;
    }
    private void genHeader(FormatUtil formatUtil){
        ArrayList<String> header = new ArrayList<>();
        if(visibility == null){
            header.add("public");
        }
        else{
            header.add(visibility.toString());
        }
        if(!isConstructor){
            if(abstract_){
                header.add("abstract");
            }
            if(static_){
                header.add("static");
            }
            if(final_){
                header.add("final");
            }
            if(returnType == null){
                header.add("void");
            }
            else{
                header.add(returnType);
            }
        }
        if(name == null){
            DevErr.get(this).kill("name field is required");
        }
        else{
            header.add(name);
        }

        if(params == null){
            header.add("()");
        }
        else{
            header.add("(" + String.join(", ", params) + ")");
        }
        header.add("{");
        formatUtil.addLine(String.join(" ", header));
    }

    @Override
    public String toString(){
        return String.format("%s(%s)", name, String.join(", ", params));
    }

    public static class MethodBuilder implements IMethodBuilder{
        private final MethodJava built;

        public MethodBuilder() {
            built = new MethodJava();
        }

        @Override
        public IMethodBuilder setVisibility(enums.VISIBILITY visibility) {
            built.visibility = visibility;
            return this;
        }

        @Override
        public IMethodBuilder setStatic() {
            built.static_ = true;
            return this;
        }

        @Override
        public IMethodBuilder setFinal() {
            built.final_ = true;
            return this;
        }

        @Override
        public IMethodBuilder setReturnType(String returnType) {
            built.returnType = returnType;
            return this;
        }

        @Override
        public IMethodBuilder setIsConstructor() {
            built.isConstructor = true;
            return this;
        }

        @Override
        public IMethodBuilder setName(String name) {
            built.name = name;
            return this;
        }

        @Override
        public IMethodBuilder setParams(String... params) {
            built.params = params;
            return this;
        }

        @Override
        public IMethod build() {
            return built;
        }
    }
}
