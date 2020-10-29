package codegen.interfaces;

import codegen.ut.FormatUtil;

import java.util.ArrayList;

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
