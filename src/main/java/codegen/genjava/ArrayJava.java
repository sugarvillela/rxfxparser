package codegen.genjava;

import codegen.interfaces.IArray;
import codegen.interfaces.IWidget;
import codegen.interfaces.enums;
import codegen.ut.FormatUtil;
import erlog.DevErr;

import java.util.ArrayList;

import static codegen.interfaces.enums.SEMICOLON;

public class ArrayJava implements IArray {
    private final ArrayList<String> content;
    private enums.VISIBILITY visibility;
    private boolean static_;
    private boolean final_;
    private String type;
    private String name;
    private String size;
    private boolean isSplit;

    public ArrayJava() {
        content = new ArrayList<>();
    }

    @Override
    public IArray add(String... items) {//out[i] = oChar + strings[i] + cChar;
        if("String".equals(type)){
            char q = '"';
            for(String item : items){
                content.add(q + item + q);
            }
        }
        else{
            for(String item : items){
                content.add(item);
            }
        }
        return this;
    }

    @Override
    public IWidget finish(FormatUtil formatUtil) {
        if(type == null || name == null){
            DevErr.get(this).kill("Name and type are required");
        }
        else if(isSplit){
            if(content.isEmpty()){
                if(size == null){
                    this.unSizedField(formatUtil);   // int[] array;
                }
                else{
                    this.sizedField(formatUtil);     // array = new int[5];
                }
            }
            else{
                this.contentField(formatUtil);       // array = new int[]{1,2,3,4,5};
            }
        }
        else{
            if(content.isEmpty()){
                if(size == null){
                    DevErr.get(this).kill("Size or content required unless split definition");
                }
                else{
                    this.sizedLocal(formatUtil);     // int array[] = new int[5];
                }
            }
            else{
                this.contentLocal(formatUtil);       // int array[] = new int[]{1,2,3,4,5};
            }
        }
        return this;
    }
    private String prefix(){
        ArrayList<String> header = new ArrayList<>();
        if(visibility != null){
            header.add(visibility.toString());
        }
        if(static_){
            header.add("static");
        }
        if(final_){
            header.add("final");
        }
        return (header.isEmpty())? "" : String.join(" ", header) + " ";
    }

    private void unSizedField(FormatUtil formatUtil){   // int[] array;
        formatUtil.addLine(
                prefix() + String.format("%s[] %s%s", type, name, SEMICOLON)
        );
    }

    private void sizedField(FormatUtil formatUtil){     // array = new int[5];
        formatUtil.addLine(
                prefix() + String.format("%s = new %s[%s]%s", name, type, size, SEMICOLON)
        );
    }

    private void contentField(FormatUtil formatUtil){   // array = new int[]{1,2,3,4,5};
        formatUtil.addLine(
                prefix() + String.format("%s = new %s[] {", name, type)
        );
        this.addContent(formatUtil);
        formatUtil.addLineSegment("}" + SEMICOLON);
    }

    private void sizedLocal(FormatUtil formatUtil){     // int array[] = new int[5];
        formatUtil.addLine(
                prefix() + String.format("%s[] %s = new %s[%s]%s", type, name, type, size, SEMICOLON)
        );
    }

    private void contentLocal(FormatUtil formatUtil){   // int array[] = new int[]{1,2,3,4,5};
        formatUtil.addLine(
                prefix() + String.format("%s[] %s = new %s[] {", type, name, type)
        );
        this.addContent(formatUtil);
        formatUtil.addLineSegment("}" + SEMICOLON);
    }

    private void addContent(FormatUtil formatUtil){
        formatUtil.inc();
        for(String item : content){
            formatUtil.accumulate(item + ", ");
        }
        formatUtil.finishAccumulate();
        formatUtil.dec();
    }

    public static class ArrayBuilder implements IArrayBuilder{
        private final ArrayJava built;

        public ArrayBuilder() {
            built = new ArrayJava();
        }

        @Override
        public IArrayBuilder setVisibility(enums.VISIBILITY visibility) {
            built.visibility = visibility;
            return this;
        }

        @Override
        public IArrayBuilder setStatic() {
            built.static_ = true;
            return this;
        }

        @Override
        public IArrayBuilder setFinal() {
            built.final_ = true;
            return this;
        }

        @Override
        public IArrayBuilder setType(String type) {
            built.type = type;
            return this;
        }

        @Override
        public IArrayBuilder setName(String name) {
            built.name = name;
            return this;
        }

        @Override
        public IArrayBuilder setSize(String size) {
            built.size = size;
            return this;
        }

        @Override
        public IArrayBuilder setSplit() {
            built.isSplit = true;
            return this;
        }

        @Override
        public IArray build() {
            return built;
        }
    }
}
