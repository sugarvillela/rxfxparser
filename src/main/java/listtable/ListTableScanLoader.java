package listtable;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import interfaces.Killable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListTableScanLoader implements Killable {
    private ListTable listTable;
    private final ArrayList<Factory_Node.ScanNode> nodes;

    private final Map <Keywords.DATATYPE, String> defaultCategory;

    public ListTableScanLoader(ListTable listTable) {
        this.listTable = listTable;
        defaultCategory = new HashMap<>(8);
        nodes = new ArrayList<>();
    }

    public void addNode(Factory_Node.ScanNode node){
        nodes.add(node);
    }

    public void onPop(){
        //commons.Commons.disp(nodes, "ListTableScanLoader nodes");
        listTable.getFileLoader().readList(nodes);
        //listTable.disp();
        nodes.clear();
    }

    public void clear(){
        nodes.clear();
    }

    public void disp(){
        System.out.println("Display Scan Loader (Default Categories)");
        for (Map.Entry<Keywords.DATATYPE, String> outer : defaultCategory.entrySet()) {
            System.out.println(outer.getKey() + ": " + outer.getValue());
        }
        System.out.println("===================");
    }

    @Override
    public void kill() {
        listTable = null;
    }
}
