package compile.basics;

import static compile.basics.Keywords.DATATYPE;
import static compile.basics.Keywords.FIELD;

public interface IParseItem {
    
    void addTo(DATATYPE datatype, FIELD key, String val);
    void setAttrib(DATATYPE datatype, FIELD key, String val);
    
}
