package compile.interfaces;

import scannode.ScanNode;

public interface IParseItem {
    
    //void addTo(DATATYPE datatype, FIELD key, String val);
    void addTo(ScanNode node);
    //void setAttrib(DATATYPE datatype, FIELD key, String val);
    void setAttrib(ScanNode node);
    
}
