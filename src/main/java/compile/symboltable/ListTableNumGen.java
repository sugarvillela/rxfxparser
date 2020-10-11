package compile.symboltable;

import commons.BIT;
import compile.basics.Keywords;
import compile.parse.Base_ParseItem;
import erlog.Erlog;
import interfaces.Killable;
import uq.UniqueItr;
import uq.Uq;
import uq.UqBoolGen;
import uq.UqDiscreteGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.DATATYPE.LIST_NUMBER;
import static java.lang.Math.max;

public class ListTableNumGen implements Killable {
    private final Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;
    private final Map<Keywords.DATATYPE, Map<String, KeyValNode>> keyValMap;
    private ListTable listTable;
    private final Uq consecutive;

    public ListTableNumGen(ListTable listTable, Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap){
        this.listTable = listTable;
        this.listTableMap = listTableMap;
        keyValMap = new HashMap<>(8);
        keyValMap.put(LIST_BOOLEAN,  new HashMap<>(8));
        keyValMap.put(LIST_DISCRETE, new HashMap<>(8));
        keyValMap.put(LIST_NUMBER,   new HashMap<>(8));
        keyValMap.put(LIST_STRING,   new HashMap<>(8));
        consecutive = new Uq();
    }

    public void gen(){
        FieldCalculator fieldCalculator = new FieldCalculator(listTable.getTypeCount());
        UniqueItr uq;
        KeyValNode<String, Integer> node;

        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {// list source
            uq = getUqSource(outer.getKey(), fieldCalculator);
            if(uq != null){
                for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {// category
                    node = new KeyValNode<>();
                    node.put(inner.getKey(), uq.next());
                    keyValMap.get(outer.getKey()).put(inner.getKey(), node);
                    for(String item : ((ListTable.ListTableNode)inner.getValue()).getList()){ // item
                        node.put(item, uq.next());
                    }
                    uq.newRow();
                }
            }
        }
    }
    private UniqueItr getUqSource(Keywords.DATATYPE datatype, FieldCalculator fieldCalculator){
        switch(datatype){
            case LIST_BOOLEAN:
                return new UqBoolGen(fieldCalculator.wrow);
            case LIST_DISCRETE:
                return new UqDiscreteGen(fieldCalculator.wrow, fieldCalculator.wcol, fieldCalculator.wval);
            case LIST_NUMBER:
            case LIST_STRING:
                return consecutive;
            default:
                return null;
        }
    }
    @Override
    public void kill() {
        this.listTable = null;
    }

    public void disp(){
        System.out.println("Display keyValMap");
        for (Map.Entry<Keywords.DATATYPE, Map<String, KeyValNode>> outer : keyValMap.entrySet()) {
            System.out.println("\nDatatype: " + outer.getKey());
            for (Map.Entry<String, KeyValNode> inner : outer.getValue().entrySet()) {
                System.out.println("\nCategory: " + inner.getKey());
                inner.getValue().disp();
            }
        }
        System.out.println("===================");
    }

    public static class  KeyValNode <K, V>{
        private final ArrayList<K> keys;
        private final ArrayList<V> vals;
        int kIndex, vIndex;

        public KeyValNode(){
            keys = new ArrayList<>();
            vals = new ArrayList<>();
        }

        public void put(K key, V val){
            keys.add(key);
            vals.add(val);
        }

        public void rewind(){
            kIndex = 0;
            vIndex = 0;
        }

        public boolean hasNext(){
            return kIndex < keys.size() && vIndex < vals.size();
        }

        public K nextKey(){
            return keys.get(kIndex++);
        }

        public V nextVal(){
            return vals.get(vIndex++);
        }

        public void disp(){
            for(int i = 0; i < keys.size(); i++){
                System.out.println(i + ": " + keys.get(i) + " = " + vals.get(i));
            }
        }
    }
    private static class FieldCalculator{
        private static final int WROW_DEFAULT = 4, DATA_WIDTH_DEFAULT = 16, WVAL_DEFAULT = 4;
        public final int maxBool, maxDiscrete;
        public final int listBoolSize;
        public final int listDiscreteSize;
        public final int listStringSize;
        public final int listNumberSize;
        public int wrow, wcol, wval;

        public FieldCalculator(ListTableTypeCount typeCount){// Expect ListTable is initialized
            typeCount.initCounts();
            maxBool = typeCount.getMaxCount(LIST_BOOLEAN);  // Each bool gets its own row, so max is the biggest row
            maxDiscrete = typeCount.getMaxCount(LIST_DISCRETE);
            listBoolSize = typeCount.getDatatypeCount(LIST_BOOLEAN);
            listDiscreteSize = typeCount.getDatatypeCount(LIST_DISCRETE);
            listStringSize = typeCount.getDatatypeCount(LIST_STRING);
            listNumberSize = typeCount.getDatatypeCount(LIST_NUMBER);
            init(WROW_DEFAULT, 5);
            System.out.printf("\nwrow=%s, wcol=%s, wval=%s \n ", wrow, wcol, wval);
        }
        private void init(int wRowDefault, int count){
            if(wRowDefault < 1 || count <= 0){
                Erlog.get(this).set("Houston, we have a problem...");
                return;
            }

            int wData = max(DATA_WIDTH_DEFAULT, maxBool);
            int wRowB = max(wRowDefault, BIT.logCeil(listBoolSize/wData));
            if(Integer.SIZE - wRowB - wData < 0){
                init(wRowDefault - 1, count - 1);
                return;
            }

            int wValD = max(WVAL_DEFAULT, BIT.logCeil(maxDiscrete));
            int nCols = (Integer.SIZE - wRowB)/wValD;
            int wColD = BIT.logCeil(nCols);
            int colStart = Integer.SIZE - wRowB - wColD;
            while(colStart % wValD != 0){
                wColD++;
                colStart = Integer.SIZE - wRowB - wColD;
            }
            nCols =  colStart/wValD;
            int wrowD = BIT.logCeil(listDiscreteSize/nCols);
            if(wrowD > wRowB){
                init(wRowDefault + 1, count - 1);
                return;
            }
            wrow = wRowB;
            wcol = wColD;
            wval = wValD;
        }
    }
}
