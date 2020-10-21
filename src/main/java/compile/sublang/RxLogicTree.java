package compile.sublang;

import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.DEFAULT_FIELD_FORMAT;
import static compile.basics.Keywords.OP.*;
import static compile.basics.Keywords.RX_PAR.CATEGORY_ITEM;

import java.util.ArrayList;

import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;
import compile.sublang.factories.PayNodes;
import compile.sublang.ut.RxParamUtil;
import compile.sublang.ut.ValidatorRx;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTableScanLoader;
import compile.symboltable.ListTable;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

public class RxLogicTree extends TreeFactory {
    protected static final ConstantTable CONSTANT_TABLE = ConstantTable.getInstance();
    protected static final Tokens_special dotTokenizer = new Tokens_special(".", "'", TK.IGNORESKIP );
    private static final RxParamUtil PARAM_UTIL = RxParamUtil.getInstance();
    private static TreeFactory instance;
    
    public static TreeFactory getInstance(){
        return (instance == null)? (instance = new RxLogicTree()) : instance;
    }
    protected RxLogicTree(){}

    private ListTable listTable;
    
    @Override
    public TreeNode treeFromWordPattern(String text){
        //System.out.println("tokenize start: root text: " + text);
        listTable = ListTable.getInstance();;
        if(listTable == null){
            Erlog.get(this).set("LIST<*> items are not defined");
            return null;
        }
        TreeNode root = TreeFactory.newTreeNode(RX, text, 0, null);
        boolean more;
        do{
            more = false;
            more |= root.split(RX, AND.asChar);
            more |= root.split(RX, OR.asChar);
//            more |= root.split(CHAR_EQUAL);
            more |= root.negate();
            more |= root.unwrap(OPAR.asChar, CPAR.asChar);
            more |= root.unquote(SQUOTE.asChar);
        }while(more);

        ArrayList<TreeNode> leaves = leaves(root);
        readConstants(leaves);    // read constants before extend
        extendLeaves(leaves);     // fix, split and unwrap

        leaves = leaves(root);    // recalculate after extend
        readConstants(leaves);    // read constants again after extend
        setPayNodes(leaves);
        //dispBreadthFirst(root);
        //dispLeaves(root);
        validateOperations(leaves);
        //Erlog.get(this).set("Happy stop");
        return root;
    }

    private void readConstants(ArrayList<TreeNode> leaves){
        for(TreeNode leaf : leaves){
            String[] tok = dotTokenizer.toArr(leaf.data);
            for(int i = 0; i < tok.length; i++){
                String read = CONSTANT_TABLE.readConstant(tok[i]);
                if(read != null){
                    tok[i] = read;
                }
            }
            leaf.data = String.join(".", tok);
        }
    }
    private void extendLeaves(ArrayList<TreeNode> leaves){
        for(TreeNode leaf : leaves){
            char splitChar = findSplitChar(leaf.data);
            if( // Optional text: add fields to balance the statement
                    splitChar == '\0' &&
                    this.balanceLeaf(leaf, leaf.data)
            ){
                splitChar = '=';
            }
            leaf.split(RX, splitChar);
            leaf.negate();
            leaf.unwrap(OPAR.asChar, CPAR.asChar);
            leaf.unquote(SQUOTE.asChar);
        }
    }
    private char findSplitChar(String text){
        boolean ignore = false;
        char[] splitChars = new char[]{'=', '<', '>'};
        for(int i = 0; i < splitChars.length; i++){
            for(int j = 0; j < text.length()-1; j++){//stops early for abc= nonsense
                if(text.charAt(j) == SQUOTE.asChar){
                    ignore = !ignore;
                }
                else if(!ignore && text.charAt(j) == splitChars[i]){
                    return splitChars[i];
                }
            }
        }
        return '\0';
    }
    private String getDefaultFieldString(Keywords.DATATYPE datatype){
        String category = listTable.getFirstCategory(datatype);
        ListTable.ListTableNode node = listTable.getItemSearch().getListTableNode(datatype, category);
        return String.format(DEFAULT_FIELD_FORMAT, category, node.getFirstField());
    }
    private boolean balanceLeaf(TreeNode leaf, String leafData){
        {
            String[] tok = dotTokenizer.toArr(leafData);
            if(tok.length > 1){
                return balanceLeaf(leaf, tok[tok.length - 1]);
            }
        }

        PARAM_UTIL.findAndSetParam(leaf, leafData);

        if(CATEGORY_ITEM.equals(PARAM_UTIL.getParamType())){
            leaf.data = String.format("%s[%s]",
                    PARAM_UTIL.getUDefCategory(),
                    PARAM_UTIL.getItem()
            );
        }
        switch(PARAM_UTIL.getOutType()){
            case BOOLEAN:
                leaf.data += "=TRUE";
                return true;
            case STRING:
                if(leaf.quoted){
                    leaf.data = "'" + leaf.data + "'";// workaround to keep leaf.quoted true when leaf is parsed
                }
                leaf.data = getDefaultFieldString(LIST_STRING) + "=" + leaf.data;
                return true;
            case NUMBER:
                leaf.data = getDefaultFieldString(LIST_NUMBER) + "=" + leaf.data;
                return true;
            case IMMUTABLE:
                Erlog.get(this).set(LIST_SCOPES.toString() + " is an immutable datatype for scoping; not allowed here", leaf.data);
        }
        Erlog.get(this).set("Syntax error", leafData);
        return false;
    }
    private void setPayNodes(ArrayList<TreeNode> leaves){
        PayNodes.RxPayNodeFactory factory = (PayNodes.RxPayNodeFactory) PayNodes.getFactory(RX);
        String[] tok;

        for(TreeNode leaf : leaves){
            tok = dotTokenizer.toArr(leaf.data);
            factory.clear();
            for(int i = 0; i < tok.length; i++){
                PARAM_UTIL.findAndSetParam(leaf, tok[i]);
                factory.addPayNode();
            }
            leaf.payNodes = factory.getPayNodes();

        }
    }
    private void validateOperations(ArrayList<TreeNode> leaves){
        ValidatorRx validator = ValidatorRx.getInstance();
        for(int i = 1; i < leaves.size(); i+=2){
            TreeNode left = leaves.get(i-1);
            TreeNode right = leaves.get(i);
            validator.assertValidOperation(
                    left.payNodes,
                    right.parent.op,
                    right.payNodes,
                    right.parent
            );
        }
    }
}
