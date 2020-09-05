package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import erlog.Erlog;
import toksource.ScanNodeSource;

public class SymbolTable_Enu extends RxlxReader_Enu{//
    private static SymbolTable_Enu instance;

    public static SymbolTable_Enu getInstance(){
        return instance;
    }

    public static void init(ScanNodeSource fin){
        instance = new SymbolTable_Enu(fin);
    }

    private SymbolTable_Enu(ScanNodeSource fin) {
        super(fin);
    }

    @Override
    public void addTo(Keywords.HANDLER handler, Keywords.KWORD key, String val) {
        SymbolTableNode symbolTableNode = ((SymbolTableNode)symbolTable.get(handler).get(currName));
        if(symbolTableNode.contains(val)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists in %s...%s definitions must be uniquely named",
                            val, currName, handler.toString()
                    )
            );
        }
        else{
            symbolTableNode.addTo(handler, null, val);
        }
    }

    @Override
    public void setAttrib(Keywords.HANDLER handler, Keywords.KWORD key, String val) {
        currName = val;
        if(symbolTable.get(handler).containsKey(val)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists...%s categories must be uniquely named",
                            currName, handler.toString()
                    )
            );
        }
        else{
            symbolTable.get(handler).put(
                    currName,
                    this.get(
                        new Factory_Node.ScanNode(
                            er.getTextStatusReporter().readableStatus(), Keywords.CMD.SET_ATTRIB, handler, key, val
                        )
                    )
            );
        }
    }
}
