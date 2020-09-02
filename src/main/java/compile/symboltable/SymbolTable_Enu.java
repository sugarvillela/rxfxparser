package compile.symboltable;

import commons.Commons;
import compile.basics.CompileInitializer;
import compile.basics.Factory_Node;
import compile.basics.Keywords;
import compile.parse.Base_ParseItem;
import erlog.Erlog;
import toksource.ScanNodeSource;
import toksource.TextSource_file;

import java.util.ArrayList;
import java.util.Map;

import static compile.basics.Keywords.CMD.*;
import static compile.basics.Keywords.CMD.POP;
import static compile.basics.Keywords.HANDLER.*;
import static compile.basics.Keywords.HANDLER.ENUD;
import static compile.basics.Keywords.INTERIM_FILE_EXTENSION;
import static compile.basics.Keywords.KWORD.DEF_NAME;

public class SymbolTable_Enu extends RxlxReader_Enu{//
    private static SymbolTable_Enu instance;

    public static SymbolTable_Enu getInstance(){
        return (instance == null)?
                (instance = new SymbolTable_Enu(
                    new ScanNodeSource(
                        new TextSource_file(
                            Keywords.fileName_symbolTableEnu()
                        )
                    )
                ))
                : instance;
    }

    private SymbolTable_Enu(ScanNodeSource fin) {
        super(fin);
    }
    @Override
    public void addTo(Keywords.HANDLER handler, Keywords.KWORD key, String val) {
        SymbolTableNode symbolTableNode = ((SymbolTableNode)symbolTable.get(currHandler).get(currName));
        if(symbolTableNode.contains(val)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists in %s...%s definitions must be uniquely named",
                            val, currName, currHandler.toString()
                    )
            );
        }
        else{
            symbolTableNode.addTo(currHandler, null, val);
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
                            new Factory_Node.ScanNode(null,null,null,null,null)
                    )
            );
        }
    }

    @Override
    public void onQuit(){
        ArrayList<Factory_Node.ScanNode> scanNodes = new ArrayList<>();
        if(!symbolTable.get(ENUB).isEmpty()){
            populateScanNodes(symbolTable.get(ENUB), scanNodes);
        }
        if(!symbolTable.get(ENUD).isEmpty()){
            populateScanNodes(symbolTable.get(ENUD), scanNodes);
        }
        Commons.disp(scanNodes, "\nscanNodes");
    }
    private void populateScanNodes(
            Map<String, Base_ParseItem> subTable,
            ArrayList<Factory_Node.ScanNode> scanNodes
    ){
        for (Map.Entry<String, Base_ParseItem> entry : subTable.entrySet()) {
            System.out.println("populateScanNodes name: " + entry.getKey());
            ((SymbolTableNode)entry.getValue()).populateScanNodes(scanNodes);
        }
    }
}
