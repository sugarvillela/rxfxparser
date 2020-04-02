/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

import parse.Keywords.HANDLER;
import parse.Keywords.CMD;
import commons.Commons;
import itr_struct.StringSource;
import java.util.ArrayList;

/**Contains the language definition, including enums, constants, node classes
 *
 * @author Dave Swanson
 */
public interface IParse {
       
    // node for input and output list
    public class ScanNode{
        public CMD cmd;
        public HANDLER h;
        public String data;
        
        public ScanNode(CMD setCommand, HANDLER setHandler){
            this(setCommand, setHandler, "");
        }
        public ScanNode(CMD setCommand, HANDLER setHandler, String setData){
            h = setHandler;
            cmd = setCommand;
            data = setData;
        }
        @Override
        public String toString(){
            return Commons.objStr(cmd) + "," + 
                    Commons.objStr(h) + "," + data + ",";
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
    
    // Below: Interface
    // Stack commands
    public void push( Base_StackItem nuTop );   // enum CMD
    public void pop();                          // enum CMD
    
    // Fancy setters
    public void add(Object obj);                // enum CMD
    public void setAttrib(String key, Object value);// enum KEY
    public Object getAttrib(String key);           // enum KEY
    
    // Event-based
    public void onCreate(); // call if object exists before push
    public void onPush();   // call immediately after push
    public void onPop();    // call just before pop
    public void onQuit();   // call when program/task is finished
    
    // Getters
    public Base_StackItem getTop();
    public ArrayList<ScanNode> getScanNodeList();
    public StringSource getStringSource();
    public void disp();
}
