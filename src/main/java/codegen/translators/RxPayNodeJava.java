package codegen.translators;

import flattree.FlatNode;

import static compile.sublang.factories.PayNodes.*;

public class RxPayNodeJava {
    private final RxPayNode[] payNodes;
    private FunUtil funUtil;

    public RxPayNodeJava(FlatNode flatNode) {
        this.payNodes = (RxPayNode[])flatNode.getPayNodes();
    }
/*
        public final Keywords.PRIM callerType;
        public final Keywords.RX_PAR paramType;
        public final Keywords.PRIM outType;
        public final Keywords.RX_FUN funType;
        public final String item;
        public final String uDefCategory;
        public final Keywords.DATATYPE listSource;
        public final int[] values;

        TEST_FALSE      (DATATYPE.BOOL_TEXT,Pattern.compile("^FALSE$")),
        TEST_NUM        (DATATYPE.NUM_TEXT, Pattern.compile("^[0-9]+$")),
        CATEGORY_ITEM   (DATATYPE.LIST,     Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*\\[([a-zA-Z][a-zA-Z0-9_]*)\\]$")),
        TEST_TEXT       (DATATYPE.RAW_TEXT, Pattern.compile("."))
* */
    public void translate(){
        for(RxPayNode payNode : payNodes){
            switch(payNode.paramType.datatype){
                case FUN:
                    if(funUtil == null){
                        funUtil = new FunUtil();
                    }
                    funUtil.push(payNode);
                    break;
                case BOOL_TEXT:
                case NUM_TEXT:
                case RAW_TEXT:
            }
        }
    }
    public static class FunUtil{
        private FunNode top;
        public void push(RxPayNode payNode){
            if(top == null){
                top = new FunNode(payNode);
            }
            else{
                top.push(new FunNode(payNode));
            }
        }
    }
    public static class FunNode{
        public final RxPayNode payNode;
        public FunNode above, below;

        public FunNode(RxPayNode payNode) {
            this.payNode = payNode;
        }

        public void push(FunNode newTop){
            newTop.below = this;
            this.above = newTop;
        }
    }


    @Override
    public String toString() {
        return null;
    }
}
