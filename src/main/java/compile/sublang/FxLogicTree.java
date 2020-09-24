package compile.sublang;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import compile.basics.RxFxTreeFactory;
import compile.sublang.ut.FxParamUtil;
import compile.sublang.factories.PayNodes;
import compile.sublang.ut.ParamUtil;
import compile.sublang.ut.RxParamUtil;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static compile.basics.Keywords.DATATYPE.FX;
import static compile.basics.Keywords.DATATYPE.RX;
import static compile.basics.Keywords.OP.*;

public class FxLogicTree  extends RxFxTreeFactory {
    private static final ConstantTable CONSTANT_TABLE = ConstantTable.getInstance();
    protected static final Tokens_special dotTokenizer = new Tokens_special(".", "'", TK.IGNORESKIP );
    private static final FxParamUtil PARAM_UTIL = (FxParamUtil) ParamUtil.getParamUtil(FX);

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
        setPayNodes(leaves);
        dispBreadthFirst(root);
        Erlog.get(this).set("Happy stop");
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
        //PayNodes.FxPayNodeFactory factory = (PayNodes.FxPayNodeFactory) PayNodes.getFactory(FX);
        String[] tok;

        for(TreeNode leaf : leaves){
            System.out.println();
            System.out.println("=======> setPayNodes: " + leaf.data);
            tok = dotTokenizer.toArr(leaf.data);

            for(int i = 0; i < tok.length; i++){
                PARAM_UTIL.findAndSetParam(leaf, tok[i]);
                System.out.println(i + ": " + tok[i]);
                System.out.println(PARAM_UTIL.getMainText());
                System.out.println(PARAM_UTIL.getBracketText());
                System.out.println(PARAM_UTIL.getRangeLow());
                System.out.println(PARAM_UTIL.getRangeHigh());
                System.out.println();
                //factory.add(tok[i]);
            }
            //leaf.payNodes = factory.getPayNodes();
            //factory.clear();
        }
    }
    @Override
    public ArrayList<Factory_Node.ScanNode> treeToScanNodeList(String lineCol, TreeNode root) {
        return null;
    }

    @Override
    public TreeNode treeFromScanNodeSource(Keywords.DATATYPE datatype, ArrayList<Factory_Node.ScanNode> cmdList) {
        return null;
    }
    /*



     * */
}
