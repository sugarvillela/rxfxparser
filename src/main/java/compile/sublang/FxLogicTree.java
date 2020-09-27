package compile.sublang;

import compile.sublang.factories.PayNodes;
import compile.sublang.factories.TreeFactory;
import compile.sublang.ut.FxAccessUtil;
import compile.sublang.ut.FxFunUtil;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static compile.basics.Keywords.DATATYPE.FX;
import static compile.basics.Keywords.OP.*;

public class FxLogicTree  extends TreeFactory {
    private static final ConstantTable CONSTANT_TABLE = ConstantTable.getInstance();
    protected static final Tokens_special dotTokenizer = new Tokens_special(".", "'", TK.IGNORESKIP );
    private static final FxAccessUtil ACCESS_UTIL = FxAccessUtil.getInstance();
    private static final FxFunUtil FUN_UTIL = FxFunUtil.getInstance();

    private static FxLogicTree instance;

    public static FxLogicTree getInstance(){
        return (instance == null)? (instance = new FxLogicTree()) : instance;
    }
    protected FxLogicTree(){}

    private ListTable listTable;

    @Override
    public TreeNode treeFromWordPattern(String text){
        System.out.println("tokenize start: root text: " + text);
        listTable = ListTable.getInstance();
        if(listTable == null){
            Erlog.get(this).set("LIST<*> items are not defined");
            return null;
        }
        TreeNode root = TreeFactory.newTreeNode(FX, text, 0, null);
        boolean more;
        do{
            more = false;
            more |= root.split(FX, AND.asChar);
            more |= root.split(FX, OR.asChar);
            more |= root.unwrap(OPAR.asChar, CPAR.asChar);
        }while(more);

        ArrayList<TreeNode> leaves = leaves(root);
        readConstants(leaves);    // read constants before extend
        setPayNodes(leaves);
        dispBreadthFirst(root);
        //Erlog.get(this).set("Happy stop");
        return root;
    }

    protected void readConstants(ArrayList<TreeNode> leaves){
        for(TreeNode leaf : leaves){
            String read = CONSTANT_TABLE.readConstant(leaf.data);
            if(read != null){
                leaf.data = read;
            }
        }
    }
    private void setPayNodes(ArrayList<TreeNode> leaves){
        PayNodes.FxPayNodeFactory factory = (PayNodes.FxPayNodeFactory) PayNodes.getFactory(FX);
        String[] tok;

        for(TreeNode leaf : leaves){
            System.out.println();
            System.out.println("=======> setPayNodes: " + leaf.data);
            tok = dotTokenizer.toArr(leaf.data);
            if(tok.length == 2){
                factory.clear();
                ACCESS_UTIL.findAndSetParam(leaf, tok[0]);
                factory.addPayNode();

                FUN_UTIL.findAndSetParam(leaf, tok[1]);
                factory.addPayNode();
                leaf.payNodes = factory.getPayNodes();
            }
            else{
                Erlog.get(this).set("FX language requires 'Access Operator' followed by 'Function'", leaf.data);
            }
        }
    }
}
