package codegen.interfaces;

import translators.ut.FormatUtil;

public interface IComment extends IWidget{
    IComment add(String... text);
    IComment finish(FormatUtil formatUtil);

    interface ICommentBuilder{
        ICommentBuilder setLong();
        IComment build();
    }
}
