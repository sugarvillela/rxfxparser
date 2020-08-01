package parse.factories;

import commons.Commons;
import parse.Keywords;

/**
 *
 * @author Dave Swanson
 */
public class Factory_Node {

    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler){
        return new ScanNode(setCommand, setHandler, null, "");
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, String setData){
        return new ScanNode(setCommand, setHandler, null, setData);
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.KWORD setKWord){
        return new ScanNode(setCommand, setHandler, setKWord, "");
    }
    public static ScanNode newScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.KWORD setKWord, String setData){
        return new ScanNode(setCommand, setHandler, setKWord, setData);
    }
    /** node for input and output list */
    public static class ScanNode{
        public Keywords.CMD cmd;
        public Keywords.HANDLER h;
        public Keywords.KWORD k;
        public String data;
        
        public ScanNode(Keywords.CMD setCommand, Keywords.HANDLER setHandler, Keywords.KWORD setKWord, String setData){
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
                "%s,%s,%s,%s,", 
                Commons.objStr(cmd), 
                Commons.objStr(h), 
                Commons.objStr(k),
                data
            );
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
