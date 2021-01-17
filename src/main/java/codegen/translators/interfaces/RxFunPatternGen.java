package codegen.translators.interfaces;

import codegen.namegen.NameGenRxFx;
import codegen.ut.FormatUtil;
import compile.basics.Keywords;
import compile.sublang.factories.PayNodes;

import java.util.ArrayList;

public interface RxFunPatternGen {
    RxFunPatternGen setFormatUtil(FormatUtil formatUtil);
    RxFunPatternGen setNameGen(NameGenRxFx nameGen);
    RxFunPatternGen setNameList(ArrayList<String> nameList);
    RxFunPatternGen setDeclarationsList(ArrayList<String> declarationList);

    void translate(PayNodes.RxPayNode[] left, Keywords.OP op, PayNodes.RxPayNode[] right);
}
