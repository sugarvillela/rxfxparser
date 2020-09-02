
package demos;

import compile.basics.Factory_Node.RxScanNode;
import compile.basics.Keywords.OP;
import static compile.basics.Keywords.OP.AND;
import static compile.basics.Keywords.OP.OR;
import static compile.basics.Keywords.OP.EQUAL;
import static compile.basics.Keywords.OP.GT;
import static compile.basics.Keywords.OP.LT;
import static compile.basics.Keywords.OP.PAYLOAD;
import erlog.Erlog;
import flagobj.FlagObject;
import java.util.ArrayList;

public abstract class Factory_RxLogicNode {
    public static LogicNode getLogicNode(RxScanNode scanNode){
        switch (scanNode.op){
            case AND:
                return new RxAnd(scanNode);
            case OR:
                return new RxOr(scanNode);
            case EQUAL:
                return new RxEqual(scanNode);
            case GT:
                return new RxGT(scanNode);
            case LT:
                return new RxLT(scanNode);
            case PAYLOAD:
                return new RxPayload(scanNode);
        }
        return null;
    }
    public static abstract class LogicNode{
        public LogicNode parent;
        //public Keywords.KWORD connector;
        public String data;
        public OP op;
        public int level, id;
        
        public LogicNode(RxScanNode scanNode){
            this.data = scanNode.data;
            this.id = scanNode.id;
            this.level = 0;
        }
        public abstract void addChild(LogicNode node);
        public abstract boolean go(FlagObject flagObject);
        public void query(FlagObject flagObject, LogicNode caller, LogicNode callee){
            Erlog.get(this).set("No query capability");
        }
        public void respond(String a, String b){
            Erlog.get(this).set("No respond capability");
        }
        public void respond(Integer a, Integer b){
            Erlog.get(this).set("No respond capability");
        }
        public void respond(Boolean a, Boolean b){
            Erlog.get(this).set("No respond capability");
        }
        //public abstract Object get(FlagObject flagObject);
    }
    public static abstract class Connector extends LogicNode{
        public ArrayList<LogicNode> nodes;
        public boolean not;
        
        public Connector(RxScanNode scanNode){
            super(scanNode);
            this.not = scanNode.not;
        }
        @Override
        public void addChild(LogicNode node){
            if(nodes == null){
                nodes = new ArrayList<>();
            }
            nodes.add(node);
        }
    }
    public static class RxAnd extends Connector{
        public RxAnd(RxScanNode scanNode){
            super(scanNode);
            this.op = AND;
        }
        @Override
        public boolean go(FlagObject flagObject){
            for(LogicNode node : nodes){
                if(!node.go(flagObject)){
                    return !not;
                }
            }
            return not;
        }
        
    }
    public static class RxOr extends Connector{
        public RxOr(RxScanNode scanNode){
            super(scanNode);
            this.op = OR;
        }
        @Override
        public boolean go(FlagObject flagObject){
            for(LogicNode node : nodes){
                if(node.go(flagObject)){
                    return not;
                }
            }
            return !not;
        }
    }
    public static abstract class Comparator extends Connector{
//        public static final int INV = 0;
//        public static final int STR = 1;
//        public static final int INT = 2;
//        public static final int BOOL = 3;
        public boolean haveReply;
        public Object valA, valB;
        
        public Comparator(RxScanNode scanNode){
            super(scanNode);
        }
        @Override
        public void respond(String a, String b){
            valA = a; valB = b;
            haveReply = true;
        }
        @Override
        public void respond(Integer a, Integer b){
            valA = a; valB = b;
            haveReply = true;
        }
        @Override
        public void respond(Boolean a, Boolean b){
            valA = a; valB = b;
            haveReply = true;
        }
    }
    public static class RxEqual extends Comparator{
        public RxEqual(RxScanNode scanNode){
            super(scanNode);
            this.op = EQUAL;
        }
        @Override
        public boolean go(FlagObject flagObject){
            haveReply = false;
            nodes.get(0).query(flagObject, this, nodes.get(1));
            if(!haveReply){
                Erlog.get(this).set("Reply to query: invalid or none");
            }
            return not? !valA.equals(valB) : valA.equals(valB);
        }
    }
    public static abstract class IntegerOnly extends Comparator{
        public IntegerOnly(RxScanNode scanNode){
            super(scanNode);
        }
        @Override
        public void respond(String a, String b){
            Erlog.get(this).set("Expected Integer reply; found String");
        }
        @Override
        public void respond(Boolean a, Boolean b){
            Erlog.get(this).set("Expected Integer reply; found Boolean");
        }
    }
    public static class RxGT extends IntegerOnly{
        public RxGT(RxScanNode scanNode){
            super(scanNode);
            this.op = GT;
        }
        @Override
        public boolean go(FlagObject flagObject){
            haveReply = false;
            nodes.get(0).query(flagObject, this, nodes.get(1));
            if(haveReply){
                int a = (Integer)valA;
                int b = (Integer)valB;
                return not? b >= a : a > b;
            }
            return false;
        }
    }
    public static class RxLT extends IntegerOnly{
        public RxLT(RxScanNode scanNode){
            super(scanNode);
            this.op = LT;
        }
        @Override
        public boolean go(FlagObject flagObject){
            haveReply = false;
            nodes.get(0).query(flagObject, this, nodes.get(1));
            if(haveReply){
                int a = (Integer)valA;
                int b = (Integer)valB;
                return not? b <= a : a < b;
            }
            return false;
        }
    }
    public static class RxPayload extends Comparator{
        public RxPayload(RxScanNode scanNode){
            super(scanNode);
            this.op = PAYLOAD;
        }
        @Override
        public boolean go(FlagObject flagObject) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
}
