package listtable;

import compile.parse.Base_ParseItem;
import erlog.Erlog;
import langdef.Keywords;
import runstate.RunState;
import toksource.ScanNodeSource;
import toksource.TextSource_file;

import java.util.HashMap;
import java.util.Map;

import static langdef.Keywords.DATATYPE.*;
import static java.lang.Math.max;

/** A bit of temporal coupling regarding the calling of initLists().
 *    If any lists are defined in the source code, initLists() is called
 *      at the first Strategies.OnPushList call.
 *    If no lists are defined, initLists() is called just before writing ListTable to file
 *  This is to allow setting of newListSet true or false in the source code.
 */
public class ListTable {
    private static ListTable instance;

    public static ListTable init(){
        return (instance == null)? (instance = new ListTable()) : instance;
    }

    public ListTable initLists(){
        //System.out.println("ListTable. initForNewListSet");
        //Erlog.get("ListTable").set("Happy stop");
        RunState runState = RunState.getInstance();
        if(runState.isNewListSet()){
            //System.out.println("ListTable runState.isNewListSet = true");
            instance.fileLoader = new ListTableFileLoader(null, instance.listTableMap, instance.firstCategory);
        }
        else{
            //System.out.println("ListTable runState.isNewListSet = false");
            String fName = instance.listTableFileName();
            //System.out.println("ListTable fileName = " + fName);
            instance.fileLoader = new ListTableFileLoader(
                new ScanNodeSource(new TextSource_file(fName)),
                instance.listTableMap,
                instance.firstCategory
            );
            instance.fileLoader.readFile();
        }
        instance.scanLoader  = new ListTableScanLoader(instance);
        instance.itemSearch = new ListTableItemSearch(instance.listTableMap);
        instance.typeCount = new ListTableTypeCount(instance.listTableMap);
        instance.numGen = new ListTableNumGen(instance, instance.listTableMap);
        initialized = true;
        return instance;
    }

    public boolean isInitialized(){
        return initialized;
    }

    private final Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;// don't let this out of ListTable class family
    private final Map <Keywords.DATATYPE, String> firstCategory;
    private ListTableFileLoader fileLoader;
    private ListTableScanLoader scanLoader;
    private ListTableItemSearch itemSearch;
    private ListTableTypeCount typeCount;
    private ListTableNumGen numGen;
    private boolean initialized;

    public ListTable(){
        listTableMap = new HashMap<>(8);
        listTableMap.put(LIST_BOOLEAN,  new HashMap<>(8));
        listTableMap.put(LIST_DISCRETE, new HashMap<>(8));
        listTableMap.put(LIST_VOTE,     new HashMap<>(8));
        listTableMap.put(LIST_NUMBER,   new HashMap<>(8));
        listTableMap.put(LIST_STRING,   new HashMap<>(8));
        listTableMap.put(LIST_SCOPES,   new HashMap<>(8));
        firstCategory = new HashMap<>(8);
        initialized = false;
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
    public ListTableTypeCount  getTypeCount (){
        return typeCount;
    }
    public ListTableNumGen     getNumGen    (){
        return numGen;
    }

    public String listTableFileName(){
        return String.format(
                "%s_%s%s",
                RunState.getInstance().getInName(),
                this.getClass().getSimpleName(),
                Keywords.INTERIM_FILE_EXTENSION
        );
    }

    public void setFirstCategory(Keywords.DATATYPE datatype, String category){
        if(!firstCategory.containsKey(datatype)){
            firstCategory.put(datatype, category);
        }
    }
    public String getFirstCategory(Keywords.DATATYPE datatype){
        if(!firstCategory.containsKey(datatype)){
            String datatypeString = (datatype == null)? "NULL" : datatype.toString();
            Erlog.get(this).set(
                    "No category/item is defined in list", datatypeString
            );
        }
        return firstCategory.get(datatype);
    }

    public void persist(){
        if(!initialized){
            this.initLists();
            fileLoader.persist(listTableFileName());
        }

    }

    public void disp(){
        System.out.println("Display List Table");
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            System.out.println();
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                System.out.println(inner.getKey() + ": Count = " + ((ListTableNode)inner.getValue()).size());
                (inner.getValue()).disp();
            }
        }
        System.out.println("===================");
    }
}
