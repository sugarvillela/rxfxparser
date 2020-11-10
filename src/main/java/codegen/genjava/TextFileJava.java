package codegen.genjava;

import codegen.interfaces.ITextFile;
import codegen.interfaces.IWidget;
import codegen.ut.FormatUtil;
import toksource.Base_TextSource;
import toksource.TextSource_file;

public class TextFileJava implements ITextFile {
    private Base_TextSource textSource;

    @Override
    public IWidget finish(FormatUtil formatUtil) {
        textSource.rewind();
        while(textSource.hasNext()){
            formatUtil.addLine(textSource.next());
        }
        return this;
    }

    public static class TextFileBuilder implements ITextFileBuilder {
        private TextFileJava built;

        public TextFileBuilder() {
            built = new TextFileJava();
        }

        @Override
        public ITextFileBuilder setFile(String fileName) {
            built.textSource = new TextSource_file(fileName);
            return this;
        }

        @Override
        public ITextFileBuilder setFile(Base_TextSource textSource) {
            built.textSource = textSource;
            return this;
        }

        @Override
        public TextFileJava build() {
            return built;
        }
    }{}
}
