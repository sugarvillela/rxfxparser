package codegen.translators;

import codegen.namegen.NameGenRxFx;
import codegen.translators.rx.RxFunPatternJava;
import codegen.translators.rx.RxWordJava;
import codegen.ut.FormatUtil;
import commons.Commons;
import compile.basics.Keywords;
import compile.sublang.factories.PayNodes.*;
import compile.sublang.factories.TreeFactory;

import java.util.ArrayList;

import static compile.basics.Keywords.DATATYPE.LIST_STRING;
import static compile.basics.Keywords.DATATYPE.RX;
import static compile.basics.Keywords.OP.*;
import static compile.basics.Keywords.PRIM.BOOLEAN;
import static compile.basics.Keywords.PRIM.STRING;
import static compile.basics.Keywords.RX_FUN.*;

public class RxCoreTest {
    public static RxPayNode genLeft(){
        return new RxPayNode(
                Keywords.PRIM.NULL, //callerType
                Keywords.RX_PAR.CATEGORY_ITEM, // paramType
                STRING, // outType
                null, // Keywords.RX_FUN funType
                "ORIG", // String item
                "TEXT", // String uDefCategory
                Keywords.DATATYPE.LIST_BOOLEAN, //listSource
                null // int[] values
        );
    }
    public static RxPayNode genRight(){
        return new RxPayNode(
                Keywords.PRIM.NULL, //callerType
                Keywords.RX_PAR.TEST_TEXT, // paramType
                STRING, // outType
                null, // Keywords.RX_FUN funType
                "Tokyo", // String item
                null, // String uDefCategory
                null, //listSource
                new int[]{1,2}
        );
    }
    public static RxPayNode[] funList(){
        return new RxPayNode[]{
                new RxPayNode(
                        Keywords.PRIM.NULL, //callerType
                        Keywords.RX_PAR.CATEGORY_ITEM, // paramType
                        STRING, // outType
                        STORE_GET_STRING, // Keywords.RX_FUN funType
                        "TEXT[IN]", // String item
                        "IN", // String uDefCategory
                        LIST_STRING, //listSource
                        null
                ),
                new RxPayNode(
                        Keywords.PRIM.NULL, //callerType
                        Keywords.RX_PAR.CATEGORY_ITEM, // paramType
                        Keywords.PRIM.NUMBER, // outType
                        LEN, // Keywords.RX_FUN funType
                        null, // String item
                        null, // String uDefCategory
                        null, //listSource
                        null
                ),
                new RxPayNode(
                        Keywords.PRIM.NUMBER, //callerType
                        Keywords.RX_PAR.RANGE_PAR, // paramType
                        BOOLEAN, // outType
                        RANGE, // Keywords.RX_FUN funType
                        null, // String item
                        null, // String uDefCategory
                        null, //listSource
                        new int[]{1,2}
                )
        };
    }
    public static RxPayNode valContainer(){
        return new RxPayNode(
                Keywords.PRIM.NULL, //callerType
                Keywords.RX_PAR.TEST_TRUE, // paramType
                Keywords.PRIM.NUMBER, // outType
                VAL_CONTAINER_INT, // Keywords.RX_FUN funType
                null, // String item
                null, // String uDefCategory
                null, //listSource
                new int[]{1}
        );
    }

    public static void RxFunTest(){
        RxPayNode node = genRight();
        for(Keywords.RX_FUN fun : Keywords.RX_FUN.values()){
            FormatUtil formatUtil = new FormatUtil();
            fun.translator.classBody("yadaYada000", node).finish(formatUtil);
            Commons.disp(formatUtil.finish(), "output");
        }
    }
    public static void RxFunListTest(){
        RxPayNode[] nodes = funList();
        FormatUtil formatUtil = new FormatUtil();
        for(RxPayNode node : nodes){
            node.funType.translator.classBody("yadaYada000", node).finish(formatUtil);
        }
        Commons.disp(formatUtil.finish(), "output");
    }
    public static void valContainerTest(){
        RxPayNode valContainer = valContainer();
        FormatUtil formatUtil = new FormatUtil();
        valContainer.funType.translator.classBody("yadaYada000", valContainer).finish(formatUtil);
        Commons.disp(formatUtil.finish(), "output");
    }

    public static  void rxFunPatternTest(){
        RxPayNode[] left = funList();
        RxPayNode[] right = new RxPayNode[]{valContainer()};
        Keywords.OP op = COMPARE_EQUAL;

        FormatUtil formatUtil = new FormatUtil();
        NameGenRxFx nameGen = new NameGenRxFx();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> declarationList = new ArrayList<>();
        new RxFunPatternJava().setFormatUtil(formatUtil).setNameGen(nameGen).
                setNameList(nameList).setDeclarationsList(declarationList).
                translate(left, op, right);

        Commons.disp(declarationList, "declarationList");
        Commons.disp(formatUtil.finish(), "output");


    }
    public static  void rxWordTest(){
        FormatUtil formatUtil = new FormatUtil();
        NameGenRxFx nameGen = new NameGenRxFx();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> declarationList = new ArrayList<>();
        new RxWordJava().setFormatUtil(formatUtil).setNameGen(nameGen).
                setNameList(nameList).setDeclarationsList(declarationList).
                translate(null);

        Commons.disp(declarationList, "declarationList");
        Commons.disp(formatUtil.finish(), "output");


    }
    private static class TreeTest extends TreeFactory {

        @Override
        public TreeNode treeFromWordPattern(String text) {
            TreeNode root = TreeFactory.newTreeNode(RX, text, 0, null);
            boolean more;
            do{
                more = false;
                more |= root.split(RX, AND.asChar);
                more |= root.split(RX, OR.asChar);
                more |= root.negate();
                more |= root.unwrap(OPAR.asChar, CPAR.asChar);
                more |= root.unquote(SQUOTE.asChar);
            }while(more);

            dispBreadthFirst(root);
            dispLeaves(root);
            return root;
        }
    }
}
