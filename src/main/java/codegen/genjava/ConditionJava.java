package codegen.genjava;

import codegen.interfaces.ICondition;
import codegen.interfaces.IWidget;
import codegen.ut.FormatUtil;

import java.util.ArrayList;

public class ConditionJava implements ICondition {

    public static ICondition set(String... text){
        return new ConditionBuilder().build().add(text);
    }
    public static ICondition setNot(String... text){
        return new ConditionBuilder().setNot().build().add(text);
    }

    public enum CONNECTOR implements ICondition{
        AND_    ("&&"),
        OR_     ("||"),
        EQUALS_ ("=="),
        GT_     (">"),
        GTE_    (">="),
        LT_     ("<"),
        LTE_    ("<="),
        NOT_    ("!")
        ;

        private final String symbol;

        CONNECTOR(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public IWidget finish(FormatUtil formatUtil) {
            return null;
        }
        @Override
        public String toString(){
            return symbol;
        }

        @Override
        public ICondition add(ICondition... condition) {
            return null;
        }

        @Override
        public ICondition add(String... text) {
            return null;
        }
    }
    private final ArrayList<IWidget> content;
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
        String formatString = (negateAll)? "!(%s)" : "%s";
        return String.format(formatString, String.join(" ", out));
    }

    public static class ConditionBuilder implements IConditionBuilder{
        private ConditionJava built;

        public ConditionBuilder() {
            built = new ConditionJava();
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
