/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse.interfaces;

import parse.Keywords.HANDLER;
import parse.Keywords.CMD;
import parse.Keywords.KWORD;

import commons.Commons;
import java.util.ArrayList;
import parse.Base_StackItem;
import toksource.TokenSource;

/**Interface and static node classes for scan/parse components
 *
 * @author Dave Swanson
 */
public interface IParse {
       
    /** node for input and output list */
    public class ScanNode{
        public CMD cmd;
        public HANDLER h;
        public KWORD k;
        public String data;
        
        public ScanNode(CMD setCommand, HANDLER setHandler){
            this(setCommand, setHandler, null, "");
        }
        public ScanNode(CMD setCommand, HANDLER setHandler, String setData){
            this(setCommand, setHandler, null, setData);
        }
        public ScanNode(CMD setCommand, HANDLER setHandler, KWORD setKWord){
            this(setCommand, setHandler, setKWord, "");
        }
        public ScanNode(CMD setCommand, HANDLER setHandler, KWORD setKWord, String setData){
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
    public class GroupNode{
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
    
//    // Fancy setters
//    public void add(Object obj);                // enum CMD
//    public void setAttrib(String key, Object value);// enum KEY
//    //public Object getAttrib(String key);           // enum KEY
    
    // Getters
    public ArrayList<ScanNode> getScanNodeList();
    public TokenSource getTokenSource();
    public void disp();
}
