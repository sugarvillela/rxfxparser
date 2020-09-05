package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.IParseItem;
import compile.basics.Keywords;
import compile.basics.RxlxReader;
import compile.parse.Base_ParseItem;
import toksource.ScanNodeSource;

public class RxlxReader_All  extends RxlxReader implements IParseItem {
    public RxlxReader_All(ScanNodeSource fin) {
        super(fin);
    }
    
    @Override
    protected Base_ParseItem get(Factory_Node.ScanNode node) {
        return null;
    }

    @Override
    public void addTo(Keywords.HANDLER handler, Keywords.KWORD key, String val) {

    }

    @Override
    public void setAttrib(Keywords.HANDLER handler, Keywords.KWORD key, String val) {

    }


}
