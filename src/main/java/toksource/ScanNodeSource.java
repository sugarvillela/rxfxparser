/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toksource;

import java.util.ArrayList;
import langdef.Keywords;
import scannode.ScanNode;

import static scannode.ScanNode.NUM_FIELDS;
import static langdef.Keywords.*;

import interfaces.ILifeCycle;
import toksource.interfaces.ITextSource;
import toksource.interfaces.ITextWordOrLine;

public class ScanNodeSource implements ITextSource, ITextWordOrLine, ILifeCycle {
    Base_TextSource fin;
    ScanNode currNode;
    private NodeGen nodeGen;
    //private final NodeGen standard, rx;
    
    public ScanNodeSource(Base_TextSource lineGetter){
        this.fin = lineGetter;
        //standard = new StandardNodeGen();
        //rx = new RxNodeGen();
        nodeGen = new StandardNodeGen();
    }

    public ScanNode nextNode(){
        String next = next();
        currNode = nodeGen.nextNode(next);
        return currNode;
    }
    
    private interface NodeGen{
        ScanNode nextNode(String next);
    }
    private class StandardNodeGen implements NodeGen{
        @Override
        public ScanNode nextNode(String next){
            String[] tok = next.split(",", NUM_FIELDS);
            return new ScanNode(
                tok[0],
                Keywords.CMD.fromString(tok[1]),
                Keywords.DATATYPE.fromString(tok[2]),
                NULL_TEXT.equals(tok[3])? null : Keywords.FIELD.fromString(tok[3]),
                NULL_TEXT.equals(tok[4])? "" : tok[4]
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
    public String getFileName() {
        return fin.getFileName();
    }

    @Override
    public String loggableStatus(){
        return String.format(LOGGABLE_FORMAT, this.getFileName(), this.getRow(), this.getCol());
    }
    @Override
    public String readableStatus() {
        return(currNode == null)?
                "interim: " + fin.readableStatus() :
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
