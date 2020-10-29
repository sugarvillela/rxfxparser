package codegen.genjava;

import codegen.interfaces.IBool;
import codegen.interfaces.IControl;
import codegen.interfaces.IWidget;
import codegen.ut.FormatUtil;
import erlog.DevErr;

import java.util.ArrayList;

public class ControlJava implements IControl {
    public enum CONTROL_TYPE{
        IF_, ELSE_, WHILE_, DO_WHILE_, FOR_, FOR_EACH_
    }

    private final ArrayList<IWidget> content;
    private CONTROL_TYPE controlType;
    private IBool condition;
    private String lo, hi, inc;
    private String type, itemName, arrayName;
    private boolean isSet;
    
    public ControlJava() {
        content = new ArrayList<>();
        inc = "1";
    }
    
    @Override
    public IControl add(IWidget... widget) {
        for(IWidget w : widget){
            content.add(w);
        }
        return this;
    }

    @Override
    public IWidget finish(FormatUtil formatUtil) {
        if(!isSet){
            DevErr.get(this).kill("Required to set control type");
        }
        switch(controlType){
            case IF_ :
                genConditionHeader("if", formatUtil, " {");
                genContent(formatUtil);
                formatUtil.addLineSegment("}");
                break;
            case WHILE_:
                genConditionHeader("while", formatUtil, " {");
                genContent(formatUtil);
                formatUtil.addLineSegment("}");
                break;
            case DO_WHILE_:
                formatUtil.addLineSegment("do {");
                genContent(formatUtil);
                genConditionHeader("} while", formatUtil, ";");
                break;
            case ELSE_:
                formatUtil.addLineSegment("else {");
                genContent(formatUtil);
                formatUtil.addLineSegment("}");
                break;
            case FOR_:
                formatUtil.addLine(String.format("for(int i = %s; i < %s; i += %s){", lo, hi, inc));
                genContent(formatUtil);
                formatUtil.addLineSegment("}");
                break;
            case FOR_EACH_:
                formatUtil.addLine(String.format("for(%s %s : %s){", type, itemName, arrayName));
                genContent(formatUtil);
                formatUtil.addLineSegment("}");
                break;
        }
        return this;
    }
    private void genConditionHeader(String head, FormatUtil formatUtil, String tail){
        formatUtil.addLineSegment(head + " (");
        formatUtil.inc();
            condition.finish(formatUtil);
        formatUtil.dec();
        formatUtil.addLineSegment(")" + tail);
    }

    private void genContent(FormatUtil formatUtil){
        formatUtil.inc();
        for(IWidget widget : content){
            widget.finish(formatUtil);
        }
        formatUtil.dec();
    }

    public static class ControlBuilder implements IControlBuilder{
        private ControlJava built;

        public ControlBuilder() {
            built = new ControlJava();
        }
        private void assertOnce(){
            if(built.isSet){
                DevErr.get(this).kill("Controller types IF, ELSE, WHILE etc are exclusive");
            }
            built.isSet = true;
        }

        @Override
        public IControlBuilder setIf(IBool condition) {
            assertOnce();
            built.controlType = CONTROL_TYPE.IF_;
            built.condition = condition;
            return this;
        }

        @Override
        public IControlBuilder setElse() {
            assertOnce();
            built.controlType = CONTROL_TYPE.ELSE_;
            return this;
        }

        @Override
        public IControlBuilder setWhile(IBool condition) {
            assertOnce();
            built.controlType = CONTROL_TYPE.WHILE_;
            built.condition = condition;
            return this;
        }

        @Override
        public IControlBuilder setDoWhile(IBool condition) {
            assertOnce();
            built.controlType = CONTROL_TYPE.DO_WHILE_;
            built.condition = condition;
            return this;
        }

        @Override
        public IControlBuilder setForEach(String type, String itemName, String arrayName) {
            assertOnce();
            built.controlType = CONTROL_TYPE.FOR_EACH_;
            built.type = type; 
            built.itemName = itemName;
            built.arrayName = arrayName;
            return this;
        }

        @Override
        public IControlBuilder setFor(String lo, String hi) {
            built.controlType = CONTROL_TYPE.FOR_;
            built.lo = lo;
            built.hi = hi;
            return this;
        }

        @Override
        public IControlBuilder setFor(String hi) {
            built.controlType = CONTROL_TYPE.FOR_;
            built.lo = "0";
            built.hi = hi;
            return this;
        }

        @Override
        public IControlBuilder setForInc(String inc) {
            built.inc = inc;
            return this;
        }

        @Override
        public ControlJava build() {
            return built;
        }
    }
}
