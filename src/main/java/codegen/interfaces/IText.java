package codegen.interfaces;

public interface IText extends IWidget{
    IText add(String... text);

    interface ITextBuilder {
        ITextBuilder setIndent();
        IText build();
    }
}
