package codegen.genjava;

import codegen.interfaces.ISimpleText;
import codegen.ut.FormatUtil;

import java.util.ArrayList;

public class SimpleText implements ISimpleText {
    private final ArrayList<String> content;
    private boolean indent;

    public SimpleText() {
        content = new ArrayList<>();
    }

    @Override
    public ISimpleText add(String... text) {// exactly as-is
        for(String t : text){
            content.add(t);
        }
        return this;
    }

    @Override
    public ISimpleText finish(FormatUtil formatUtil) {
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

    public static class SimpleTextBuilder implements ISimpleTextBuilder {
        private SimpleText built;

        public SimpleTextBuilder() {
            built = new SimpleText();
        }

        @Override
        public ISimpleTextBuilder setIndent() {
            built.indent = true;
            return this;
        }

        @Override
        public ISimpleText build() {
            return built;
        }
    }
}
