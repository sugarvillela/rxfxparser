package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.IParseItem;
import compile.basics.Keywords;
import compile.basics.RxlxReader;
import compile.parse.Base_ParseItem;
import toksource.ScanNodeSource;

import java.util.ArrayList;
import java.util.Map;

/** handle initialization from file
 *
 */
public class ListTableFileLoader extends RxlxReader implements IParseItem {
    private Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;
    private Factory_Node scanNodeFactory;
    private String currCategory;

    public ListTableFileLoader(ScanNodeSource fin, Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap){
        super(fin);
        this.listTableMap = listTableMap;
        scanNodeFactory = Factory_Node.getInstance();
    }

    @Override
    protected Base_ParseItem get(Factory_Node.ScanNode node){
        return new ListTable.ListTableNode(node, this.listTableMap);
    }

    @Override
    public void addTo(Factory_Node.ScanNode node) {
        listTableMap.get(node.h).get(currCategory).addTo(node);
    }

    @Override
    public void setAttrib(Factory_Node.ScanNode node) {
        currCategory = node.data;
        listTableMap.get(node.h).put(currCategory, this.get(node));
    }

    public void persist(String fileName){
        ArrayList<Factory_Node.ScanNode> scanNodes = new ArrayList<>();
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                ((ListTable.ListTableNode)inner.getValue()).populateScanNodes(scanNodes);
            }
        }
        if(!scanNodes.isEmpty()){
            nodeFactory.persist(fileName, scanNodes, "Defines lists, categories and items");
        }
    }
}
