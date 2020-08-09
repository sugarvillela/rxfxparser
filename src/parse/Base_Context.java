package parse;

import erlog.Erlog;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Keywords.HANDLER[] allowedHandlers;// children handlers to instantiate
    
    public Base_Context(){
        P = Class_Scanner.getInstance();
        nodes = ((Class_Scanner)P).getScanNodeList();
        action = ContextAction.getInstance();
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
    
    protected final void setAllowedHandlers(Keywords.HANDLER[] allowedHandlers){
        System.out.println("Set Allowed Handlers");
        System.out.println(Arrays.toString(allowedHandlers));
        this.allowedHandlers = allowedHandlers;
    }
    // sets a parse error
    public final boolean assertGoodHandler(Keywords.HANDLER handler){
        System.out.println("assertGoodHandler");
        if(allowedHandlers == null){
            System.out.println("allowedHandlers is null");
            return false;
        }
        System.out.println(Arrays.toString(allowedHandlers));
        if(handler == null){
            return false;
        }      
        else if(!Arrays.asList(allowedHandlers).contains(handler)){
            Erlog.get(this).set( "Disallowed handler: ", handler.toString());
            return false;
        }
        return true;
    }
}
    
