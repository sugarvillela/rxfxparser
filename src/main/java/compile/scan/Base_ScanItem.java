package compile.scan;

import compile.implitem.Base_StackItem;
import compile.scan.factories.Factory_Strategy;
import compile.symboltable.TextSniffer;

import java.util.Arrays;
import langdef.Keywords.DATATYPE;
import runstate.Glob;
import scannode.ScanNode;

import java.util.ArrayList;

import static langdef.Keywords.DATATYPE.SCOPE;

/**Base class provides common tasks
 * Derived classes are handlers for context-sensitive control of stack
 *
 * @author Dave Swanson
 */

public abstract class Base_ScanItem extends Base_StackItem{
    protected DATATYPE datatype;                    // class's own enum
    protected DATATYPE[] allowedDatatypes;// children datatypes to instantiate
    protected Factory_Strategy.StrategyEnum[] strategies;
    protected Factory_Strategy.PushEnum[] onPushStrategies;
    protected Factory_Strategy.PopEnum[] onPopStrategies;
    protected String defName;
    protected int state;
    protected final TextSniffer textSniffer;
    public final boolean cacheable;
    protected boolean specialScope;
    
    public Base_ScanItem(boolean cacheable){
        state = 0;
        this.cacheable = cacheable;
        specialScope = false;
        this.textSniffer = Glob.TEXT_SNIFFER;
    }

    /*=====IStackItem methods============================================*/

    @Override
    public void onPush(){
//        String datatypeStr = (h == null)? "Null Datatype" : h.toString();
//        System.out.println("Base_ScanItem onPush: " + datatypeStr + ", loIndex: " + loIndex);
        textSniffer.onPush(this);
        if(onPushStrategies != null){
            for(Factory_Strategy.PushEnum strategyEnum : onPushStrategies){
                if(strategyEnum.strategy.go(null, this)){
                    return;
                }
            }
        }
    }
    
    @Override
    public void onPop(){
        textSniffer.onPop(this);
        if(onPopStrategies != null){
            for(Factory_Strategy.PopEnum strategyEnum : onPopStrategies){
                if(strategyEnum.strategy.go(null, this)){
                    break;
                }
            }
        }
//        String datatypeStr = (datatype == null)? "Null Datatype" : datatype.toString();
//        System.out.println("====onPop()====" + datatypeStr);
    }

    /*=====ScanItem methods==============================================*/

    public void pushPop(String text) {
        if(strategies != null){
            //System.out.println("====pushPop====" + getDebugName());
            textSniffer.sniff(text);
            for(Factory_Strategy.StrategyEnum strategyEnum : strategies){
                if(strategyEnum.strategy.go(text, this)){
                    break;
                }
            }
        }

    }

    public void setDatatype(DATATYPE datatype){
        this.datatype = datatype;
    }
    public DATATYPE getDatatype(){
        return this.datatype;
    }
    
    public void setDefName(String defName){
        this.defName = defName;
    }
    public String getDefName(){
        return this.defName;
    }

    public void setSpecialScope(){
        if(SCOPE.equals(datatype)){
            specialScope = true;
        }
        else if (below != null){
            ((Base_ScanItem)below).setSpecialScope();
        }

    }
    public boolean isSpecialScope(){
        return specialScope ||
            (!SCOPE.equals(datatype) && below != null && ((Base_ScanItem)below).isSpecialScope());
    }

    public void setState(int state){
        this.state = state;
    }
    public int getState(){
        return this.state;
    }
    public void assertDoneState(){
        if(state != 0){
            System.out.println("A language structure is incomplete");
            //Erlog.get(this).set("A language structure is incomplete");
        }
    }

    public final void addNode(ScanNode node){
        ((Class_Scanner)P).addNode(node);
    }
    public final void addNodes(ArrayList<ScanNode> newNodes){
        ((Class_Scanner)P).addNodes(newNodes);
    }

    public final void setAllowedDatatypes(DATATYPE[] allowedDatatypes){
        this.allowedDatatypes = allowedDatatypes;
    }
    public final boolean isGoodDatatype(DATATYPE datatype){
        return(
            allowedDatatypes != null &&
            Arrays.asList(allowedDatatypes).contains(datatype)
        );
    }
}
    
