package codegen.interfaces;

import codegen.ut.FormatUtil;

import java.util.ArrayList;

public interface ISwitch extends IWidget {
    ISwitch startCase(String case_);
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
