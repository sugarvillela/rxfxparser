package demos;

import commons.Commons;
import compile.basics.Factory_Node.RxScanNode;
import demos.RxTree.TreeNode;
import java.util.ArrayList;//RX_AND, RX_OR, RX_NOT, RX_EQ,

public class RxTree_ {
    private static RxTree_ instance;
    
    public static RxTree_ getInstance(){
        return (instance == null)? (instance = new RxTree_()) : instance;
    }
    protected RxTree_(){
        rxTree = RxLogicTree.getInstance();
    }
    
    private final RxTree rxTree;
    
    public void test1(){
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        TreeNode root = rxTree.treeFromRxWord(text);
        rxTree.dispBreadthFirst(root);
        //rxTree.dispPreOrder(root);
    }
    public void test2(){
        //String text = "~(A=a&B='b')&(C=c&D=d)&~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        TreeNode root = rxTree.treeFromRxWord(text);
        //rxTree.dispPreOrder(root);
        //rxTree.dispLeaves(root);
        rxTree.dispBreadthFirst(root);
        ArrayList<RxScanNode> cmdList = rxTree.treeToScanNodeList(root);
        //Commons.disp(cmdList, "\nCommandList");
        ArrayList<String> strList = scanNodesToString(cmdList);
        TreeNode reroot = rxTree.treeFromScanNodeSource(strList);
        //rxTree.dispPreOrder(reroot);
        rxTree.dispBreadthFirst(root);
    }
    public void test3(){
        //String text = "~(A=a&B='b')&(C=c&D=d)&~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        String text = "~(A=a&B='b')";//"dru='&'&LEN()=2";
        TreeNode root = rxTree.treeFromRxWord(text);
        ArrayList<RxScanNode> cmdList = rxTree.treeToScanNodeList(root);
        //Commons.disp(cmdList);
        ArrayList<String> strList = scanNodesToString(cmdList);
        TreeNode reroot = rxTree.treeFromScanNodeSource(strList);
        assertEqual(root, reroot);
    }
    public void test4(){
        String text = "~((myFunction()=6&myAge<65)&(C=c&D>d))&~(weightOfEarth>157pounds&state.done=true)&'G'";
        //String text = "~(~(A=a&B=~'b'))";//&(C=c)&'D'='d'|~(E=e&F=f)&'G'
        TreeNode root = rxTree.treeFromRxWord(text);
        rxTree.dispBreadthFirst(root);
        //rxLeafUtil.finishAndValidate(root);
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
    

    public ArrayList<String> scanNodesToString(ArrayList<RxScanNode> scanNodes){
        ArrayList<String> nodesToString = new ArrayList<>();
        for(RxScanNode node : scanNodes){
            nodesToString.add(node.toString());
        }
        return nodesToString;
    }
}
