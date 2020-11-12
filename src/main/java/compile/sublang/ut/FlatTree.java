package compile.sublang.ut;

import commons.Commons;
import compile.basics.Factory_Node;
import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;
import erlog.DevErr;
import erlog.Erlog;
import interfaces.DataNode;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.DATATYPE.FX_PAY_NODE;
import static compile.basics.Keywords.FIELD.LENGTH;
import static compile.basics.Keywords.FIELD.VAL;
import static compile.basics.Keywords.OP.PAYLOAD;

public class FlatTree extends TreeFactory{ //ArrayList<TreeNode> leaves = leaves(root);
    private static final Factory_Node factoryNode = Factory_Node.getInstance();
    private static final Tokens_special T = new Tokens_special(",", "'", TK.IGNORESKIP );
    private Keywords.DATATYPE builderType;

    private final FlatNode[] treeArray;
    private int iNew;

    /*=====Scan Time Build and Extract================================================================================*/

    public FlatTree(Keywords.DATATYPE datatype, TreeNode root) {
        setBuilderType(datatype);
        treeArray = new FlatNode[size(root)];
        iNew = -1;
        treeFromTree(root);
    }
    private void setBuilderType(Keywords.DATATYPE datatype){
        switch (datatype){
            case RX:
            case RX_BUILDER:
                this.builderType = RX_BUILDER;
                break;
            case FX:
            case FX_BUILDER:
                this.builderType = FX_BUILDER;
                break;
            default:
                DevErr.get(this).kill("Developer: " + datatype);
        }
    }

    private void treeFromTree(TreeNode root){
        FlatNode newParent = newFlatNode(root, null);
        BuildUsingDepthFirst(root, newParent);
        disp();
    }
    private FlatNode newFlatNode(TreeNode treeSelf, FlatNode flatParent){
        iNew++;
        int iParent = (flatParent == null)? -1 : flatParent.self;
        return (treeArray[iNew] = new FlatNode(iNew, iParent, treeSelf));
    }
    private void BuildUsingDepthFirst(TreeNode treeParent, FlatNode flatParent){
        //System.out.println(oneToString(flatParent));
        if(treeParent.nodes != null){
            ArrayList<TreeNode> treeChildNodes = treeParent.nodes;
            for(TreeNode treeSelf : treeChildNodes){
                FlatNode newParent = newFlatNode(treeSelf, flatParent);
                flatParent.addChild(iNew);
                BuildUsingDepthFirst(treeSelf, newParent);
            }
        }
    }

    public ArrayList<Factory_Node.ScanNode> treeToScanNodeList(){
        Keywords.DATATYPE payNodeType = (RX_BUILDER.equals(builderType))? RX_PAY_NODE : FX_PAY_NODE;
        ArrayList<Factory_Node.ScanNode> cmdList = new ArrayList<>();
        cmdList.add(factoryNode.newPushNode(builderType));
        cmdList.add(
                factoryNode.newScanNode(Keywords.CMD.SET_ATTRIB, builderType, LENGTH, String.valueOf(treeArray.length))
        );
        for(FlatNode flatNode : treeArray){
            cmdList.add(
                factoryNode.newScanNode(Keywords.CMD.ADD_TO, builderType, VAL, flatNode.toString())
            );
            if(PAYLOAD.asChar == flatNode.op){
                cmdList.add(nodeFactory.newPushNode(payNodeType));
                for(DataNode payNode : flatNode.payNodes){
                    cmdList.add(
                            nodeFactory.newScanNode(Keywords.CMD.ADD_TO, payNodeType, VAL, payNode.toString())
                    );
                }
                cmdList.add(nodeFactory.newPopNode(payNodeType));
            }
        }
        cmdList.add(factoryNode.newPopNode(builderType));
        return cmdList;
    }

    /*=====Parse Time Rebuild=========================================================================================*/

    public FlatTree(Keywords.DATATYPE datatype, int length) {
        setBuilderType(datatype);
        treeArray = new FlatNode[length];
    }

    public void addTo(FlatNode newNode){
        iNew = newNode.self;
        treeArray[iNew] = newNode;
    }
    public void addPayNode(DataNode payNode){
        treeArray[iNew].addPayNode(payNode);
    }

    /*=====Debug======================================================================================================*/

    public void disp(){
        System.out.println("Display FlatTree");
        int i = 0;
        for(FlatNode flatNode : treeArray){
            System.out.print("    " + i + ": ");
            System.out.println((flatNode == null)? "NULL" : flatNode.readableContent());
            i++;
        }
        System.out.println("End Display FlatTree");
    }

    public static class FlatNode extends DataNode {
        private static final int NUM_FIELDS = 6;
        public final char op;
        public final int parent, self;
        public final int children[];
        public DataNode[] payNodes;
        private int iChild, iPayNode;

        public FlatNode(int iSelf, int iParent, TreeNode treeNode){
            this.op = treeNode.op.asChar;
            self = iSelf;
            parent = iParent;
            children = (treeNode.nodes == null)? null : new int[treeNode.nodes.size()];
            this.payNodes = (treeNode.payNodes == null)?
                    null :
                    treeNode.payNodes.toArray(new DataNode[treeNode.payNodes.size()]);
            iChild = -1;
        }

        public void addChild(int child){
            if(children != null){
                iChild++;
                children[iChild] = child;
            }
        }
        public FlatNode getParent(FlatNode[] flatTree){
            return (parent == -1)? null : flatTree[parent];
        }
        public void rewind(){
            iChild = -1;
        }
        public boolean hasNext(){
            return iChild < (children.length - 1);
        }
        public FlatNode next(FlatNode[] flatTree){
            return flatTree[children[++iChild]];
        }

        @Override
        public String readableContent() {
            return String.format(
                    "op='%c', self=%d, parent=%d, children=%s, payNodes=%s ",
                    op,
                    self,
                    parent,
                    Commons.nullSafe(children),
                    allReadablePayloadContent()
            );
        }

        @Override
        public String toString(){
            int childLen =  (children == null)? 0 : children.length;
            int payLen =    (payNodes == null)? 0 : payNodes.length;
            return String.format("%c,%d,%d,%d,%s,%d", op, self, parent, childLen, Commons.nullSafe(children), payLen);
        }

        /*=====Rebuild================================================================================================*/

        public FlatNode(String scanNodeText){
            String[] tok = T.toArr(scanNodeText);
            //Commons.disp(tok, "FlatNode Constructor");
            if(tok.length != NUM_FIELDS){
                Erlog.get(this).set("Bad scan node text size" + tok.length, scanNodeText);
            }
            op = tok[0].charAt(0);
            self = Commons.undoNullSafe_int(tok[1]);
            parent = Commons.undoNullSafe_int(tok[2]);
            int childLen = Commons.undoNullSafe_int(tok[3]);
            children = (childLen == 0)? null : Commons.undoNullSafe_intArray(tok[4]);
            int payLen = Commons.undoNullSafe_int(tok[5]);
            payNodes = (payLen == 0)? null : new DataNode[payLen];
            iChild = -1;
            iPayNode = -1;
        }
        public void addPayNode(DataNode payNode){
            payNodes[++iPayNode] = payNode;
        }
        private String allReadablePayloadContent(){
            if(payNodes == null){
                return "None";
            }
            ArrayList<String> out = new ArrayList<>();
            for(DataNode rxPayNode : payNodes){
                out.add("\t\t" + rxPayNode.readableContent());
            }
            return "\n" + String.join("\n", out);
        }
    }

    @Override
    public TreeNode treeFromWordPattern(String text) {
        return null;
    }
}
