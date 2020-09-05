package compile.symboltable;

import compile.basics.Factory_Node;
import compile.basics.Factory_Node.ScanNode;
import compile.basics.IParseItem;
import compile.basics.Keywords;
import compile.basics.RxlxReader;
import compile.parse.Base_ParseItem;
import erlog.Erlog;
import toksource.ScanNodeSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.CMD.*;
import static compile.basics.Keywords.CMD.POP;
import static compile.basics.Keywords.HANDLER.ENUB;
import static compile.basics.Keywords.HANDLER.ENUD;
import static compile.basics.Keywords.KWORD.DEF_NAME;

/** Abstract class to handler SymbolTable_Enu initialization from file
 *
 */
public abstract class RxlxReader_Enu extends RxlxReader implements IParseItem {
    Map <Keywords.HANDLER, Map<String, Base_ParseItem>> symbolTable;
    //protected Keywords.HANDLER currHandler;
    protected String currName;

    public RxlxReader_Enu(ScanNodeSource fin){
        super(fin);
        symbolTable = new HashMap<>(3);
        symbolTable.put(ENUB, new HashMap<>(8));
        symbolTable.put(ENUD, new HashMap<>(8));
    }

    public boolean contains(Keywords.HANDLER handler, String val){
        return symbolTable.get(handler).containsKey(val);
    }
    @Override
    protected Base_ParseItem get(ScanNode node){
        return new SymbolTableNode(node, this.symbolTable);
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
        //Commons.disp(scanNodes, "\nscanNodes");
        if(!scanNodes.isEmpty()){
            Factory_Node.persist(Keywords.fileName_symbolTableEnu(), scanNodes, "Lists ENUB, ENUD group names and items");
        }
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


    public static class SymbolTableNode extends Base_ParseItem{
        private final ArrayList<String> list;
        private final Map <Keywords.HANDLER, Map<String, Base_ParseItem>> parentTable;

        public SymbolTableNode(ScanNode node, Map <Keywords.HANDLER, Map<String, Base_ParseItem>> parentTable) {
            super(node);
            this.parentTable = parentTable;
            list = new ArrayList<>();
        }

        @Override
        public void onPush() {}

        @Override
        public void onPop() {
            if(NULL_TEXT.equals(node.data)){
                Erlog.get(this).set("Bad rxlx file");
            }
            else{
                parentTable.get(node.h).put(node.data, this);
            }
        }

        @Override
        public void addTo(Keywords.HANDLER handler, Keywords.KWORD key, String val) {
//            System.out.println("_____addTo_____");
//            System.out.println(handler);
//            System.out.println(node.h);
//            System.out.println("_______________");
            if(handler == node.h){
                list.add(val);
            }
            else{
                Erlog.get(this).set("Bad rxlx file");
            }
        }

        @Override
        public void setAttrib(Keywords.HANDLER handler, Keywords.KWORD key, String val) {
            if(key == Keywords.KWORD.DEF_NAME && handler == node.h){
                node.data = val;
            }
            else{
                Erlog.get(this).set("Bad rxlx file");
            }
        }
        public boolean contains(String val){
            return list.contains(val);
        }
        public void populateScanNodes(ArrayList<Factory_Node.ScanNode> scanNodes){
            scanNodes.add(new Factory_Node.ScanNode(node.lineCol, PUSH, node.h, null, null));
            scanNodes.add(new Factory_Node.ScanNode(node.lineCol, SET_ATTRIB, node.h, DEF_NAME, node.data));

            for(String item : list){
                //System.out.print("     item: " + item);
                scanNodes.add(new Factory_Node.ScanNode(node.lineCol, ADD_TO, node.h, null, item));
            }
            scanNodes.add(new Factory_Node.ScanNode(node.lineCol, POP, node.h, null, null));
        }
        @Override
        public String toString(){
            return String.format("%s: %s: %s", node.data, node.h.toString(), String.join(" ", list));
        }
        @Override
        public void disp(){
            System.out.println(this.toString());
        }
    }
}
