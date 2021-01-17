package codegen.translators.interfaces;

import codegen.namegen.NameGenRxFx;
import codegen.ut.FormatUtil;
import compile.sublang.factories.TreeFactory;

import java.util.ArrayList;

public interface RxWordGen {
    RxWordGen setFormatUtil(FormatUtil formatUtil);
    RxWordGen setNameGen(NameGenRxFx nameGen);
    RxWordGen setNameList(ArrayList<String> nameList);
    RxWordGen setDeclarationsList(ArrayList<String> declarationList);

    void translate(TreeFactory.RxTreeNode root);
}
