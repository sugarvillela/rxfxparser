package translators.interfaces;

import namegen.NameGenRx;
import translators.ut.FormatUtil;
import langdef.Keywords;
import sublang.factories.PayNodes;

import java.util.ArrayList;

public interface RxFunPatternGen {
    RxFunPatternGen setFormatUtil(FormatUtil formatUtil);
    RxFunPatternGen setNameGen(NameGenRx nameGen);
    RxFunPatternGen setNameList(ArrayList<String> nameList);
    RxFunPatternGen setDeclarationsList(ArrayList<String> declarationList);

    void translate(PayNodes.RxPayNode[] left, Keywords.OP op, PayNodes.RxPayNode[] right);
}
