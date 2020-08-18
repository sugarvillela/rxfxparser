/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toksource;

import erlog.Erlog;
import java.util.ArrayList;
import compile.basics.Keywords;
import compile.basics.Factory_Node.ScanNode;
import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import static compile.basics.Factory_Node.ScanNode.NUM_FIELDS;
import toksource.interfaces.ITextSource;

public class ScanNodeSource implements ITextSource{
    protected final Erlog er;
    TextSource_file fin;
    ScanNode currNode;
    
    public ScanNodeSource(String path){
        er = Erlog.get();
        fin = new TextSource_file(path);
        if( !fin.hasData() ){
            er.set( "Bad input file name", path );
        }
        er.setTextStatusReporter(fin);
    }
    
    public ScanNode nextNode(){
        String[] tok = this.next().split(",", NUM_FIELDS);
        currNode = new ScanNode(
            NULL_TEXT.equals(tok[0])? "" : tok[0],
            NULL_TEXT.equals(tok[1])? null : Keywords.CMD.get(tok[1]),
            NULL_TEXT.equals(tok[2])? null : Keywords.HANDLER.get(tok[2]),
            NULL_TEXT.equals(tok[3])? null : Keywords.KWORD.get(tok[3]),
            NULL_TEXT.equals(tok[4])? "" : tok[4]
        );
        //System.out.print("\ncurrNode = ");
        //System.out.println(currNode);
        return currNode;
    }

    @Override
    public void rewind() {fin.rewind();}

    @Override
    public String next() {return fin.next();}

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
    
    public static ArrayList<ScanNode> readAll(String name){
        ScanNodeSource source = new ScanNodeSource( name );
        ArrayList<ScanNode> out = new ArrayList<>();
        if( source.hasData() ){
            while(source.hasNext()){
                out.add(source.nextNode());
            }
        }
        return out;
    }
}
