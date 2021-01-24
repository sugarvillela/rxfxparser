package sublang.treenode;

import commons.Commons;
import erlog.Erlog;
import interfaces.DataNode;
import langdef.Keywords;
import runstate.Glob;
import sublang.TreeBuildUtil;
import sublang.interfaces.ITreeNode;
import toktools.TK;
import toktools.Tokens_special;
import uq.Uq;

import java.util.ArrayList;

import static langdef.Keywords.NULL_TEXT;
import static langdef.Keywords.OP.NOT;
import static langdef.Keywords.OP.PAYLOAD;

/**
 * Expect non-empty string
 */
public abstract class TreeNodeBase extends DataNode implements ITreeNode {
    protected static final Tokens_special T = new Tokens_special("", "('", TK.IGNORESKIP);
    protected static final Uq uq = new Uq();
    public ArrayList<TreeNodeBase> nodes;//--
    public TreeNodeBase parent;
    public String data;
    public Keywords.OP op;
    public boolean quoted, not;
    public int level, id;
    protected final Keywords.DATATYPE rxOrFx;

    public ArrayList<DataNode> payNodes;

    public TreeNodeBase(Keywords.DATATYPE rxOrFx) {
        this.rxOrFx = rxOrFx;
        this.op = PAYLOAD;
    }

    public TreeNodeBase(Keywords.DATATYPE rxOrFx, String data, int level, TreeNodeBase parent) {
        this.rxOrFx = rxOrFx;
        this.op = PAYLOAD;
        this.id = uq.next();
        this.quoted = false;
        this.not = false;
        this.data = data;
        this.level = level;
        this.parent = parent;
    }

    public boolean split(char delim) {
        //System.out.println("\ndelim = " + delim + ", data = " + data + ", connector = " + connector);
        if (data == null) {
            boolean more = false;
            for (TreeNodeBase node : nodes) {
                more |= node.split(delim);
            }
            return more;
        } else {
            nodes = new ArrayList<>();
            T.setDelims(delim);
            T.parse(data);
            ArrayList<String> tokens = T.getTokens();
            if (tokens.size() > 1) {
                data = null;
                op = Keywords.OP.fromChar(delim);
                //setConnector();
                for (String token : tokens) {
                    nodes.add(
                            Glob.TREE_BUILD_UTIL.newTreeNode(rxOrFx, token, level + 1, this)
                    );
                }
                return true;
            } else {
                nodes = null;

            }
        }
        return false;
    }

    public boolean negate() {
        if (data != null) {
            int i = 0;
            while (data.charAt(i) == NOT.asChar) {
                not = !not;
                i++;
            }
            if (i > 0) {
                data = data.substring(i);
                //System.out.println(level + ": negate: " + not + ": data = " + data);
                return true;
            }
            return false;
        } else {
            boolean more = false;
            for (TreeNodeBase node : nodes) {
                more |= node.negate();
            }
            return more;
        }
    }

    /**
     * Public starting point for unwrapping.
     * If not a leaf (nodes != null) then call recursively on children
     * Else call private version to unwrap self
     *
     * @param first Opening parentheses
     * @param last  Closing parentheses
     * @return true if something got changed
     */
    public boolean unwrap(char first, char last) {
        if (nodes != null) {
            boolean more = false;
            for (TreeNodeBase node : nodes) {
                more |= node.unwrap(first, last);
            }
            return more;
        }
        return data != null && unwrap(false, first, last);
    }

    /**
     * Private recursive method for unwrapping a single string
     * If isWrapped(), unwraps text and calls self again to unwrap inner wrappings
     *
     * @param changed OR recursive calls together so any changes are known (last call always false)
     * @param first   Opening parentheses
     * @param last    Closing parentheses
     * @return true if something got changed
     */
    private boolean unwrap(boolean changed, char first, char last) {
        if (isWrapped(first, last)) {
            data = data.substring(1, data.length() - 1);
            return unwrap(true, first, last);
        }
        return changed;
    }

