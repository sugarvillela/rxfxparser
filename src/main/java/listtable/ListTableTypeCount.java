package listtable;

import compile.basics.Keywords;
import compile.parse.Base_ParseItem;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;

public class ListTableTypeCount {
    protected final Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;
    protected final Map <Keywords.DATATYPE, Integer> listSourceCounts;
    protected final Map <Keywords.DATATYPE, Integer> listSourceMaxes;

    public ListTableTypeCount(Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap) {
        this.listTableMap = listTableMap;
        listSourceCounts = new HashMap<>(8);
        listSourceMaxes = new HashMap<>(8);
    }

    public void initCounts(){
        initFieldCounts();
        initMaxCounts();
    }

    private void initFieldCounts(){
        int count = 0;
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            count = 0;
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                count += ((ListTable.ListTableNode)inner.getValue()).size();
            }
            listSourceCounts.put(outer.getKey(), count);
        }
    }

    private void initMaxCounts(){
        int m;
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            m = 0;
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                m = max(m, ((ListTable.ListTableNode)inner.getValue()).size());
            }
            //System.out.println(outer.getKey() + ": max = " + m);
            listSourceMaxes.put(outer.getKey(), m);
        }
    }


    public Map <Keywords.DATATYPE, Integer> getListSourceCounts(){
        return listSourceCounts;
    }

    public int getDatatypeCount(Keywords.DATATYPE datatype){
        return listSourceCounts.get(datatype);
    }

    public int getMaxCount(Keywords.DATATYPE datatype){
        return listSourceMaxes.get(datatype);
    }

    public void disp(){
        System.out.println("Display List Table with counts");
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            System.out.println();
            System.out.println(outer.getKey() + ": totalCount = " + listSourceCounts.get(outer.getKey()));
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                System.out.println(inner.getKey() + ": Count = " + ((ListTable.ListTableNode)inner.getValue()).size());
                inner.getValue().disp();
            }
        }
        System.out.println("===================");
    }
}
