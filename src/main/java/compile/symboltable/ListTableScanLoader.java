package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import erlog.Erlog;
import interfaces.Killable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static compile.basics.Keywords.DEFAULT_FIELD_FORMAT;

public class ListTableScanLoader {

    public ListTableScanLoader() {
        defaults = new HashMap<>(8);
    }

    private Map <Keywords.DATATYPE, String> defaults;

    public final void readList(ArrayList<Factory_Node.ScanNode> list){
        ListTable.getInstance().getFileLoader().readList(list);
    }

    public void setDefaultFieldString(Keywords.DATATYPE datatype, String category, String item){
        if(!defaults.containsKey(datatype)){
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

    public void disp(){
        System.out.println("Display Scan Loader (Defaults)");
        for (Map.Entry<Keywords.DATATYPE, String> outer : defaults.entrySet()) {
            System.out.println(outer.getKey() + ": " + outer.getValue());
        }
        System.out.println("===================");
    }
}
