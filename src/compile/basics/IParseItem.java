package compile.basics;

import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.KWORD;

public interface IParseItem {
    
    public void addTo(HANDLER handler, KWORD key, String val);
    public void setAttrib(KWORD key, String val);
    
}