    /**
     * Publicly exposed helper. Checks for wrapping and also unbalanced wraps
     */
    public boolean isWrapped(char first, char last) {
        int brace = 0, len = data.length();
        boolean outer = true;                   // Stays true if {a=b} or {{a=b}&{c=d}}
        for (int i = 0; i < len; i++) {
            if (data.charAt(i) == first) {
                brace++;
            } else if (data.charAt(i) == last) {
                brace--;
                if (brace == 0 && i != len - 1) {// Finds {a}&{b}
                    outer = false;
                }
            }
        }
        if (brace != 0) {// Finds {a}}
            Erlog.get(this).set("Symbol mismatch", data);
        }
        return outer && data.charAt(0) == first && last == data.charAt(len - 1);
    }


    public abstract boolean unquote(char quote);

    //=======Rebuild====================================================

    public void addChild(TreeNodeBase node) {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        nodes.add(node);
    }

    //=======Access functions===========================================

    public int treeDepth() {
        return this.treeDepth(0);
    }

    public int treeDepth(int max) {
        if (nodes == null) {
            return (level > max) ? level : max;
        } else {
            for (TreeNodeBase node : nodes) {
                int curr = node.treeDepth(max);
                if (curr > max) {
                    max = curr;
                }
            }
            return max;
        }
    }

    public void leaves(ArrayList<TreeNodeBase> leaves) {
        if (nodes == null) {
            leaves.add(this);
        } else {
            for (TreeNodeBase node : nodes) {
                node.leaves(leaves);
            }
        }
    }

    public void breadthFirst(ArrayList<TreeNodeBase>[] levels) {
        levels[level].add(this);
        if (nodes != null) {
            for (TreeNodeBase node : nodes) {
                node.breadthFirst(levels);
            }
        }
    }

    public void preOrder(ArrayList<TreeNodeBase> leaves) {
        leaves.add(this);
        if (nodes != null) {
            for (TreeNodeBase node : nodes) {
                node.preOrder(leaves);
            }
        }
    }

    //=======Display functions===========================================
    protected String readableId() {
        String dispNot = not ? "!" : " ";
        return String.format("%s%c%d", dispNot, op.asChar, id);
    }

    @Override
    public String toString() {
        return String.format("%b,%s,%d,%s",
                not,                    // negate
                Commons.nullSafe(op),   // operation
                id,                     // unique id
                Commons.nullSafe(data)  // text payload
        );
    }

    @Override
    public String readableContent() {
        String position;
        String dispParent = (parent == null) ? "start" : parent.readableId();
        String dispRole = (op == null) ? NULL_TEXT : op.toString();

        //String paramTypeString = (paramType == null)? NULL_TEXT : paramType.toString();
        if (nodes == null) {
            position = "LEAF, " + data;
        } else {
            String[] childNodes = new String[nodes.size()];
            int i = 0;
            for (TreeNodeBase node : nodes) {
                childNodes[i++] = node.readableId();
            }
            String children = String.join(", ", childNodes);
            position = String.format("BRANCH %d children: %s", nodes.size(), children);
        }
        return String.format("%d: parent %s -> %s, role = %s, position = %s \n    %s",
                level, dispParent, this.readableId(), dispRole, position, allReadablePayloadContent()
        );
    }

    private String allReadablePayloadContent() {
        if (payNodes == null) {
            return "";
        }
        ArrayList<String> out = new ArrayList<>();
        for (DataNode rxPayNode : payNodes) {
            out.add("\t" + rxPayNode.readableContent());
        }
        return String.join("\n", out);
    }

    public String leafOpToString() {
        if (op == null || nodes == null || nodes.size() < 2) {
            return "";
        }
        return String.format("%s %c %s", nodes.get(0).data, op.asChar, nodes.get(1).data);
    }
}
