/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toksource;

import erlog.Erlog;
import java.util.ArrayList;
import parse.Keywords;
import parse.factories.Factory_Node.ScanNode;
import static parse.factories.Factory_Node.ScanNode.NULL_TEXT;
import static parse.factories.Factory_Node.ScanNode.NUM_FIELDS;
import toksource.interfaces.ITextSource;

public class ScanNodeSource implements ITextSource{
    protected final Erlog er;
    TextSource_file fin;
    private final ArrayList<ScanNode> nodes;
    
    public ScanNodeSource(String inName){
        inName += ".rxlx";
        er = Erlog.get();
        fin = new TextSource_file(inName);
        if( !fin.hasData() ){
            er.set( "Bad input file name", inName );
        }
        er.setTextStatusReporter(fin);
        nodes = new ArrayList<>();
    }
    
    public ScanNode nextNode(){
        String text = this.next();
        String[] tok = new String[NUM_FIELDS];
        int j = 0, start = 0;
        for( int i=0; i<text.length(); i++ ){
            if( text.charAt(i) == ',' ){
                tok[j]=text.substring(start, i);
                System.out.printf("%d, %d, %d, %s \n", i, j, start, tok[j]);
                start=i+1;
                j++;
            }
            if(j == NUM_FIELDS){
                break;
            }
        }
        return new ScanNode(
            NULL_TEXT.equals(tok[0])? "" : tok[0],
            NULL_TEXT.equals(tok[1])? null : Keywords.CMD.get(tok[1]),
            NULL_TEXT.equals(tok[2])? null : Keywords.HANDLER.get(tok[2]),
            NULL_TEXT.equals(tok[3])? null : Keywords.KWORD.get(tok[3]),
            NULL_TEXT.equals(tok[4])? "" : tok[4]
        );
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
        return fin.readableStatus();
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
