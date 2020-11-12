package listtable;

import commons.BIT;
import commons.Commons;
import compile.basics.Keywords;
import compile.parse.Base_ParseItem;
import erlog.DevErr;
import interfaces.Killable;
import uq.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.DATATYPE.LIST_NUMBER;
import static java.lang.Math.max;

public class ListTableNumGen implements Killable {
    private final Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;
    private final Map<Keywords.DATATYPE, KeyValNode[]> keyValMap;
    private ListTable listTable;
    private final Keywords.DATATYPE[] listOrder;
    public ListTableNumGen(
            ListTable listTable,
            Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap
    ){
        this.listTable = listTable;
        this.listTableMap = listTableMap;
        keyValMap = new HashMap<>(8);
        listOrder = new Keywords.DATATYPE[]{LIST_STRING, LIST_NUMBER, LIST_DISCRETE, LIST_BOOLEAN};

    }

    public void genKeyValMap(){// follows listOrder
        FieldSizeCalculations fieldSizeCalculations = new FieldSizeCalculations(listTable.getTypeCount());
        GeneratedList[] generators = new GeneratedList[]{
                new SimpleList(listTableMap, LIST_STRING, fieldSizeCalculations),
                new SimpleList(listTableMap, LIST_NUMBER, fieldSizeCalculations),
                new DiscreteList(listTableMap, fieldSizeCalculations),
                new BooleanList(listTableMap, fieldSizeCalculations)
        };

        GeneratedList prevList = null;
        for(GeneratedList generator : generators){
            if(generator == null){break;}
            generator.setPrevList(prevList);
            generator.genKeyValList(fieldSizeCalculations);
            generator.get(keyValMap);
            prevList = generator;
        }
    }

    public KeyValNode[] keyValsByType(Keywords.DATATYPE datatype){
        return keyValMap.get(datatype);
    }

    public Keywords.DATATYPE[] getListOrder(){// a reference for clients to keep everything in the same order
        return listOrder;
    }
    @Override
    public void kill() {
        this.listTable = null;
    }

    public void disp(){
        System.out.println("Display keyValMap");
        for (Map.Entry<Keywords.DATATYPE, KeyValNode[]> outer : keyValMap.entrySet()) {
            System.out.println("\nDatatype: " + outer.getKey());
            for (KeyValNode keyValNode : outer.getValue()) {
                System.out.println("\nCategory: " + keyValNode.getCategoryName());
                keyValNode.disp();
            }
        }
        System.out.println("===================");
    }

    public static class  KeyValNode{
        private final ArrayList<String> keys;
        private final ArrayList<Integer> vals;
        private String categoryName;
        private int categoryEnum;
        int kIndex, vIndex;

        public KeyValNode(){
            keys = new ArrayList<>();
            vals = new ArrayList<>();
        }

        public void put(String key, Integer val){
            keys.add(key);
            vals.add(val);
        }

        public int getCategoryEnum() {
            return categoryEnum;
        }
        public void setCategoryEnum(int categoryEnum) {
            this.categoryEnum = categoryEnum;
        }

        public void setCategoryName(String groupName){
            this.categoryName = groupName;
        }
        public String getCategoryName(){
            return this.categoryName;
        }

        public int getRangeLow(){
            return vals.get(0);
        }
        public int getRangeHi(){
            return vals.get(vals.size() - 1);
        }

        public int size(){
            return keys.size();
        }
        public boolean isEmpty(){
            return keys.isEmpty();
        }

        public void rewind(){
            kIndex = 0;
            vIndex = 0;
        }

        public boolean hasNext(){
            return kIndex < keys.size() && vIndex < vals.size();
        }

        public String nextKey(){
            return keys.get(kIndex++);
        }

        public Integer nextVal(){
            return vals.get(vIndex++);
        }

        public void disp(){
//            for(int i = 0; i < keys.size(); i++){
//                System.out.printf("%02d: %s = 0x%08X \n", i, keys.get(i), vals.get(i));
//            }
            System.out.println(this.toString());
        }

        @Override
        public String toString() {
            return "KeyValNode{" +
                    "\n\tcategoryName='" + categoryName + '\'' +
                    "\n\tcategoryEnum=" + Commons.toHexString(categoryEnum) +
                    "\n\tkeys=" + keys +
                    "\n\tvals=" + Commons.toHexString(vals) +
                    "\n}";
        }
    }

    private static class FieldSizeCalculations {
        private static final int WROW_DEFAULT = 4, DATA_WIDTH_DEFAULT = 16, WVAL_DEFAULT = 4;
        public final int maxBool, maxDiscrete;
        public final int listBoolSize;
        public final int listDiscreteSize;
        public final int listStringSize;
        public final int listNumberSize;
        public final int rowMask, colMask;
        public int wrow, wcol, wval;

