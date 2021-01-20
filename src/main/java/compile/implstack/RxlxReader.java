package compile.implstack;

import runstate.Glob;
import scannode.ScanNode;
import scannode.ScanNodeFactory;
import compile.interfaces.IParseItem;
import compile.parse.Base_ParseItem;
import runstate.RunState;
import toksource.ScanNodeSource;

import java.util.ArrayList;

public abstract class RxlxReader extends Base_Stack {
    //protected final ScanNodeFactory nodeFactory;
    protected ScanNodeSource fin;

    public RxlxReader(ScanNodeSource fin){
        this.fin = fin;
        //this.nodeFactory = Glob.SCAN_NODE_FACTORY;
    }

    protected abstract Base_ParseItem get(ScanNode node);

    @Override
    public void readFile(){
        if(fin != null && fin.hasData()){
            readFileLines();
        }
    }
    protected final void readFileLines(){
        RunState.getInstance().pauseActiveParserStack(this, fin);
        while(fin.hasNext()){
            ScanNode currNode = fin.nextNode();
            //System.out.println(currNode);
            if(currNode.cmd == null || currNode.datatype == null){
                er.set("Null command in rxlx file");
            }
            else{
                readNode(currNode);
            }
        }
        RunState.getInstance().resumeCurrParserStack();
    }

    public final void readList(ArrayList<ScanNode> list){
        //System.out.println("++++readList++++");
        RunState.getInstance().pauseActiveParserStack(this, fin);
        for(ScanNode node : list ){
            //System.out.println(node);
            //String topName = top==null? "NULL" : top.getDebugName();
            //System.out.println("readList: "+ stackSize +". top = " + topName);
            if(node.cmd == null || node.datatype == null){
                er.set("Null command in rxlx file");
            }
            else{
                readNode(node);
            }
        }
        RunState.getInstance().resumeCurrParserStack();
        //System.out.println("++++++++++++++++");
    }

    public void readNode(ScanNode node){
        switch (node.cmd){
            case PUSH:
                push(this.get(node));
                break;
            case POP:
                if(!node.datatype.equals(((Base_ParseItem)getTop()).getNode().datatype)){
                    er.set("POP: Stack mismatch", node.datatype.toString());
                }
                pop();
                break;
            case ADD_TO:
                //((IParseItem)getTop()).addTo(node.h, node.k, node.data);
                ((IParseItem)getTop()).addTo(node);
                break;
            case SET_ATTRIB:
                if(node.field == null){
                    er.set("Null key in rxlx file");
                }
                //((IParseItem)getTop()).setAttrib(node.h, node.k, node.data);
                ((IParseItem)getTop()).setAttrib(node);
                break;
            default:
                er.set("readFileLines: rxlx file improperly edited", node.cmd.toString());
        }
    }
}
