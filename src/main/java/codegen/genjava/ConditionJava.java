package codegen.genjava;

import codegen.interfaces.ICondition;
import codegen.interfaces.IWidget;
import translators.ut.FormatUtil;

import java.util.ArrayList;

public class ConditionJava implements ICondition {

    public static ICondition set(String... text){
        return new ConditionBuilder().build().add(text);
    }
    public static ICondition setNot(String... text){
        return new ConditionBuilder().setNot().build().add(text);
    }


    private final ArrayList<IWidget> content;
    private CONNECTOR connector;
    private boolean negateAll;

    public ConditionJava(){
        content = new ArrayList<>();
    }

    @Override
    public ICondition add(ICondition... condition) {
        for(IWidget w : condition){
            content.add(w);
        }
        return this;
    }

    @Override
    public ICondition add(String... text) {
        content.add(new TextJava().add(text));
        return this;
    }

    @Override
    public IWidget finish(FormatUtil formatUtil) {
        formatUtil.addLine(this.toString());
        return this;
    }

    @Override
    public String toString(){
        ArrayList<String> out = new ArrayList<>(content.size());
        for(IWidget w : content){
            out.add(w.toString());
        }
        String formatString = (negateAll)? "!(%s)" : "(%s)";
        String delim = (connector == null)? " " : connector.toString();
        return String.format(formatString, String.join(delim, out));
    }

    public static class ConditionBuilder implements IConditionBuilder{
        private ConditionJava built;

        public ConditionBuilder() {
            built = new ConditionJava();
        }

        @Override
        public IConditionBuilder setConnector(CONNECTOR connector) {
            built.connector = connector;
            return this;
        }

        @Override
        public IConditionBuilder setNot() {
            built.negateAll = true;
            return this;
        }

        @Override
        public ConditionJava build() {
            return built;
        }
    }
}
