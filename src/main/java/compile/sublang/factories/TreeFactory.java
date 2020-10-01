package compile.sublang.factories;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import compile.basics.Keywords.OP;

import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.DATATYPE.RX_PAY_NODE;
import static compile.basics.Keywords.FIELD.VAL;
import static compile.basics.Keywords.NULL_TEXT;
import static compile.basics.Keywords.OP.NOT;
import static compile.basics.Keywords.OP.PAYLOAD;
import commons.Commons;
import erlog.Erlog;
import java.util.ArrayList;

import interfaces.DataNode;
import toksource.ScanNodeSource;
import toksource.TextSource_list;
import toktools.TK;
import toktools.Tokens_special;
import uq.Uq;

public abstract class TreeFactory {
    //protected final Keywords.DATATYPE rxOrFx;
    protected final Factory_Node nodeFactory;

    protected TreeFactory(){
        //this.rxOrFx = rxOrFx;
        this.nodeFactory = Factory_Node.getInstance();
    }

    public static TreeNode newTreeNode(Keywords.DATATYPE rxOrFx, String data, int level, TreeNode parent){
        return (RX.equals(rxOrFx))? new RxTreeNode(data, level, parent) : new FxTreeNode(data, level, parent);
    }
    public static TreeNode newTreeNode(Keywords.DATATYPE rxOrFx, Factory_Node.ScanNode scanNode){
        return (RX.equals(rxOrFx))? new RxTreeNode(scanNode) : new FxTreeNode(scanNode);
    }

    /*=====Root display methods=======================================================================================*/

    public void dispBreadthFirst(TreeNode root){
        ArrayList<TreeNode>[] levels = breadthFirst(root);
        ArrayList<String> disp = new ArrayList<>();
        for(int i = 0; i < levels.length; i++){
            disp.add("Level: " + i);
            for(TreeNode node : levels[i]){
                disp.add(node.readableContent());
            }
        }
        //Commons.disp(disp, "\nBreadthFirst");
    }
    
    public void dispLeaves(TreeNode root){
        ArrayList<TreeNode> leaves = leaves(root);
        ArrayList<String> readable = new ArrayList<>();
        for(TreeNode leaf : leaves){
            readable.add(leaf.readableContent());
        }
        Commons.disp(readable, "\nLeaves");
    }
    
    public void dispPreOrder(TreeNode root){
        ArrayList<TreeNode> preOrder = preOrder(root);
        ArrayList<String> readable = new ArrayList<>();
        for(TreeNode node : preOrder){
            readable.add(node.readableContent());
        }
        Commons.disp(readable, "\nPreOrder");
    }

    /*=====Root access methods========================================================================================*/
    
    public ArrayList<TreeNode>[] breadthFirst(TreeNode root){
        int max = root.treeDepth(0)+1;
        ArrayList<TreeNode>[] levels = new ArrayList[max];
        for(int i = 0; i < max; i++){
            levels[i] = new ArrayList<>();
        }
        root.breadthFirst(levels);
        return levels;
    }
    
    public ArrayList<TreeNode> leaves(TreeNode root){
        ArrayList<TreeNode> leaves = new ArrayList<>();
        root.leaves(leaves);
        return leaves;
    }
    
    public ArrayList<TreeNode> preOrder(TreeNode root){
        ArrayList<TreeNode> preOrder = new ArrayList<>();
        root.preOrder(preOrder);
        return preOrder;
    }

    /*=====Tree build and convert methods=============================================================================*/

    public abstract TreeNode treeFromWordPattern(String text);

