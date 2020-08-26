package demos;

import static compile.basics.Keywords.CHAR_CPAR;
import static compile.basics.Keywords.CHAR_OPAR;
import static compile.basics.Keywords.CHAR_SQUOTE;
import static compile.basics.Keywords.TEXT_FIELD_NAME;
import demos.RxTree.TreeNode;
import java.util.ArrayList;
import toktools.TK;
import toktools.Tokens_special;

public class RxLeafUtil {
    private static RxLeafUtil instance;
    
    public static RxLeafUtil getInstance(){
        return (instance == null)? (instance = new RxLeafUtil()) : instance;
    }
    protected RxLeafUtil(){
        rxTree = RxTree.getInstance();
    }
    
    private final RxTree rxTree;
    private final Tokens_special T = new Tokens_special("=", "'", TK.IGNORESKIP );
    private final String lineCol = "line 0 word 0";
    
    public void go(TreeNode root){
        ArrayList<TreeNode> leaves = rxTree.leaves(root);
        balance(leaves);
        splitOnSymb(leaves, '=');
        
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
    public void splitOnSymb(ArrayList<TreeNode> leaves, char symb){
        for(TreeNode leaf : leaves){
            leaf.split(symb);
            leaf.negate();
            leaf.unwrap(CHAR_OPAR, CHAR_CPAR);
            leaf.unquote(CHAR_SQUOTE);
        }
    }
    public boolean validate(){
        return true;
    }
}
