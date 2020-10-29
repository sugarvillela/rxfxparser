package codegen.interfaces;

import codegen.ut.FormatUtil;

public interface ISimpleText extends IWidget{
    ISimpleText add(String... text);
    ISimpleText finish(FormatUtil formatUtil);

    interface ISimpleTextBuilder {
        ISimpleTextBuilder setIndent();
        ISimpleText build();
    }
}
