package sublang.rxfun;

import erlog.Erlog;
import langdef.Keywords;
import listtable.ListTableNode;
import runstate.Glob;

import static langdef.Keywords.*;

import java.util.HashSet;
import java.util.Set;

import static langdef.Keywords.OP.*;

public class RxFunPattern {
    private static final OP[] SPLIT_CHARS = new OP[]{COMPARE_EQUAL, COMPARE_LT, COMPARE_GT};
    private final String origText;
    private final BalancedStatement[] balancedStatements;
    private final LeftRightOutTypeUtil leftRightOutTypeUtil;
    private String textLeft, textRight;
    private RxFunChain chainLeft, chainRight;
    private OP op;

    public RxFunPattern(String origText) {
        this.origText = origText;
        balancedStatements = new BalancedStatement[]{
            new HangingValueUtil(origText), new HangingCategoryUtil(origText)
        };
        leftRightOutTypeUtil = new LeftRightOutTypeUtil(origText);
        System.out.println("RxFunPattern: "+origText);
        this.initPattern();

    }

    private void initPattern(){
        splitOnOp(origText);
        if(op == null){
            System.out.println("no op: "+origText);
            textLeft = origText;
            chainLeft = new RxFunChain(textLeft);
            for(BalancedStatement balancedStatement : balancedStatements){
                if(balancedStatement.balance(chainLeft.getTail())){
                    this.op = balancedStatement.getGeneratedOp();
                    this.chainRight = new RxFunChain(balancedStatement.getGeneratedFun());
                    break;
                }
            }
        }
        else{
            System.out.println("op: " + op);
            chainLeft = new RxFunChain(textLeft);
            chainRight = new RxFunChain(textRight);
            leftRightOutTypeUtil.matchOutputs(chainLeft.getTail(), chainRight.getTail(), op);
        }
    }

    public OP getOp(){
        return op;
    }

    public RxFun[] getLeft(){
        return chainLeft.toArray();
    }
    public RxFun[] getRight(){
        return chainRight.toArray();
    }

    private void splitOnOp(String text){
        boolean ignore = false;
        for (OP splitChar : SPLIT_CHARS) {
            for (int i = 0; i < text.length() - 1; i++) {
                if (text.charAt(i) == SQUOTE.asChar) {
                    ignore = !ignore;
                } else if (!ignore && text.charAt(i) == splitChar.asChar) {
                    textLeft = text.substring(0, i);
                    textRight = text.substring(i + 1);
                    op = splitChar;
                    return;
                }
            }
        }
    }

    private static abstract class BalancedStatement{
        protected final String origText;
        protected RxFun generatedFun;
        protected OP generatedOp;

        protected BalancedStatement(String origText) {
            this.origText = origText;
        }

        public abstract boolean balance(RxFun leftFun);

        public OP getGeneratedOp(){
            return generatedOp;
        }

        public RxFun getGeneratedFun(){
            return generatedFun;
        }
        protected String defaultFieldForRight(Keywords.DATATYPE listSource){
            String category = Glob.LIST_TABLE.getFirstCategory(listSource);
            if(category != null){
                ListTableNode node = Glob.LIST_TABLE.getItemSearch().getListTableNode(listSource, category);
                if(node != null){
                    return String.format(DEFAULT_FIELD_FORMAT, category, node.getFirstField());
                }
            }
            return null;
        }

        protected void setErr(){
            Erlog.get(this).set("Unable to generate equality", origText);
        }
    }

    private static class HangingValueUtil extends BalancedStatement{
        protected HangingValueUtil(String origText) {
            super(origText);
        }

        @Override
        public boolean balance(RxFun leftFun){
            RX_FUN funType = leftFun.getFunType();
            String defaultField;
            switch(funType){
                case VAL_CONTAINER_OBJECT:
                case VAL_CONTAINER_INT:
                    defaultField = this.defaultFieldForRight(leftFun.getListSource());
                    if(defaultField == null){
                        this.setErr();
                        return false;
                    }
                    else{
                    generatedOp = COMPARE_EQUAL;
                    generatedFun = new RxFun(defaultField);
                    return true;
                }
                default:
                    return false;

            }
        }

    }
    private static class HangingCategoryUtil extends BalancedStatement{
        protected HangingCategoryUtil(String origText) {
            super(origText);
        }

        @Override
        public boolean balance(RxFun leftFun){
            RX_FUN[] availableFunctions = leftFun.getOutTypeUtil().getAvailableFunctions(leftFun);
            for(RX_FUN  availableFun : availableFunctions){
                if(generateRight(leftFun, availableFun)){
                    leftFun.setFunType(availableFun);
                    return true;
                }
            }
            this.setErr();
            return false;
        }

