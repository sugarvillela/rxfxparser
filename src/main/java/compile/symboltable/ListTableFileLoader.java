package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.IParseItem;
import compile.basics.Keywords;
import compile.basics.RxlxReader;
import compile.parse.Base_ParseItem;
import erlog.Erlog;
import interfaces.Killable;
import toksource.ScanNodeSource;

import java.util.ArrayList;
import java.util.Map;

/** handle initialization from file
 *
 */
public class ListTableFileLoader  extends RxlxReader implements IParseItem, Killable {
    private ListTable listTable;
    private Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;
    private Factory_Node scanNodeFactory;
    private String currCategory;

    public ListTableFileLoader(ScanNodeSource fin, ListTable listTable){
        super(fin);
        this.listTable = listTable;
        this.listTableMap = listTable.getListTableMap();
        scanNodeFactory = Factory_Node.getInstance();
    }

    @Override
    protected Base_ParseItem get(Factory_Node.ScanNode node){
        return new ListTable.ListTableNode(node, this.listTableMap);
    }

    @Override
    public void addTo(Factory_Node.ScanNode node) {
        ListTable.ListTableNode listTableNode = ((ListTable.ListTableNode) listTableMap.get(node.h).get(currCategory));
        if(listTableNode.contains(node.data)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists in %s...%s definitions must be uniquely named",
                            node.data, currCategory, node.h.toString()
                    )
            );
        }
        else{
            listTableNode.addTo(node);
            //setDefaultFieldString(datatype, currCategory, val); // add default field string for dataType
        }
    }

    @Override
    public void setAttrib(Factory_Node.ScanNode node) {
        currCategory = node.data;
        if(listTableMap.get(node.h).containsKey(currCategory)){
            Erlog.get(this).set(
                    String.format(
                            "%s already exists...%s categories must be uniquely named",
                            currCategory, node.h.toString()
                    )
            );
        }
        else{
            listTableMap.get(node.h).put(currCategory, this.get(node));
        }
    }

    public void persist(){
        ArrayList<Factory_Node.ScanNode> scanNodes = new ArrayList<>();
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                ((ListTable.ListTableNode)inner.getValue()).populateScanNodes(scanNodes);
            }
        }
        if(!scanNodes.isEmpty()){
            nodeFactory.persist(listTable.listTableFileName(), scanNodes, "Defines lists, categories and items");
        }
    }

    @Override
    public void kill() {
        listTable = null;
    }
}