    /* For later */
    public TreeNode treeFromScanNodeSource(Keywords.DATATYPE rxOrFx, ArrayList<Factory_Node.ScanNode> cmdList){
        ArrayList<String> textCommands = new ArrayList<>();
        for(Factory_Node.ScanNode inputNode : cmdList){
            textCommands.add(inputNode.toString());
        }
        ScanNodeSource source = new ScanNodeSource(new TextSource_list(textCommands));
        PayNodes.PayNodeFactory factory = PayNodes.getFactory(rxOrFx);
        TreeNode reroot = null, head = null;
        while(source.hasNext()){
            Factory_Node.ScanNode scanNode = source.nextNode();
            switch(scanNode.h){
                case RX_BUILDER:
                    switch(scanNode.cmd){
                        case PUSH:
                            if(reroot == null){
                                reroot = head = TreeFactory.newTreeNode(rxOrFx, scanNode);
                            }
                            else{
                                TreeNode treeNode = TreeFactory.newTreeNode(rxOrFx, scanNode);
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
                case RX_PAY_NODE:
                    switch(scanNode.cmd){
                        case PUSH:
                            head.payNodes = new ArrayList<>();
                            break;
                        case ADD_TO:
                            head.payNodes.add(factory.payNodeFromScanNode(scanNode.data));
                            break;
                        case POP:
                            break;
                    }
                    break;
            }

        }
        return reroot;
    }

    public ArrayList<Factory_Node.ScanNode> treeToScanNodeList(Keywords.DATATYPE datatype, TreeNode root){
        Keywords.DATATYPE builderType;
        Keywords.DATATYPE payNodeType;
        if(RX.equals(datatype)){
            builderType = RX_BUILDER;
            payNodeType = RX_PAY_NODE;
        }
        else{
            builderType = FX_BUILDER;
            payNodeType = FX_PAY_NODE;
        }
        ArrayList<TreeNode> nodes = preOrder(root);
        int stackLevel = 0;
        ArrayList<Factory_Node.ScanNode> cmdList = new ArrayList<>();
        for(TreeNode node : nodes){

            while(stackLevel > node.level){
                stackLevel--;
                cmdList.add(
                        nodeFactory.newPopNode(builderType)
                );
            }
            stackLevel++;
            cmdList.add(
                    nodeFactory.newScanNode(Keywords.CMD.PUSH, builderType, VAL, node.toString())
            );
            if(PAYLOAD.equals(node.op)){
                cmdList.add(nodeFactory.newPushNode(RX_PAY_NODE));
                for(DataNode payNode : node.payNodes){
                    cmdList.add(
                            nodeFactory.newScanNode(Keywords.CMD.ADD_TO, RX_PAY_NODE, VAL, payNode.toString())
                    );
                }
                cmdList.add(nodeFactory.newPopNode(RX_PAY_NODE));
            }
        }
        return cmdList;
    }

    public boolean assertEqual(TreeFactory.TreeNode root1, TreeFactory.TreeNode root2){
        ArrayList<TreeFactory.TreeNode>[] levels1 = this.breadthFirst(root1);
        ArrayList<TreeFactory.TreeNode>[] levels2 = this.breadthFirst(root2);
        if(levels1.length != levels2.length){
            Erlog.get(this).set("fail: levels1.length != levels2.length");
            return false;
        }
        for(int i = 0; i<levels1.length; i++){
            int len1 = levels1[i].size();
            int len2 = levels2[i].size();
            if(len1 != len2){
                Erlog.get(this).set("fail: len1 != len2");
                return false;
            }
            for(int j = 0; j < len1; j++){
                String node1 = levels1[i].get(j).readableContent();
                String node2 = levels2[i].get(j).readableContent();
                boolean equal = node1.equals(node2);
                System.out.printf("\n%d:%d: equal: %b\n    %s \n    %s \n", i, j, equal, node1, node2);
                if(!equal){
                    //Error!
                    System.out.println("not equal");
                }
            }
        }
        return true;
    }

    public static abstract class TreeNode{
        protected static final Tokens_special T = new Tokens_special("", "('", TK.IGNORESKIP );
        protected static final Uq uq = new Uq();
        protected ArrayList<TreeNode> nodes;//--
        public TreeNode parent;     //--
        public String data;         //--
        public OP op;//, parentOp;//--
        public boolean quoted, not;         //--
        public int level, id;       //--
        // payload
        public ArrayList<DataNode> payNodes;
        
        public TreeNode(){
            this.op = PAYLOAD;
        }
        
        public TreeNode(String data, int level, TreeNode parent){
            this.op = PAYLOAD;
            //connector = RX_PAYLOAD;
            this.id = uq.next();
            this.quoted = false;
            this.not = false;
            this.data = data;
            this.level = level;
            this.parent = parent;
        }

        public boolean split(Keywords.DATATYPE rxOrFx, char delim){
            //System.out.println("\ndelim = " + delim + ", data = " + data + ", connector = " + connector);
            if(data == null){
                boolean more = false;
                for(TreeNode node : nodes){
                    more |= node.split(rxOrFx, delim);
                }
                return more;
            }
            else{
                nodes = new ArrayList<>();
                T.setDelims(delim);
                T.parse(data);
                ArrayList<String> tokens = T.getTokens();
                if(tokens.size() > 1){
                    data = null;
                    op = OP.fromChar(delim);
                    //setConnector();
                    for(String token : tokens){
                        this.addChild(
                                TreeFactory.newTreeNode(rxOrFx, token, level + 1, this)
                        );
                    }
                    return true;
                }
                else{
                    nodes = null;
                    
                }
            }
            return false;
        }

        public boolean negate(){
            if(data != null){
                int i = 0;
                while(data.charAt(i) == NOT.asChar){
                    not = !not;
                    i++;
                }
                if(i > 0){
                    data = data.substring(i);
                    //System.out.println(level + ": negate: " + not + ": data = " + data);
                    return true;
                }
                return false;
            }
            else {
                boolean more = false;
                for(TreeNode node : nodes){
                    more |= node.negate();
                }
                return more;
            }
        }

        /**Public starting point for unwrapping.
         * If not a leaf (nodes != null) then call recursively on children
         * Else call private version to unwrap self
         * @param first Opening parentheses
         * @param last Closing parentheses
         * @return true if something got changed
         */
        public boolean unwrap(char first, char last){
            if(nodes != null){
                boolean more = false;
                for(TreeNode node : nodes){
                    more |= node.unwrap(first, last);
                }
                return more;
            }
            return data != null && unwrap(false, first, last);
        }

        /**Private recursive method for unwrapping a single string
         * If isWrapped(), unwraps text and calls self again to unwrap inner wrappings
         * @param changed OR recursive calls together so any changes are known (last call always false)
         * @param first Opening parentheses
         * @param last Closing parentheses
         * @return true if something got changed
         */
        private boolean unwrap(boolean changed, char first, char last){
            if(isWrapped(first, last)){
                data = data.substring(1, data.length()-1);
                return unwrap(true, first, last);
            }
            return changed;
        }

        /**Publicly exposed helper. Checks for wrapping and also unbalanced wraps */
        public boolean isWrapped(char first, char last){
            int brace = 0, len = data.length();
            boolean outer = true;                   // Stays true if {a=b} or {{a=b}&{c=d}}
            for(int i = 0; i<len; i++){
                if(data.charAt(i) == first){
                    brace++;
                }
                else if(data.charAt(i) == last){
                    brace--;
                    if(brace == 0 && i != len - 1){// Finds {a}&{b}
                        outer = false;
                    }
                }
            }
            if(brace != 0){// Finds {a}}
                Erlog.get(this).set("Symbol mismatch", data);
            }
            return outer && data.charAt(0) == first && last == data.charAt(len - 1);
        }


        public abstract boolean unquote(char quote);

        public void addChild(TreeNode node){
            nodes.add(node);
        }
        
        //=======Rebuild====================================================
        
        public void addChildExternal(TreeNode node){
            if(nodes == null){
                nodes = new ArrayList<>();
            }
            nodes.add(node);
        }

        //=======Access functions===========================================
        
        public int treeDepth(int max){
            if(nodes == null){
                return (level > max)? level : max;
            }
            else{
                for(TreeNode node : nodes){
                    int curr = node.treeDepth(max);
                    if(curr > max){
                        max = curr;
                    }
                }
                return max;
            }
        }
        
        public void leaves(ArrayList<TreeNode> leaves){
            if(nodes == null){
                leaves.add(this);
            }
            else{
                for(TreeNode node : nodes){
                    node.leaves(leaves);
                }
            }
        }
        
        public void breadthFirst(ArrayList<TreeNode>[] levels){
            levels[level].add(this);
            if(nodes != null){
                for(TreeNode node : nodes){
                    node.breadthFirst(levels);
                }
            }
        }
        
        public void preOrder(ArrayList<TreeNode> leaves){
            leaves.add(this);
            if(nodes != null){
                for(TreeNode node : nodes){
                    node.preOrder(leaves);
                }
            }
        }
        
        //=======Display functions===========================================
        public String readableId(){
            String dispNot = not? "!" : " ";
            return String.format("%s%c%d", dispNot, op.asChar, id);
        }
        
        @Override
        public String toString(){
            return String.format("%b,%s,%d,%s",
                not,                    // negate
                Commons.nullSafe(op),   // operation
                id,                     // unique id
                Commons.nullSafe(data)  // text payload
            );
        }
        public String readableContent(){
            String position;
            String dispParent = (parent == null)? "start" : parent.readableId();
            String dispRole = (op == null)? NULL_TEXT : op.toString();

            //String paramTypeString = (paramType == null)? NULL_TEXT : paramType.toString();
            if(nodes == null){
                position = "LEAF " + data;
            }
            else{
                String[] childNodes = new String[nodes.size()];
                int i = 0;
                for(TreeNode node : nodes){
                    childNodes[i++] = node.readableId();
                }
                String children = String.join(", ", childNodes);
                position = String.format("BRANCH %d children: %s", nodes.size(), children);
            }
            return String.format("%d: parent %s -> %s, role = %s, position = %s \n%s",
                    level, dispParent, this.readableId(), dispRole, position, allReadablePayloadContent()
            );
        }
        private String allReadablePayloadContent(){
            if(payNodes == null){
                return "";
            }
            ArrayList<String> out = new ArrayList<>();
            for(DataNode rxPayNode : payNodes){
                out.add("\t" + rxPayNode.readableContent());
            }
            return String.join("\n", out);
        }
        public String leafOp(){
            if(op == null || nodes == null || nodes.size() < 2){
                return "";
            }
            return String.format("%s %c %s", nodes.get(0).data, op.asChar, nodes.get(1).data);
        }
    }
    public static class RxTreeNode extends TreeNode{
        protected static final int NUM_TREE_FIELDS = 4;

        public RxTreeNode(String data, int level, TreeNode parent){
            super(data, level, parent);
        }
        public RxTreeNode(Factory_Node.ScanNode scanNode){
            String[] tok = scanNode.data.split(",", NUM_TREE_FIELDS);
            this.not = Boolean.parseBoolean(tok[0]);
            this.op = OP.fromString(tok[1]);
            this.id = Integer.parseInt(tok[2]);
            this.data = tok[3];
            this.quoted = false;
            this.level = 0;
            this.parent = null;
        }

        @Override
        public boolean unquote(char quote){
            if(nodes != null){
                boolean more = false;
                for(TreeNode node : nodes){
                    more |= node.unquote(quote);
                }
                return more;
            }
            if(data != null){
                int len = data.length(), count = 0;
                for(int i = 0; i<len; i++){
                    if(data.charAt(i) == quote){
                        count++;
                    }
                }
                if(count % 2 != 0){// ''a' mismatch
                    Erlog.get(this).set("Unclosed quote", data);
                    return false;
                }
                if(count != 2 || data.charAt(0) != quote || quote != data.charAt(len - 1)){// 'a'='b' ignore until later
                    return false;
                }
                data = data.substring(1, len - 1);
                quoted = true;
                return true;
            }

            return false;
        }
    }
    public static class FxTreeNode extends TreeNode{
        protected static final int NUM_TREE_FIELDS = 4;

        public FxTreeNode(String data, int level, TreeNode parent){
            super(data, level, parent);
        }
        public FxTreeNode(Factory_Node.ScanNode scanNode){
            String[] tok = scanNode.data.split(",", NUM_TREE_FIELDS);
            this.not = Boolean.parseBoolean(tok[0]);
            this.op = OP.fromString(tok[1]);
            this.id = Integer.parseInt(tok[2]);
            this.data = tok[3];
            this.quoted = false;
            this.level = 0;
            this.parent = null;
        }

        @Override
        public boolean unquote(char quote){// just in case I forget
            Erlog.get(this).set("FX doesn't support quoted text");
            return false;
        }
    }
}
