package compile.parse.ut;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import compile.parse.Base_ParseItem;
import compile.sublang.RxLogicTree;
import compile.sublang.factories.PayNodes;
import compile.sublang.factories.TreeFactory;
import toksource.ScanNodeSource;

import java.util.ArrayList;
import static compile.basics.Keywords.DATATYPE.*;

public class RxFxBuilder{
    private final Base_ParseItem parent;
    private final ScanNodeSource source;
    private final Keywords.DATATYPE datatype;
    private final PayNodes.PayNodeFactory factory;
    private TreeFactory.TreeNode root, head;
    boolean popParent;

    public RxFxBuilder(Keywords.DATATYPE datatype, ScanNodeSource source, Base_ParseItem parent){
        this.parent = parent;
        this.source = source;
        this.datatype = datatype = (RX_WORD.equals(datatype))? RX : FX;
        factory = PayNodes.getFactory(datatype);
        root = null;
        head = null;
        popParent = false;
    }
    public void build(){
        System.out.println("RxFxBuilder go!");
        while(!popParent && source.hasNext()){
            readNode(source.nextNode());
        }
        RxLogicTree.getInstance().dispBreadthFirst(root);
    }
    public TreeFactory.TreeNode get(){
        return root;
    }
    private void readNode(Factory_Node.ScanNode currNode){
        System.out.println(currNode);
        switch(currNode.datatype){
            case RX_BUILDER:
            case FX_BUILDER:
                readBuilderCmd(currNode);
                break;
            case RX_PAY_NODE:
            case FX_PAY_NODE:
                readPayNodeCmd(currNode);
                break;
            default:
                readParentCmd(currNode);
        }
    }
    private void readParentCmd(Factory_Node.ScanNode currNode){
        switch(currNode.cmd){
            case SET_ATTRIB:
                parent.setAttrib(currNode);
                break;
            case POP:
                popParent = true;
        }
    }
    private void readBuilderCmd(Factory_Node.ScanNode currNode){
        switch(currNode.cmd){
            case PUSH:
                if(root == null){
                    root = head = TreeFactory.newTreeNode(datatype, currNode);
                }
                else{
                    TreeFactory.TreeNode treeNode = TreeFactory.newTreeNode(datatype, currNode);
                    treeNode.level = head.level + 1;
                    treeNode.parent = head;
                    head.addChildExternal(treeNode);
                    head = treeNode;
                }
                break;
            case POP:
                head = head.parent;
                break;
        }
    }
    private void readPayNodeCmd(Factory_Node.ScanNode currNode){
        switch(currNode.cmd){
            case PUSH:
                head.payNodes = new ArrayList<>();
                break;
            case ADD_TO:
                head.payNodes.add(factory.payNodeFromScanNode(currNode.data));
                break;
            case POP:
                break;
        }
    }
}