        public FieldSizeCalculations(ListTableTypeCount typeCount){// Expect ListTable is initialized
            typeCount.initCounts();
            maxBool = typeCount.getMaxCount(LIST_BOOLEAN);          // Each bool category gets own row; need bit space in int >= largest category
            maxDiscrete = typeCount.getMaxCount(LIST_DISCRETE) + 1; // Each disc category gets own col; need log_2 space for largest category. Add 1 to make room for category enum
            listBoolSize = typeCount.getDatatypeCount(LIST_BOOLEAN);      // need log_2 space for bool rows
            listDiscreteSize = typeCount.getDatatypeCount(LIST_DISCRETE); // need log_2 space for disc rows (fewer than bool due to compactness)
            listStringSize = typeCount.getDatatypeCount(LIST_STRING);// plain old array
            listNumberSize = typeCount.getDatatypeCount(LIST_NUMBER);// plain old array

            init(WROW_DEFAULT, 5);
            System.out.printf("\nFieldCalculator: wrow=%s, wcol=%s, wval=%s \n ", wrow, wcol, wval);
            rowMask = ~((1 << (Integer.SIZE - wrow)) - 1);
            colMask = ~rowMask - ((1 << (Integer.SIZE - wrow - wcol)) - 1);
            BIT.disp(rowMask);
            BIT.disp(colMask);
        }
        private void init(int wRowDefault, int count){
            if(wRowDefault < 1 || count <= 0){
                DevErr.get(this).kill("Houston, we have a problem...");
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
        public int getRow(int n){
            return n & rowMask;
        }
        public int getCol(int n){
            return n & colMask;
        }
    }

    private static abstract class  GeneratedList{
        protected final FieldSizeCalculations fieldSizeCalculations;
        protected final Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;
        protected final ArrayList<KeyValNode> keyValNodes;
        protected final Keywords.DATATYPE datatype;
        protected UqGen uqGen;

        public GeneratedList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                Keywords.DATATYPE datatype,
                FieldSizeCalculations fieldSizeCalculations
        ){
            this.listTableMap = listTableMap;
            this.datatype = datatype;
            this.fieldSizeCalculations = fieldSizeCalculations;
            keyValNodes = new ArrayList<>();
        }

        public abstract void setPrevList(GeneratedList prevList);
        public abstract void genKeyValList(FieldSizeCalculations fieldSizeCalculations);  // expect fieldCalcuator is initialized
        public void get(Map<Keywords.DATATYPE, KeyValNode[]> keyValMap){
            keyValMap.put(
                datatype,
                keyValNodes.toArray(new KeyValNode[keyValNodes.size()])
            );
        }
        public UqGen getUqGen(){
            return uqGen;
        }
        protected void disp(){
            System.out.println();
            System.out.println(datatype);
            for(KeyValNode keyValNode : keyValNodes){
                keyValNode.disp();
            }
        }
    }
    private static class SimpleList extends  GeneratedList{
        public SimpleList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                Keywords.DATATYPE datatype,
                FieldSizeCalculations fieldSizeCalculations
        ){
            super(listTableMap, datatype, fieldSizeCalculations);
        }

        @Override
        public void setPrevList(GeneratedList prevList) {
            if(prevList == null){
                uqGen = new Uq();
            }
            else{
                uqGen = prevList.getUqGen();
            }
        }

        @Override
        public void genKeyValList(FieldSizeCalculations fieldSizeCalculations) {
            Map<String, Base_ParseItem> listItems = listTableMap.get(datatype);
            KeyValNode node;

            for (Map.Entry<String, Base_ParseItem> inner : listItems.entrySet()) {// category
                node = new KeyValNode();

                node.setCategoryName(inner.getKey());
                node.setCategoryEnum(uqGen.next());

                for(String item : ((ListTable.ListTableNode)inner.getValue()).getList()){ // item
                    node.put(item, uqGen.next());
                }
                keyValNodes.add(node);
            }
            disp();
        }
    }
    private static class DiscreteList extends GeneratedList{
        public DiscreteList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                FieldSizeCalculations fieldSizeCalculations
        ) {
            super(listTableMap, LIST_DISCRETE, fieldSizeCalculations);
        }

        @Override
        public void setPrevList(GeneratedList prevList) {
            if(prevList == null || !(prevList.getUqGen() instanceof UqDiscreteGen)){
                uqGen = new UqDiscreteGen(
                        fieldSizeCalculations.wrow,
                        fieldSizeCalculations.wcol,
                        fieldSizeCalculations.wval
                );
                uqGen.newRow();
            }
            else{
                uqGen = new UqDiscreteGen((UqDiscreteGen)prevList.getUqGen());
            }
        }

        @Override
        public void genKeyValList(FieldSizeCalculations fieldSizeCalculations) {
            Map<String, Base_ParseItem> listItems = listTableMap.get(datatype);
            KeyValNode node;

            for (Map.Entry<String, Base_ParseItem> inner : listItems.entrySet()) {// category
                node = new KeyValNode();

                node.setCategoryName(inner.getKey());
                node.setCategoryEnum(uqGen.next());
                for(String item : ((ListTable.ListTableNode)inner.getValue()).getList()){ // item
                    node.put(item, uqGen.next());
                }
                keyValNodes.add(node);
                uqGen.newCol();
            }
            disp();
        }
    }
    private static class BooleanList extends GeneratedList{
        public BooleanList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                FieldSizeCalculations fieldSizeCalculations
        ) {
            super(listTableMap, LIST_BOOLEAN, fieldSizeCalculations);
        }

        @Override
        public void setPrevList(GeneratedList prevList) {
            if(prevList == null || !(prevList.getUqGen() instanceof UqGenComposite)){
                uqGen = new UqBoolGen(
                        fieldSizeCalculations.wrow
                );
            }
            else{
                uqGen = new UqBoolGen((UqGenComposite) prevList.getUqGen());
                //uqGen.newRow();
            }
        }

        @Override
        public void genKeyValList(FieldSizeCalculations fieldSizeCalculations) {
            Map<String, Base_ParseItem> listItems = listTableMap.get(datatype);
            KeyValNode node;

            for (Map.Entry<String, Base_ParseItem> inner : listItems.entrySet()) {// category
                node = new KeyValNode();

                node.setCategoryName(inner.getKey());
                for(String item : ((ListTable.ListTableNode)inner.getValue()).getList()){ // item
                    node.put(item, uqGen.next());
                }

                keyValNodes.add(node);
                node.setCategoryEnum(((UqGenComposite)uqGen).currRowCol());
                uqGen.newRow();
            }
            disp();
        }
    }
}
