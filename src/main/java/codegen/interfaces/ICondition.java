package codegen.interfaces;

import codegen.ut.FormatUtil;

public interface ICondition extends IWidget{
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

    ICondition add(ICondition... condition);
    ICondition add(String... text);

    interface IConditionBuilder {
        IConditionBuilder setConnector(CONNECTOR connector);
        IConditionBuilder setNot();
        ICondition build();
    }
}
