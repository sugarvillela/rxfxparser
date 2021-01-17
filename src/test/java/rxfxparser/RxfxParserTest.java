package rxfxparser;

import codegen.translators.RxCoreTest;
import runstate.StaticState;

public class RxfxParserTest {
    public static void main(String[] args) {
        System.out.println("RxfxParserTest running...");

        StaticState.init();

        //uq.UqTest.uqDiscreteGen();
        //listtable.ListTableTest.numGen();
        //RxPatternsJavaTest.test();
        //NameGenTest.globalNames();
        RxCoreTest.rxWordTest();
    }
}
