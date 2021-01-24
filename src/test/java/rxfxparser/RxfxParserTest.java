package rxfxparser;

import runstate.RunState;
import sublang.RxTreeTest;

public class RxfxParserTest {
    public static void main(String[] args) {
        System.out.println("RxfxParserTest running...");

//        args = new String[]{"semantic1", "-s"};//, "-s"
//        RunState.getInstance().init(args);

        RxTreeTest.testSimpleFunPattern();
        //uq.UqTest.uqDiscreteGen();
        //listtable.ListTableTest.numGen();
        //RxPatternsJavaTest.test();
        //NameGenTest.globalNames();
        //RxCoreTest.rxWordTest();
    }
}
