package demos;

import compile.basics.Keywords.KWORD;
import static compile.basics.Keywords.CHAR_AND;
import static compile.basics.Keywords.CHAR_OR;
import static compile.basics.Keywords.KWORD.RX_AND;
import static compile.basics.Keywords.KWORD.RX_OR;
import commons.Commons;
import compile.basics.Factory_Node;
import static compile.basics.Keywords.CHAR_EQUAL;
import static compile.basics.Keywords.CHAR_GT;
import static compile.basics.Keywords.CHAR_LT;
import static compile.basics.Keywords.CHAR_PAYLOAD;
import static compile.basics.Keywords.KWORD.RX_EQUAL;
import static compile.basics.Keywords.KWORD.RX_GT;
import static compile.basics.Keywords.KWORD.RX_LT;
import erlog.Erlog;
import java.util.ArrayList;
import toktools.TK;
import toktools.Tokens_special;
import unique.Unique;
import static compile.basics.Keywords.KWORD.RX_PAYLOAD;

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
    public abstract ArrayList<Factory_Node.RxScanNode> treeToScanNodeList(TreeNode root);
    public abstract TreeNode treeFromScanNodeSource(ArrayList<String> cmdList);
    
    public static class TreeNode{
        private static final Tokens_special T = new Tokens_special("", "('", TK.IGNORESKIP );
        private static final Unique UQ = new Unique();
        public ArrayList<TreeNode> nodes;//--
        public TreeNode parent;     //--
        public KWORD connector;     //--
        public String data;         //--
        public char op;//, parentOp;//--
        public boolean not;         //--
        public int level, id;       //--
        
        public TreeNode(){
            this.op = CHAR_PAYLOAD;
            connector = RX_PAYLOAD;
        }
        
        public TreeNode(String data, int level, TreeNode parent){
            this.op = CHAR_PAYLOAD;
            connector = RX_PAYLOAD;
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
                    op = delim;
                    setConnector();
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
        public void setConnector(){
            switch(op){
                case CHAR_AND:
                    connector = RX_AND;
                    break;
                case CHAR_OR:
                    connector = RX_OR;
                    break;
                case CHAR_EQUAL:
                    connector = RX_EQUAL;
                    break;
                case CHAR_GT:
                    connector = RX_GT;
                    break;
                case CHAR_LT:
                    connector = RX_LT;
                    break;
            }
        }
        public boolean negate(){
            if(data != null){
                int i = 0;
                while(data.charAt(i) == '~'){
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
            else {//if(nodes != null)
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
            boolean outer = true;
            for(int i = 0; i<len; i++){
                if(data.charAt(i) == first){
                    brace++;
                }
                else if(data.charAt(i) == last){
                    brace--;
                    if(brace == 0 && i != len - 1){// {a}&{b}
                        outer = false;
                    }
                }
            }
            if(brace != 0){// {a}}
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
            if(data == null){
                return false;
            }
            int len = data.length(), count = 0;
            for(int i = 0; i<len; i++){
                if(data.charAt(i) == quote){
                    count++;
                }
            }
            if(count % 2 != 0){// ''a' mismatch
                System.out.println(data);
                Erlog.get(this).set("Unclosed quote", data);
                return false;
            }
            if(count > 2){// 'a'='b' ignore until later
                return false;
            }
            int j = 0;
            while(data.charAt(j) == quote && quote == data.charAt(len - j - 1)){
                //System.out.println(i + " : " + (len - i) + " : " + data.charAt(i) + " : " + data.charAt(len - i));
                j++;
            }
            if(j > 0){
                data = data.substring(j, len - j);
                //System.out.println(level + ": unwrap: " + data);
                return true;
            }
            return false;
        }
        public void addChild(TreeNode node){
            nodes.add(node);
        }
        
        //=======Rebuild====================================================
        
        public void addChildExternal(TreeNode node){
            System.out.println("addChildExternal: "+node.op);
            node.setConnector();
            System.out.println("                : "+node.connector);
            if(nodes == null){
                //System.out.print("nodes == null: ");
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
            return String.format("%s%c%d", dispNot, op, id);
        }
        
        @Override
        public String toString(){
            String position;// = (nodes == null)? "none" : role.toString();
            String dispParent = (parent == null)? "start" : parent.readableId();
            String dispRole = (connector == null)? "NULL CONNECTOR" : connector.toString();
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
            return String.format("%d: parent %s -> %s, role = %s, position = %s", 
                level, dispParent, this.readableId(), dispRole, position
            );
        }
    }
}
