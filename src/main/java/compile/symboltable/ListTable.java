package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import erlog.Erlog;
import toksource.ScanNodeSource;

import java.util.HashMap;
import java.util.Map;

import static compile.basics.Keywords.DEFAULT_FIELD_FORMAT;

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
        defaults = new HashMap<>(8);
        scanNodeFactory = Factory_Node.getInstance();
    }

    private Map <Keywords.DATATYPE, String> defaults;
    private final Factory_Node scanNodeFactory;

    @Override
    public void addTo(Keywords.DATATYPE datatype, Keywords.FIELD key, String val) {
        SymbolTableNode symbolTableNode = ((SymbolTableNode)symbolTable.get(datatype).get(currCategory));
        if(symbolTableNode.contains(val)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists in %s...%s definitions must be uniquely named",
                            val, currCategory, datatype.toString()
                    )
            );
        }
        else{
            symbolTableNode.addTo(datatype, null, val);
            setDefaultFieldString(datatype, currCategory, val); // add default field string for dataType
        }
    }

    @Override
    public void setAttrib(Keywords.DATATYPE datatype, Keywords.FIELD key, String val) {
        currCategory = val;
        if(symbolTable.get(datatype).containsKey(currCategory)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists...%s categories must be uniquely named",
                            currCategory, datatype.toString()
                    )
            );
        }
        else{
            symbolTable.get(datatype).put(
                    currCategory,
                    this.get(
                        scanNodeFactory.newScanNode(Keywords.CMD.SET_ATTRIB, datatype, key, currCategory)
                    )
            );
        }
    }

    public void setDefaultFieldString(Keywords.DATATYPE datatype, String category, String item){
        if(!defaults.containsKey(datatype)){
            //System.out.println(datatype+" setDefaultFieldString : " + String.format(DEFAULT_FIELD_FORMAT, category, item));
            defaults.put(datatype, String.format(DEFAULT_FIELD_FORMAT, category, item));
        }
    }

    public String getDefaultFieldString(Keywords.DATATYPE datatype){
        if(!defaults.containsKey(datatype)){
            Erlog.get(this).set(
                    "No default category/item is defined", datatype.toString()
            );
        }
        return defaults.get(datatype);
    }
}
