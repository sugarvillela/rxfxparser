/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toksource;

import compile.basics.Factory_Node.RxScanNode;
import static compile.basics.Factory_Node.RxScanNode.NUM_RX_FIELDS;
import erlog.Erlog;
import java.util.ArrayList;
import compile.basics.Keywords;
import compile.basics.Factory_Node.ScanNode;
import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Factory_Node.ScanNode.NUM_FIELDS;
import static compile.basics.Keywords.HANDLER.RX_BUILDER;
import toksource.interfaces.ITextSource;

public class ScanNodeSource implements ITextSource{
    protected Erlog er;
    ITextSource fin;
    ScanNode currNode;
    private NodeGen nodeGen;
    private final NodeGen standard, rx;
    
    public ScanNodeSource(ITextSource lineGetter){
        this.fin = lineGetter;
        standard = new StandardNodeGen();
        rx = new RxNodeGen();
        nodeGen = standard;
        init();
    }
    private void init(){
        er = Erlog.get(this);
        er.setTextStatusReporter(fin);
    }
    private void toggleNodeGen(){
        if(nodeGen == standard){
            nodeGen = rx;
        }
        else{
            nodeGen = standard;
        }
    }
    
    public ScanNode nextNode(){
        String next = next();
        currNode = nodeGen.nextNode(next);
        if(currNode == null){
            toggleNodeGen();
            currNode = nodeGen.nextNode(next);
        }
        return currNode;
    }
    
    private interface NodeGen{
        ScanNode nextNode(String next);
    }
    private class StandardNodeGen implements NodeGen{
        @Override
        public ScanNode nextNode(String next){
            String[] tok = next.split(",", NUM_FIELDS);
            Keywords.HANDLER handler = Keywords.HANDLER.fromString(tok[2]);
            if(RX_BUILDER.equals(handler)){
                return null;
            }
            return new ScanNode(
                tok[0],
                Keywords.CMD.get(tok[1]),
                handler,
                NULL_TEXT.equals(tok[3])? null : Keywords.KWORD.fromString(tok[3]),
                NULL_TEXT.equals(tok[4])? "" : tok[4]
            );
        }
    }
    private class RxNodeGen implements NodeGen{
        @Override
        public ScanNode nextNode(String next){
            String[] tok = next.split(",", NUM_RX_FIELDS);
            Keywords.HANDLER handler = Keywords.HANDLER.fromString(tok[2]);
            if(!RX_BUILDER.equals(handler)){
                return null;
            }
//                String lineCol, 
//                Keywords.CMD setCommand, 
//                Keywords.HANDLER setHandler, 
//                Keywords.OP setOp, 
//                String setData, 
//                boolean negate, 
//                int id
            return new RxScanNode(
                tok[0],
                Keywords.CMD.get(tok[1]),
                handler,
                NULL_TEXT.equals(tok[3])? null : Keywords.OP.fromString(tok[3]),
                NULL_TEXT.equals(tok[4])? "" : tok[4], 
                Boolean.parseBoolean(tok[5]), 
                NULL_TEXT.equals(tok[6])? -1 : Integer.parseInt(tok[6])
            );
        }
    }
    
    @Override
    public void rewind() {fin.rewind();}

    /**This one ignores comment lines that start with #
     * @return first non-comment string encountered */
    @Override
    public String next() {
        String next = null;
        while(fin.hasNext()){
            next = fin.next();
            if(!next.startsWith("#")){
                break;
            }
        }
        return next;
    }

    @Override
    public boolean hasNext() {return fin.hasNext();}

    @Override
    public boolean hasData() {return fin.hasData();}

    @Override
    public void setLineGetter() {}

    @Override
    public void setWordGetter() {}

    @Override
    public boolean isLineGetter() {return fin.isLineGetter();}

    @Override
    public boolean isWordGetter() {return fin.isWordGetter();}

    @Override
    public int getRow() {return fin.getRow();}

    @Override
    public int getCol() {return fin.getCol();}

    @Override
    public String readableStatus() {
        return(currNode == null)? 
            "interim rxlx file location" + fin.readableStatus() : 
            "source " + currNode.lineCol + " interim " + fin.readableStatus();
    }

    @Override
    public boolean isEndLine() {
        return fin.isEndLine();
    }

    @Override
    public void onCreate() {}

    @Override
    public void onPush() {}
    
    @Override
    public void onBeginStep(){}
    
    @Override
    public void onEndStep(){}
    
    @Override
    public void onPop() {}

    @Override
    public void onQuit() {}
    
    public static ArrayList<ScanNode> readAll(String path){
        ScanNodeSource source = new ScanNodeSource(new TextSource_file(path));
        ArrayList<ScanNode> out = new ArrayList<>();
        if( source.hasData() ){
            while(source.hasNext()){
                out.add(source.nextNode());
            }
        }
        return out;
    }
}
