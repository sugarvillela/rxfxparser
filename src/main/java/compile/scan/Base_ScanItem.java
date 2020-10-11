package compile.scan;

import compile.basics.Base_StackItem;
import compile.scan.factories.Factory_Strategy;
import compile.symboltable.TextSniffer;

import java.util.Arrays;
import compile.basics.Keywords.DATATYPE;
import compile.basics.Factory_Node.ScanNode;

import java.util.ArrayList;

import static compile.basics.Keywords.DATATYPE.SCOPE;

/**Base class provides common tasks
 * Derived classes are handlers for context-sensitive control of stack
 *
 * @author Dave Swanson
 */

public abstract class Base_ScanItem extends Base_StackItem{
    protected DATATYPE h;                    // class's own enum
    protected DATATYPE[] allowedDatatypes;// children datatypes to instantiate
    protected Factory_Strategy.StrategyEnum[] strategies;
    protected Factory_Strategy.PushEnum[] onPushStrategies;
    protected Factory_Strategy.PopEnum[] onPopStrategies;
    protected String defName;
    protected int state;//, loIndex, hiIndex;
    protected final TextSniffer textSniffer;
    protected boolean specialScope;
    
    public Base_ScanItem(){
        textSniffer = TextSniffer.getInstance();
        state = 0;
        specialScope = false;
    }

    @Override
    public void onPush(){
        System.out.println("Base_ScanItem onPush: P=" + P.getDebugName() + ", this=" + this.getDebugName());
        //loIndex = Class_Scanner.getInstance().getScanNodeList().size();
//        String datatypeStr = (h == null)? "Null Datatype" : h.toString();
//        System.out.println("Base_ScanItem onPush: " + datatypeStr + ", loIndex: " + loIndex);
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
        if(onPopStrategies != null){
            for(Factory_Strategy.PopEnum strategyEnum : onPopStrategies){
                if(strategyEnum.strategy.go(null, this)){
                    break;
                }
            }
        }
        //hiIndex = Class_Scanner.getInstance().getScanNodeList().size();
        String datatypeStr = (h == null)? "Null Datatype" : h.toString();
        System.out.println("====onPop()====" + datatypeStr);
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
    public void setDatatype(DATATYPE h){
        this.h = h;
    }
    public DATATYPE getDatatype(){
        return this.h;
    }
    
    public void setDefName(String defName){
        this.defName = defName;
    }
    public String getDefName(){
        return this.defName;
    }

    public void setSpecialScope(){
        if(SCOPE.equals(h)){
            specialScope = true;
        }
        else if (below != null){
            ((Base_ScanItem)below).setSpecialScope();
        }

    }
    public boolean isSpecialScope(){
        return specialScope ||
            (!SCOPE.equals(h) && below != null && ((Base_ScanItem)below).isSpecialScope());
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

//    public final ArrayList<ScanNode> getScanNodeList(){
//        hiIndex = Class_Scanner.getInstance().getScanNodeList().size();
//        return new ArrayList<>(((Class_Scanner)P).getScanNodeList().subList(loIndex, hiIndex));
//    }


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
    
