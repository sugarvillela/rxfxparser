package codegen.interfaces;

public interface IStaticBlock extends IWidget{
    IStaticBlock add(IWidget... widget);
    IStaticBlock add(String... text);

    interface IStaticBlockBuilder{
        IStaticBlock build();
    }
}
