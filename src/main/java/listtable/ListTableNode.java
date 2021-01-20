package listtable;

import compile.parse.Base_ParseItem;
import erlog.Erlog;
import langdef.Keywords;
import runstate.Glob;
import scannode.ScanNode;

import java.util.ArrayList;
import java.util.Map;

import static langdef.Keywords.CMD.*;
import static langdef.Keywords.FIELD.DEF_NAME;
import static langdef.Keywords.NULL_TEXT;

public class ListTableNode extends Base_ParseItem {
    private final ArrayList<String> list;
    private final Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> parentTable;//reference to same obj in surrounding class
    //private String category;

    public ListTableNode(ScanNode node, Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> parentTable) {
        super(node);
        // System.out.println("ListTableNode: " + node);
        this.parentTable = parentTable;
        list = new ArrayList<>();
    }

    @Override
    public void onPush() {
    }

    @Override
    public void onPop() {
        if (NULL_TEXT.equals(node.data)) {
            Erlog.get(this).set("Bad rxlx file");
        } else {
            parentTable.get(node.datatype).put(node.data, this);
        }
    }

    @Override
    public void addTo(ScanNode node) {
//            System.out.println("_____addTo_____");
//            System.out.println(datatype);
//            System.out.println(node.h);
//            System.out.println("_______________");
        if (this.node.datatype != node.datatype) {
            Erlog.get(this).set("Bad rxlx file");
        }
        list.add(node.data);
    }

    @Override
    public void setAttrib(ScanNode node) {
        //System.out.println("ListTableNode setAttrib: " + node.toString());
        switch (node.field) {
            case DEF_NAME: // = category
                this.node.data = node.data;
                Glob.LIST_TABLE.setFirstCategory(node.datatype, node.data);
                break;
            default:
                Erlog.get(this).set(String.format("Expected keyword %s", DEF_NAME));
        }
    }

    public boolean contains(String val) {
        return list.contains(val);
    }

    public int size() {
        return list.size();
    }

    public void populateScanNodes(ArrayList<ScanNode> scanNodes) {
        scanNodes.add(new ScanNode(node.lineCol, PUSH, node.datatype, null, null));
        scanNodes.add(new ScanNode(node.lineCol, SET_ATTRIB, node.datatype, DEF_NAME, getCategory()));

        for (String item : list) {
            //System.out.print("     item: " + item);
            scanNodes.add(new ScanNode(node.lineCol, ADD_TO, node.datatype, null, item));
        }
        scanNodes.add(new ScanNode(node.lineCol, POP, node.datatype, null, null));
    }

    public ArrayList<String> getList() {
        return list;
    }

    @Override
    public String toString() {
        return String.format(
                "ListSource = %s \t Category = %s \t Items = {%s} \t First = %s \t Last = %s",
                node.datatype.toString(), getCategory(), String.join(" ", list), getFirstField(), getLastField()
        );
    }

    @Override
    public void disp() {
        System.out.println(this.toString());
    }

    public String getCategory() {
        return node.data;
    }

    public String getFirstField() {
        if (list.isEmpty()) {
            Erlog.get(this).set("Empty List Table category");
        }
        return list.get(0);
    }

    public String getLastField() {
        if (list.isEmpty()) {
            Erlog.get(this).set("Empty List Table category");
        }
        return list.get(list.size() - 1);
    }
}
