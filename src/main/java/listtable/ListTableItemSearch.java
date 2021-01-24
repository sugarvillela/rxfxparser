package listtable;

import langdef.Keywords;
import compile.parse.Base_ParseItem;
import erlog.Erlog;

import java.util.Map;

public class ListTableItemSearch {
    protected final Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap;

    public ListTableItemSearch(Map <Keywords.DATATYPE, Map<String, Base_ParseItem>> listTableMap) {
        this.listTableMap = listTableMap;
    }

    public boolean contains(Keywords.DATATYPE datatype, String val){
        return listTableMap.get(datatype).containsKey(val);
    }

    public boolean isItem(Keywords.DATATYPE dataType, String category, String val){
        return ((ListTableNode) listTableMap.get(dataType).get(category)).contains(val);
    }

    public Keywords.DATATYPE getDataType(String text){
        Keywords.DATATYPE datatype;
        if(text == null || (
            (datatype = datatypeByCategoryName(text)) == null &&    // Case 1: text is a category name in the map
            (datatype = datatypeByItemName(text)) == null &&        // Case 2: text is an item name in the map
            (datatype = Keywords.DATATYPE.fromString(text)) == null // Case 3: text is a datatype name, not necessarily in the map
        )){
            return null;
        }
        else{
            return datatype;
        }
    }

    public Keywords.DATATYPE datatypeByCategoryName(String categoryName){
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            if(outer.getValue().containsKey(categoryName)){
                return outer.getKey();
            }
        }
        return null;
    }

    public Keywords.DATATYPE datatypeByItemName(String itemName){
        String category = categoryByItemName(itemName);
        return (category == null)? null : datatypeByCategoryName(category);
    }

    public String categoryByItemName(String itemName){
        for (Map.Entry<Keywords.DATATYPE, Map<String, Base_ParseItem>> outer : listTableMap.entrySet()) {
            for (Map.Entry<String, Base_ParseItem> inner : outer.getValue().entrySet()) {
                if(((ListTableNode)inner.getValue()).contains(itemName)){
                    return inner.getKey();
                }
            }
        }
        return null;
    }

    public ListTableNode getListTableNode(Keywords.DATATYPE datatype, String category){
        Base_ParseItem listTableNode = listTableMap.get(datatype).get(category);
        if(listTableNode == null){
            String datatypeString = (datatype == null)? "NULL" : datatype.toString();
            Erlog.get(this).set(String.format("Can't find %s in %s", category, datatypeString));
            return null;
        }
        return (ListTableNode)listTableNode;
    }
    public boolean isSpecialField(Keywords.DATATYPE datatype, String category, String item){
        return item.equals(getListTableNode(datatype, category).getLastField());
    }
}
