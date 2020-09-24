package compile.basics;

import compile.basics.Keywords.OP;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.OP.NOT;
import static compile.basics.Keywords.OP.PAYLOAD;
import commons.Commons;
import compile.sublang.factories.PayNodes;
import erlog.Erlog;
import java.util.ArrayList;
import toktools.TK;
import toktools.Tokens_special;
import unique.Unique;

public abstract class RxFxTreeFactory {
    protected final TreeNodeFactory treeNodeFactory;

    protected RxFxTreeFactory(TreeNodeFactory treeNodeFactory){
        this.treeNodeFactory = treeNodeFactory;
    }

    public TreeNodeFactory getTreeNodeFactory(){
        return treeNodeFactory;
    }

    public void dispBreadthFirst(TreeNode root){
        ArrayList<TreeNode>[] levels = breadthFirst(root);
        ArrayList<String> disp = new ArrayList<>();
        for(int i = 0; i < levels.length; i++){
            disp.add("Level: " + i);
            for(TreeNode node : levels[i]){
                disp.add(node.readableContent());
            }
        }
        Commons.disp(disp, "\nBreadthFirst");
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
    
    public abstract TreeNode treeFromWordPattern(String text);
    public abstract ArrayList<Factory_Node.ScanNode> treeToScanNodeList(String lineCol, TreeNode root);
    public abstract TreeNode treeFromScanNodeSource(Keywords.DATATYPE datatype, ArrayList<Factory_Node.ScanNode> cmdList);




    public static abstract class TreeNode{
        protected static final Tokens_special T = new Tokens_special("", "('", TK.IGNORESKIP );
        protected static final Unique UQ = new Unique();
        protected ArrayList<TreeNode> nodes;//--
        public TreeNode parent;     //--
        public String data;         //--
        public OP op;//, parentOp;//--
        public boolean quoted, not;         //--
        public int level, id;       //--
        // payload
        public ArrayList<PayNodes.PayNode> payNodes;
        
        public TreeNode(){
            this.op = PAYLOAD;
        }
        
        public TreeNode(String data, int level, TreeNode parent){
            this.op = PAYLOAD;
            //connector = RX_PAYLOAD;
            this.id = UQ.next();
            this.quoted = false;
            this.not = false;
            this.data = data;
            this.level = level;
            this.parent = parent;
        }

        public boolean split(RxFxTreeFactory factory, char delim){
            //System.out.println("\ndelim = " + delim + ", data = " + data + ", connector = " + connector);
            if(data == null){
                boolean more = false;
                for(TreeNode node : nodes){
                    more |= node.split(factory, delim);
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
                            factory.getTreeNodeFactory().get(token, level + 1, this)
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

        private boolean unwrap(boolean changed, char first, char last){
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
                return false;
            }
            if(outer && data.charAt(0) == first && last == data.charAt(len - 1)){
                data = data.substring(1, len-1);
                return unwrap(true, first, last);
            }
            return changed;
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
            for(PayNodes.PayNode rxPayNode : payNodes){
                out.add("\t" + rxPayNode.readableContent());
            }
            return String.join("\n", out);
        }
    }
    public static class RxTreeNode extends TreeNode{
        protected static final int NUM_TREE_FIELDS = 4;

        public RxTreeNode(String data, int level, TreeNode parent){
            super(data, level, parent);
        }
        public RxTreeNode(String scanNodeData){
            String[] tok = scanNodeData.split(",", NUM_TREE_FIELDS);
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
        public FxTreeNode(String scanNodeData){
            String[] tok = scanNodeData.split(",", NUM_TREE_FIELDS);
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

    public interface TreeNodeFactory{
        TreeNode get(String data, int level, TreeNode parent);
    }
    public static class RxTreeNodeFactory implements TreeNodeFactory{
        @Override
        public TreeNode get(String data, int level, TreeNode parent){
            return new RxTreeNode(data, level, parent);
        }
    }
    public static class FxTreeNodeFactory implements TreeNodeFactory{
        @Override
        public TreeNode get(String data, int level, TreeNode parent){
            return new RxTreeNode(data, level, parent);
        }
    }
}
