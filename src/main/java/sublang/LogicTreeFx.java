package sublang;

import runstate.Glob;
import sublang.factories.PayNodes;
import sublang.interfaces.ILogicTree;
import sublang.treenode.TreeNodeBase;
import erlog.Erlog;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static langdef.Keywords.DATATYPE.FX;
import static langdef.Keywords.OP.*;

public class LogicTreeFx implements ILogicTree {
    protected static final Tokens_special dotTokenizer = new Tokens_special(".", "'", TK.IGNORESKIP );

    private static LogicTreeFx instance;

    public static LogicTreeFx init(){
        return (instance == null)? (instance = new LogicTreeFx()) : instance;
    }

    protected LogicTreeFx(){}

    @Override
    public TreeNodeBase treeFromWordPattern(String text){
        TreeBuildUtil treeBuildUtil = Glob.TREE_BUILD_UTIL;
        //System.out.println("tokenize start: root text: " + text);
        TreeNodeBase root = this.buildTree(text);

        ArrayList<TreeNodeBase> leaves = treeBuildUtil.leaves(root);
        this.replaceConstants(leaves);    // read constants before extend
        this.setPayNodes(leaves);
        //dispBreadthFirst(root);
        this.validateFunParam(leaves);
        //Erlog.get(this).set("Happy stop");
        return root;
    }

    private TreeNodeBase buildTree(String text){
        TreeNodeBase root = Glob.TREE_BUILD_UTIL.newTreeNode(FX, text, 0, null);
        boolean more;
        do{
            more = false;
            more |= root.split(AND.asChar);
            more |= root.split(OR.asChar);
            more |= root.unwrap(OPAR.asChar, CPAR.asChar);
        }
        while(more);

        return root;
    }

    private void replaceConstants(ArrayList<TreeNodeBase> leaves){
        for(TreeNodeBase leaf : leaves){
            String read = Glob.CONSTANT_TABLE.getConstantValue(leaf.data);
            if(read != null){
                leaf.data = read;
            }
        }
    }

    private void setPayNodes(ArrayList<TreeNodeBase> leaves){
        PayNodes.FxPayNodeFactory factory = (PayNodes.FxPayNodeFactory) PayNodes.getFactory(FX);
        String[] tok;

        for(TreeNodeBase leaf : leaves){
            //System.out.println();
            //System.out.println("=======> setPayNodes: " + leaf.data);
            tok = dotTokenizer.toArr(leaf.data);
            if(tok.length == 2){
                factory.clear();
                Glob.FX_ACCESS_UTIL.findAndSetParam(leaf, tok[0]);
                factory.addPayNode();

                Glob.FX_PARAM_UTIL.findAndSetParam(leaf, tok[1]);
                factory.addPayNode();
                leaf.payNodes = factory.getPayNodes();
            }
            else{
                Erlog.get(this).set("FX language requires 'Access Operator' followed by 'Function'", leaf.data);
            }
        }
    }

    private void validateFunParam(ArrayList<TreeNodeBase> leaves){
        for(TreeNodeBase leaf : leaves){
            for(int i = 1; i < leaf.payNodes.size(); i+=2){
                PayNodes.FxPayNode left = (PayNodes.FxPayNode)leaf.payNodes.get(i-1);
                PayNodes.FxPayNode right = (PayNodes.FxPayNode)leaf.payNodes.get(i);

                Glob.VALIDATOR_FX.assertValidAccessModifier(left, right, leaf);
                Glob.VALIDATOR_FX.assertValidFunParam(right, leaf);
            }
        }
    }
}
