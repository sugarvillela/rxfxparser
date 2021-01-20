package compile.symboltable;

import erlog.Erlog;
import runstate.Glob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConstantTable {
    private static ConstantTable instance;

    private ConstantTable () {
        constantTableMap = new HashMap<>();
    }

    public static ConstantTable init(){
        return (instance == null)? (instance = new ConstantTable ()) : instance;
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

    public String getConstantValue(String text){
        if(Glob.SYMBOL_TEST.isUserDef(text)){
            String defName = Glob.SYMBOL_TEST.stripUserDef(text);
            if(isConstant(defName)){
                return constantTableMap.get(defName).value;
            }
        }
        return null;
    }

    public boolean isConstant(String constantName){
        return constantTableMap.containsKey(constantName);
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
