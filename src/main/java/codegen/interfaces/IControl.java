package codegen.interfaces;

public interface IControl extends IWidget { // if, else, while, doWhile
    IControl add(IWidget... widget);
    IControl add(String... text);

    interface IControlBuilder{
        IControlBuilder setIf(IWidget condition);
        IControlBuilder setElse();
        IControlBuilder setWhile(IWidget condition);
        IControlBuilder setDoWhile(IWidget condition);
        IControlBuilder setForEach(String type, String itemName, String arrayName);
        IControlBuilder setFor(String lo, String hi);
        IControlBuilder setFor(String hi);
        IControlBuilder setForInc(String inc);
        IControl build();
    }
}
