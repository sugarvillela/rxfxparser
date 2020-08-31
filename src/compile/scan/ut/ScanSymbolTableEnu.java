package compile.scan.ut;

import compile.basics.CompileInitializer;
import compile.basics.Factory_Node.ScanNode;
import static compile.basics.Factory_Node.ScanNode.STATUS_FORMAT;
import compile.basics.Keywords;
import static compile.basics.Keywords.CMD.ADD_TO;
import static compile.basics.Keywords.CMD.POP;
import static compile.basics.Keywords.CMD.PUSH;
import static compile.basics.Keywords.CMD.SET_ATTRIB;
import compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.HANDLER.ENUB;
import static compile.basics.Keywords.HANDLER.ENUD;
import static compile.basics.Keywords.HANDLER.SYMBOL_TABLE;
import static compile.basics.Keywords.KWORD.DEF_NAME;
import erlog.Erlog;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import toksource.ScanNodeSource;
import toksource.TextSource_file;

public class ScanSymbolTableEnu {
    private final Map<HANDLER, Map<String, ArrayList<String>>> table;
    private final String nullStatus;
    private String currName;
    private HANDLER currHandler;

    public ScanSymbolTableEnu(){
        nullStatus = String.format(STATUS_FORMAT, 0, 0);
        table = new HashMap(3);
        table.put(ENUB, new HashMap<>(8));
        table.put(ENUD, new HashMap<>(8));
    }
    public void addCategory(String defName, HANDLER type){
        currName = defName;
        currHandler = type;
        if(table.get(currHandler).containsKey(defName)){
            Erlog.get(this).set(
                String.format(
                    "%s already exists...%s categories must be uniquely named",
                    defName, type.toString()
                )
            );
        }
        else{
            table.get(currHandler).put(defName, new ArrayList<>());
        }
    }
    public void add(String text){
        if(table.get(currHandler).get(currName).contains(text)){
            Erlog.get(this).set(
                String.format(
                    "%s already exists in %s...%s definitions must be uniquely named",
                    text, currName, currHandler.toString()
                )
            );
        }
        else{
            table.get(currHandler).get(currName).add(text);
        }
    }
       
    public boolean write_rxlx_file(String path){
        ArrayList<ScanNode> scanNodes = new ArrayList<>();
        scanNodes.add(new ScanNode(nullStatus, PUSH, SYMBOL_TABLE, null, null));
        if(!table.get(ENUB).isEmpty()){
            populateScanNodes(table.get(ENUB), scanNodes, ENUB);
        }
        if(!table.get(ENUD).isEmpty()){
            populateScanNodes(table.get(ENUD), scanNodes, ENUD);
        }
        scanNodes.add(new ScanNode(nullStatus, POP, SYMBOL_TABLE, null, null));
        if(scanNodes.size() == 2){
            return false;
        }
        try(BufferedWriter file = new BufferedWriter(new FileWriter(path)) 
        ){
            file.write("# Generated file, do not edit");
            file.newLine();
            file.write("# Last write: " + CompileInitializer.getInstance().getInitTime());
            file.newLine();
            file.write("# Lists ENUB and ENUD group names and items");
            file.newLine();
            for (ScanNode node: scanNodes) {
                file.write(node.toString());
                file.newLine();
            }
            file.close();
            return true;
        }
        catch(IOException e){
            return false;
        }
    }
    private void populateScanNodes(
        Map<String, ArrayList<String>> table,
        ArrayList<ScanNode> scanNodes,
        HANDLER h
    ){
        //System.out.print("\npopulate: " + h.toString());
        for (Map.Entry<String, ArrayList<String>> entry : table.entrySet()) {
            //System.out.print("name: " + entry.getKey());//DEF_NAME
            
            scanNodes.add(new ScanNode(nullStatus, PUSH, h, null, null));
            scanNodes.add(new ScanNode(nullStatus, SET_ATTRIB, h, DEF_NAME, entry.getKey()));

            for(String item : entry.getValue()){
                //System.out.print("     item: " + item);
                scanNodes.add(new ScanNode(nullStatus, ADD_TO, h, null, item));
            }
            scanNodes.add(new ScanNode(nullStatus, POP, h, null, null));
        }
    }
    public boolean read_rxlx_file(String path){
        ScanNodeSource source = new ScanNodeSource(
            new TextSource_file(Keywords.fileName_symbolTableEnu())
        );
        if(!source.hasData()){
            return false;
        }
        while(source.hasNext()){
        
        }
        return true;
    }
}
