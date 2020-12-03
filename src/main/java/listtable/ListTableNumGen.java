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
    private final Map<Keywords.DATATYPE, CategoryNode[]> keyValMap;
    private final Keywords.DATATYPE[] listOrder;
    private FieldSizeCalculations fieldSizeCalculations;
    private ListTable listTable;

    public ListTableNumGen(
            ListTable listTable,
            Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap
    ){
        this.listTable = listTable;
        this.listTableMap = listTableMap;
        keyValMap = new HashMap<>(8);
        listOrder = new Keywords.DATATYPE[]{
                LIST_STRING,
                LIST_NUMBER,
                LIST_DISCRETE,
                LIST_SCOPES,
                LIST_VOTE,
                LIST_BOOLEAN
        };
    }

    public void initCategoryNodes(){// follows listOrder
        fieldSizeCalculations = new FieldSizeCalculations(listTable.getTypeCount());
        GeneratedList[] generatedLists = new GeneratedList[]{
                new SimpleList(    listTableMap, LIST_STRING),
                new SimpleList(    listTableMap, LIST_NUMBER),
                new DiscreteList(  listTableMap, LIST_DISCRETE, fieldSizeCalculations),
                new DiscreteList(  listTableMap, LIST_SCOPES, fieldSizeCalculations),
                new VoteList(      listTableMap, LIST_VOTE, fieldSizeCalculations),
                new BooleanList(   listTableMap, fieldSizeCalculations)
        };

        GeneratedList prevList = null;
        for(GeneratedList generatedList : generatedLists){
            generatedList.setPrevList(prevList);
            generatedList.genKeyValList();
            generatedList.saveToMap(keyValMap);
            prevList = generatedList;
        }
    }

    public CategoryNode[] categoryNodesByType(Keywords.DATATYPE datatype){
        return keyValMap.get(datatype);
    }

    public Keywords.DATATYPE[] getListOrder(){// a reference for clients to keep everything in the same order
        return listOrder;
    }

    public int getWRow(){
        return fieldSizeCalculations.wrow;
    }
    public int getWCol(){
        return fieldSizeCalculations.wcol;
    }
    public int getWVal(){
        return fieldSizeCalculations.wval;
    }

    @Override
    public void kill() {
        this.listTable = null;
    }

    public void disp(){
        System.out.println("Display keyValMap");
        for(Keywords.DATATYPE datatype : listOrder){
            System.out.println("\nDatatype: " + datatype);
            for (CategoryNode categoryNode : keyValMap.get(datatype)) {
                categoryNode.disp();
            }
        }
        System.out.println("===================");
    }

    public static class CategoryNode {
        private final ArrayList<String> keys;
        private final ArrayList<Integer> vals;
        private String categoryName;
        private int categoryEnum;

        private int rowOffset;
        int kIndex, vIndex;

        public CategoryNode(){
            keys = new ArrayList<>();
            vals = new ArrayList<>();
        }

        public void put(String key, Integer val){
            keys.add(key);
            vals.add(val);
        }

        public void setCategoryName(String groupName){
            this.categoryName = groupName;
        }
        public String getCategoryName(){
            return this.categoryName;
        }

        public int getCategoryEnum() {
            return categoryEnum;
        }
        public void setCategoryEnum(int categoryEnum) {
            this.categoryEnum = categoryEnum;
        }

        public int getRowOffset() {
            return rowOffset;
        }
        public void setRowOffset(int rowOffset) {
            this.rowOffset = rowOffset;
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
            System.out.println(this.toString());
        }

        @Override
        public String toString() {
            return "CategoryNode{" +
                    "\n\tcategoryName='" + categoryName + '\'' +
                    "\n\tcategoryEnum=" + Commons.toHexString(categoryEnum) +
                    "\n\trowOffset=" + Commons.toHexString(rowOffset) +
                    "\n\tkeys=" + keys +
                    "\n\tvals=" + Commons.toHexString(vals) +
                    "\n}";
        }
    }

    private static class FieldSizeCalculations {
        private static final int WORD_LEN = Long.SIZE, WROW_DEFAULT = 8, DATA_WIDTH_DEFAULT = 24, WVAL_DEFAULT = 4;
        public final int maxBool, maxDiscrete;
        public final int listBoolSize;
        public final int listDiscreteSize;
        public final int listStringSize;
        public final int listNumberSize;

        public final int listBoolCategories;
        public final int listDiscreteCategories;
        public int wrow, wcol, wval;

        public FieldSizeCalculations(ListTableTypeCount typeCount){// Expect ListTable is initialized
            typeCount.initCounts();

            maxBool = typeCount.getMaxCount(LIST_BOOLEAN);          // Each bool category gets own row; need bit space in int >= largest category
            maxDiscrete = typeCount.getMaxCount(LIST_DISCRETE) + 1; // Each disc category gets own col; need log_2 space for largest category. Add 1 to make room for category enum

            listBoolSize = typeCount.getDatatypeCount(LIST_BOOLEAN);      // need log_2 space for bool rows
            listDiscreteSize = typeCount.getDatatypeCount(LIST_DISCRETE) + typeCount.getDatatypeCount(LIST_SCOPES); // need log_2 space for disc rows (fewer than bool due to compactness)
            listStringSize = typeCount.getDatatypeCount(LIST_STRING);     // plain old array
            listNumberSize = typeCount.getDatatypeCount(LIST_NUMBER);     // plain old array

            listBoolCategories = typeCount.getCategoryCount(LIST_BOOLEAN);
            listDiscreteCategories = typeCount.getCategoryCount(LIST_DISCRETE) + typeCount.getCategoryCount(LIST_SCOPES) + typeCount.getDatatypeCount(LIST_VOTE);
            System.out.printf("\nSizes: listBoolSize=%s, listDiscreteSize=%s \n ", listBoolSize, listDiscreteSize);
            System.out.printf("Categories: bool=%s, discrete=%s \n ", listBoolCategories, listDiscreteCategories);

            init(WROW_DEFAULT, WVAL_DEFAULT,5);
            System.out.printf("FieldCalculator: wrow=%s, wcol=%s, wval=%s \n ", wrow, wcol, wval);
        }
        private void init(int wRowDefault, int wValDefault, int count){
            if(wRowDefault < 1 || count <= 0){
                DevErr.get(this).kill("Houston, we have a problem...");
                return;
            }

            int wData = max(DATA_WIDTH_DEFAULT, maxBool);
            int wRowB = max(wRowDefault, BIT.logCeil(rowsNeeded(wValDefault)));
            if(WORD_LEN - wRowB - wData < 0){
                init(wRowDefault - 1, wValDefault,count - 1);
                return;
            }

            int wValD = max(WVAL_DEFAULT, BIT.logCeil(maxDiscrete));
            int nCols = (WORD_LEN - wRowB)/wValD;
            int wColD = BIT.logCeil(nCols);
            int colStart = WORD_LEN - wRowB - wColD;
            while(colStart % wValD != 0){
                wColD++;
                colStart = WORD_LEN - wRowB - wColD;
            }
            nCols = colStart/wValD;
            int wrowD = BIT.logCeil(listDiscreteSize/nCols);
            if(wrowD > wRowB){
                init(wRowDefault + 1, wColD,count - 1);
                return;
            }
            wrow = wRowB;
            wcol = wColD;
            wval = wValD;
        }
        private int rowsNeeded(int nCols){
            return listBoolCategories + listDiscreteCategories/nCols + 1;
        }
    }

    private static abstract class  GeneratedList{
        protected final Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;
        protected final ArrayList<CategoryNode> categoryNodes;
        protected final Keywords.DATATYPE datatype;
        protected UqGen uqGen;

        public GeneratedList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                Keywords.DATATYPE datatype
        ){
            this.listTableMap = listTableMap;
            this.datatype = datatype;
            categoryNodes = new ArrayList<>();
        }

        public abstract void setPrevList(GeneratedList prevList);
        public abstract void genKeyValList();
        public void saveToMap(Map<Keywords.DATATYPE, CategoryNode[]> keyValMap){
            keyValMap.put(
                datatype,
                categoryNodes.toArray(new CategoryNode[categoryNodes.size()])
            );
        }
        public UqGen getUqGen(){
            return uqGen;
        }
        protected void disp(){
            System.out.println();
            System.out.println(datatype);
            for(CategoryNode categoryNode : categoryNodes){
                categoryNode.disp();
            }
        }
    }
    private static class SimpleList extends  GeneratedList{
        public SimpleList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                Keywords.DATATYPE datatype
        ){
            super(listTableMap, datatype);
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
        public void genKeyValList() {
            Map<String, Base_ParseItem> listItems = listTableMap.get(datatype);
            CategoryNode node;

            for (Map.Entry<String, Base_ParseItem> inner : listItems.entrySet()) {// category
                node = new CategoryNode();

                node.setCategoryName(inner.getKey());
                node.setCategoryEnum(uqGen.next());

                for(String item : ((ListTable.ListTableNode)inner.getValue()).getList()){ // item
                    node.put(item, uqGen.next());
                }
                categoryNodes.add(node);
            }
        }
    }
    private static class DiscreteList extends GeneratedList{
        protected final FieldSizeCalculations fieldSizeCalculations;

        public DiscreteList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                Keywords.DATATYPE datatype,
                FieldSizeCalculations fieldSizeCalculations
        ) {
            super(listTableMap, datatype);
            this.fieldSizeCalculations = fieldSizeCalculations;
        }

        @Override
        public void setPrevList(GeneratedList prevList) {
            if(prevList == null || !(prevList.getUqGen() instanceof UqDiscreteGen)){
                //System.out.println("setPrevList if: " + datatype);
                uqGen = new UqDiscreteGen(
                        fieldSizeCalculations.wrow,
                        fieldSizeCalculations.wcol,
                        fieldSizeCalculations.wval
                );
                uqGen.newRow();
            }
            else{
                //System.out.println("setPrevList else: " + datatype);
                uqGen = new UqDiscreteGen((UqDiscreteGen)prevList.getUqGen());
            }
        }

        @Override
        public void genKeyValList() {
            Map<String, Base_ParseItem> listItems = listTableMap.get(datatype);
            CategoryNode node;

            for (Map.Entry<String, Base_ParseItem> inner : listItems.entrySet()) {// category
                node = new CategoryNode();

                node.setCategoryName(inner.getKey());
                node.setCategoryEnum(uqGen.next());
                node.setRowOffset(((UqGenComposite)uqGen).curRowOffset());
                for(String item : ((ListTable.ListTableNode)inner.getValue()).getList()){ // item
                    node.put(item, uqGen.next());
                }
                categoryNodes.add(node);
                uqGen.newCol();
            }
        }
    }
    private static class VoteList extends DiscreteList{
        public VoteList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                Keywords.DATATYPE datatype,
                FieldSizeCalculations fieldSizeCalculations
        ) {
            super(listTableMap, datatype, fieldSizeCalculations);
        }

        @Override
        public void genKeyValList() {
            Map<String, Base_ParseItem> listItems = listTableMap.get(datatype);
            CategoryNode node;

            for (Map.Entry<String, Base_ParseItem> inner : listItems.entrySet()) {// category
                node = new CategoryNode();

                node.setCategoryName(inner.getKey());
                node.setCategoryEnum(uqGen.next());
                node.setRowOffset(((UqGenComposite)uqGen).curRowOffset());
                for(String item : ((ListTable.ListTableNode)inner.getValue()).getList()){ // item
                    node.put(item, uqGen.next());
                    //System.out.println("put: " + item);
                    uqGen.newCol();
                    uqGen.next();
                }
                categoryNodes.add(node);
                //uqGen.newCol();
            }
        }
    }
    private static class BooleanList extends GeneratedList{
        private final FieldSizeCalculations fieldSizeCalculations;

        public BooleanList(
                Map<Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap,
                FieldSizeCalculations fieldSizeCalculations
        ) {
            super(listTableMap, LIST_BOOLEAN);
            this.fieldSizeCalculations = fieldSizeCalculations;
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
        public void genKeyValList() {
            Map<String, Base_ParseItem> listItems = listTableMap.get(datatype);
            CategoryNode node;

            for (Map.Entry<String, Base_ParseItem> inner : listItems.entrySet()) {// category
                node = new CategoryNode();

                node.setCategoryName(inner.getKey());
                for(String item : ((ListTable.ListTableNode)inner.getValue()).getList()){ // item
                    node.put(item, uqGen.next());
                }

                categoryNodes.add(node);
                node.setCategoryEnum(((UqGenComposite)uqGen).currRowCol());
                node.setRowOffset(((UqGenComposite)uqGen).curRowOffset());
                uqGen.newRow();
            }
        }
    }
}
