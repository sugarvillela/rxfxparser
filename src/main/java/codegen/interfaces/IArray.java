package codegen.interfaces;

public interface IArray extends IWidget {
    IArray add(String... items);

    interface IArrayBuilder{
        IArrayBuilder setVisibility(enums.VISIBILITY visibility);
        IArrayBuilder setStatic();
        IArrayBuilder setFinal();
        IArrayBuilder setType(String type);
        IArrayBuilder setName(String name);
        IArrayBuilder setSize(String size);
        IArrayBuilder setSplit();
        IArray build();
    }
}
