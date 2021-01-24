package sublang;

import static langdef.Keywords.DATATYPE.*;
import static langdef.Keywords.DEFAULT_FIELD_FORMAT;
import static langdef.Keywords.OP.*;
import static langdef.Keywords.RX_PAR.CATEGORY_ITEM;

import java.util.ArrayList;

import langdef.Keywords;
import listtable.ListTableNode;
import runstate.Glob;
import sublang.factories.PayNodes;
import sublang.interfaces.ILogicTree;
import sublang.rxfun.RxFunPattern;
import sublang.treenode.TreeNodeBase;
import erlog.DevErr;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

public class LogicTreeRx implements ILogicTree {//extends LogicTree
    private static final Tokens_special dotTokenizer = new Tokens_special(".", "'", TK.IGNORESKIP );
    private static final Keywords.OP[] SPLIT_CHARS = new Keywords.OP[]{COMPARE_EQUAL, COMPARE_LT, COMPARE_GT};

    private static LogicTreeRx instance;
    private LogicTreeRx(){}
    public static LogicTreeRx init(){
        return (instance == null)? (instance = new LogicTreeRx()) : instance;
    }

    @Override
    public TreeNodeBase treeFromWordPattern(String text){
        //TreeBuildUtil treeBuildUtil = Glob.TREE_BUILD_UTIL;
        System.out.println("tokenize start: root text: " + text);
        if(!Glob.LIST_TABLE.isInitialized()){
            Erlog.get(this).set("LIST<*> items are not defined");
            return null;
        }
        TreeNodeBase root = this.buildTree(text);

        ArrayList<TreeNodeBase> leaves = Glob.TREE_BUILD_UTIL.leaves(root);
        for(TreeNodeBase leaf : leaves){
            RxFunPattern funPattern = new RxFunPattern(leaf.data);
        }



//        this.replaceConstants(leaves);    // read constants before extend

//        this.extendLeaves(leaves);     // fix, split and unwrap
//
//        leaves = Glob.TREE_BUILD_UTIL.leaves(root);    // recalculate after extend
//        this.replaceConstants(leaves);    // read constants again after extend
//        this.setPayNodes(leaves);
//        //Glob.TREE_BUILD_UTIL.dispBreadthFirst(root);
//
//        this.validateOperations(leaves);
//
//        Glob.TREE_BUILD_UTIL.dispLeaves(root);
//        Erlog.get(this).set("Happy stop");
        return root;
    }

    private TreeNodeBase buildTree(String text){
        TreeNodeBase root = Glob.TREE_BUILD_UTIL.newTreeNode(RX, text, 0, null);
        boolean more;
        do{
            more = false;
            more |= root.split(AND.asChar);
            more |= root.split(OR.asChar);
            more |= root.negate();
            more |= root.unwrap(OPAR.asChar, CPAR.asChar);
            more |= root.unquote(SQUOTE.asChar);
        }
        while(more);

        return root;
    }

    private void replaceConstants(ArrayList<TreeNodeBase> leaves){
        for(TreeNodeBase leaf : leaves){
            String[] tok = dotTokenizer.toArr(leaf.data);
            for(int i = 0; i < tok.length; i++){
                String read = Glob.CONSTANT_TABLE.getConstantValue(tok[i]);
                if(read != null){
                    tok[i] = read;
                }
            }
            leaf.data = String.join(".", tok);
        }
    }

    private void extendLeaves(ArrayList<TreeNodeBase> leaves){
        for(TreeNodeBase leaf : leaves){
            char splitChar = findSplitChar(leaf.data);
            if( // Optional text: add fields to balance the statement
                    splitChar == '\0' &&
                    this.balanceLeaf(leaf, leaf.data)
            ){
                splitChar = '=';
            }
            leaf.split(splitChar);
            leaf.negate();
            leaf.unwrap(OPAR.asChar, CPAR.asChar);
            leaf.unquote(SQUOTE.asChar);
        }
    }

    private char findSplitChar(String text){
        boolean ignore = false;
        for(int i = 0; i < SPLIT_CHARS.length; i++){
            for(int j = 0; j < text.length()-1; j++){//stops early for abc= nonsense
                if(text.charAt(j) == SQUOTE.asChar){
                    ignore = !ignore;
                }
                else if(!ignore && text.charAt(j) == SPLIT_CHARS[i].asChar){
                    return SPLIT_CHARS[i].asChar;
                }
            }
        }
        return '\0';
    }

    private String getDefaultFieldString(Keywords.DATATYPE datatype){
        String category = Glob.LIST_TABLE.getFirstCategory(datatype);
        ListTableNode node = Glob.LIST_TABLE.getItemSearch().getListTableNode(datatype, category);
        return String.format(DEFAULT_FIELD_FORMAT, category, node.getFirstField());
    }

    private boolean balanceLeaf(TreeNodeBase leaf, String leafData){
        {
            String[] tok = dotTokenizer.toArr(leafData);
            if(tok.length > 1){
                return balanceLeaf(leaf, tok[tok.length - 1]);
            }
        }

        Glob.RX_PARAM_UTIL.findAndSetParam(leaf, leafData);

        if(CATEGORY_ITEM.equals(Glob.RX_PARAM_UTIL.getParamType())){
            leaf.data = String.format(DEFAULT_FIELD_FORMAT,
                    Glob.RX_PARAM_UTIL.getUDefCategory(),
                    Glob.RX_PARAM_UTIL.getItem()
            );
        }
        System.out.println("getOutType: " + Glob.RX_PARAM_UTIL.getOutType());
        switch(Glob.RX_PARAM_UTIL.getOutType()){
            case BOOLEAN:
            case DISCRETE://TODO make discrete and vote compatible with this
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
        DevErr.get(this).kill("Syntax error", leafData);
        return false;
    }

    private void setPayNodes(ArrayList<TreeNodeBase> leaves){
        PayNodes.RxPayNodeFactory factory = (PayNodes.RxPayNodeFactory) PayNodes.getFactory(RX);
        String[] tok;

        for(TreeNodeBase leaf : leaves){
            tok = dotTokenizer.toArr(leaf.data);
            factory.clear();
            for(int i = 0; i < tok.length; i++){
                Glob.RX_PARAM_UTIL.findAndSetParam(leaf, tok[i]);
                factory.addPayNode();
            }
            leaf.payNodes = factory.getPayNodes();

        }
    }

    private void validateOperations(ArrayList<TreeNodeBase> leaves){
        for(int i = 1; i < leaves.size(); i+=2){
            TreeNodeBase left = leaves.get(i-1);
            TreeNodeBase right = leaves.get(i);
            Glob.VALIDATOR_RX.assertValidOperation(
                    left.payNodes,
                    right.parent.op,
                    right.payNodes,
                    right.parent
            );
        }
    }
}
