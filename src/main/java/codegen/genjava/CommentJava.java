package codegen.genjava;

import codegen.interfaces.IComment;
import codegen.ut.FormatUtil;

import java.util.ArrayList;

import static codegen.interfaces.enums.*;

public class CommentJava implements IComment {
    private final ArrayList<String> content;
    private boolean longComment;

    public CommentJava() {
        content = new ArrayList<>();
    }

    @Override
    public IComment add(String... text) {
        for(String t : text){
            this.content.add(t);
        }
        return this;
    }

    @Override
    public IComment finish(FormatUtil formatUtil) {
        if(longComment){
            addLongComment(formatUtil);
        }
        else{
            for(String text : content){
                addShortComment(text, formatUtil);
            }
        }
        return this;
    }

    private void addLongComment(FormatUtil formatUtil){
        formatUtil.addLineSegment(COMMENT_OPEN);
        formatUtil.inc();
        for(String text : content){
            formatUtil.addLine(text);
        }
        formatUtil.dec();
        formatUtil.addLineSegment(COMMENT_CLOSE);
    }

    private void addShortComment(String text, FormatUtil formatUtil){
        while((text = formatUtil.addLineSegment(COMMENT_SHORT + text)) != null){}
    }

    @Override
    public String toString(){
        return String.join("\n// ", content);
    }
    public static class CommentBuilder implements ICommentBuilder{
        private final CommentJava built;

        public CommentBuilder() {
            built = new CommentJava();
        }

        @Override
        public ICommentBuilder setLong() {
            built.longComment = true;
            return this;
        }

        @Override
        public IComment build() {
            return built;
        }
    }
}
