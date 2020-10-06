package compile.symboltable;

import compile.basics.Keywords;
import compile.parse.Base_ParseItem;

import java.util.Map;

import static compile.basics.Keywords.DATATYPE.RAW_TEXT;

public class ListTableItemSearch {
    protected final Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;

    public ListTableItemSearch(Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap) {
        this.listTableMap = listTableMap;
    }

    public boolean contains(Keywords.DATATYPE datatype, String val){
        return listTableMap.get(datatype).containsKey(val);
    }

    public boolean isItem(Keywords.DATATYPE dataType, String category, String val){
        return ((ListTable.ListTableNode) listTableMap.get(dataType).get(category)).contains(val);
    }

    public Keywords.DATATYPE getDataType(String text){
        if(text != null){
            for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
                if(outer.getValue().containsKey(text)){
                    return outer.getKey();
                }
            }
            String category = getCategory(text);
            if(category != null){
                for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
                    if(outer.getValue().containsKey(category)){
                        return outer.getKey();
                    }
                }
            }
            Keywords.DATATYPE datatype = Keywords.DATATYPE.fromString(text);
            if(datatype != null){
                return datatype;
            }
        }
        return RAW_TEXT;
    }

    public String getCategory(String val){
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                if(((ListTable.ListTableNode)inner.getValue()).contains(val)){
                    return inner.getKey();
                }
            }
        }
        return null;
    }
}
