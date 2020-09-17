package compile.rx;

import compile.basics.Keywords;
import compile.basics.Keywords.OP;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Keywords.OP.NOT;
import static compile.basics.Keywords.OP.PAYLOAD;
import commons.Commons;
import compile.basics.Factory_Node;
import erlog.Erlog;
import java.util.ArrayList;
import toktools.TK;
import toktools.Tokens_special;
import unique.Unique;

public abstract class RxTree {

    protected RxTree(){}
    
    public void dispBreadthFirst(TreeNode root){
        ArrayList<TreeNode>[] levels = breadthFirst(root);
        ArrayList<String> disp = new ArrayList<>();
        for(int i = 0; i < levels.length; i++){
            disp.add("Level: " + i);
            for(TreeNode node : levels[i]){
                disp.add(node.toString());
            }
        }
        Commons.disp(disp, "\nBreadthFirst");
    }
    
    public void dispLeaves(TreeNode root){
        ArrayList<TreeNode> leaves = leaves(root);
        Commons.disp(leaves, "\nLeaves");
    }
    
    public void dispPreOrder(TreeNode root){
        ArrayList<TreeNode> disp = preOrder(root);
        Commons.disp(disp, "\nPreOrder");
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
    
    public abstract TreeNode treeFromRxWord(String text);
    public abstract ArrayList<Factory_Node.ScanNode> treeToScanNodeList(TreeNode root, String lineCol);
    public abstract TreeNode treeFromScanNodeSource(ArrayList<String> cmdList);
    
    public static class TreeNode{
        private static final Tokens_special T = new Tokens_special("", "('", TK.IGNORESKIP );
        private static final Unique UQ = new Unique();
        public ArrayList<TreeNode> nodes;//--
        public TreeNode parent;     //--
        public String data;         //--
        public OP op;//, parentOp;//--
        public boolean not;         //--
        public int level, id;       //--
        // payload
        public PayNode[] payNodes;
        
        public TreeNode(){
            this.op = PAYLOAD;
            //connector = RX_PAYLOAD;
        }
        
        public TreeNode(String data, int level, TreeNode parent){
            this.op = PAYLOAD;
            //connector = RX_PAYLOAD;
            this.id = UQ.next();
            this.not = false;
            this.data = data;
            this.level = level;
            this.parent = parent;
        }

        public boolean split(char delim){
            //System.out.println("\ndelim = " + delim + ", data = " + data + ", connector = " + connector);
            if(data == null){
                boolean more = false;
                for(TreeNode node : nodes){
                    more |= node.split(delim);
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
                        this.addChild(new TreeNode(token, level + 1, this));
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
                return true;
            }

            return false;
        }
        public void addChild(TreeNode node){
            nodes.add(node);
        }
        
        //=======Rebuild====================================================
        
        public void addChildExternal(TreeNode node){
            //System.out.println("addChildExternal: "+node.op);
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
            String position;// = (nodes == null)? "none" : role.toString();
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
                level, dispParent, this.readableId(), dispRole, position, payNodeInfo()
            );
        }
        public String payNodeInfo(){
            if(payNodes == null){
                return "";
            }
            ArrayList<String> out = new ArrayList<>();
            for(PayNode payNode : payNodes){
                out.add("\t" + payNode.toString());
            }
            return String.join("\n", out);
        }
    }
    public static class PayNode{
        public Keywords.PAR paramType;
        public Keywords.RX_FUN funType;
        public String bodyText, paramText;
        public String uDefCategory;
        public Keywords.DATATYPE listSource;
        public Keywords.PRIM outType;

        @Override
        public String toString(){
            return String.format(
                "paramType=%s, funType=%s, bodyText=%s, paramText=%s, category=%s, listSource=%s, outType=%s",
                    Commons.nullSafe(paramType),
                    Commons.nullSafe(funType),
                    Commons.nullSafe(bodyText),
                    Commons.nullSafe(paramText),
                    Commons.nullSafe(uDefCategory),
                    Commons.nullSafe(listSource),
                    Commons.nullSafe(outType)
            );
        }
    }
}
