package codegen.interfaces;

public interface ICondition extends IWidget{
    ICondition add(ICondition... condition);
    ICondition add(String... text);

    interface IConditionBuilder {
        IConditionBuilder setNot();
        ICondition build();
    }
}
