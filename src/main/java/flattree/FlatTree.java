package flattree;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;
import erlog.DevErr;
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

    private Keywords.DATATYPE builderType;

    private final FlatNode[] treeArr;
    private int iNew;

    /*=====Scan Time Build and Extract================================================================================*/

    public FlatTree(Keywords.DATATYPE datatype, TreeNode root) {
        setBuilderType(datatype);
        treeArr = new FlatNode[size(root)];
        iNew = -1;
        flatTreeFromTree(root);
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
    private void flatTreeFromTree(TreeNode root){
        FlatNode newParent = newFlatNode(root, null);
        BuildUsingDepthFirst(root, newParent);
        disp();
    }
    private FlatNode newFlatNode(TreeNode treeSelf, FlatNode flatParent){
        iNew++;
        int iParent = (flatParent == null)? -1 : flatParent.self;
        return (treeArr[iNew] = new FlatNode(iNew, iParent, treeSelf, treeArr));
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
                factoryNode.newScanNode(Keywords.CMD.SET_ATTRIB, builderType, LENGTH, String.valueOf(treeArr.length))
        );
        for(FlatNode flatNode : treeArr){
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

    public FlatNode[] getTreeArr(){
        return treeArr;
    }

    /*=====Parse Time Rebuild=========================================================================================*/

    public FlatTree(Keywords.DATATYPE datatype, int length) {
        setBuilderType(datatype);
        treeArr = new FlatNode[length];
    }

    public void addTo(String scanNodeText){
        FlatNode newNode = new FlatNode(scanNodeText, treeArr);
        iNew = newNode.self;
        treeArr[iNew] = newNode;
    }
    public void addPayNode(DataNode payNode){
        treeArr[iNew].addPayNode(payNode);
    }

    /*=====Debug======================================================================================================*/

    public void disp(){
        System.out.println("Display FlatTree");
        int i = 0;
        for(FlatNode flatNode : treeArr){
            System.out.print("    " + i + ": ");
            System.out.println((flatNode == null)? "NULL" : flatNode.readableContent());
            i++;
        }
        System.out.println("End Display FlatTree");
    }
    public FlatNode[] preOrder(){
        ArrayList<FlatNode> list = new ArrayList<>(treeArr.length);
        treeArr[0].preOrder(list);
        return list.toArray(new FlatNode[treeArr.length]);
    }
    public FlatNode[] breadthFirst(){
        FlatNode[] list = new FlatNode[treeArr.length];
        int i = 0, j = -1;
        while(i < treeArr.length){
            for(FlatNode  node : treeArr){
                if(node.parent == j){
                    System.out.println("add: " + node.toString());
                    list[i++] = node;
                }
            }
            j++;
        }
        return list;
    }
    public void buildString(){
        if(this.builderType != RX_BUILDER){
            System.out.println("FX node");
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        treeArr[0].buildString(list);
        String out = String.join(" ", list);
        System.out.println(out);
    }

    @Override
    public TreeNode treeFromWordPattern(String text) {
        return null;
    }
}
