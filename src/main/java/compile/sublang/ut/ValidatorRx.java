package compile.sublang.ut;

import compile.basics.Keywords;
import compile.sublang.factories.PayNodes;
import compile.sublang.factories.TreeFactory;
import erlog.Erlog;
import interfaces.DataNode;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static compile.basics.Keywords.PRIM.NULL;

public class ValidatorRx {
    private static ValidatorRx instance;
    
    private ValidatorRx(){
        DUP_SYMBOLS = Pattern.compile(".*(&&|\\|\\||==|~~).*");
        BAD_BRACKET = Pattern.compile(".*(\\)\\(|\\}\\{).*");
    }
    
    public static ValidatorRx getInstance(){
        return (instance == null)? (instance = new ValidatorRx()) : instance;
    }

    private final Pattern DUP_SYMBOLS;
    private final Pattern BAD_BRACKET;

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

    public void assertValidOperation(
            ArrayList<DataNode> leftNodes,
            Keywords.OP op,
            ArrayList<DataNode> rightNodes,
            TreeFactory.TreeNode parent
    ){
        assertValidTest(
                assertValidChain(leftNodes, parent),
                op,
                assertValidChain(rightNodes, parent),
                parent
        );
    }
    private Keywords.PRIM assertValidChain(ArrayList<DataNode> nodes, TreeFactory.TreeNode parent){
        Keywords.PRIM last = NULL;
        for(DataNode payNode : nodes){
            PayNodes.RxPayNode rxPayNode = (PayNodes.RxPayNode) payNode;
            if(last != rxPayNode.callerType){
                String identifier = (rxPayNode.funType == null)? rxPayNode.item : rxPayNode.funType.toString();
                Erlog.get(this).set(
                        String.format("Expected %s input to be %s, found %s",
                                rxPayNode.callerType.toString(), identifier, last.toString()
                        ),
                        parent.leafOpToString()
                );
            }
            last = rxPayNode.outType;
        }
        return last;
    }
    private void assertValidTest(Keywords.PRIM left, Keywords.OP op, Keywords.PRIM right, TreeFactory.TreeNode parent){
        if(left != right){
            Erlog.get(this).set(
                    String.format("Comparing %s to %s", left.toString(), right.toString()), parent.leafOpToString()
            );
        }
        if(!left.isAllowedOp(op)){
            Erlog.get(this).set(
                    String.format("%s %C %s not allowed", left.toString(), op.asChar, right.toString()), parent.leafOpToString()
            );
        }
    }
}
