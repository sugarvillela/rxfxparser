package compile.rx;

import static compile.basics.Keywords.DATATYPE.LIST_BOOLEAN;
import static compile.basics.Keywords.OP.AND;
import static compile.basics.Keywords.OP.OR;
import static compile.basics.Keywords.PAR.CATEGORY;
import static compile.basics.Keywords.PAR.CATEGORY_ITEM;
import static compile.basics.Keywords.TEXT_FIELD_NAME;

import commons.Commons;
import compile.basics.Factory_Node;
import compile.basics.Factory_Node.RxScanNode;
import static compile.basics.Keywords.OP.CPAR;
import static compile.basics.Keywords.OP.OPAR;
import static compile.basics.Keywords.OP.SQUOTE;
import java.util.ArrayList;

import compile.basics.Keywords;
import compile.rx.ut.RxParamUtil;
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
        setPayloads(leaves);
        setDataTypes(leaves);
        return root;
    }
    
    @Override
    public ArrayList<Factory_Node.ScanNode> treeToScanNodeList(TreeNode root, String lineCol){
        ArrayList<TreeNode> nodes = instance.preOrder(root);
        int stackLevel = 0;
        ArrayList<Factory_Node.ScanNode> cmdList = new ArrayList<>();
        for(TreeNode node : nodes){
            //System.out.println(stackLevel + "... "+ node.level);
            while(stackLevel > node.level){
                stackLevel--;
                cmdList.add(
                    Factory_Node.newRxPop(lineCol)
                );
            }
            stackLevel++;
            cmdList.add(
                Factory_Node.newRxPush(lineCol, node)
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

    private void readConstants(ArrayList<TreeNode> leaves){
        for(TreeNode leaf : leaves){
            String read = CONSTANT_TABLE.readConstant(leaf.data);
            if(read != null){
                leaf.data = read;
            }
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
        PARAM_UTIL.findAndSetParam(leafData);
        if(PARAM_UTIL.isFun()){
            Keywords.RX_FUN rxFun = Keywords.RX_FUN.fromString(PARAM_UTIL.getTruncated());
            Keywords.PRIM outType = rxFun.outType;
            switch(outType){
                case BOOLEAN:
                    leaf.data += "=true";
                    break;
                case NUMBER:
                    leaf.data += "=truthy";
                    break;
                default:
                    leaf.data = TEXT_FIELD_NAME + "=" + leaf.data;
            }
            //Erlog.get(this).set("isFun!!! stop: ", leaf.toString());
            return true;
        }
        else {
            String[] tok;
            switch(PARAM_UTIL.getParamType()){
                case SINGLE_FIELD:
                    //System.out.println("balanceLeaf SINGLE_FIELD: "+leafData);
                    Keywords.DATATYPE datatype = listTable.getDataType(leafData);
                    //System.out.print("datatype: ");
                    //System.out.println(datatype);
                    if(datatype == null){
                        leaf.data = TEXT_FIELD_NAME + "=" + leaf.data;
                    }
                    else if(LIST_BOOLEAN.equals(datatype)){
                        leaf.data += "=true";
                    }
                    else{
                        leaf.data += "=truthy";
                    }
                    //Erlog.get(this).set("is Single field!!! stop: ", leaf.toString());
                    return true;
                case DOTTED_FIELD:
                    tok = tokenizer.toArr(leafData);
                    //Commons.disp(tok, "balanceLeaf DOTTED_FIELD: "+leafData);
                    return balanceLeaf(leaf, tok[0]);
                case DOTTED_FUN:
                    tok = tokenizer.toArr(leafData);
                    //Commons.disp(tok, "balanceLeaf DOTTED_FUN: "+leafData);
                    return balanceLeaf(leaf, tok[tok.length - 1]);
            }
        }
        Erlog.get(this).set("Syntax error", leafData);
        return false;
    }
    private void setPayloads(ArrayList<TreeNode> leaves){
        for(TreeNode leaf : leaves){

            PARAM_UTIL.findAndSetParam(leaf.data);
            leaf.paramType = PARAM_UTIL.getParamType();

            if(PARAM_UTIL.isFun()){
                leaf.data = PARAM_UTIL.getTruncated();
                leaf.param = PARAM_UTIL.getParam();
                //System.out.print("found rx function:" + leaf.data);
            }
//            System.out.printf(":  %s: %s - %s : %s \n",
//                leaf.data, PARAM_UTIL.getTruncated(), PARAM_UTIL.getParam(), PARAM_UTIL.getParamType()
//            );
        }
    }
    private void setDataTypes(ArrayList<TreeNode> leaves){
        Keywords.DATATYPE dataType;
        Keywords.PAR prevParamType = null, currParamType = null;
        String category;
        for(TreeNode leaf : leaves){
            if(leaf.paramType == null){
                System.out.println("missing param type: " + leaf.data);
                continue;
            }
            if(!leaf.paramType.isFun){
                if((dataType = listTable.getDataType(leaf.data)) != null){
                    leaf.dataType = dataType;
                    currParamType = CATEGORY;
                }
                else if((category = listTable.getCategory(leaf.data)) != null){
                    //leaf.dataType = dataType;
                    currParamType = CATEGORY_ITEM;
                }
            }

//            System.out.printf(":  %s: %s - %s : %s \n",
//                leaf.data, PARAM_UTIL.getTruncated(), PARAM_UTIL.getParam(), PARAM_UTIL.getParamType()
//            );
        }
    }
}
