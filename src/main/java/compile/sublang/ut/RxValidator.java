package compile.sublang.ut;

import compile.basics.Keywords;
import compile.basics.Keywords.RX_FUN;
import compile.sublang.factories.PayNodes;
import erlog.Erlog;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static compile.basics.Keywords.PRIM.NULL;

public class RxValidator {
    private static RxValidator instance;
    
    private RxValidator(){}
    
    public static RxValidator getInstance(){
        return (instance == null)? (instance = new RxValidator()) : instance;
    } 

    private final Pattern DUP_SYMBOLS = Pattern.compile(".*(&&|\\|\\||==|~~).*");
    private final Pattern BAD_BRACKET = Pattern.compile(".*(\\)\\(|\\}\\{).*");
    private final Pattern FUNCT_BRACES = Pattern.compile("\\([0-9]*\\)$");
    private String truncated, param;
    
    public boolean assertValidRxWord(String text){// Early test on whole word
        if(DUP_SYMBOLS.matcher(text).matches()){
            Erlog.get(this).set( "Single & | ~ required", text);
            return false;
        }
        if(BAD_BRACKET.matcher(text).matches()){
            Erlog.get(this).set( "Invalid parentheses or braces", text);
            return false;
        }
        return true;
    }

    public boolean assertValidRange(String range){
        String[] toks = range.split("-");
        boolean good = false;
        if(toks.length == 2){
            try{
                good = Integer.parseInt(toks[0]) < Integer.parseInt(toks[1]);
            }catch(NumberFormatException e){ }
        }
        if(!good){
            Erlog.get(this).set( "Invalid RX Function range", range);
        }
        return good;
    }
    public boolean assertValidParam(RX_FUN fun, Keywords.PAR givenParam){
        for(Keywords.PAR testParam: fun.parTypes){
            if(testParam.equals(givenParam)){
                return true;
            }
        }
        Erlog.get(this).set(
            String.format("%s not a valid parameter type in %s. Allowed types: %s",
                    givenParam.toString(), fun.toString(), fun.readableParTypes())
        );
        return false;
    }
    public boolean assertValidOperation(
            ArrayList<PayNodes.PayNode> leftNodes,
            Keywords.OP op,
            ArrayList<PayNodes.PayNode> rightNodes
    ){
        return assertValidTest(
                assertValidChain(leftNodes),
                op,
                assertValidChain(rightNodes)
        );
    }
    public Keywords.PRIM assertValidChain(ArrayList<PayNodes.PayNode> nodes){
        Keywords.PRIM last = NULL;
        for(PayNodes.PayNode payNode : nodes){
            PayNodes.RxPayNode rxPayNode = (PayNodes.RxPayNode) payNode;
            if(last != rxPayNode.callerType){
                Erlog.get(this).set(
                        String.format("Expected %s input to %s, found %s",
                                rxPayNode.callerType.toString(), rxPayNode.mainText, last.toString())
                );
            }
            last = rxPayNode.outType;
        }
        return last;
    }
    public boolean assertValidTest(Keywords.PRIM left, Keywords.OP op, Keywords.PRIM right){
        if(left != right){
            Erlog.get(this).set(
                    String.format("Comparing %s to %s",
                        left.toString(), right.toString())
            );
            return false;
        }
        if(!left.isAllowedOp(op)){
            Erlog.get(this).set(
                    String.format("%s %C %s not allowed",
                            left.toString(), op.asChar, right.toString())
            );
            return false;
        }
        return true;
    }
    
    // TODO
    public boolean assertFieldName(String text){
        // TODO get list of field names like trait.numeric 
        // from ENU definition or file
        return true;
    }
}
