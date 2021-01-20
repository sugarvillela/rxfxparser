package listtable;

import runstate.Glob;
import scannode.ScanNode;
import compile.interfaces.IParseItem;
import langdef.Keywords;
import compile.implstack.RxlxReader;
import compile.parse.Base_ParseItem;
import erlog.DevErr;
import toksource.ScanNodeSource;

import java.util.ArrayList;
import java.util.Map;

/** handle initialization from file
 *
 */
public class ListTableFileLoader extends RxlxReader implements IParseItem {
    private Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;
    private final Map <Keywords.DATATYPE, String> firstCategory;
    private String currCategory;

    public ListTableFileLoader(
            ScanNodeSource fin,
            Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
            Map <Keywords.DATATYPE, String> firstCategory
    ){
        super(fin);
        this.listTableMap = listTableMap;
        this.firstCategory = firstCategory;
    }

    @Override
    protected Base_ParseItem get(ScanNode node){
        return new ListTableNode(node, this.listTableMap);
    }

    @Override
    public void addTo(ScanNode node) {
        DevErr.get(this).kill("found usage: addTo: ", node.toString());
//        listTableMap.get(node.datatype).get(currCategory).addTo(node);
    }

    @Override
    public void setAttrib(ScanNode node) {
        DevErr.get(this).kill("found usage: setAttrib: ", node.toString());
//        listTableMap.get(node.datatype).put(node.data, this.get(node));// Add new node to map
//        currCategory = node.data;                       // Use this category for subsequent adds to that node
    }

    public void persist(String fileName){
        ArrayList<ScanNode> scanNodes = new ArrayList<>();
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                ((ListTableNode)inner.getValue()).populateScanNodes(scanNodes);
            }
        }
        if(!scanNodes.isEmpty()){
            Glob.SCAN_NODE_FACTORY.persist(fileName, scanNodes, "Defines lists, categories and items");
        }
    }
}
