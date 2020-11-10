package codegen.interfaces;

import toksource.Base_TextSource;

public interface ITextFile extends IWidget{
    public interface ITextFileBuilder{
        ITextFileBuilder setFile(String fileName);
        ITextFileBuilder setFile(Base_TextSource textSource);
        ITextFile build();
    }
}