        //DATATYPE listSource, paramType: CATEGORY, CATEGORY_ITEM -> ==<> generatedRight
        private boolean generateRight(RxFun leftFun, RX_FUN  availableFun){
            switch(availableFun.outType){
                case BOOLEAN:
                    generatedOp = COMPARE_EQUAL;
                    generatedFun = new RxFun("TRUE");
                    return true;
                case NUMBER:
                    generatedOp = COMPARE_GT;
                    generatedFun = new RxFun("0");
                    return true;
                default:
                    String defaultField = this.defaultFieldForRight(leftFun.getListSource());
                    if(defaultField != null){
                        generatedOp = COMPARE_EQUAL;
                        generatedFun = new RxFun(defaultField);
                        return true;
                    }
            }
            return false;
        }
    }

    private static class LeftRightOutTypeUtil{
        private final String origText;

        private LeftRightOutTypeUtil(String origText) {
            this.origText = origText;
        }

        public boolean matchOutputs(RxFun leftFun, RxFun rightFun, OP op){
            if(leftFun.haveFunType()){
                if(rightFun.haveFunType()){//both singular
                    return matchZeroUnknowns(leftFun, rightFun, op);
                }
                else{
                    return matchOneUnknown(leftFun, rightFun, op);
                }
            }
            else{
                if(rightFun.haveFunType()){
                    return matchOneUnknown(rightFun, leftFun, op);
                }
                else{
                    return matchTwoUnknowns(leftFun, rightFun, op);
                }
            }
        }

        private boolean matchZeroUnknowns(RxFun left, RxFun right, OP op){
            if(left.getFunType().outType == right.getFunType().outType){
                for(OP allowedOp : left.getFunType().outType.allowedOps){
                    if(allowedOp == op){
                        return true;
                    }
                }
            }
            setErr(
                    new RX_FUN[]{left.getFunType()},
                    new RX_FUN[]{right.getFunType()},
                    op
            );
            return false;
        }

        private boolean matchOneUnknown(RxFun known, RxFun unknown, OP op){
            PRIM knownType = known.getFunType().outType;
            RX_FUN[] availableFunctions = Glob.RX_FUN_UTIL.getAvailableFunctions(unknown);
            for(RX_FUN availableFun : availableFunctions){
                if(availableFun.outType == knownType){
                    for(OP allowedOp : knownType.allowedOps){
                        if(allowedOp == op){
                            unknown.setFunType(availableFun);
                            return true;
                        }
                    }
                }
            }
            setErr(
                    new RX_FUN[]{known.getFunType()},
                    availableFunctions,
                    op
            );
            return false;
        }

        private boolean matchTwoUnknowns(RxFun left, RxFun right, OP op){// left is unknown, right is unknown
            RX_FUN[] availableFunctionsLeft = Glob.RX_FUN_UTIL.getAvailableFunctions(left);
            RX_FUN[] availableFunctionsRight = Glob.RX_FUN_UTIL.getAvailableFunctions(right);

            for(RX_FUN availableFunA : availableFunctionsLeft){
                for(RX_FUN availableFunB : availableFunctionsRight){
                    if(availableFunA.outType == availableFunB.outType){
                        for(OP allowedOp : availableFunA.outType.allowedOps){
                            if(allowedOp == op){
                                left.setFunType(availableFunA);
                                right.setFunType(availableFunB);
                                return true;
                            }
                        }
                    }
                }
            }
            setErr(availableFunctionsLeft, availableFunctionsRight, op);
            return false;
        }

        private void setErr(RX_FUN[] funTypesLeft, RX_FUN[] funTypesRight, OP op){
            Set<PRIM> outTypesLeft = outTypesByFunTypes(funTypesLeft);
            Set<PRIM> outTypesRight = outTypesByFunTypes(funTypesRight);

            Erlog.get(this).set(
                "OutType mismatch",
                String.format(
                    "%s: \nleft side output types %s, allowed comparators %s" +
                        "\nright side output types %s, allowed comparators %s" +
                        "\nComparators provided %s",
                    this.origText,
                    outTypesLeft.toString(), opsByOutTypes(outTypesLeft).toString(),
                    outTypesRight.toString(), opsByOutTypes(outTypesRight).toString(),
                    op.toString()
                )
            );
        }

        private Set<PRIM> outTypesByFunTypes(RX_FUN[] funTypes){
            Set<PRIM> outTypes = new HashSet<>();
            for(RX_FUN funType : funTypes){
                outTypes.add(funType.outType);
            }
            return outTypes;
        }

        private Set<OP> opsByOutTypes(Set<PRIM> types){
            Set<OP> ops = new HashSet<>();
            for(PRIM type : types){
                for(OP op : type.allowedOps){
                    ops.add(op);
                }
            }
            return ops;
        }
    }
}
