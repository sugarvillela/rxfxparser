package parse;

import java.util.ArrayList;
import parse.Keywords.HANDLER;
import parse.factories.Factory_Node.ScanNode;
import parse.factories.Factory_Node;
import parse.interfaces.IContext;
import parse.ut.ContextAction;

/**Base class provides common tasks
 * Derived classes are handlers for context-sensitive control of stack
 *
 * @author Dave Swanson
 */

public abstract class Base_Context extends Base_StackItem implements IContext{
    protected final ArrayList<ScanNode> nodes;
    protected final ContextAction action;
    protected HANDLER h;                    // class's own enum

    public Base_Context(){
        P = Class_Scanner.getInstance();
        nodes = ((Class_Scanner)P).getScanNodeList();
        action = new ContextAction();
    }
    
    // stubs: children may override to implement
    @Override
    public void add(Object obj){}
    @Override
    public void setAttrib(String key, Object value){}

    @Override
    public void onPush(){
        nodes.add( Factory_Node.newScanNode( Keywords.CMD.PUSH, this.h ) );
    }
    
    @Override
    public void onPop(){
        nodes.add( Factory_Node.newScanNode( Keywords.CMD.POP, this.h ) );
    }
    
    // helper Util
    protected void addText( String text ){//override to add more validation
        nodes.add( Factory_Node.newScanNode( Keywords.CMD.ADD_TO, h, text));
    }
}
    
