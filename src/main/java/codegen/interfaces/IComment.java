package codegen.interfaces;

import codegen.ut.FormatUtil;

import java.util.ArrayList;

public interface IComment extends IWidget{
    IComment add(String... text);
    IComment finish(FormatUtil formatUtil);

    interface ICommentBuilder{
        ICommentBuilder setLong();
        IComment build();
    }
}
