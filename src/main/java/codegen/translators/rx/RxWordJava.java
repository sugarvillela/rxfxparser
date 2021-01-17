package codegen.translators.rx;

import codegen.genjava.ClassJava;
import codegen.genjava.CommentJava;
import codegen.genjava.MethodJava;
import codegen.interfaces.IMethod;
import codegen.namegen.NameGenRxFx;
import codegen.translators.interfaces.RxWordGen;
import codegen.ut.FormatUtil;
import commons.Util_string;
import compile.basics.Keywords;
import compile.sublang.factories.TreeFactory;

import java.util.ArrayList;

import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;

public class RxWordJava implements RxWordGen {
    private static final String WORD_DECLARATION = "public static final RxWord %s = new %s();";
    private static final String WORD_EXTENDS = "RxWordImpl";
    private static final String PARAM_FORMAT = "super(%d, %d);";

    private FormatUtil formatUtil;
    private NameGenRxFx nameGen;
    private ArrayList<String> nameList, declarationList;

    @Override
    public RxWordGen setFormatUtil(FormatUtil formatUtil) {
        this.formatUtil = formatUtil;
        return this;
    }

    @Override
    public RxWordGen setNameGen(NameGenRxFx nameGen) {
        this.nameGen = nameGen;
        return this;
    }

    @Override
    public RxWordGen setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
        return this;
    }

    @Override
    public RxWordGen setDeclarationsList(ArrayList<String> declarationList) {
        this.declarationList = declarationList;
        return this;
    }

    @Override
    public void translate(TreeFactory.RxTreeNode root) {
        this.genWordClassBody(1, 5);
    }
    /*
        public RxWord000_00() {
            super(1, 3);
        }

        @Override
        public boolean go() {
            return f.run(RX_FUN_PATTERN_000_00_00);
        }
    * */

    private void genWordClassBody(int lo, int hi){
        String upperCaseName = nameGen.getRx(NameGenRxFx.RX_WORD);
        String className = Util_string.toPascalCase(upperCaseName);
        nameList.add(upperCaseName);
        declarationList.add(
                String.format(WORD_DECLARATION, upperCaseName, className)
        );

        new ClassJava.ClassJavaBuilder().
                setVisibility(PUBLIC_).setStatic().setInner().
                setName(className).setExtends(WORD_EXTENDS).build().
                add(genWordConstruct(className, lo, hi), genGo()).finish(formatUtil);
    }

    private IMethod genWordConstruct(String className, int lo, int hi){
        return new MethodJava.MethodBuilder().
                setIsConstructor().setName(className).build().
                add(String.format(PARAM_FORMAT, lo, hi));
    }

    private IMethod genGo(){
        return new MethodJava.MethodBuilder().setReturnType("boolean").setName("go").build().
                add(
                        CommentJava.quickComment("tree unpack here")
                );
    }
}
