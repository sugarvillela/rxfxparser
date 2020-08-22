package compile.scan.ut;

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

/**
 *
 * @author newAdmin
 */
public class ScannerSymbolTable {
    private final String nullStatus;
    private final ArrayList<ScanNode> symbolTable;
    
    public ScannerSymbolTable(){
        nullStatus = String.format(STATUS_FORMAT, 0, 0);
        symbolTable = new ArrayList<>();
        symbolTable.add(new ScanNode(nullStatus, PUSH, SYMBOL_TABLE, null, null));
    }
    public int indexOf(String text){
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
        else if(symbolTable.get(index).h != type){
            Erlog.get(this).set(
                String.format(
                    "%s exists, of type %s; New var, same name, of type %s",
                    text, symbolTable.get(index).h.toString(), type.toString()
                )
            );
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
    public boolean isNewUserDef(String text, HANDLER type){
        return isUserDef(text) && addIfNew(text, type);
    }
    public boolean isOldUserDef(String text, HANDLER type){
        return isUserDef(text) && !addIfNew(text, type);
    }
    public ArrayList<ScanNode> getSymbolTable(){
        if(symbolTable.size() > 1){
            symbolTable.add(new ScanNode(nullStatus, POP, SYMBOL_TABLE, null, null));
            return symbolTable;
        }
        else{
            return new ArrayList<>();
        }
    }
}
