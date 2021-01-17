package codegen.translators;

import codegen.namegen.NameGenRxFx;
import compile.basics.Keywords;
import compile.sublang.factories.PayNodes.*;

public class RxCoreJava {
    private final NameGenRxFx nameGen;
    private String rxPatternName, rxWordName;

    public RxCoreJava() {
        nameGen = new NameGenRxFx();
    }
    public void rxPattern(){
        //rxPatternName = nameGen.rxPattern();
    }
    public void rxWord(){
        //rxWordName = nameGen.rxWord();
    }
    public String rxFun(RxPayNode left, Keywords.OP op,RxPayNode right){
        return null;
    }
}
