package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import erlog.Erlog;
import interfaces.Killable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static compile.basics.Keywords.CMD.ADD_TO;
import static compile.basics.Keywords.CMD.SET_ATTRIB;
import static compile.basics.Keywords.DEFAULT_FIELD_FORMAT;
import static compile.basics.Keywords.FIELD.DEFAULT_FIELD;
import static compile.basics.Keywords.FIELD.SPECIAL_FIELD;

public class ListTableScanLoader implements Killable {
    private ListTable listTable;
    private final ArrayList<Factory_Node.ScanNode> nodes;

    private final Map <Keywords.DATATYPE, String> defaultCategory;
    private String defaultField, specialField;

    public ListTableScanLoader(ListTable listTable) {
        this.listTable = listTable;
        defaultCategory = new HashMap<>(8);
        nodes = new ArrayList<>();
    }

    public void setDefaultField(String defaultField){
        this.defaultField = defaultField;
    }

    public void setDefaultCategory(Keywords.DATATYPE datatype, String category){
        if(!defaultCategory.containsKey(datatype)){
            defaultCategory.put(datatype, category);
        }
    }

    public String getDefaultCategory(Keywords.DATATYPE datatype){
        if(!defaultCategory.containsKey(datatype)){
            String datatypeString = (datatype == null)? "NULL" : datatype.toString();
            Erlog.get(this).set(
                    "No default category/item is defined in list", datatypeString
            );
        }
        return defaultCategory.get(datatype);
    }

    public String getDefaultFieldString(Keywords.DATATYPE datatype){
        String category = getDefaultCategory(datatype);
        ListTable.ListTableNode node = listTable.getItemSearch().getListTableNode(datatype, category);
        return String.format(DEFAULT_FIELD_FORMAT, category, node.getDefaultField());
    }


    public void addNode(Factory_Node.ScanNode node){
        if(ADD_TO.equals(node.cmd)){
            specialField = node.data;   // last field added
        }
        System.out.println("readNode: specialField = "+specialField);
        nodes.add(node);
    }

    public void onPop(){
        for(Factory_Node.ScanNode node : nodes){
            if(SET_ATTRIB.equals(node.cmd) && DEFAULT_FIELD.equals(node.k)){
                if(defaultField == null){
                    Erlog.get(this).set(
                            "List is not defined", node.h.toString()
                    );
                }
                node.data = defaultField;
                break;
            }
        }
        for(Factory_Node.ScanNode node : nodes){
            if(SET_ATTRIB.equals(node.cmd) && SPECIAL_FIELD.equals(node.k)){
                if(specialField == null){
                    Erlog.get(this).set(
                            "List is not defined", node.h.toString()
                    );
                }
                node.data = specialField;
                break;
            }
        }
        listTable.getFileLoader().readList(nodes);
        nodes.clear();
        defaultField = null;
        specialField = null;
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
