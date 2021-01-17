package codegen.translators.rx;

import codegen.genjava.ClassJava;
import codegen.genjava.MethodJava;
import codegen.interfaces.IMethod;
import codegen.namegen.NameGenRxFx;
import codegen.translators.interfaces.RxFunPatternGen;
import codegen.ut.FormatUtil;
import commons.Util_string;
import compile.basics.Keywords;
import compile.sublang.factories.PayNodes;

import java.util.ArrayList;

import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;

public class RxFunPatternJava implements RxFunPatternGen {
    private static final String FUN_DECLARATION = "public static final RxFun %s = new %s();";
    private static final String FUN_PATTERN_DECLARATION = "public static final RxFunPattern %s = new %s();";
    private static final String FUN_PATTERN_EXTENDS = "RxFunPatternBase";

    private FormatUtil formatUtil;
    private NameGenRxFx nameGen;
    private ArrayList<String> nameList, declarationList;
    private String paramsLeft, paramsRight;

    @Override
    public RxFunPatternGen setFormatUtil(FormatUtil formatUtil){
        this.formatUtil = formatUtil;
        return this;
    }

    @Override
    public RxFunPatternGen setNameGen(NameGenRxFx nameGen){
        this.nameGen = nameGen;
        return this;
    }

    @Override
    public RxFunPatternGen setNameList(ArrayList<String> nameList){
        this.nameList = nameList;
        return this;
    }

    @Override
    public RxFunPatternGen setDeclarationsList(ArrayList<String> declarationList){
        this.declarationList = declarationList;
        return this;
    }

    @Override
    public void translate(PayNodes.RxPayNode[] left, Keywords.OP op, PayNodes.RxPayNode[] right){


        this.genFunClassNames(left);
        this.genFunClassNames(right);

        paramsLeft =  this.genParamsString(left);
        paramsRight = this.genParamsString(right);

        this.genFunClassBodies(left);
        this.genFunClassBodies(right);

        genFunPatternClassBody(op);
    }

    private void genFunClassNames(PayNodes.RxPayNode[] funList){
        for(PayNodes.RxPayNode node : funList){
            String upperCaseName = nameGen.getRx(NameGenRxFx.RX_FUN);   // Screaming snake case
            nameList.add(upperCaseName);                                // For static instantiations
            declarationList.add(
                String.format(FUN_DECLARATION, upperCaseName, Util_string.toPascalCase(upperCaseName))
            );
            node.setFunName(upperCaseName);                             // For main algo procedural calls
            nameGen.incRx(NameGenRxFx.RX_FUN);
        }
    }

    private String genParamsString(PayNodes.RxPayNode[] funList){// For FunPattern gen constructor param list
        String[] paramsList = new String[funList.length];
        int i = 0;
        for(PayNodes.RxPayNode node : funList){
            paramsList[i++] = node.getFunName();
        }
        return String.join(", ", paramsList);
    }

    private void genFunClassBodies(PayNodes.RxPayNode[] funList){
        for(PayNodes.RxPayNode node : funList){
            node.funType.translator.classBody(
                    Util_string.toPascalCase(node.getFunName()), node).
                    finish(formatUtil);
        }
    }

    private void genFunPatternClassBody(Keywords.OP op){
        String upperCaseName = nameGen.getRx(NameGenRxFx.RX_FUN_PATTERN);
        String className = Util_string.toPascalCase(upperCaseName);
        nameList.add(upperCaseName);
        declarationList.add(
                String.format(FUN_PATTERN_DECLARATION, upperCaseName, className)
        );



        new ClassJava.ClassJavaBuilder().
                setVisibility(PUBLIC_).setStatic().setInner().
                setName(className).setExtends(FUN_PATTERN_EXTENDS).build().
                add(genFunPatternConstruct(className, op)).finish(formatUtil);
    }

    private IMethod genFunPatternConstruct(String pascalName, Keywords.OP op){
        return new MethodJava.MethodBuilder().
                setIsConstructor().setName(pascalName).build().
                add(paramsLeft, op.toString(), paramsRight);
    }
}
