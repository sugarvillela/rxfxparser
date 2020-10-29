package compile.parse.ut;

import compile.basics.Factory_Node;
import compile.basics.Keywords;
import compile.parse.Base_ParseItem;
import compile.sublang.factories.PayNodes;
import compile.sublang.ut.FlatTree;
import toksource.ScanNodeSource;

import static compile.basics.Keywords.DATATYPE.*;
import static compile.basics.Keywords.FIELD.LENGTH;

public class FlatTreeBuilder{
    private final Base_ParseItem parent;
    private final ScanNodeSource source;
    private final Keywords.DATATYPE datatype;
    private final PayNodes.PayNodeFactory factory;
    private FlatTree flatTree;
    boolean popParent;

    public FlatTreeBuilder(Keywords.DATATYPE datatype, ScanNodeSource source, Base_ParseItem parent){
        this.parent = parent;
        this.source = source;
        this.datatype = datatype = (RX_WORD.equals(datatype))? RX : FX;
        factory = PayNodes.getFactory(datatype);
        popParent = false;
    }
    public void build(){
        while(!popParent && source.hasNext()){
            readNode(source.nextNode());
        }
        flatTree.disp();
    }
    public FlatTree get(){
        return flatTree;
    }
    private void readNode(Factory_Node.ScanNode currNode){
        //System.out.println(currNode);
        switch(currNode.datatype){
            case RX_BUILDER:
            case FX_BUILDER:
                readBuilderCmd(currNode);
                break;
            case RX_PAY_NODE:
            case FX_PAY_NODE:
                readPayNodeCmd(currNode);
                break;
            default:
                readParentCmd(currNode);
        }
    }
    private void readParentCmd(Factory_Node.ScanNode currNode){
        switch(currNode.cmd){
            case SET_ATTRIB:
                parent.setAttrib(currNode);
                break;
            case POP:
                popParent = true;
        }
    }

    private void readBuilderCmd(Factory_Node.ScanNode currNode){
        switch(currNode.cmd){
            case SET_ATTRIB:
                if(LENGTH.equals(currNode.field)){
                    int length = Integer.parseInt(currNode.data);
                    flatTree = new FlatTree(currNode.datatype, length);
                }
                break;
            case ADD_TO:
                flatTree.addTo(new FlatTree.FlatNode(currNode.data));
                break;
            case PUSH:
            case POP:
                //System.out.println("readBuilderCmd:" + currNode.cmd);
                break;
        }
    }
    private void readPayNodeCmd(Factory_Node.ScanNode currNode){
        switch(currNode.cmd){
            case ADD_TO:
                flatTree.addPayNode(factory.payNodeFromScanNode(currNode.data));
                break;
            case PUSH:
            case POP:
                //System.out.println("readPayNodeCmd:" + currNode.cmd);
                break;
        }
    }
}
