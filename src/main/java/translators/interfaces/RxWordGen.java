package translators.interfaces;

import namegen.NameGenRx;
import sublang.treenode.RxTreeNode;
import translators.ut.FormatUtil;

import java.util.ArrayList;

public interface RxWordGen {
    RxWordGen setFormatUtil(FormatUtil formatUtil);
    RxWordGen setNameGen(NameGenRx nameGen);
    RxWordGen setNameList(ArrayList<String> nameList);
    RxWordGen setDeclarationsList(ArrayList<String> declarationList);

    void translate(RxTreeNode root);
}
