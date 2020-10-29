package codegen.interfaces;

public interface IControl extends IWidget { // if, else, while, doWhile


    IControl add(IWidget... widget);

    interface IControlBuilder{
        IControlBuilder setIf(IBool condition);
        IControlBuilder setElse();
        IControlBuilder setWhile(IBool condition);
        IControlBuilder setDoWhile(IBool condition);
        IControlBuilder setForEach(String type, String itemName, String arrayName);
        IControlBuilder setFor(String lo, String hi);
        IControlBuilder setFor(String hi);
        IControlBuilder setForInc(String inc);
        IControl build();
    }
}
