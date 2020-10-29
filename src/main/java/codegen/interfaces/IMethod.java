package codegen.interfaces;

public interface IMethod extends IWidget {
    IMethod add(IWidget... widget);

    interface IMethodBuilder{
        IMethodBuilder setVisibility(enums.VISIBILITY visibility);
        IMethodBuilder setStatic();
        IMethodBuilder setFinal();
        IMethodBuilder setReturnType(String returnType);
        IMethodBuilder setIsConstructor();
        IMethodBuilder setName(String name);
        IMethodBuilder setParams(String... params);
        IMethod build();
    }
}
