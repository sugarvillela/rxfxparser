package sublang.rxfun;

import runstate.Glob;

import static langdef.Keywords.*;
import static langdef.Keywords.RX_FUN.*;

public class RxFunUtil {
    private static RxFunUtil instance;

    public static RxFunUtil init(){
        return (instance == null)? (instance = new RxFunUtil()) : instance;
    }
    private RxFunUtil() {}

    public RX_FUN[] getAvailableFunctions(RxFun rxFun){
        DATATYPE listSource = rxFun.getListSource();
        RX_PAR paramType = rxFun.getParamType();

        switch(listSource){// LIST_STRING, LIST_DISCRETE etc
            case LIST_BOOLEAN:
                switch(paramType){
                    case CATEGORY:
                        return  new RX_FUN[]{STORE_ANY_SET, STORE_NUM_SET};
                    case CATEGORY_ITEM:
                        return new RX_FUN[]{STORE_GET_BOOLEAN, STORE_GET_STATE};
                }
                break;
            case LIST_DISCRETE:
                switch(paramType){
                    case CATEGORY:
                        return new RX_FUN[]{STORE_GET_STATE, STORE_ANY_SET};
                    case CATEGORY_ITEM:
                        return new RX_FUN[]{STORE_GET_BOOLEAN};
                }
                break;
            case LIST_VOTE:
                switch(paramType){
                    case CATEGORY:
                        return new RX_FUN[]{STORE_ANY_SET, STORE_NUM_SET};
                    case CATEGORY_ITEM:
                        return new RX_FUN[]{STORE_GET_NUMBER, STORE_GET_BOOLEAN};
                }
                break;
            case LIST_STRING:
                switch(paramType){
                    case CATEGORY:
                        return new RX_FUN[]{STORE_ANY_SET, STORE_NUM_SET};
                    case CATEGORY_ITEM:
                        return new RX_FUN[]{STORE_GET_STRING, STORE_GET_BOOLEAN};
                }
                break;
            case LIST_NUMBER:
                switch(paramType){
                    case CATEGORY:
                        return new RX_FUN[]{STORE_ANY_SET, STORE_NUM_SET};
                    case CATEGORY_ITEM:
                        return new RX_FUN[]{STORE_GET_NUMBER, STORE_GET_BOOLEAN};
                }
                break;
        }
        return null;
    }

    public boolean setChainIO(RxFun rxFun){
        // caller types are singular; match all possible outs with singular caller
        RxFun next = rxFun.getNext();
        boolean good = false;
        if(next == null){
            return true;
        }
        else if(rxFun.haveFunType()){
            good = matchZeroUnknowns(rxFun, next);
        }
        else{
            good = matchOneUnknown(rxFun, next);
        }
        return good && setChainIO(next);
    }
    private boolean matchZeroUnknowns(RxFun thisFun, RxFun nextFun){
        return (thisFun.getFunType().outType == nextFun.getFunType().caller);
    }
    private boolean matchOneUnknown(RxFun thisFun, RxFun nextFun){
        RX_FUN[] availableFunctions = Glob.RX_FUN_UTIL.getAvailableFunctions(thisFun);
        for (RX_FUN availableFun: availableFunctions) {
            if(nextFun.getFunType().caller == availableFun.outType){
                thisFun.setFunType(availableFun);
                return true;
            }
        }
        return false;
    }
}
