package sublang.ut;

import commons.Commons;
import sublang.factories.PayNodes;
import erlog.Erlog;
import sublang.treenode.TreeNodeBase;

import static langdef.Keywords.ACCESS_MOD;

public class ValidatorFx {
    private static ValidatorFx instance;

    private ValidatorFx(){}

    public static ValidatorFx init(){
        return (instance == null)? (instance = new ValidatorFx()) : instance;
    }

    public void assertValidAccessModifier(PayNodes.FxPayNode left, PayNodes.FxPayNode right, TreeNodeBase treeNode){
        //System.out.println(left.readableContent());
        if(!right.funType.isAllowedAccessMod(left.accessMod)){
            Erlog.get(this).set(
                String.format(
                    "Function %s only operates on RX string. '%s' modifier not allowed here",
                    right.funType.toString(), ACCESS_MOD
                ),
                treeNode.data
            );
        }
    }
    public void assertValidFunParam(PayNodes.FxPayNode payNode, TreeNodeBase treeNode){
        //System.out.println(payNode.readableContent());
        if(!payNode.funType.isAllowedParam(payNode.paramType)){
            Erlog.get(this).set(
                String.format(
                    "Expected %s parameter to be %s, found %s",
                    payNode.funType.toString(),
                    Commons.join(", ", payNode.funType.parTypes),
                    payNode.paramType.datatype.toString()
                ),
                treeNode.data
            );
        }
    }
}
