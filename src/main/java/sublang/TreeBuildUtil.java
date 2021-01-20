package sublang;

import namegen.NameGenSimple;
import runstate.Glob;
import scannode.ScanNode;
import scannode.ScanNodeFactory;
import langdef.Keywords;

import static langdef.Keywords.DATATYPE.*;
import static langdef.Keywords.DATATYPE.RX_PAY_NODE;
import static langdef.Keywords.FIELD.VAL;
import static langdef.Keywords.OP.*;
import static langdef.Keywords.OP.SQUOTE;

import commons.Commons;
import erlog.Erlog;
import java.util.ArrayList;

import interfaces.DataNode;
import sublang.factories.PayNodes;
import sublang.treenode.FxTreeNode;
import sublang.treenode.RxTreeNode;
import sublang.treenode.TreeNodeBase;
import toksource.ScanNodeSource;
import toksource.TextSource_list;

public class TreeBuildUtil {
    private static TreeBuildUtil instance;

    public static TreeBuildUtil init(){
        return (instance == null)? (instance = new TreeBuildUtil()) : instance;
    }

    private TreeBuildUtil(){
    }

    public TreeNodeBase newTreeNode(Keywords.DATATYPE rxOrFx, String data, int level, TreeNodeBase parent){
        return (RX.equals(rxOrFx))? new RxTreeNode(data, level, parent) : new FxTreeNode(data, level, parent);
    }
    public TreeNodeBase newTreeNode(Keywords.DATATYPE rxOrFx, ScanNode scanNode){
        return (RX.equals(rxOrFx))? new RxTreeNode(scanNode) : new FxTreeNode(scanNode);
    }

    /*=====Root display methods=======================================================================================*/

    public void dispPreOrder(TreeNodeBase root){
        ArrayList<TreeNodeBase> preOrder = preOrder(root);
        ArrayList<String> readable = new ArrayList<>();
        for(TreeNodeBase node : preOrder){
            readable.add(node.readableContent());
        }
        Commons.disp(readable, "\nPreOrder");
    }

    public void dispBreadthFirst(TreeNodeBase root){
        //System.out.println("Size: " + root.size());
        ArrayList<TreeNodeBase>[] levels = breadthFirst(root);
        ArrayList<String> disp = new ArrayList<>();
        for(int i = 0; i < levels.length; i++){
            disp.add("Level: " + i);
            for(TreeNodeBase node : levels[i]){
                disp.add(node.readableContent());
            }
        }
        Commons.disp(disp, "\nBreadthFirst");
    }
    
    public void dispLeaves(TreeNodeBase root){
        ArrayList<TreeNodeBase> leaves = leaves(root);
        ArrayList<String> readable = new ArrayList<>();
        for(TreeNodeBase leaf : leaves){
            readable.add(leaf.readableContent());
        }
        Commons.disp(readable, "\nLeaves");
    }

    /*=====Root access methods========================================================================================*/

    public ArrayList<TreeNodeBase> preOrder(TreeNodeBase root){
        ArrayList<TreeNodeBase> preOrder = new ArrayList<>();
        root.preOrder(preOrder);
        return preOrder;
    }

    public ArrayList<TreeNodeBase>[] breadthFirst(TreeNodeBase root){
        int max = root.treeDepth()+1;
        ArrayList<TreeNodeBase>[] levels = new ArrayList[max];
        for(int i = 0; i < max; i++){
            levels[i] = new ArrayList<>();
        }
        root.breadthFirst(levels);
        return levels;
    }
    
    public ArrayList<TreeNodeBase> leaves(TreeNodeBase root){
        ArrayList<TreeNodeBase> leaves = new ArrayList<>();
        root.leaves(leaves);
        return leaves;
    }

    /*=====Tree build and convert methods=============================================================================*/

