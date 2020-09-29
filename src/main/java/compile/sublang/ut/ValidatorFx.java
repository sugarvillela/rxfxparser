package compile.sublang.ut;

import commons.Commons;
import compile.basics.Keywords;
import compile.sublang.factories.PayNodes;
import compile.sublang.factories.TreeFactory;
import erlog.Erlog;
import interfaces.DataNode;

import java.util.ArrayList;

import static compile.basics.Keywords.ACCESS_MOD;

public class ValidatorFx {
    private static ValidatorFx instance;

    private ValidatorFx(){}

    public static ValidatorFx getInstance(){
        return (instance == null)? (instance = new ValidatorFx()) : instance;
    }

    public void assertValidAccessModifier(PayNodes.FxPayNode left, PayNodes.FxPayNode right, TreeFactory.TreeNode treeNode){
        System.out.println(left.readableContent());
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
    public void assertValidFunParam(PayNodes.FxPayNode payNode, TreeFactory.TreeNode treeNode){
        System.out.println(payNode.readableContent());
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
