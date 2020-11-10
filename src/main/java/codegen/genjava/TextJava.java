package codegen.genjava;

import codegen.interfaces.IText;
import codegen.ut.FormatUtil;

import java.util.ArrayList;

public class TextJava implements IText {
    private final ArrayList<String> content;
    private boolean indent;

    public TextJava() {
        content = new ArrayList<>();
    }

    public static IText set(String... text){
        return new TextBuilder().build().add(text);
    }

    @Override
    public IText add(String... text) {// exactly as-is
        for(String t : text){
            content.add(t);
        }
        return this;
    }

    @Override
    public IText finish(FormatUtil formatUtil) {
        if(indent){
            formatUtil.inc();
            for(String text : content){
                formatUtil.addLine(text);
            }
            formatUtil.dec();
        }
        else{
            for(String text : content){
                formatUtil.addLine(text);
            }
        }
        return this;
    }

    @Override
    public String toString(){
        return String.join(" ", content);
    }

    public static class TextBuilder implements ITextBuilder {
        private final TextJava built;

        public TextBuilder() {
            built = new TextJava();
        }

        @Override
        public ITextBuilder setIndent() {
            built.indent = true;
            return this;
        }

        @Override
        public IText build() {
            return built;
        }
    }
}
