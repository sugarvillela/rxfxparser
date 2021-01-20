package translators;

import namegen.NameGenRx;
import runstate.Glob;
import sublang.treenode.TreeNodeBase;
import translators.rx.RxFunPatternJava;
import translators.rx.RxWordJava;
import translators.ut.FormatUtil;
import commons.Commons;
import langdef.Keywords;
import sublang.factories.PayNodes.*;
import sublang.TreeBuildUtil;

import java.util.ArrayList;

import static langdef.Keywords.DATATYPE.LIST_STRING;
import static langdef.Keywords.DATATYPE.RX;
import static langdef.Keywords.OP.*;
import static langdef.Keywords.PRIM.BOOLEAN;
import static langdef.Keywords.PRIM.STRING;
import static langdef.Keywords.RX_FUN.*;

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
        NameGenRx nameGen = Glob.NAME_GEN_RX;
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
        NameGenRx nameGen = Glob.NAME_GEN_RX;
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> declarationList = new ArrayList<>();
        new RxWordJava().setFormatUtil(formatUtil).setNameGen(nameGen).
                setNameList(nameList).setDeclarationsList(declarationList).
                translate(null);

        Commons.disp(declarationList, "declarationList");
        Commons.disp(formatUtil.finish(), "output");


    }
    private static class TreeTest {

        //@Override
        public TreeNodeBase treeFromWordPattern(String text) {
            TreeBuildUtil treeBuildUtil = Glob.TREE_BUILD_UTIL;
            TreeNodeBase root = treeBuildUtil.newTreeNode(RX, text, 0, null);
            boolean more;
            do{
                more = false;
                more |= root.split(AND.asChar);
                more |= root.split(OR.asChar);
                more |= root.negate();
                more |= root.unwrap(OPAR.asChar, CPAR.asChar);
                more |= root.unquote(SQUOTE.asChar);
            }while(more);

            treeBuildUtil.dispBreadthFirst(root);
            treeBuildUtil.dispLeaves(root);
            return root;
        }
    }
}
