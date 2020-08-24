package demos;

import static compile.basics.Keywords.KWORD.BRANCH;
import static compile.basics.Keywords.KWORD.LEAF;
import commons.Commons;
import compile.basics.Factory_Node;
import compile.basics.Factory_Node.RxScanNode;
import compile.basics.Factory_Node.ScanNode;
import compile.basics.Keywords;
import demos.RxTree.TreeNode;
import java.util.ArrayList;//RX_AND, RX_OR, RX_NOT, RX_EQ,
import toksource.ScanNodeSource;
import toksource.TextSource_list;
//import java.util.Stack;

public class RxTreeUtil {
    private static RxTreeUtil instance;
    
    public static RxTreeUtil getInstance(){
        return (instance == null)? (instance = new RxTreeUtil()) : instance;
    }
    protected RxTreeUtil(){
        rxTree = RxTree.getInstance();
    }
    
    private final RxTree rxTree;
    private int stackLevel;
    //private Stack<TreeNode> stack;
    private ArrayList<String> cmdList;
    private TreeNode reroot, head;
    private final String lineCol = "line 0 word 0";
    
    public void test1(){
        //String text = "~(A=a&B='b')&(C=c&D=d)&~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        TreeNode root = rxTree.toTree(text);
        rxTree.dispBreadthFirst(root);
        //rxTree.dispPreOrder(root);
    }
    public void genScanNodes(TreeNode root){
        ArrayList<TreeNode> nodes = rxTree.preOrder(root);
        stackLevel = 0;
        cmdList = new ArrayList<>();
        for(TreeNode node : nodes){
            System.out.println(stackLevel + "... "+ node.level);
            while(stackLevel > node.level){
                pop();
                if(stackLevel < 0){
                    System.out.println("runaway pop 1");
                    break;
                }
            }
            push(node);
            if(stackLevel > 10){
                System.out.println("runaway push");
                break;
            }
            else if(stackLevel < 0){
                System.out.println("runaway pop 2");
                break;
            }
        }
        while(stackLevel > 0){
            pop();
        }
        Commons.disp(cmdList, "\nCommandList");
        rebuild();
    }
    public void rebuild(){
        ScanNodeSource source = new ScanNodeSource(new TextSource_list(cmdList));
        if(source.hasData()){
            System.out.println("GOOD: source.hasData()");
        }
        else{
            System.out.println("!source.hasData()");
        }
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
                        head.addChild(treeNode);
                        head = treeNode;
                    }
                    break;
                case POP:
                    head = head.parent;
                    if(head == null){
                        System.out.println("head == null");
                    }
                    break;
            }
//            System.out.println(scanNode);
//            TreeNode treeNode = scanNode.toTreeNode();
//            System.out.println(treeNode);
//            System.out.println("===============");
        }
        rxTree.dispBreadthFirst(reroot);
    }
    public void push(TreeNode node){
        stackLevel++;
        RxScanNode scanNode = (RxScanNode)Factory_Node.newRxPush(lineCol, node);
        String cmd = scanNode.toString();
        System.out.println(cmd);
        cmdList.add(cmd);
    }
    public void pop(){
        stackLevel--;
        RxScanNode scanNode = (RxScanNode)Factory_Node.newRxPop(lineCol);
        String cmd = scanNode.toString();
        System.out.println(cmd);
        cmdList.add(cmd);
    }
       
    public TreeNode buildNode(RxScanNode scanNode){
        //line 0 word 0,PUSH,RX_WORD,RX_AND,NULL,false,0
        TreeNode node = scanNode.toTreeNode();
        return null;
    }
    
}
