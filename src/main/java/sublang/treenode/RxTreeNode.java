package sublang.treenode;

import erlog.Erlog;
import langdef.Keywords;
import scannode.ScanNode;

import static langdef.Keywords.DATATYPE.RX;

public class RxTreeNode extends TreeNodeBase {
    protected static final int NUM_TREE_FIELDS = 4;

    public RxTreeNode(String data, int level, TreeNodeBase parent) {
        super(RX, data, level, parent);
    }

    public RxTreeNode(ScanNode scanNode) {
        super(RX);
        String[] tok = scanNode.data.split(",", NUM_TREE_FIELDS);
        this.not = Boolean.parseBoolean(tok[0]);
        this.op = Keywords.OP.fromString(tok[1]);
        this.id = Integer.parseInt(tok[2]);
        this.data = tok[3];
        this.quoted = false;
        this.level = 0;
        this.parent = null;
    }

    @Override
    public boolean unquote(char quote) {
        if (nodes != null) {
            boolean more = false;
            for (TreeNodeBase node : nodes) {
                more |= node.unquote(quote);
            }
            return more;
        }
        if (data != null) {
            int len = data.length(), count = 0;
            for (int i = 0; i < len; i++) {
                if (data.charAt(i) == quote) {
                    count++;
                }
            }
            if (count % 2 != 0) {// ''a' mismatch
                Erlog.get(this).set("Unclosed quote", data);
                return false;
            }
            if (count != 2 || data.charAt(0) != quote || quote != data.charAt(len - 1)) {// 'a'='b' ignore until later
                return false;
            }
            data = data.substring(1, len - 1);
            quoted = true;
            return true;
        }

        return false;
    }
}
