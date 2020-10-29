package codegen.interfaces;

public interface IVar extends IWidget {

    public interface IFieldBuilder{
        IFieldBuilder setVisibility(enums.VISIBILITY visibility);
        IFieldBuilder setStatic();
        IFieldBuilder setFinal();
        IFieldBuilder setType(String type);
        IFieldBuilder setName(String name);
        IFieldBuilder seValue(String value);
        IVar build();
    }

    public interface IVarBuilder{
        IVarBuilder setType(String type);
        IVarBuilder setName(String name);
        IVarBuilder seValue(String value);
        IVar build();
    }

    public interface IAssignmentBuilder{
        IAssignmentBuilder setName(String name);
        IAssignmentBuilder seValue(String value);
        IVar build();
    }
}
