package codegen.interfaces;

import codegen.ut.FormatUtil;

import java.util.ArrayList;

public interface IClass extends IWidget{
    IClass add(IWidget... widget);
    IClass add(String... text);

    interface IClassBuilder{
        IClassBuilder setVisibility(enums.VISIBILITY visibility);
        IClassBuilder setAbstract();
        IClassBuilder setStatic();
        IClassBuilder setName(String name);
        IClassBuilder setExtends(String extends_);
        IClassBuilder setImplements(String... implements_);
        IClassBuilder setSubPackages(String... subPackages);
        IClassBuilder setImports(String... imports);
        IClass build();
    }
}
