package compile.basics;

import static compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.FIELD;

public interface IParseItem {
    
    void addTo(HANDLER handler, FIELD key, String val);
    void setAttrib(HANDLER handler, FIELD key, String val);
    
}
