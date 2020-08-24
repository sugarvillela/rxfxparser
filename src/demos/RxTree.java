package demos;

import compile.basics.Keywords.KWORD;
import static compile.basics.Keywords.CHAR_AND;
import static compile.basics.Keywords.CHAR_OR;
import static compile.basics.Keywords.CHAR_OPAR;
import static compile.basics.Keywords.CHAR_CPAR;
import static compile.basics.Keywords.CHAR_SQUOTE;
import static compile.basics.Keywords.KWORD.BRANCH;
import static compile.basics.Keywords.KWORD.LEAF;
import static compile.basics.Keywords.KWORD.RX_AND;
import static compile.basics.Keywords.KWORD.RX_DATA;
import static compile.basics.Keywords.KWORD.RX_OR;
import commons.Commons;
import erlog.Erlog;
import java.util.ArrayList;
import toktools.TK;
import toktools.Tokens_special;
import unique.Unique;

public class RxTree {
    private static RxTree instance;
    
    public static RxTree getInstance(){
        return (instance == null)? (instance = new RxTree()) : instance;
    }
    protected RxTree(){}
    
    private static final Tokens_special T = new Tokens_special("", "('", TK.IGNORESKIP );
    private static final Unique UQ = new Unique();

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
    
    public TreeNode toTree(String text){
        System.out.println("tokenize start: root text: " + text);
        TreeNode root = new TreeNode(text, 0, null);
        boolean more;
        do{
            more = false;
            more |= root.split(CHAR_AND);
            more |= root.split(CHAR_OR);
//            more |= root.split(CHAR_EQUAL);
            root.negate();
            root.unwrap(CHAR_OPAR, CHAR_CPAR);
            root.unwrap(CHAR_SQUOTE, CHAR_SQUOTE);
        }while(more);
        return root;
    }
    
    public static class TreeNode{
        public ArrayList<TreeNode> nodes;
        public TreeNode parent;
        public KWORD role;
        public KWORD connector;
        public String data;
        public char op;//, parentOp;
        public boolean not;
        public int level, id;
        
        public TreeNode(){
            nodes = new ArrayList<>();
        }
        
        public TreeNode(String data, int level, TreeNode parent){
            this.data = data;
            this.level = level;
            this.parent = parent;
            this.id = UQ.next();
            this.op = 'v';
            this.not = false;
        }

        public boolean split(char delim){
            if(data == null){
                if(nodes != null){
                    
                    boolean more = false;
                    for(TreeNode node : nodes){
                        more |= node.split(delim);
                    }
                    return more;
                }
                else{
                    Erlog.get(this).set("nodes == null");
                }
            }
            else{
                //System.out.println(level + ": split: " + delim + ": " + data);
                nodes = new ArrayList<>();
                T.setDelims(delim);
                T.parse(data);
                ArrayList<String> tokens = T.getTokens();
                if(tokens.size() > 1){
                    role = BRANCH;
                    op = delim;
                    if(op == CHAR_AND){
                        connector = RX_AND;
                    }
                    else if(op == CHAR_OR){
                        connector = RX_OR;
                    }
                    data = null;
                    for(String token : tokens){
                        this.addChild(new TreeNode(token, level + 1, this));
                    }
                    //System.out.println(this);
                    return true;
                }
                else{
                    role = LEAF;
                    nodes = null;
                    connector = RX_DATA;
                    //System.out.println(this);
                }
            }
            return false;
        }
        
        public void negate(){
            if(data != null){
                int i = 0;
                while(data.charAt(i) == '~'){
                    not = !not;
                    i++;
                }
                if(i > 0){
                    data = data.substring(i);
                    //System.out.println(level + ": negate: " + not + ": data = " + data);
                }
            }
            if(nodes != null){
                for(TreeNode node : nodes){
                    node.negate();
                }
            }
        }
        
        public void unwrap(char first, char last){
            if(data != null){
                int i = 0, len = data.length();
                //System.out.println(data);
                while(data.charAt(i) == first && last == data.charAt(len - i - 1)){
                    //System.out.println(i + " : " + (len - i) + " : " + data.charAt(i) + " : " + data.charAt(len - i));
                    i++;
                }
                if(i > 0){
                    data = data.substring(i, len - i);
                    //System.out.println(level + ": unwrap: " + data);
                }
            }
            if(nodes != null){
                for(TreeNode node : nodes){
                    node.unwrap(first, last);
                }
            }
        }
        
        public void addChild(TreeNode node){
            //System.out.println(level + ": addChild: " + node.data);
            nodes.add(node);
        }
        
        //=======Access functions===========================================
        
        public int treeDepth(int max){
            if(role == LEAF){
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
            if(role == LEAF){
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
            if(role == BRANCH){
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
            String dispRole = (role == null)? "none" : role.toString();
            String dispParent = (parent == null)? "start" : parent.readableId();
            if(role == BRANCH){
                String[] childNodes = new String[nodes.size()];
                int i = 0;
                for(TreeNode node : nodes){
                    childNodes[i++] = node.readableId();
                }
                String children = String.join(", ", childNodes);
                dispRole += String.format(" %d children: %s", nodes.size(), children);
            }
            else{
                dispRole += " " + data;
            }
            return String.format("%d: parent %s -> %s: role = %s", 
                level, dispParent, this.readableId(), dispRole
            );
        }
    }
}
/*
    public void testUnwrap(){
        TreeNode node = new TreeNode("(((ab)cde))", 0, ' ', 0);
        node.unwrap(CHAR_OPAR, CHAR_CPAR);
        node.data = "'b'";
        node.unwrap(CHAR_SQUOTE, CHAR_SQUOTE);
        node.data = "()";
        node.unwrap(CHAR_OPAR, CHAR_CPAR);
    }
*/
