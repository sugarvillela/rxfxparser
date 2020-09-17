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
    
    public static ScanNode newRxPush(String lineCol,TreeNode treeNode){
        return new RxScanNode(lineCol, PUSH, treeNode);
    }
    public static ScanNode newRxPop(String lineCol){
        return new RxScanNode(lineCol, POP, null);
    }
    /** node for rx-specific input and output list items */
    public static class RxScanNode extends ScanNode{
        public static final int NUM_RX_FIELDS = 9;
        public final OP op;
        public final boolean not;
        public final int id;
        //public final Keywords.PAR paramType;
        //public final String param;

        public RxScanNode(// 0 text status, 1 push or pop, 2 RX_BUILDER, 3 negate, 4 operation, 5 data format in leaf, 6 text payload, 7 function parameter, 8 unique id
                          String lineCol,             // 0
                          Keywords.CMD setCommand,    // 1
                          Keywords.DATATYPE setDatatype,// 2
                          boolean negate,             // 3
                          OP setOp,                   // 4
                          Keywords.PAR paramType,// 5
                          String setData,             // 6
                          String param,               // 7
                          int id                      // 8
        ) {
            super(lineCol, setCommand, setDatatype, null, setData);
            this.op = setOp;
            this.not = negate;
            this.id = id;
            //this.paramType = paramType;
            //this.param = param;
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
                VAL,
                (treeNode==null)? null : treeNode.data
            );
            if(treeNode == null){
                this.op = null;
                this.not = false;
                this.id = -1;
                //this.paramType = null;
                //this.param = null;
            }
            else{
                this.op = treeNode.op;
                this.not = treeNode.not;
                this.id = treeNode.id;
                //this.paramType = treeNode.paramType;
                //this.param = treeNode.param;
            }
        }
        /**Data to string for writing to file
         * @return one line of a csv file */
        @Override
        public String toString(){//one line of a csv file
            return String.format(// 0 text status, 1 push or pop, 2 RX_BUILDER, 3 negate, 4 operation, 5 data format in leaf, 6 text payload, 7 function parameter, 8 unique id
                "%s,%s,%s,%b,%s,%s,%s,%s,%d",
                Commons.nullSafe(lineCol),  // 0 text status
                Commons.nullSafe(cmd),      // 1 push or pop
                Commons.nullSafe(h),        // 2 RX_BUILDER
                not,                // 3 negate
                Commons.nullSafe(op),       // 4 operation
                //Commons.nullSafe(paramType),// 5 data format in leaf
                Commons.nullSafe(data),     // 6 text payload
                //Commons.nullSafe(param),    // 7 function parameter taken from input text
                id                  // 8 unique id
            );  
        }
        public TreeNode toTreeNode(){
            TreeNode treeNode = new TreeNode();
            treeNode.not = this.not;    // 3 negate
            treeNode.op = this.op;      // 4 operation
            //treeNode.paramType = this.paramType;// 5 data format
            treeNode.data = this.data;  // 6 text payload
            //treeNode.param = this.param;// 7 function parameter
            treeNode.id = this.id;      // 8 unique id
            treeNode.level = 0;

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
