package codegen.translators;

import codegen.translators.rx.RxPatternsJava;

public class RxPatternsJavaTest {
    public static void test(){
        RxPatternsJava translator = new RxPatternsJava();
        translator.translate();
    }
}
