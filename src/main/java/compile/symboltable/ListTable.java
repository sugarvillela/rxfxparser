package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import erlog.Erlog;
import toksource.ScanNodeSource;

public class ListTable extends ListTable_RxlxReader {//
    private static ListTable instance;

    public static ListTable getInstance(){
        return instance;
    }

    public static void init(ScanNodeSource fin){
        instance = new ListTable(fin);
    }

    private ListTable(ScanNodeSource fin) {
        super(fin);
    }

    @Override
    public void addTo(Keywords.DATATYPE datatype, Keywords.FIELD key, String val) {
        SymbolTableNode symbolTableNode = ((SymbolTableNode)symbolTable.get(datatype).get(currName));
        if(symbolTableNode.contains(val)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists in %s...%s definitions must be uniquely named",
                            val, currName, datatype.toString()
                    )
            );
        }
        else{
            symbolTableNode.addTo(datatype, null, val);
        }
    }

    @Override
    public void setAttrib(Keywords.DATATYPE datatype, Keywords.FIELD key, String val) {
        currName = val;
        if(symbolTable.get(datatype).containsKey(val)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists...%s categories must be uniquely named",
                            currName, datatype.toString()
                    )
            );
        }
        else{
            symbolTable.get(datatype).put(
                    currName,
                    this.get(
                        new Factory_Node.ScanNode(
                            Erlog.getTextStatusReporter().readableStatus(), Keywords.CMD.SET_ATTRIB, datatype, key, val
                        )
                    )
            );
        }
    }
}
