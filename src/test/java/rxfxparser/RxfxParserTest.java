package rxfxparser;

import runstate.RunState;

public class RxfxParserTest {
    public static void main(String[] args) {
        System.out.println("RxfxParserTest running...");

        //StaticState.init();
        args = new String[]{"semantic1", "-s"};//, "-s"
        RunState.getInstance().init(args);
        //uq.UqTest.uqDiscreteGen();
        //listtable.ListTableTest.numGen();
        //RxPatternsJavaTest.test();
        //NameGenTest.globalNames();
        //RxCoreTest.rxWordTest();
    }
}
