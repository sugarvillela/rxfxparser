package demos;

import compile.basics.Factory_Node;
import compile.basics.Factory_Node.RxScanNode;
import demos.RxTree.TreeNode;
import java.util.ArrayList;//RX_AND, RX_OR, RX_NOT, RX_EQ,
import toksource.ScanNodeSource;
import toksource.TextSource_list;

public class RxTreeUtil {
    private static RxTreeUtil instance;
    
    public static RxTreeUtil getInstance(){
        return (instance == null)? (instance = new RxTreeUtil()) : instance;
    }
    protected RxTreeUtil(){
        rxTree = RxTree.getInstance();
        rxLeafUtil = new RxLeafUtil();
    }
    
    private final RxTree rxTree;
    private final RxLeafUtil rxLeafUtil;
    private final String lineCol = "line 0 word 0";
    
    public void test1(){
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        TreeNode root = rxTree.toTree(text);
        rxTree.dispBreadthFirst(root);
        //rxTree.dispPreOrder(root);
    }
    public void test2(){
        //String text = "~(A=a&B='b')&(C=c&D=d)&~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        TreeNode root = rxTree.toTree(text);
        //rxTree.dispPreOrder(root);
        //rxTree.dispLeaves(root);
        rxTree.dispBreadthFirst(root);
        ArrayList<RxScanNode> cmdList = genScanNodes(root);
        //Commons.disp(cmdList, "\nCommandList");
        ArrayList<String> strList = scanNodesToString(cmdList);
        TreeNode reroot = rebuild(strList);
        //rxTree.dispPreOrder(reroot);
        rxTree.dispBreadthFirst(root);
    }
    public void test3(){
        //String text = "~(A=a&B='b')&(C=c&D=d)&~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        TreeNode root = rxTree.toTree(text);
        ArrayList<RxScanNode> cmdList = genScanNodes(root);
        ArrayList<String> strList = scanNodesToString(cmdList);
        TreeNode reroot = rebuild(strList);
        assertEqual(root, reroot);
    }
    public void test4(){
        String text = "~((A=a&B='b')&(C=c&D=d))&~(E=e&F=f)&'G'";
        //String text = "~(~(A=a&B=~'b'))";//&(C=c)&'D'='d'|~(E=e&F=f)&'G'
        TreeNode root = rxTree.toTree(text);
        rxTree.dispBreadthFirst(root);
//        rxLeafUtil.go(root);
//        rxTree.dispBreadthFirst(root);
    }
    public boolean assertEqual(TreeNode root1, TreeNode root2){
        ArrayList<TreeNode>[] levels1 = rxTree.breadthFirst(root1);
        ArrayList<TreeNode>[] levels2 = rxTree.breadthFirst(root2);
        if(levels1.length != levels2.length){
            System.out.println("fail: levels1.length != levels2.length");
            return false;
        }
        for(int i = 0; i<levels1.length; i++){
            int len1 = levels1[i].size();
            int len2 = levels2[i].size();
            if(len1 != len2){
                System.out.println("fail: len1 != len2");
                return false;
            }
            for(int j = 0; j < len1; j++){
                String node1 = levels1[i].get(j).toString();
                String node2 = levels2[i].get(j).toString();
                System.out.printf("\n%d:%d: \n    %s \n    %s \n", i, j, node1, node2);
                if(!node1.equals(node2)){
                    System.out.println("NOT EQUAL");
                }
            }
        }
        return true;
    }
    
    public ArrayList<RxScanNode> genScanNodes(TreeNode root){
        ArrayList<TreeNode> nodes = rxTree.preOrder(root);
        int stackLevel = 0;
        ArrayList<RxScanNode> cmdList = new ArrayList<>();
        for(TreeNode node : nodes){
            //System.out.println(stackLevel + "... "+ node.level);
            while(stackLevel > node.level){
                stackLevel = pop(cmdList, stackLevel);
                if(stackLevel < 0){
                    System.out.println("runaway pop 1");
                    break;
                }
            }
            stackLevel = push(node, cmdList, stackLevel);
            if(stackLevel > 10){
                System.out.println("runaway push");
                break;
            }
            else if(stackLevel < 0){
                System.out.println("runaway pop 2");
                break;
            }
        }
//        while(stackLevel > 0){
//            //stackLevel = pop(cmdList, stackLevel);
//        }
        return cmdList;
    }
    
    public int push(TreeNode node, ArrayList<RxScanNode> cmdList, int stackLevel){
        RxScanNode scanNode = (RxScanNode)Factory_Node.newRxPush(lineCol, node);
        //String cmd = scanNode.toString();
        //System.out.println(cmd);
        cmdList.add(scanNode);
        return stackLevel + 1;
    }
    public int pop(ArrayList<RxScanNode> cmdList, int stackLevel){
        RxScanNode scanNode = (RxScanNode)Factory_Node.newRxPop(lineCol);
        //String cmd = scanNode.toString();
        //System.out.println(cmd);
        cmdList.add(scanNode);
        return stackLevel - 1;
    }
    public ArrayList<String> scanNodesToString(ArrayList<RxScanNode> scanNodes){
        ArrayList<String> nodesToString = new ArrayList<>();
        for(RxScanNode node : scanNodes){
            nodesToString.add(node.toString());
        }
        return nodesToString;
    }
    
    public TreeNode rebuild(ArrayList<String> cmdList){
        ScanNodeSource source = new ScanNodeSource(new TextSource_list(cmdList));
        TreeNode reroot = null, head = null;
        while(source.hasNext()){
            RxScanNode scanNode = (RxScanNode)source.nextNode();
            switch(scanNode.cmd){
                case PUSH:
                    if(reroot == null){
                        reroot = head = scanNode.toTreeNode();
                    }
                    else{
                        TreeNode treeNode = scanNode.toTreeNode();
                        treeNode.level = head.level + 1;
                        treeNode.parent = head;
                        head.addChildExternal(treeNode);
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
        }
        return reroot;
    }
   
}
