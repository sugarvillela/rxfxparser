package sublang.treenode;

import erlog.Erlog;
import langdef.Keywords;
import scannode.ScanNode;

import static langdef.Keywords.DATATYPE.FX;

public class FxTreeNode extends TreeNodeBase {
    protected static final int NUM_TREE_FIELDS = 4;

    public FxTreeNode(String data, int level, TreeNodeBase parent) {
        super(FX, data, level, parent);
    }

    public FxTreeNode(ScanNode scanNode) {
        super(FX);
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
    public boolean unquote(char quote) {// just in case I forget
        Erlog.get(this).set("FX doesn't support quoted text");
        return false;
    }
}
