package compile.symboltable;

import compile.basics.*;
import compile.basics.Factory_Node.ScanNode;
import compile.parse.Base_ParseItem;
import erlog.Erlog;
import toksource.ScanNodeSource;
import toksource.TextSource_file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static compile.basics.Keywords.DEFAULT_FIELD_FORMAT;
import static compile.basics.Keywords.FIELD.*;
import static compile.basics.Keywords.NULL_TEXT;
import static compile.basics.Keywords.CMD.*;
import static compile.basics.Keywords.CMD.POP;
import static compile.basics.Keywords.DATATYPE.*;
import static java.lang.Math.max;

public class ListTable {
    private static ListTable instance;

    public static ListTable getInstance(){
        return (instance == null)? init() : instance;
    }

    public static void killInstance(){
        if(instance != null){
            instance.numGen.kill();
            instance.scanLoader.kill();
            instance = null;
        }
    }

    public static ListTable init(){
        instance = new ListTable();
        CompileInitializer compileInitializer = CompileInitializer.getInstance();
        if(compileInitializer.isNewEnumSet()){
            instance.fileLoader = new ListTableFileLoader(null, instance.listTableMap);
        }
        else{
            String fName = instance.listTableFileName();
            instance.fileLoader = new ListTableFileLoader(
                    new ScanNodeSource(
                            new TextSource_file(fName)
                    ),
                    instance.listTableMap
            );
            instance.fileLoader.onCreate();
        }
        instance.scanLoader  = new ListTableScanLoader(instance);
        instance.itemSearch = new ListTableItemSearch(instance.listTableMap);
        instance.typeCount = new ListTableTypeCount(instance.listTableMap);
        instance.numGen = new ListTableNumGen(instance, instance.listTableMap);
        return instance;
    }

    public static boolean isInitialized(){
        return instance != null;
    }

    private final Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;// don't let this out of ListTable class family
    private ListTableFileLoader fileLoader;
    private ListTableScanLoader scanLoader;
    private ListTableItemSearch itemSearch;
    private ListTableTypeCount typeCount;
    private ListTableNumGen numGen;

    public ListTable(){
        listTableMap = new HashMap<>(8);
        listTableMap.put(LIST_BOOLEAN,  new HashMap<>(8));
        listTableMap.put(LIST_DISCRETE, new HashMap<>(8));
        listTableMap.put(LIST_NUMBER,   new HashMap<>(8));
        listTableMap.put(LIST_STRING,   new HashMap<>(8));
        listTableMap.put(LIST_SCOPES,   new HashMap<>(8));
    }

    public ListTableScanLoader getScanLoader(){
        return scanLoader;
    }
    public ListTableFileLoader getFileLoader(){
        return fileLoader;
    }
    public ListTableItemSearch getItemSearch(){
        return itemSearch;
    }
    public ListTableTypeCount  getTypeCount(){
        return typeCount;
    }
    public ListTableNumGen     getNumGen(){
        return numGen;
    }

    public String listTableFileName(){
        return String.format(
                "%s_%s%s",
                CompileInitializer.getInstance().getInName(),
                this.getClass().getSimpleName(),
                Keywords.INTERIM_FILE_EXTENSION
        );
    }

    public void persist(){
        fileLoader.persist(listTableFileName());
    }

    public void disp(){
        System.out.println("Display List Table");
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            System.out.println();
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                System.out.println(inner.getKey() + ": Count = " + ((ListTableNode)inner.getValue()).size());
                ((ListTableNode)inner.getValue()).disp();
            }
        }
        System.out.println("===================");
    }

    public static class ListTableNode extends Base_ParseItem{
        private final ArrayList<String> list;
        private final Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> parentTable;//reference to same obj in surrounding class
        private String category, defaultField,  specialField;

        public ListTableNode(ScanNode node, Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> parentTable) {
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
        public void addTo(Factory_Node.ScanNode node) {
//            System.out.println("_____addTo_____");
//            System.out.println(datatype);
//            System.out.println(node.h);
//            System.out.println("_______________");
            if(this.node.h == node.h){
                list.add(node.data);
            }
            else{
                Erlog.get(this).set("Bad rxlx file");
            }
        }

        @Override
        public void setAttrib(Factory_Node.ScanNode node) {
            switch(node.k){
                case DEF_NAME:
                    this.node.data = category = node.data;
                    break;
                case DEFAULT_FIELD:
                    defaultField = node.data;
                    break;
                case SPECIAL_FIELD:
                    specialField = node.data;
                    break;
                default:
                    Erlog.get(this).set(String.format("Expected keyword %s, %s or %s", DEF_NAME, DEFAULT_FIELD, SPECIAL_FIELD));
            }
        }

        public boolean contains(String val){
            return list.contains(val);
        }

        public int size(){
            return list.size();
        }

        public void populateScanNodes(ArrayList<Factory_Node.ScanNode> scanNodes){
            scanNodes.add(new Factory_Node.ScanNode(node.lineCol, PUSH, node.h, null, null));
            scanNodes.add(new Factory_Node.ScanNode(node.lineCol, SET_ATTRIB, node.h, DEF_NAME, category));
            scanNodes.add(new Factory_Node.ScanNode(node.lineCol, SET_ATTRIB, node.h, DEFAULT_FIELD, defaultField));
            scanNodes.add(new Factory_Node.ScanNode(node.lineCol, SET_ATTRIB, node.h, SPECIAL_FIELD, specialField));

            for(String item : list){
                //System.out.print("     item: " + item);
                scanNodes.add(new Factory_Node.ScanNode(node.lineCol, ADD_TO, node.h, null, item));
            }
            scanNodes.add(new Factory_Node.ScanNode(node.lineCol, POP, node.h, null, null));
        }

        public ArrayList<String> getList(){
            return list;
        }

        @Override
        public String toString(){
            return String.format(
                "ListSource = %s \t Category = %s \t Items = {%s} \t Default = %s \t Special = %s",
                node.h.toString(), category,  String.join(" ", list), defaultField, specialField
            );
        }

        @Override
        public void disp(){
            System.out.println(this.toString());
        }

        public String getCategory() {
            return category;
        }

        public String getDefaultField() {
            return defaultField;
        }

        public String getSpecialField() {
            return specialField;
        }
    }
}
