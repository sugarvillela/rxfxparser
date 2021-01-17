package codegen.interfaces;

public interface IException  extends IWidget{
    IException add(IWidget... widget);
    IException add(String... text);

    interface IExceptionBuilder{
        IExceptionBuilder setExType(String... exType);
        IExceptionBuilder setCatchActions(IWidget... action);
        IExceptionBuilder setCatchActions(String... action);
        IExceptionBuilder setSilent();
        IExceptionBuilder setDisplay();
        IException build();
    }
}
