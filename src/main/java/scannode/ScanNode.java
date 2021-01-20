package scannode;

import commons.Commons;
import interfaces.DataNode;
import langdef.Keywords;

import java.util.ArrayList;

/**
 * node for input and output list
 */
public class ScanNode extends DataNode {
    public static final int NUM_FIELDS = 5;
    public final String lineCol;
    public final Keywords.CMD cmd;
    public final Keywords.DATATYPE datatype;
    public final Keywords.FIELD field;
    public String data;

    public ScanNode(String lineCol, Keywords.CMD setCommand, Keywords.DATATYPE setDatatype, Keywords.FIELD setField, String setData) {
        this.lineCol = lineCol;
        datatype = setDatatype;
        cmd = setCommand;
        field = setField;
        data = setData;
    }

    @Override
    public String readableContent() {
        ArrayList<String> out = new ArrayList<>();
        if (lineCol != null) {
            out.add("lineCol: " + lineCol);
        }
        if (cmd != null) {
            out.add("cmd: " + cmd.toString());
        }
        if (cmd != null) {
            out.add("cmd: " + cmd.toString());
        }
        if (datatype != null) {
            out.add("datatype: " + datatype.toString());
        }
        if (field != null) {
            out.add("field: " + field.toString());
        }
        if (data != null) {
            out.add("data: " + data);
        }
        return String.join(", ", out);
    }

    /**
     * Data to string for writing to file
     *
     * @return one line of a csv file
     */
    @Override
    public String toString() {//one line of a csv file
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
