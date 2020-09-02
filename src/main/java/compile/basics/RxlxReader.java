package compile.basics;

import compile.parse.Base_ParseItem;
import erlog.Erlog;
import toksource.ScanNodeSource;

public abstract class RxlxReader extends Base_Stack{
    protected ScanNodeSource fin;

    public RxlxReader(ScanNodeSource fin){
        this.fin = fin;
        er.setTextStatusReporter(this.fin);
    }

    protected abstract Base_ParseItem get(Factory_Node.ScanNode node);

    public void onCreate(){
        if(fin == null || !fin.hasData()){
            return;
        }
        CompileInitializer.getInstance().setCurrParserStack(this);
        while(fin.hasNext()){
            Factory_Node.ScanNode currNode = fin.nextNode();
            System.out.println(currNode);
            if(currNode.cmd == null || currNode.h == null){
                er.set("readFile: Null command");
            }
            switch (currNode.cmd){
                case PUSH:
                    push(this.get(currNode));
                    break;
                case POP://
                    if(!currNode.h.equals(((Base_ParseItem)getTop()).getNode().h)){
                        er.set("POP: Stack mismatch, check source file", currNode.h.toString());
                    }
                    pop();
                    break;
                case ADD_TO:
                    ((IParseItem)getTop()).addTo(currNode.h, currNode.k, currNode.data);
                    break;
                case SET_ATTRIB:
                    if(currNode.k == null){
                        er.set("SET_ATTRIB: Null key");
                    }
                    ((IParseItem)getTop()).setAttrib(currNode.h, currNode.k, currNode.data);
                    break;
                default:
                    er.set("readFile: rxlx file improperly edited", currNode.cmd.toString());
            }
        }
    }
}
