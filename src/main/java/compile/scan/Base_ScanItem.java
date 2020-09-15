package compile.scan;

import commons.Commons;
import compile.basics.Keywords;
import compile.basics.Base_StackItem;
import compile.scan.factories.Factory_Strategy;
import compile.symboltable.TextSniffer;
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
    protected Factory_Strategy.StrategyEnum[] strategies, onPushStrategies, onPopStrategies;
    protected String defName;
    protected int state, loIndex, hiIndex;
    protected final TextSniffer textSniffer;
    
    public Base_ScanItem(){
        textSniffer = TextSniffer.getInstance();
        state = 0;
    }

    @Override
    public void onPush(){
        loIndex = ((Class_Scanner)P).getScanNodeList().size();
//        String handlerStr = (h == null)? "Null Handler" : h.toString();
//        System.out.println("Base_ScanItem onPush: " + handlerStr + ", loIndex: " + loIndex);
        if(onPushStrategies != null){
            for(Factory_Strategy.StrategyEnum strategyEnum : onPushStrategies){
                if(strategyEnum.strategy.go(null, this)){
                    return;
                }
            }
        }
    }
    
    @Override
    public void onPop(){
        if(onPopStrategies != null){
            for(Factory_Strategy.StrategyEnum strategyEnum : onPopStrategies){
                if(strategyEnum.strategy.go(null, this)){
                    break;
                }
            }
        }
        hiIndex = ((Class_Scanner)P).getScanNodeList().size();
//        String handlerStr = (h == null)? "Null Handler" : h.toString();
//        System.out.println("Base_ScanItem onPop: " + handlerStr + ", loIndex: " + hiIndex);
    }

    public void pushPop(String text) {
        if(textSniffer.isSniffing()){
            textSniffer.sniff(text);
        }
        if(strategies != null){
            for(Factory_Strategy.StrategyEnum strategyEnum : strategies){
                if(strategyEnum.strategy.go(text, this)){
                    return;
                }
            }
        }
    }
    public void back(){
        textSniffer.back();
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

    public void setState(int state){
        this.state = state;
    }
    public int getState(){
        return this.state;
    }
    public boolean isDoneState(){
        return state == 0;
    }
    public void assertDoneState(){
        if(state != 0){
            er.set("A language structure is incomplete");
        }
    }
    public final void addNode(ScanNode node){
        ((Class_Scanner)P).addNode(node);
    }
    public final void addNodes(ArrayList<ScanNode> newNodes){
        ((Class_Scanner)P).addNodes(newNodes);
    }

    public final ArrayList<ScanNode> getScanNodeList(){
        hiIndex = ((Class_Scanner)P).getScanNodeList().size();
        return new ArrayList<>(((Class_Scanner)P).getScanNodeList().subList(loIndex, hiIndex));
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
    
