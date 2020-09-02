package compile.scan;

import compile.basics.Keywords;
import compile.basics.Base_StackItem;
import erlog.Erlog;
import java.util.Arrays;
import compile.basics.Keywords.HANDLER;
import compile.basics.Factory_Node.ScanNode;
import compile.scan.factories.Factory_Strategy.Strategy;
import java.util.ArrayList;

/**Base class provides common tasks
 * Derived classes are handlers for context-sensitive control of stack
 *
 * @author Dave Swanson
 */

public abstract class Base_ScanItem extends Base_StackItem{
    protected HANDLER h;                    // class's own enum
    protected Keywords.HANDLER[] allowedHandlers;// children handlers to instantiate
    protected Strategy[] strategies, onPushStrategies, onPopStrategies;
    protected String defName;
    
    public Base_ScanItem(){
    }

    @Override
    public void onPush(){
        if(h != null){
            System.out.println("onPush: " + h.toString());
        }
        if(onPushStrategies != null){
            for(Strategy strategy : onPushStrategies){
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
    
    public void setDefName(String defName){
        this.defName = defName;
    }
    public String getDefName(){
        return this.defName;
    }
    
    public final void addNode(ScanNode node){
        ((Class_Scanner)P).addNode(node);
    }
    public final void prependNodes(ArrayList<ScanNode> prepend){
        ((Class_Scanner)P).prependNodes(prepend);
    }
    
    public final void setAllowedHandlers(Keywords.HANDLER[] allowedHandlers){
        this.allowedHandlers = allowedHandlers;
    }
    public final boolean isGoodHandler(Keywords.HANDLER handler){
        return(
            allowedHandlers != null && 
            Arrays.asList(allowedHandlers).contains(handler)
        );
    }
}
    
