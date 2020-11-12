package compile.basics;

import static compile.basics.Keywords.CMD.POP;
import static compile.basics.Keywords.CMD.PUSH;

import commons.Commons;
import interfaces.DataNode;
import runstate.RunState;
import toksource.interfaces.ChangeListener;
import toksource.interfaces.ChangeNotifier;
import toksource.interfaces.ITextStatus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Dave Swanson
 */
public class Factory_Node implements ChangeListener {
    private static Factory_Node instance;

    public static Factory_Node getInstance(){
        return (instance == null)? (instance = new Factory_Node()) : instance;
    }
    private Factory_Node(){}

    private ITextStatus textStatus;

    @Override
    public void onTextSourceChange(ITextStatus textStatus, ChangeNotifier caller) {
        this.textStatus = textStatus;
    }

    /* factory access for scan node */

    public ScanNode newScanNode(Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, String setData){
        return new ScanNode(textStatus.loggableStatus(), setCommand, setDatatype, null, setData);
    }
    public ScanNode newScanNode(Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, Keywords.FIELD setField, String setData){
        return new ScanNode(textStatus.loggableStatus(), setCommand, setDatatype, setField, setData);
    }
    public ScanNode newPushNode(Keywords.DATATYPE setDatatype){
        return new ScanNode(textStatus.loggableStatus(), PUSH, setDatatype, null, null);
    }
    public ScanNode newPopNode(Keywords.DATATYPE setDatatype){
        return new ScanNode(textStatus.loggableStatus(), POP, setDatatype, null, null);
    }

    /** node for input and output list */
    public static class ScanNode extends DataNode {
        public static final int NUM_FIELDS = 5;
        public final String lineCol;
        public final Keywords.CMD cmd;
        public final Keywords.DATATYPE datatype;
        public final Keywords.FIELD field;
        public String data;
        
        public ScanNode(String lineCol, Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, Keywords.FIELD setField, String setData){
            this.lineCol =  lineCol;
            datatype =      setDatatype;
            cmd =           setCommand;
            field =         setField;
            data =          setData;
        }

        @Override
        public String readableContent() {
            ArrayList<String> out = new ArrayList<>();
            if(lineCol != null)     {out.add("lineCol: " +   lineCol);}
            if(cmd != null)         {out.add("cmd: " +       cmd.toString());}
            if(cmd != null)         {out.add("cmd: " +       cmd.toString());}
            if(datatype != null)    {out.add("datatype: " +  datatype.toString());}
            if(field != null)       {out.add("field: " +     field.toString());}
            if(data != null)        {out.add("data: " +      data);}
            return String.join(", ", out);
        }

        /**Data to string for writing to file
         * @return one line of a csv file */
        @Override
        public String toString(){//one line of a csv file
            return String.format(
                "%s,%s,%s,%s,%s",
                lineCol,
                Commons.nullSafe(cmd),
                Commons.nullSafe(datatype),
                Commons.nullSafe(field),
                Commons.nullSafe(data)
            );
        }
    }

    /*========================================================================*/

    public boolean persist(String path, ArrayList<Factory_Node.ScanNode> scanNodes){
        return persist(path, scanNodes, null);
    }
    public boolean persist(String path, ArrayList<ScanNode> scanNodes, String comment){
        try(BufferedWriter file = new BufferedWriter(new FileWriter(path))
        ){
            file.write("# Generated file, do not edit");
            file.newLine();
            file.write("# Last write: " + RunState.getInstance().getInitTime());
            file.newLine();
            if(comment != null){
                file.write("# " + comment);
                file.newLine();
            }

            for (ScanNode node: scanNodes) {
                file.write(node.toString());
                file.newLine();
            }
            file.close();
            return true;
        }
        catch(IOException e){
            return false;
        }
    }

    /*=====Unused=============================================================*/

    public static GroupNode newGroupNode(String name, int n){
        return new GroupNode(name, n);
    }
    public static class GroupNode{
        public final String n;
        public final int s, e;
        public GroupNode( String name, int n ){
            this.n = name;
            this.s = this.e = n;
        }
        @Override
        public String toString(){
            return this.n + ": start=" + s + " end=" + e;
        }
    }
}
