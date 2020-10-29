package codegen.interfaces;

import codegen.ut.FormatUtil;

import java.util.ArrayList;

public interface IBool extends IWidget {
    enum CONNECTOR{
        AND_, OR_
    }

    public IBool addComment(IComment comment);
    public IBool addContent(IWidget widget);
    public IBool addNext(IBool next, CONNECTOR connector);
    public IBool addNest(IBool nest, CONNECTOR connector);
    public IBool finishCurrent();

    public interface IBoolBuilder{
        public IBool build();
    }
}
