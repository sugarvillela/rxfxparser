package compile.basics;

import compile.parse.Base_ParseItem;
import erlog.Erlog;
import toksource.ScanNodeSource;
import toksource.interfaces.ITextStatus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class RxlxReader extends Base_Stack{
    protected final Factory_Node nodeFactory;


    protected ScanNodeSource fin;
    private Base_Stack otherParseStack;
    private ITextStatus otherStatusReporter;

    public RxlxReader(ScanNodeSource fin){
        this.fin = fin;
        this.nodeFactory = Factory_Node.getInstance();
    }

    protected abstract Base_ParseItem get(Factory_Node.ScanNode node);

    @Override
    public void onCreate(){
        if(fin != null && fin.hasData()){
            readFile();
        }
    }
    private void pauseOther(){
        CompileInitializer compileInitializer = CompileInitializer.getInstance();
        otherParseStack = compileInitializer.getCurrParserStack();
        compileInitializer.setCurrParserStack(this);
        otherStatusReporter = Erlog.getTextStatusReporter();
        Erlog.setTextStatusReporter(fin);
    }
    private void resumeOther(){
        CompileInitializer.getInstance().setCurrParserStack(otherParseStack);
        Erlog.setTextStatusReporter(otherStatusReporter);
    }
    protected final void readFile(){
        pauseOther();
        while(fin.hasNext()){
            Factory_Node.ScanNode currNode = fin.nextNode();
            //System.out.println(currNode);
            if(currNode.cmd == null || currNode.h == null){
                er.set("Null command in rxlx file");
            }
            else{
                readNode(currNode);
            }
        }
        resumeOther();
    }

    public final void readList(ArrayList<Factory_Node.ScanNode> list){
        //System.out.println("++++readList++++");
        pauseOther();
        for(Factory_Node.ScanNode node : list ){
            //System.out.println(node);
            //String topName = top==null? "NULL" : top.getDebugName();
            //System.out.println("readList: "+ stackSize +". top = " + topName);
            if(node.cmd == null || node.h == null){
                er.set("Null command in rxlx file");
            }
            else{
                readNode(node);
            }
        }
        resumeOther();
        //System.out.println("++++++++++++++++");
    }

    public void readNode(Factory_Node.ScanNode node){
        switch (node.cmd){
            case PUSH:
                push(this.get(node));
                break;
            case POP:
                if(!node.h.equals(((Base_ParseItem)getTop()).getNode().h)){
                    er.set("POP: Stack mismatch", node.h.toString());
                }
                pop();
                break;
            case ADD_TO:
                ((IParseItem)getTop()).addTo(node.h, node.k, node.data);
                break;
            case SET_ATTRIB:
                if(node.k == null){
                    er.set("Null key in rxlx file");
                }
                ((IParseItem)getTop()).setAttrib(node.h, node.k, node.data);
                break;
            default:
                er.set("readFile: rxlx file improperly edited", node.cmd.toString());
        }
    }
}
