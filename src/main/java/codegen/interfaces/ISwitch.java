package codegen.interfaces;

public interface ISwitch extends IWidget {
    ISwitch startCase(String case_);
//    ISwitch startCase(String case_, boolean addToExisting);
    ISwitch startDefault();
    ISwitch add(IWidget... widget);
    ISwitch add(String... text);
    ISwitch finishCase();

    interface ISwitchBuilder{
        ISwitchBuilder setTestObject(String testObject);
        ISwitchBuilder setNoBreaks();
        ISwitch build();
    }
}
