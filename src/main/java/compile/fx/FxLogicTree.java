package compile.fx;

import compile.basics.Factory_Node;
import compile.basics.RxFxTreeFactory;
import compile.fx.ut.FxParamUtil;
import compile.rx.factories.Factory_PayNode;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static compile.basics.Keywords.OP.*;

public class FxLogicTree  extends RxFxTreeFactory {
    private static final ConstantTable CONSTANT_TABLE = ConstantTable.getInstance();
    protected static final Tokens_special dotTokenizer = new Tokens_special(".", "'", TK.IGNORESKIP );
    private static final FxParamUtil PARAM_UTIL = FxParamUtil.getInstance();

    private static FxLogicTree instance;

    public static FxLogicTree getInstance(){
        return (instance == null)? (instance = new FxLogicTree()) : instance;
    }
    protected FxLogicTree(){
        super(new FxTreeNodeFactory());
    }

    private ListTable listTable;

    @Override
    public TreeNode treeFromWordPattern(String text){
        System.out.println("tokenize start: root text: " + text);
        listTable = ListTable.getInstance();
        if(listTable == null){
            Erlog.get(this).set("LIST<*> items are not defined");
            return null;
        }
        TreeNode root = treeNodeFactory.get(text, 0, null);
        boolean more;
        do{
            more = false;
            more |= root.split(this, AND.asChar);
            more |= root.split(this, OR.asChar);
            more |= root.unwrap(OPAR.asChar, CPAR.asChar);
        }while(more);

        ArrayList<TreeNode> leaves = leaves(root);
        readConstants(leaves);    // read constants before extend
        dispBreadthFirst(root);
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
        Factory_PayNode factory = new Factory_PayNode();
        String[] tok;

        for(TreeNode leaf : leaves){
            tok = dotTokenizer.toArr(leaf.data);

            for(int i = 0; i < tok.length; i++){
                PARAM_UTIL.findAndSetParam(leaf, tok[i]);
                //factory.add(tok[i]);
            }
            leaf.payNodes = factory.getPayNodes();
            factory.clear();
        }
    }
    @Override
    public ArrayList<Factory_Node.ScanNode> treeToScanNodeList(String lineCol, TreeNode root) {
        return null;
    }

    @Override
    public TreeNode treeFromScanNodeSource(ArrayList<Factory_Node.ScanNode> cmdList) {
        return null;
    }
    /*



     * */
}
