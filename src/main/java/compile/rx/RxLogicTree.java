package compile.rx;

import static compile.basics.Keywords.*;
import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.FIELD.VAL;
import static compile.basics.Keywords.OP.*;

import compile.basics.Factory_Node;

import java.util.ArrayList;

import compile.basics.Keywords;
import compile.rx.factories.Factory_PayNode;
import compile.rx.ut.RxParamUtil;
import compile.rx.ut.RxValidator;
import compile.symboltable.ConstantTable;
import compile.symboltable.ListTable;
import erlog.Erlog;
import toksource.ScanNodeSource;
import toksource.TextSource_list;
import toktools.TK;
import toktools.Tokens_special;

public class RxLogicTree extends RxTree{
    private static final ConstantTable CONSTANT_TABLE = ConstantTable.getInstance();
    private static final RxParamUtil PARAM_UTIL = RxParamUtil.getInstance();
    private static final Tokens_special tokenizer = new Tokens_special(".", "(", TK.IGNORESKIP );
    private static RxTree instance;
    
    public static RxTree getInstance(){
        return (instance == null)? (instance = new RxLogicTree()) : instance;
    }
    protected RxLogicTree(){}
    
    private ListTable listTable;
    
    @Override
    public TreeNode treeFromRxWord(String text){
        System.out.println("tokenize start: root text: " + text);
        listTable = ListTable.getInstance();
        if(listTable == null){
            Erlog.get(this).set("LIST<*> items are not defined");
            return null;
        }
        TreeNode root = new TreeNode(text, 0, null);
        boolean more;
        do{
            more = false;
            more |= root.split(AND.asChar);
            more |= root.split(OR.asChar);
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
    
    @Override
    public ArrayList<Factory_Node.ScanNode> treeToScanNodeList(String lineCol, TreeNode root){
        ArrayList<TreeNode> nodes = instance.preOrder(root);
        int stackLevel = 0;
        ArrayList<Factory_Node.ScanNode> cmdList = new ArrayList<>();
        for(TreeNode node : nodes){
            //System.out.println(stackLevel + "... "+ node.level);
            while(stackLevel > node.level){
                stackLevel--;
                cmdList.add(//setCommand, RX_BUILDER, VAL, setData
                    Factory_Node.newPopNode(lineCol, RX_BUILDER)
                );
            }
            stackLevel++;
            cmdList.add(
                Factory_Node.newScanNode(lineCol, CMD.PUSH, RX_BUILDER, VAL, node.toString())
            );
            if(PAYLOAD.equals(node.op)){
                cmdList.add(Factory_Node.newPushNode(lineCol, PAY_NODE));
                for(Factory_PayNode.PayNode payNode : node.payNodes){
                    cmdList.add(
                        Factory_Node.newScanNode(lineCol, CMD.ADD_TO, PAY_NODE, VAL, payNode.toString())
                    );
                }
                cmdList.add(Factory_Node.newPopNode(lineCol, PAY_NODE));
            }
        }
        return cmdList;
    }
    @Override
    public TreeNode treeFromScanNodeSource(ArrayList<Factory_Node.ScanNode> cmdList){
        ArrayList<String> textCommands = new ArrayList<>();
        for(Factory_Node.ScanNode inputNode : cmdList){
            textCommands.add(inputNode.toString());
        }

        ScanNodeSource source = new ScanNodeSource(new TextSource_list(textCommands));
        Factory_PayNode factoryPayNode = new Factory_PayNode();
        TreeNode reroot = null, head = null;
        while(source.hasNext()){
            Factory_Node.ScanNode scanNode = source.nextNode();
            switch(scanNode.h){
                case RX_BUILDER:
                    switch(scanNode.cmd){
                        case PUSH:
                            if(reroot == null){
                                reroot = head = new TreeNode(scanNode.data);
                            }
                            else{
                                TreeNode treeNode = new TreeNode(scanNode.data);
                                treeNode.level = head.level + 1;
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
                    break;
                case PAY_NODE:
                    switch(scanNode.cmd){
                        case PUSH:
                            head.payNodes = new ArrayList<>();
                            break;
                        case ADD_TO:
                            head.payNodes.add(factoryPayNode.payNodeFromScanNode(scanNode.data));
                            break;
                        case POP:
                            break;
                    }
                    break;
            }

        }
        return reroot;
    }

    private void readConstants(ArrayList<TreeNode> leaves){
        for(TreeNode leaf : leaves){
            String[] tok = tokenizer.toArr(leaf.data);
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
            leaf.split(splitChar);
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
    private boolean balanceLeaf(TreeNode leaf, String leafData){
        PARAM_UTIL.findAndSetParam(leaf, leafData);
        Keywords.PAR paramType = PARAM_UTIL.getParamType();
        switch(paramType){
            case DOTTED_FUN:
                String[] tok = tokenizer.toArr(leafData);
                //Commons.disp(tok, "balanceLeaf DOTTED_FUN: "+leafData);
                return balanceLeaf(leaf, tok[tok.length - 1]);
            case CATEGORY_ITEM:
                leaf.data = String.format("%s[%s]",
                        PARAM_UTIL.getMainText(),
                        PARAM_UTIL.getBracketText()
                );
                break;
        }
        PRIM outType = PARAM_UTIL.getOutType();
        switch(outType){
            case BOOLEAN:
                leaf.data += "=TRUE";
                return true;
            case STRING:
                leaf.data = listTable.getDefaultFieldString(LIST_STRING) + "=" + leaf.data;
                return true;
            case NUMBER:
                leaf.data = listTable.getDefaultFieldString(LIST_NUMBER) + "=" + leaf.data;
                return true;
        }
        Erlog.get(this).set("Syntax error", leafData);
        return false;
    }
    private void setPayNodes(ArrayList<TreeNode> leaves){
        Factory_PayNode factory = new Factory_PayNode();
        String[] tok;

        for(TreeNode leaf : leaves){
            tok = tokenizer.toArr(leaf.data);

            for(int i = 0; i < tok.length; i++){
                PARAM_UTIL.findAndSetParam(leaf, tok[i]);
                factory.add(tok[i]);
            }
            leaf.payNodes = factory.getPayNodes();
            factory.clear();
        }
    }
    private void validateOperations(ArrayList<TreeNode> leaves){
        RxValidator validator = RxValidator.getInstance();
        for(int i = 1; i < leaves.size(); i+=2){
            validator.assertValidOperation(
                leaves.get(i-1).payNodes,
                leaves.get(i).parent.op,
                leaves.get(i).payNodes
            );
        }
    }
}
