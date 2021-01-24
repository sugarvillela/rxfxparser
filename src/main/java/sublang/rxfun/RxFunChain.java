package sublang.rxfun;

import static langdef.Keywords.*;

import erlog.Erlog;
import runstate.Glob;
import toktools.TK;
import toktools.Tokens_special;

import java.util.Arrays;

public class RxFunChain {
    private static final Tokens_special dotTokenizer = new Tokens_special(".", "'", TK.IGNORESKIP );
    private RxFun[] funList;
    private RxFun head, tail;
    private int size;

    public RxFunChain(String origText) {
        System.out.println("RxFunChain: " + origText);
        this.initChain(origText);
        Glob.RX_FUN_UTIL.setChainIO(head);
    }
    public RxFunChain(RxFun singleFun) {
        head = tail = singleFun;
    }

    private void initChain(String origText){
        String[] tok = dotTokenizer.toArr(origText);
        size = tok.length;
        RxFun curr = head = new RxFun(tok[0]);
        for(int i = 1; i < size; i++){
            curr.setNext(new RxFun(tok[i]));
            curr = curr.getNext();
        }
        tail = curr;
    }

    public RxFun getTail(){
        return tail;
    }

    public RxFun[] toArray(){
        RxFun[] out = new RxFun[size];
        RxFun curr = head;
        for(int i = 0; i < size; i++){
            out[i] = curr;
            curr = curr.getNext();
        }
        return out;
    }

}
