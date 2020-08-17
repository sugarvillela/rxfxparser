package compile.scan;

import compile.basics.Keywords;
import compile.basics.Base_StackItem;
import erlog.Erlog;
import java.util.Arrays;
import compile.basics.Keywords.HANDLER;
import compile.basics.Factory_Node.ScanNode;
import compile.scan.factories.Factory_Strategy.Strategy;

/**Base class provides common tasks
 * Derived classes are handlers for context-sensitive control of stack
 *
 * @author Dave Swanson
 */

public abstract class Base_ScanItem extends Base_StackItem{
    protected Class_Scanner P;
    protected HANDLER h;                    // class's own enum
    protected Keywords.HANDLER[] allowedHandlers;// children handlers to instantiate
    protected Strategy[] strategies, onPushStrategies, onPopStrategies;
    
    public Base_ScanItem(){
        P = Class_Scanner.getInstance();
    }

    @Override
    public void onPush(){
        if(h != null){
            System.out.println("onPush: " + h.toString());
        }
        if(onPushStrategies != null){
            for(Strategy strategy : onPushStrategies){
                System.out.println(strategy);
                if(strategy.go(null, this)){
                    return;
                }
            }
        }
    }
    
    @Override
    public void onPop(){
        if(h != null){
            System.out.println("onPop: " + h.toString());
        }
        if(onPopStrategies != null){
            for(Strategy strategy : onPopStrategies){
                System.out.println(strategy);
                if(strategy.go(null, this)){
                    return;
                }
            }
        }
    }

    public void pushPop(String text) {
        if(strategies != null){
            for(Strategy strategy : strategies){
                if(strategy.go(text, this)){
                    return;
                }
            }
        }
    }
    public void setHandler(HANDLER h){
        this.h = h;
    }
    public HANDLER getHandler(){
        return this.h;
    }
    
    public final void addNode(ScanNode node){
        P.addNode(node);
    }
    
    public final void setAllowedHandlers(Keywords.HANDLER[] allowedHandlers){
        System.out.println("Set Allowed Handlers");
        System.out.println(Arrays.toString(allowedHandlers));
        this.allowedHandlers = allowedHandlers;
    }

    public final boolean isBadHandler(Keywords.HANDLER handler){
        return(
            allowedHandlers == null || 
            !Arrays.asList(allowedHandlers).contains(handler)
        );
    }
    
    public final boolean assertGoodHandler(Keywords.HANDLER handler){
        if(isBadHandler(handler)){
            Erlog.get(this).set( "Disallowed handler: ", handler.toString());
            return false;
        }
        return true;
    }
}
    
