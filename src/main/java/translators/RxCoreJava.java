package translators;

import namegen.NameGenRx;
import langdef.Keywords;
import runstate.Glob;
import sublang.factories.PayNodes.*;

public class RxCoreJava {
    private final NameGenRx nameGen;
    private String rxPatternName, rxWordName;

    public RxCoreJava() {
        nameGen = Glob.NAME_GEN_RX;
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
