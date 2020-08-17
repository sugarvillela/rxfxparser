package compile.scan.ut;

import java.util.ArrayList;

public class LineBuffer {
    private final ArrayList<String> buffer;
    
    public LineBuffer(){
        buffer = new ArrayList<>();
    }
    public void add(String text){
        buffer.add(text);
    }
    public boolean isEmpty(){
        return buffer.isEmpty();
    }
    public final void clear(){
        buffer.clear();
    }
    public String dump(){
        String joined = String.join(" ", buffer);
        buffer.clear();
        return joined;
    }
}
