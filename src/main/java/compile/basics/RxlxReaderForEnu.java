package compile.basics;

import compile.basics.Factory_Node.ScanNode;
import compile.parse.Base_ParseItem;
import erlog.Erlog;
import toksource.ScanNodeSource;
import toksource.TextSource_file;

public class RxlxReaderForEnu extends Base_Stack{
    
    public RxlxReaderForEnu(){
        Erlog.get(this);
    }
    private Base_StackItem get(ScanNode node){
        switch(node.h){
            case ENUB:
                return new TableBuilderENUB();
            case ENUD:
                return new TableBuilderENUD();
            }
        return null;
    }
    
    public boolean readFile(String path){
        ScanNodeSource source = new ScanNodeSource(
            new TextSource_file(Keywords.fileName_symbolTableEnu())
        );
        if(!source.hasData()){
            return false;
        }
        while(source.hasNext()){
            ScanNode currNode = source.nextNode();
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
                    ((IParseItem)getTop()).setAttrib(currNode.k, currNode.data);
                    break;
                default:
                    er.set("readFile: rxlx file improperly edited", currNode.cmd.toString());
            }
        }
        return true;
    }
    public static class TableBuilderENUB extends Base_StackItem{
        @Override
        public void onPush() {}

        @Override
        public void onPop() {}
    }
    public static class TableBuilderENUD extends Base_StackItem implements IParseItem{
        @Override
        public void onPush() {}

        @Override
        public void onPop() {}

        @Override
        public void addTo(Keywords.HANDLER handler, Keywords.KWORD key, String val) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setAttrib(Keywords.KWORD key, String val) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
