package scannode;

import static langdef.Keywords.CMD.POP;
import static langdef.Keywords.CMD.PUSH;

import langdef.Keywords;
import runstate.Glob;
import sublang.TreeBuildUtil;
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
public class ScanNodeFactory implements ChangeListener {
    private static ScanNodeFactory instance;

    public static ScanNodeFactory init(){
        return (instance == null)? (instance = new ScanNodeFactory()) : instance;
    }

    private ScanNodeFactory(){}

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

    /*========================================================================*/

    public boolean persist(String path, ArrayList<ScanNode> scanNodes){
        return persist(path, scanNodes, null);
    }

    public boolean persist(String path, ArrayList<ScanNode> scanNodes, String comment){
        try(BufferedWriter file = new BufferedWriter(new FileWriter(path))
        ){
            file.write("# Generated file, do not edit");
            file.newLine();
            file.write("# Last write: " + Glob.TIME_INIT);
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
}
