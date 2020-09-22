package compile.basics;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Factory_Node.ScanNode.NUM_FIELDS;
import static compile.basics.Keywords.CMD.POP;
import static compile.basics.Keywords.CMD.PUSH;
import static compile.basics.Keywords.FIELD.VAL;
import static compile.basics.Keywords.DATATYPE.RX_BUILDER;

import commons.Commons;
import compile.basics.Keywords.OP;
import compile.rx.RxTree.TreeNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public class Factory_Node {
    public static ScanNode newScanNode(String text){
        String[] tok = text.split(",", NUM_FIELDS);
        Keywords.DATATYPE datatype = Keywords.DATATYPE.fromString(tok[2]);
        if(RX_BUILDER.equals(datatype)){
            return null;
        }
        return new ScanNode(
                tok[0],
                Keywords.CMD.fromString(tok[1]),
                datatype,
                NULL_TEXT.equals(tok[3])? null : Keywords.FIELD.fromString(tok[3]),
                NULL_TEXT.equals(tok[4])? "" : tok[4]
        );
    }
    /* factory access for scan node */
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.DATATYPE setDatatype){
        return new ScanNode(null, setCommand, setDatatype, null, "");
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, String setData){
        return new ScanNode(null, setCommand, setDatatype, null, setData);
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, Keywords.FIELD setKWord){
        return new ScanNode(null, setCommand, setDatatype, setKWord, "");
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, Keywords.FIELD setKWord, String setData){
        return new ScanNode(null, setCommand, setDatatype, setKWord, setData);
    }
    public static ScanNode newScanNode(String lineCol, Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, Keywords.FIELD setKWord, String setData){
        return new ScanNode(lineCol, setCommand, setDatatype, setKWord, setData);
    }
    public static ScanNode newPushNode(Keywords.DATATYPE setDatatype){
        return new ScanNode(null, PUSH, setDatatype, null, null);
    }
    public static ScanNode newPushNode(String lineCol, Keywords.DATATYPE setDatatype){
        return new ScanNode(lineCol, PUSH, setDatatype, null, null);
    }
    public static ScanNode newPopNode(Keywords.DATATYPE setDatatype){
        return new ScanNode(null, POP, setDatatype, null, null);
    }
    public static ScanNode newPopNode(String lineCol, Keywords.DATATYPE setDatatype){
        return new ScanNode(lineCol, POP, setDatatype, null, null);
    }
    /** node for input and output list */
    public static class ScanNode{
        public static final String NULL_TEXT = "NULL";
        public static final int NUM_FIELDS = 5;
        public String lineCol;
        public Keywords.CMD cmd;
        public Keywords.DATATYPE h;
        public Keywords.FIELD k;
        public String data;
        
        public ScanNode(String lineCol, Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, Keywords.FIELD setKWord, String setData){
            this.lineCol = lineCol;
            h = setDatatype;
            cmd = setCommand;
            k = setKWord;
            data = setData;
        }
        /**Data to string for writing to file
         * @return one line of a csv file */
        @Override
        public String toString(){//one line of a csv file
            return String.format(
                "%s,%s,%s,%s,%s",
                Commons.nullSafe(lineCol),
                Commons.nullSafe(cmd),
                Commons.nullSafe(h),
                Commons.nullSafe(k),
                Commons.nullSafe(data)
            );
        }

    }
    /*========================================================================*/
    
    public static GroupNode newGroupNode(String name, int n){
        return new GroupNode(name, n);
    }
    public static class GroupNode{
        public String n;
        public int s, e;
        public GroupNode( String name, int n ){
            this.n = name;
            this.s = this.e = n;
        }
        @Override
        public String toString(){
            return this.n + ": start=" + s + " end=" + e;
        }
    }

    public static boolean persist(String path, ArrayList<Factory_Node.ScanNode> scanNodes){
        return persist(path, scanNodes, null);
    }
    public static boolean persist(String path, ArrayList<ScanNode> scanNodes, String comment){
        try(BufferedWriter file = new BufferedWriter(new FileWriter(path))
        ){
            file.write("# Generated file, do not edit");
            file.newLine();
            file.write("# Last write: " + CompileInitializer.getInstance().getInitTime());
            file.newLine();
            if(comment != null){
                file.write("# " + comment);
                file.newLine();
            }

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
}