    /* For later */
    public TreeNodeBase treeFromScanNodeSource(Keywords.DATATYPE rxOrFx, ArrayList<ScanNode> cmdList){
        ArrayList<String> textCommands = new ArrayList<>();
        for(ScanNode inputNode : cmdList){
            textCommands.add(inputNode.toString());
        }
        ScanNodeSource source = new ScanNodeSource(new TextSource_list(textCommands));
        PayNodes.PayNodeFactory factory = PayNodes.getFactory(rxOrFx);
        TreeNodeBase reroot = null, head = null;
        while(source.hasNext()){
            ScanNode scanNode = source.nextNode();
            switch(scanNode.datatype){
                case RX_BUILDER:
                case FX_BUILDER:
                    switch(scanNode.cmd){
                        case PUSH:
                            if(reroot == null){
                                reroot = head = this.newTreeNode(rxOrFx, scanNode);
                            }
                            else{
                                TreeNodeBase treeNode = this.newTreeNode(rxOrFx, scanNode);
                                treeNode.level = head.level + 1;
                                treeNode.parent = head;
                                head.addChild(treeNode);
                                head = treeNode;
                            }
                            break;
                        case POP:
                            head = head.parent;
                            if(head == null){
                                return reroot;
                            }
                            break;
                    }
                    break;
                case RX_PAY_NODE:
                case FX_PAY_NODE:
                    switch(scanNode.cmd){
                        case PUSH:
                            head.payNodes = new ArrayList<>();
                            break;
                        case ADD_TO:
                            head.payNodes.add(factory.payNodeFromScanNode(scanNode.data));
                            break;
                        case POP:
                            break;
                    }
                    break;
            }

        }
        return reroot;
    }

    /*=====Tree build and convert methods=============================================================================*/

    public int size(TreeNodeBase root){
        return preOrder(root).size();
    }

    public ArrayList<ScanNode> treeToScanNodeList(Keywords.DATATYPE datatype, TreeNodeBase root){
        final ScanNodeFactory nodeFactory = Glob.SCAN_NODE_FACTORY;
        Keywords.DATATYPE builderType;
        Keywords.DATATYPE payNodeType;
        if(RX.equals(datatype)){
            builderType = RX_BUILDER;
            payNodeType = RX_PAY_NODE;
        }
        else{
            builderType = FX_BUILDER;
            payNodeType = FX_PAY_NODE;
        }
        ArrayList<TreeNodeBase> nodes = preOrder(root);
        int stackLevel = 0;
        ArrayList<ScanNode> cmdList = new ArrayList<>();
        for(TreeNodeBase node : nodes){

            while(stackLevel > node.level){
                stackLevel--;
                cmdList.add(nodeFactory.newPopNode(builderType));
            }
            stackLevel++;
            cmdList.add(
                    nodeFactory.newScanNode(Keywords.CMD.PUSH, builderType, VAL, node.toString())
            );
            if(PAYLOAD.equals(node.op)){
                cmdList.add(nodeFactory.newPushNode(payNodeType));
                for(DataNode payNode : node.payNodes){
                    cmdList.add(
                            nodeFactory.newScanNode(Keywords.CMD.ADD_TO, payNodeType, VAL, payNode.toString())
                    );
                }
                cmdList.add(nodeFactory.newPopNode(payNodeType));
            }
        }
        while(stackLevel > 0){
            stackLevel--;
            cmdList.add(nodeFactory.newPopNode(builderType));
        }
        return cmdList;
    }

    public boolean assertEqual(TreeNodeBase root1, TreeNodeBase root2){
        ArrayList<TreeNodeBase>[] levels1 = this.breadthFirst(root1);
        ArrayList<TreeNodeBase>[] levels2 = this.breadthFirst(root2);
        if(levels1.length != levels2.length){
            Erlog.get(this).set("fail: levels1.length != levels2.length");
            return false;
        }
        for(int i = 0; i<levels1.length; i++){
            int len1 = levels1[i].size();
            int len2 = levels2[i].size();
            if(len1 != len2){
                Erlog.get(this).set("fail: len1 != len2");
                return false;
            }
            for(int j = 0; j < len1; j++){
                String node1 = levels1[i].get(j).readableContent();
                String node2 = levels2[i].get(j).readableContent();
                boolean equal = node1.equals(node2);
                System.out.printf("\n%d:%d: equal: %b\n    %s \n    %s \n", i, j, equal, node1, node2);
                if(!equal){
                    //Error!
                    System.out.println("not equal");
                }
            }
        }
        return true;
    }

}
