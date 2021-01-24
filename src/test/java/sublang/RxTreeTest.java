package sublang;

import listtable.ListTableItemSearch;
import runstate.Glob;
import scannode.ScanNode;
import sublang.treenode.TreeNodeBase;

import java.util.ArrayList;

public class RxTreeTest {
    public static void testSimpleTree(){
        Glob.LIST_TABLE.initLists();
        Glob.LIST_TABLE.disp();
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        TreeNodeBase root = Glob.LOGIC_TREE_RX.treeFromWordPattern(text);
        Glob.TREE_BUILD_UTIL.dispPreOrder(root);
    }
    public static void testListTableMethods(){
        Glob.LIST_TABLE.initLists();
        //Glob.LIST_TABLE.disp();
        ListTableItemSearch search = Glob.LIST_TABLE.getItemSearch();
        System.out.println("item:     datatype: " + search.getDataType("IN"));
        System.out.println("category: datatype: " + search.getDataType("TEXT"));
        System.out.println("datatype: datatype: " + search.getDataType("LIST_STRING"));

        System.out.println("item:     datatype: " + search.getDataType("ARTICLE"));
        System.out.println("category: datatype: " + search.getDataType("WORD_SUB_TYPE"));
        System.out.println("datatype: datatype: " + search.getDataType("LIST_BOOLEAN"));

        System.out.println("categoryByItemName(IN): " + search.categoryByItemName("IN"));
        System.out.println("categoryByItemName(ARTICLE): " + search.categoryByItemName("ARTICLE"));
    }
    public static void testSimpleFunPattern(){
        Glob.LIST_TABLE.initLists();
        //Glob.LIST_TABLE.disp();
        String text = "TEXT[IN].LEN().RANGE(1,6)=TRUE&WORD_SUB_TYPE[ARTICLE]"; //
        TreeNodeBase root = Glob.LOGIC_TREE_RX.treeFromWordPattern(text);
        //Glob.TREE_BUILD_UTIL.dispPreOrder(root);
    }
    public void test3(){
//        //String text = "~(A=a&B='b')&(C=c&D=d)&~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
//        String text = "~(A=a&B='b')";//"dru='&'&LEN()=2";
//        TreeNodeBase root = rxTree.treeFromWordPattern(text);
//        ArrayList<ScanNode> cmdList = rxTree.treeToScanNodeList(RX, root);
//        Commons.disp(cmdList);
//        ArrayList<String> strList = scanNodesToString(cmdList);
//        //TreeNode reroot = rxTree.treeFromScanNodeSource(strList);
//        //assertEqual(root, reroot);
    }
    public void test4(){
//        String text = "~((myFunction()=6&myAge<65)&(C=c&D>d))&~(weightOfEarth>157pounds&state.done=true)&'G'";
//        //String text = "~(~(A=a&B=~'b'))";//&(C=c)&'D'='d'|~(E=e&F=f)&'G'
//        TreeNodeBase root = rxTree.treeFromWordPattern(text);
//        rxTree.dispBreadthFirst(root);
//        //rxLeafUtil.finishAndValidate(root);
//        rxTree.dispBreadthFirst(root);
    }
    public boolean assertEqual(TreeNodeBase root1, TreeNodeBase root2){
        ArrayList<TreeNodeBase>[] levels1 = Glob.TREE_BUILD_UTIL.breadthFirst(root1);
        ArrayList<TreeNodeBase>[] levels2 = Glob.TREE_BUILD_UTIL.breadthFirst(root2);
        if(levels1.length != levels2.length){
            System.out.println("fail: levels1.length != levels2.length");
            return false;
        }
        for(int i = 0; i<levels1.length; i++){
            int len1 = levels1[i].size();
            int len2 = levels2[i].size();
            if(len1 != len2){
                System.out.println("fail: len1 != len2");
                return false;
            }
            for(int j = 0; j < len1; j++){
                String node1 = levels1[i].get(j).toString();
                String node2 = levels2[i].get(j).toString();
                boolean equal = node1.equals(node2);
                System.out.printf("\n%d:%d: equal: %b\n    %s \n    %s \n", i, j, equal, node1, node2);
                if(!equal){
                    //Error!
                }
            }
        }
        return true;
    }


    public ArrayList<String> scanNodesToString(ArrayList<ScanNode> scanNodes){
        ArrayList<String> nodesToString = new ArrayList<>();
        for(ScanNode node : scanNodes){
            nodesToString.add(node.toString());
        }
        return nodesToString;
    }
}
