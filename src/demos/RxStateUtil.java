package demos;

import static compile.basics.Keywords.TEXT_FIELD_NAME;
import demos.RxTree.TreeNode;
import java.util.ArrayList;
import toktools.TK;
import toktools.Tokens_special;

public class RxStateUtil {
    private static RxStateUtil instance;
    
    public static RxStateUtil getInstance(){
        return (instance == null)? (instance = new RxStateUtil()) : instance;
    }
    protected RxStateUtil(){
        rxTree = RxTree.getInstance();
    }
    
    private final RxTree rxTree;
    private final Tokens_special T = new Tokens_special("", "'", TK.IGNORESKIP );
    private final String lineCol = "line 0 word 0";
    
    public void go(TreeNode root){
        ArrayList<TreeNode> leaves = rxTree.leaves(root);
        balance(leaves);
        
    }
    public void balance(ArrayList<TreeNode> leaves){
        for(TreeNode leaf : leaves){
            T.parse(leaf.data);
            ArrayList<String> tokens = T.getTokens();
            if(tokens.size() != 2){
                leaf.data = TEXT_FIELD_NAME + "=" + leaf.data;
            }
        }
    }
    public boolean validate(){
        return true;
    }
}
