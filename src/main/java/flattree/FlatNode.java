package flattree;

import commons.Commons;
import sublang.factories.PayNodes;
import erlog.Erlog;
import interfaces.DataNode;
import sublang.treenode.TreeNodeBase;
import toktools.TK;
import toktools.Tokens_special;

import java.util.ArrayList;

import static langdef.Keywords.OP.PAYLOAD;

public class FlatNode extends DataNode {
    private static final Tokens_special tokenizer = new Tokens_special(",", "'", TK.IGNORESKIP );
    private static final int NUM_FIELDS = 6;
    public final char op;
    public final int parent, self;
    public final int children[];
    public DataNode[] payNodes;
    private int iChild;

    private int iPayNode;
    private final FlatNode[] treeArr;

    public FlatNode(int iSelf, int iParent, TreeNodeBase treeNode, FlatNode[] treeArr) {
        this.op = treeNode.op.asChar;
        self = iSelf;
        parent = iParent;
        children = (treeNode.nodes == null) ? null : new int[treeNode.nodes.size()];
        this.payNodes = (treeNode.payNodes == null) ?
                null :
                treeNode.payNodes.toArray(new DataNode[treeNode.payNodes.size()]);
        this.treeArr = treeArr;
        iChild = -1;
    }

    public void addChild(int child) {
        if (children != null) {
            iChild++;
            children[iChild] = child;
        }
    }

    public FlatNode getParent(FlatNode[] flatTree) {
        return (parent == -1) ? null : flatTree[parent];
    }

    @Override
    public String readableContent() {
        return String.format(
                "op='%c', self=%d, parent=%d, children=%s, payNodes=%s ",
                op,
                self,
                parent,
                Commons.nullSafe(children),
                allReadablePayloadContent()
        );
    }

    @Override
    public String toString() {
        int childLen = (children == null) ? 0 : children.length;
        int payLen = (payNodes == null) ? 0 : payNodes.length;
        return String.format("%c,%d,%d,%d,%s,%d", op, self, parent, childLen, Commons.nullSafe(children), payLen);
    }

    /*=====Rebuild================================================================================================*/

    public FlatNode(String scanNodeText, FlatNode[] treeArr) {
        this.treeArr = treeArr;
        String[] tok = tokenizer.toArr(scanNodeText);
        //Commons.disp(tok, "FlatNode Constructor");
        if (tok.length != NUM_FIELDS) {
            Erlog.get(this).set("Bad scan node text size" + tok.length, scanNodeText);
        }
        op = tok[0].charAt(0);
        self = Commons.undoNullSafe_int(tok[1]);
        parent = Commons.undoNullSafe_int(tok[2]);
        int childLen = Commons.undoNullSafe_int(tok[3]);
        children = (childLen == 0) ? null : Commons.undoNullSafe_intArray(tok[4]);
        int payLen = Commons.undoNullSafe_int(tok[5]);
        payNodes = (payLen == 0) ? null : new DataNode[payLen];
        iChild = -1;
        iPayNode = -1;
    }

    public void addPayNode(DataNode payNode) {
        payNodes[++iPayNode] = payNode;
    }

    private String allReadablePayloadContent() {
        if (payNodes == null) {
            return "None";
        }
        ArrayList<String> out = new ArrayList<>();
        for (DataNode rxPayNode : payNodes) {
            out.add("\t\t" + rxPayNode.readableContent());
        }
        return "\n" + String.join("\n", out);
    }

    /*=====Iterate================================================================================================*/

    public void preOrder(ArrayList<FlatNode> list) {
        list.add(this);
        if (children != null) {
            for (int i : children) {
                treeArr[i].preOrder(list);
            }
        }
    }

    public void buildString(ArrayList<String> list) {
        if (op == PAYLOAD.asChar) {
            ArrayList<String> payNodeStrings = new ArrayList<>();
            for (DataNode dataNode : payNodes) {
                PayNodes.RxPayNode rxNode = ((PayNodes.RxPayNode) dataNode);
                payNodeStrings.add(rxNode.item);
            }
            list.add(String.format("%s", String.join(".", payNodeStrings)));
        } else {
            ArrayList<String> opList = new ArrayList<>();
            String delimiter = String.format(") %c%c (", op, op);
            for (int i : children) {
                treeArr[i].buildString(opList);
            }
            list.add(String.format("(%s)", String.join(delimiter, opList)));
        }

    }

    public FlatNode[] getTreeArr() {
        return treeArr;
    }

    public DataNode[] getPayNodes() {
        return payNodes;
    }
//        public void rewind(){
//            iChild = 0;
//        }
//        public boolean hasNext(){
//            return treeArr[children[iChild]].hasNext() ||
//                (++iChild < children.length && treeArr[children[iChild]].hasNext());
//        }
//        public FlatNode next(){
//            return treeArr[children[iChild]];
//        }
}
