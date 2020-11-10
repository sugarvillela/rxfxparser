package codegen.genjava;

import codegen.interfaces.*;
import codegen.ut.FormatUtil;
import erlog.DevErr;

import java.util.ArrayList;

import static codegen.interfaces.enums.SEMICOLON;

public class VarJava implements IVar {
    private enums.VISIBILITY visibility;
    private boolean static_;
    private boolean final_;
    private String type;
    private String name;
    private String value;

    public VarJava() {
    }

    @Override
    public IWidget finish(FormatUtil formatUtil) {
        genHeader(formatUtil);
        return this;
    }
    private void genHeader(FormatUtil formatUtil){
        ArrayList<String> header = new ArrayList<>();
        if(visibility != null){
            header.add(visibility.toString());
        }
        if(static_){
            header.add("static");
        }
        if(final_){
            header.add("final");
        }
        if(type != null){
            header.add(type);
        }
        if(name == null || value == null){
            DevErr.get(this).kill("name and value field are required");
        }
        else{
            header.add(name + " = " + value);
        }
        formatUtil.addLine(String.join(" ", header) + SEMICOLON);
    }

    @Override
    public String toString(){
        return name;
    }
    public static class FieldBuilder implements IFieldBuilder {
        private final VarJava built;

        public FieldBuilder() {
            built = new VarJava();
        }

        @Override
        public IFieldBuilder setVisibility(enums.VISIBILITY visibility) {
            built.visibility = visibility;
            return this;
        }

        @Override
        public IFieldBuilder setStatic() {
            built.static_ = true;
            return this;
        }

        @Override
        public IFieldBuilder setFinal() {
            built.final_ = true;
            return this;
        }

        @Override
        public IFieldBuilder setType(String type) {
            built.type = type;
            return this;
        }

        @Override
        public IFieldBuilder setName(String name) {
            built.name = name;
            return this;
        }

        @Override
        public IFieldBuilder seValue(String value) {
            built.value = value;
            return this;
        }

        @Override
        public VarJava build() {
            return built;
        }
    }

    public static class VarBuilder implements IVarBuilder {
        private final VarJava built;

        public VarBuilder() {
            built = new VarJava();
        }

        @Override
        public IVarBuilder setType(String type) {
            built.type = type;
            return this;
        }

        @Override
        public IVarBuilder setName(String name) {
            built.name = name;
            return this;
        }

        @Override
        public IVarBuilder seValue(String value) {
            built.value = value;
            return this;
        }

        @Override
        public VarJava build() {
            return built;
        }
    }

    public static class AssignmentBuilder implements IAssignmentBuilder {
        private final VarJava built;

        public AssignmentBuilder() {
            built = new VarJava();
        }

        @Override
        public IAssignmentBuilder setName(String name) {
            built.name = name;
            return this;
        }

        @Override
        public IAssignmentBuilder seValue(String value) {
            built.value = value;
            return this;
        }

        @Override
        public VarJava build() {
            return built;
        }
    }
}
