package demos;

import compile.basics.Factory_Node;
import compile.basics.Factory_Node.RxScanNode;
import static compile.basics.Keywords.CHAR_AND;
import static compile.basics.Keywords.CHAR_CPAR;
import static compile.basics.Keywords.CHAR_OPAR;
import static compile.basics.Keywords.CHAR_OR;
import static compile.basics.Keywords.CHAR_SQUOTE;
import static compile.basics.Keywords.TEXT_FIELD_NAME;
import java.util.ArrayList;
import toksource.ScanNodeSource;
import toksource.TextSource_list;

public class RxLogicTree extends RxTree{
    private static RxTree instance;
    
    public static RxTree getInstance(){
        return (instance == null)? (instance = new RxLogicTree()) : instance;
    }
    protected RxLogicTree(){}
    
    private final String lineCol = "line 0 word 0";
    
    @Override
    public TreeNode treeFromRxWord(String text){
        System.out.println("tokenize start: root text: " + text);
        TreeNode root = new TreeNode(text, 0, null);
        boolean more;
        do{
            more = false;
            more |= root.split(CHAR_AND);
            more |= root.split(CHAR_OR);
//            more |= root.split(CHAR_EQUAL);
            more |= root.negate();
            more |= root.unwrap(CHAR_OPAR, CHAR_CPAR);
            more |= root.unquote(CHAR_SQUOTE);
        }while(more);
        balanceLeaves(root);
        return root;
    }
    
    @Override
    public ArrayList<RxScanNode> treeToScanNodeList(TreeNode root){
        ArrayList<TreeNode> nodes = instance.preOrder(root);
        int stackLevel = 0;
        ArrayList<RxScanNode> cmdList = new ArrayList<>();
        for(TreeNode node : nodes){
            //System.out.println(stackLevel + "... "+ node.level);
            while(stackLevel > node.level){
                stackLevel--;
                cmdList.add(
                    (Factory_Node.RxScanNode)Factory_Node.newRxPop(lineCol)
                );
            }
            stackLevel++;
            cmdList.add(
                (RxScanNode)Factory_Node.newRxPush(lineCol, node)
            );
        }
        return cmdList;
    }
    @Override
    public TreeNode treeFromScanNodeSource(ArrayList<String> cmdList){
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
                        treeNode.op = head.op;
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

    private void balanceLeaves(TreeNode root){
        ArrayList<TreeNode> leaves = leaves(root);
        for(TreeNode leaf : leaves){
            char splitChar = findSplitChar(leaf.data);
            if(splitChar == '\0'){
                splitChar = '=';
                leaf.data = TEXT_FIELD_NAME + "=" + leaf.data;
            }
            leaf.split(splitChar);
            leaf.negate();
            leaf.unwrap(CHAR_OPAR, CHAR_CPAR);
            leaf.unquote(CHAR_SQUOTE);
        }
    }
    private char findSplitChar(String text){
        boolean ignore = false;
        char[] splitChars = new char[]{'=', '<', '>'};
        for(int i = 0; i < splitChars.length; i++){
            for(int j = 0; j < text.length()-1; j++){//stops early for abc= nonsense
                if(text.charAt(j) == CHAR_SQUOTE){
                    ignore = !ignore;
                }
                else if(!ignore && text.charAt(j) == splitChars[i]){
                    return splitChars[i];
                }
            }
        }
        return '\0';
    }
}
