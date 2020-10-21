package compile.symboltable;

import erlog.Erlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConstantTable {
    private static final SymbolTest SYMBOL_TEST = SymbolTest.getInstance();
    private static ConstantTable instance;

    private ConstantTable () {
        constantTableMap = new HashMap<>();
    }

    public static ConstantTable getInstance(){
        return (instance == null)? (instance = new ConstantTable ()) : instance;
    }
    public static void killInstance(){
        instance = null;
    }

    private Map<String, ConstantNode> constantTableMap;
    private ConstantNode currNode;

    //=====Methods to populate nodes=====================================

    public void startConstant(){
        currNode = new ConstantNode();
    }

    public void setConstantName(String name){
        //System.out.println("setConstantName: "+name);
        if(constantTableMap.containsKey(name)){
            Erlog.get(this).set("Identifier already exists", name);
        }
        else{
            currNode.name = name;
        }
    }

    public void setValue(String value){
        //System.out.println("setValue: "+value);
        currNode.value = value;
        constantTableMap.put(currNode.name, currNode);
        currNode = null;
    }

    //=====Methods to access table data==================================

    public String readConstant(String text){
        if(SYMBOL_TEST.isUserDef(text)){
            String defName = SYMBOL_TEST.stripUserDef(text);
            if(isConstant(defName)){
                return getValue(defName);
            }
        }
        return null;
    }

    public boolean isConstant(String constantName){
        return constantTableMap.containsKey(constantName);
    }

    public String getValue(String constantName){
        return constantTableMap.get(constantName).value;
    }

    public String toString(){
        ArrayList<String> out = new ArrayList<>();
        for (Map.Entry<String, ConstantNode> entry : constantTableMap.entrySet()) {
            out.add(entry.getValue().toString());
        }
        return String.join("\n", out);
    }

    private static class ConstantNode{
        public String name, value;

        @Override
        public String toString(){
            return name + " = " + value;
        }
    }
}
