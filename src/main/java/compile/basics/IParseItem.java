package compile.basics;

import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.KWORD;

public interface IParseItem {
    
    void addTo(HANDLER handler, KWORD key, String val);
    void setAttrib(HANDLER handler, KWORD key, String val);
    
}
