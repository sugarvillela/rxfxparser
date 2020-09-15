package compile.basics;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Factory_Node.ScanNode.NUM_FIELDS;
import static compile.basics.Keywords.CMD.POP;
import static compile.basics.Keywords.CMD.PUSH;
import static compile.basics.Keywords.HANDLER.RX_BUILDER;
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
        Keywords.HANDLER handler = Keywords.HANDLER.fromString(tok[2]);
        if(RX_BUILDER.equals(handler)){
            return null;
        }
        return new ScanNode(
                tok[0],
                Keywords.CMD.get(tok[1]),
                handler,
                NULL_TEXT.equals(tok[3])? null : Keywords.FIELD.fromString(tok[3]),
                NULL_TEXT.equals(tok[4])? "" : tok[4]
        );
    }
    /* factory access for scan node */
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler){
        return new ScanNode(null, setCommand, setHandler, null, "");
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, String setData){
        return new ScanNode(null, setCommand, setHandler, null, setData);
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.FIELD setKWord){
        return new ScanNode(null, setCommand, setHandler, setKWord, "");
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.FIELD setKWord, String setData){
        return new ScanNode(null, setCommand, setHandler, setKWord, setData);
    }
    /** node for input and output list */
    public static class ScanNode{
        public static final String NULL_TEXT = "NULL";
        public static final int NUM_FIELDS = 5;
        public String lineCol;
        public Keywords.CMD cmd;
        public Keywords.HANDLER h;
        public Keywords.FIELD k;
        public String data;
        
        public ScanNode(String lineCol, Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.FIELD setKWord, String setData){
            this.lineCol = lineCol;
            h = setHandler;
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
                nullSafe(lineCol), 
                nullSafe(cmd), 
                nullSafe(h), 
                nullSafe(k),
                nullSafe(data)
            );
        }
        public String nullSafe(Object obj){//safe toString() for nullable object
            return (obj==null)? NULL_TEXT : obj.toString();
        }
        public String nullSafe(String str){//safe toString() for nullable object
            return (str == null || str.isEmpty())? NULL_TEXT : str;
        }
    }
    
    public static ScanNode newRxPush(String lineCol,TreeNode treeNode){
        return new RxScanNode(lineCol, PUSH, treeNode);
    }
    public static ScanNode newRxPop(String lineCol){
        return new RxScanNode(lineCol, POP, null);
    }
    /** node for rx-specific input and output list items */
    public static class RxScanNode extends ScanNode{
        public static final int NUM_RX_FIELDS = 7;
        public final OP op;
        public final boolean not;
        public final int id;

        public RxScanNode(
                String lineCol, 
                Keywords.CMD setCommand, 
                Keywords.HANDLER setHandler, 
                Keywords.OP setOp, 
                String setData, 
                boolean negate, 
                int id
        ) {
            super(lineCol, setCommand, setHandler, null, setData);
            this.op = setOp;
            this.not = negate;
            this.id = id;
        }
        
        public RxScanNode(
                String lineCol, 
                Keywords.CMD setCommand, 
                TreeNode treeNode
        ) {
            super(
                lineCol, 
                setCommand, 
                RX_BUILDER, 
                null,
                (treeNode==null || treeNode.data==null)? NULL_TEXT : treeNode.data
            );
            this.op = (treeNode==null)? null : treeNode.op;
            this.not = (treeNode==null)? false: treeNode.not;
            this.id = (treeNode==null)? -1: treeNode.id;
        }
        /**Data to string for writing to file
         * @return one line of a csv file */
        @Override
        public String toString(){//one line of a csv file
            return String.format(
                "%s,%s,%s,%s,%s,%b,%d",
                nullSafe(lineCol), 
                nullSafe(cmd),      //push or pop
                nullSafe(h),        // RX_BUILDER
                nullSafe(op),        //operation
                nullSafe(data),     // text payload
                not,
                id
            );  
        }
        public TreeNode toTreeNode(){
            TreeNode treeNode = new TreeNode();
            treeNode.data = this.data;
            treeNode.not = this.not;
            treeNode.id = this.id;
            treeNode.level = 0;
            treeNode.op = this.op;
            //System.out.printf("\n===== \n%s \n%s \n", this.toString(), treeNode.toString());
            return treeNode;
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
