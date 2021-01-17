package codegen.interfaces;

public interface IClass extends IWidget{
    IClass add(IWidget... widget);
    IClass add(String... text);

    interface IClassBuilder{
        IClassBuilder setVisibility(enums.VISIBILITY visibility);
        IClassBuilder setAbstract();
        IClassBuilder setStatic();
        IClassBuilder setInner();
        IClassBuilder setName(String name);
        IClassBuilder setExtends(String extends_);
        IClassBuilder setImplements(String... implements_);
        IClassBuilder setPathPackages(String... pathPackages);
        IClassBuilder setImports(IImport... imports);
        IClass build();
    }

    interface IImport extends IWidget{
//        IImport add(IWidget... widget);
//        IImport add(String... text);
    }
    interface IImportBuilder{
        IImportBuilder setPathPackages(String... pathPackages);
        IImportBuilder setName(String name);
        IImportBuilder setStatic();
        IImportBuilder setWildcard();
        IImport build();
    }
}
