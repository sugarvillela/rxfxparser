package compile.basics;

import compile.basics.Keywords;

/**
 *
 * @author Dave Swanson
 */
public class Factory_Node {

    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler){
        return new ScanNode(null, setCommand, setHandler, null, "");
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, String setData){
        return new ScanNode(null, setCommand, setHandler, null, setData);
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.KWORD setKWord){
        return new ScanNode(null, setCommand, setHandler, setKWord, "");
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.KWORD setKWord, String setData){
        return new ScanNode(null, setCommand, setHandler, setKWord, setData);
    }
    /** node for input and output list */
    public static class ScanNode{
        public static final String NULL_TEXT = "NULL";
        public static final int NUM_FIELDS = 5;
        public String lineCol;
        public Keywords.CMD cmd;
        public Keywords.HANDLER h;
        public Keywords.KWORD k;
        public String data;
        
        public ScanNode(String lineCol, Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.KWORD setKWord, String setData){
            this.lineCol = lineCol;
            h = setHandler;
            cmd = setCommand;
            k = setKWord;
            data = setData;
        }
        /**Data to string for writing to file
         * @return one line of a csv file */
        @Override
        public String toString(){//one line of a csv file
            return String.format(
                "%s,%s,%s,%s,%s",
                nullSafe(lineCol), 
                nullSafe(cmd), 
                nullSafe(h), 
                nullSafe(k),
                nullSafe(data)
            );
        }
        public String nullSafe(Object obj){//safe toString() for nullable object
            return(obj==null)? NULL_TEXT : obj.toString();
        }
        public String nullSafe(String str){//safe toString() for nullable object
            return(str == null || str.isEmpty())? NULL_TEXT : str;
        }
    }
    
    /*========================================================================*/
    
    public static GroupNode newGroupNode(String name, int n){
        return new GroupNode(name, n);
    }
    public static class GroupNode{
        public String n;
        public int s, e;
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
