package codegen.genjava;

import codegen.interfaces.ISimpleText;
import codegen.interfaces.ISwitch;
import codegen.interfaces.IVar;
import codegen.interfaces.IWidget;
import codegen.ut.FormatUtil;
import erlog.DevErr;

import java.util.ArrayList;

import static codegen.interfaces.ISwitch.*;
import static codegen.interfaces.enums.SEMICOLON;

public class SwitchJava implements ISwitch {
    private String testObject;
    private final ArrayList<String> cases;
    private final ArrayList<ArrayList<IWidget>> actions;
    private ArrayList<IWidget> currActions;
    private boolean defaultAdded;

    public SwitchJava() {
        cases = new ArrayList<>();
        actions = new ArrayList<>();
        defaultAdded = false;
    }

    @Override
    public ISwitch startCase(String case_) {
        cases.add(case_);
        currActions = new ArrayList<>();
        return this;
    }

    @Override
    public ISwitch startDefault() {
        defaultAdded = true;
        return startCase("default");
    }

    @Override
    public ISwitch add(IWidget... widget) {
        if(currActions == null){
            DevErr.get(this).kill("Call startCase() before adding");
        }
        for(IWidget w : widget){
            currActions.add(w);
        }
        return this;
    }

    @Override
    public ISwitch finishCase() {
        if(currActions == null){
            DevErr.get(this).kill("Call startCase() and add content before finishCase()");
        }
        actions.add(currActions);
        currActions = null;
        return this;
    }

    @Override
    public IWidget finish(FormatUtil formatUtil) {
        if(!defaultAdded){
            this.startDefault().finishCase();
        }
        genHeader(formatUtil);
        formatUtil.inc();
            genContent(formatUtil);
        formatUtil.dec();
        formatUtil.addLineSegment("}");
        return this;
    }
    private void genHeader(FormatUtil formatUtil) {
        if(testObject == null){
            DevErr.get(this).kill("testObject is required");
        }
        formatUtil.addLine("switch (" + testObject + ") {");
    }
    private void genContent(FormatUtil formatUtil) {
        int i = 0;
        for(ArrayList<IWidget> lines : actions){
            String case_ = cases.get(i);
            if("default".equals(case_)){
                formatUtil.addLineSegment("default:");
            }
            else{
                formatUtil.addLineSegment("case " + case_ + ":");
            }
            formatUtil.inc();
                for(IWidget line : lines){
                    line.finish(formatUtil);
                }
            formatUtil.addLineSegment("break" + SEMICOLON);
            formatUtil.dec();
            i++;
        }
    }
    public static class SwitchBuilder implements ISwitchBuilder{
        private SwitchJava built;

        public SwitchBuilder() {
            built = new SwitchJava();
        }

        public ISwitchBuilder setTestObject(String testObject){
            built.testObject = testObject;
            return this;
        }
        public ISwitch build(){
            return built;
        }

    }
}
