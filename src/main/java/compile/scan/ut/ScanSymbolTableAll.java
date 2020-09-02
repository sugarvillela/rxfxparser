package compile.scan.ut;

import compile.basics.CompileInitializer;
import java.util.ArrayList;

import compile.basics.Factory_Node.ScanNode;
import static compile.basics.Factory_Node.ScanNode.STATUS_FORMAT;
import compile.basics.Keywords;
import compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.CMD.ADD_TO;
import static compile.basics.Keywords.CMD.POP;
import static compile.basics.Keywords.CMD.PUSH;
import static compile.basics.Keywords.HANDLER.SYMBOL_TABLE;
import static compile.basics.Keywords.USERDEF_OPEN;
import erlog.Erlog;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import unique.Unique;

public class ScanSymbolTableAll {
    private final String nullStatus;
    private final Unique uq;
    private final ArrayList<ScanNode> symbolTable;
    
    public ScanSymbolTableAll(){
        nullStatus = String.format(STATUS_FORMAT, 0, 0);
        uq = new Unique();
        symbolTable = new ArrayList<>();
        symbolTable.add(new ScanNode(nullStatus, PUSH, SYMBOL_TABLE, null, null));
    }
    private int indexOf(String text){
        int i = 0;
        for(ScanNode node : symbolTable){
            if(text.equals(node.data)){
                return i;
            }
            i++;
        }
        return -1;
    }
    public boolean isUserDef(String text){
        return (text.startsWith(USERDEF_OPEN) && text.length() > 1);
    }
    public boolean addIfNew(String text, HANDLER type){
        int index = this.indexOf(text);
        if(index == -1){
            symbolTable.add(
                new ScanNode(nullStatus, ADD_TO, type, Keywords.KWORD.DEF_NAME, text)
            );
            return true;
        }
        return false;
    }
    public boolean assertNew(String text, HANDLER type){
        if(!addIfNew(text, type)){
            Erlog.get(this).set(
                String.format(
                    "%s already exists...%s definitions must be uniquely named",
                    text, type.toString()
                )
            );
            return false;
        }
        return true;
    }

    public boolean write_rxlx_file(String path){
        if(symbolTable.size() == 1){
            return false;
        }
        try(BufferedWriter file = new BufferedWriter(new FileWriter(path)) 
        ){
            file.write("# Generated file, do not edit");
            file.newLine();
            file.write("# Last write: " + CompileInitializer.getInstance().getInitTime());
            file.newLine();
            file.write("# Lists all top-level identifiers defined in .rxfx source file");
            file.newLine();
            for (ScanNode node: symbolTable) {
                //System.out.println("node:"+node );
                file.write(node.toString());
                file.newLine();
            }
            file.write((new ScanNode(nullStatus, POP, SYMBOL_TABLE, null, null)).toString());
            file.newLine();
            file.close();
            return true;
        }
        catch(IOException e){
            return false;
        }
    }
}
