package translators.list;

import codegen.Widget;
import codegen.genjava.*;
import codegen.interfaces.IMethod;
import codegen.interfaces.IText;
import runstate.Glob;
import translators.ut.FormatUtil;
import translators.ut.GenFileUtil;
import commons.Commons;
import langdef.Keywords;
import namegen.NameGenSimple;
import listtable.ListTable;
import listtable.ListTableNumGen;

import java.util.ArrayList;

import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;
import static langdef.Keywords.DATATYPE.*;
import static codegen.genjava.SwitchJava.*;
import static listtable.ListTableNumGen.*;

public class ListJava {
    private static final String KEY_VAL_FORMAT = "public static final int %s = 0x0%X;";
    private static final String VAL_FORMAT = "0x0%X";
    private static final String RANGE_TEST_FORMAT = "0x0%X <= index && index <= 0x0%X";
    private static final String RETURN_STRING = "return \"%s\";";
    private static final String RETURN_INT = "return 0x0%X;";
    private static final String RETURN_DATATYPE = "return %s;";
    private static final String STAT_CLASS_NAME = "FlagStats";
    private static final String PATH_PACKAGE = "lists";
    private static final String EXCEPTION = "throw new IllegalStateException(\"Dev err: unknown datatype\");";

    private ListTableNumGen numGen;
    private NameGenSimple nameGen;
    private MethodUtil methodUtil;
    private ScopesUtil scopesUtil;
    private StatUtil statUtil;
    private GenFileUtil genFileUtil;
    private String fileExt;

    public void translate(){
        numGen = Glob.LIST_TABLE.getNumGen();
        nameGen = Glob.NAME_GEN_SIMPLE;
        methodUtil = new MethodUtil();
        scopesUtil = new ScopesUtil();
        statUtil = new StatUtil(numGen);
        genFileUtil = new GenFileUtil();
        fileExt = "." + Widget.getFileExt();

        this.genClassFile(LIST_STRING, false, false);
        this.genClassFile(LIST_NUMBER, false, false);
        this.genClassFile(LIST_DISCRETE, true, false);
        this.genClassFile(LIST_SCOPES, true, true);
        this.genClassFile(LIST_VOTE, true, false);
        this.genClassFile(LIST_BOOLEAN, true, false);

        this.genStatsClassFile();
    }

    private void genClassFile(Keywords.DATATYPE datatype, boolean isComposite, boolean isScope){
        CategoryNode[] nodes = numGen.categoryNodesByType(datatype);
        if(nodes.length > 0){
            String className = nameGen.className(datatype.toString());
            ArrayList<String> toFile = genList(nodes, className, isComposite, isScope);
            Commons.disp(toFile, datatype.toString());
            genFileUtil.persist(toFile, PATH_PACKAGE, className + fileExt);
            toFile.clear();

            statUtil.addStats(nodes);
            statUtil.addBaseIndices(nodes);
        }
        else{
            statUtil.addNulls();
        }
    }

    private void genStatsClassFile(){
        ArrayList<String> toFile = statUtil.genStats(numGen);
        Commons.disp(toFile, "stats");
        genFileUtil.persist(toFile, PATH_PACKAGE, STAT_CLASS_NAME + fileExt);
    }

    private ArrayList<String> genList(CategoryNode[] nodes, String className, boolean isComposite, boolean isScope){
        FormatUtil formatUtil = new FormatUtil();
        ClassJava classJava = (ClassJava)new ClassJava.ClassJavaBuilder().setPathPackages(PATH_PACKAGE).
                setVisibility(PUBLIC_).setName(className).build().
                add(
                    genListContent(nodes),
                    methodUtil.categoryByRange(nodes),
                    methodUtil.categoryByBaseIndex(nodes),
                    methodUtil.baseIndexByRange(nodes),
                    CommentJava.quickComment("for monotonic values across different arrays"),
                    methodUtil.offset(nodes[0], isComposite),
                    CommentJava.quickComment("for debug or user-friendly display"),
                    methodUtil.nameByIndex(nodes)
                );
        if(isScope){
            classJava.add(
                scopesUtil.topScope(nodes[0]),
                scopesUtil.bottomScope(nodes[0]),
                scopesUtil.nextScopeUp(nodes[0]),
                scopesUtil.nextScopeDown(nodes[0])
            );
        }
        classJava.finish(formatUtil);
        return formatUtil.finish();
    }

    private IText genListContent(CategoryNode[] nodes){
        TextJava textJava = (TextJava)new TextJava.TextBuilder().build();

        for (CategoryNode node : nodes) {
            //inner.getValue().disp();
            //System.out.println("\nCategory: " + node.getCategoryName());
            textJava.add(String.format(KEY_VAL_FORMAT, node.getCategoryName(), node.getCategoryEnum()));
            textJava.add("");
            node.rewind();
            while(node.hasNext()){
                textJava.add(String.format(KEY_VAL_FORMAT, node.nextKey(), node.nextVal()));
            }
            textJava.add("");
        }
        return textJava;
    }

    private class MethodUtil{
        private IMethod categoryByRange(CategoryNode[] nodes){
            MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                    setReturnType("String").setName("categoryByRange").setStatic().
                    setParams("int index").
                    build();
            for (CategoryNode node : nodes) {
                method.add(
                        new ControlJava.ControlBuilder().setIf(
                                new ConditionJava.ConditionBuilder().build().add(
                                        String.format(RANGE_TEST_FORMAT, node.getValFirst(), node.getValLast())
                                )
                        ).build().add(
                                String.format(RETURN_STRING, node.getCategoryName())
                        )
                );
            }
            return method.add(EXCEPTION);
        }

        private IMethod baseIndexByRange(CategoryNode[] nodes){
            MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                    setReturnType("int").setName("baseIndexByRange").setStatic().
                    setParams("int index").
                    build();
            for (CategoryNode node : nodes) {
                method.add(
                        new ControlJava.ControlBuilder().setIf(
                                new ConditionJava.ConditionBuilder().build().add(
                                        String.format(RANGE_TEST_FORMAT, node.getValFirst(), node.getValLast())
                                )
                        ).build().add(
                                String.format(RETURN_INT, node.getCategoryEnum())
                        )
                );
            }
            return method.add(EXCEPTION);
        }

        private IMethod categoryByBaseIndex(CategoryNode[] nodes){
            MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                    setReturnType("String").setName("categoryByBaseIndex").setStatic().
                    setParams("int index").
                    build();
            SwitchJava switchJava = (SwitchJava)new SwitchBuilder().setTestObject("index").setNoBreaks().build();

            for (CategoryNode node : nodes) {
                switchJava.startCase(String.format(VAL_FORMAT, node.getCategoryEnum())).
                        add(
                                String.format(RETURN_STRING, node.getCategoryName())
                        ).
                        finishCase();
            }
            switchJava.startDefault().add(EXCEPTION).finishCase();
            return method.add(switchJava);
        }

        private IMethod offset(CategoryNode node, boolean isComposite){
            int offset = (isComposite)? node.getRowOffset() : node.getCategoryEnum();
            return new MethodJava.MethodBuilder().
                    setReturnType("int").setName("offset").setStatic().
                    build().add(
                    String.format(RETURN_INT, offset)
            );
        }

        private IMethod nameByIndex(CategoryNode[] nodes){
            MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                    setReturnType("String").setName("nameByIndex").setStatic().
                    setParams("int index").
                    build();
            SwitchJava switchJava = (SwitchJava)new SwitchBuilder().setTestObject("index").setNoBreaks().build();
            for (CategoryNode node : nodes) {
                ArrayList<String> keys = node.getKeys();
                for(String key : keys){
                    switchJava.startCase(key).
                            add(
                                    String.format(RETURN_STRING, key)
                            ).
                            finishCase();
                }

            }
            switchJava.startDefault().add(String.format(RETURN_STRING, "none")).finishCase();
            return method.add(switchJava);
        }
    }
    private class ScopesUtil{
        private IMethod topScope(CategoryNode node){
            return new MethodJava.MethodBuilder().
                    setReturnType("int").setName("topScope").setStatic().
                    build().add(
                    String.format(RETURN_DATATYPE, node.getKeyFirst())
            );
        }
        private IMethod bottomScope(CategoryNode node){
            return new MethodJava.MethodBuilder().
                    setReturnType("int").setName("bottomScope").setStatic().
                    build().add(
                    String.format(RETURN_DATATYPE, node.getKeyLast())
            );
        }
        private IMethod nextScopeUp(CategoryNode node){
            MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                    setReturnType("int").setName("nextScopeUp").setStatic().
                    setParams("int index").
                    build();
            SwitchJava switchJava = (SwitchJava)new SwitchBuilder().setTestObject("index").setNoBreaks().build();

            String prev = "0";
            for(String key : node.getKeys()){
                switchJava.startCase(key).
                        add(String.format(RETURN_DATATYPE, prev)).
                        finishCase();
                prev = key;
            }
            switchJava.startDefault().add(EXCEPTION).finishCase();
            return method.add(switchJava);
        }
        private IMethod nextScopeDown(CategoryNode node){
            MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                    setReturnType("int").setName("nextScopeDown").setStatic().
                    setParams("int index").
                    build();
            SwitchJava switchJava = (SwitchJava)new SwitchBuilder().setTestObject("index").setNoBreaks().build();

            ArrayList<String> keys = node.getKeys();
            String prev = "0";
            for(int i = keys.size() - 1; i >= 0; i--){
                switchJava.startCase(keys.get(i)).
                        add(String.format(RETURN_DATATYPE, prev)).
                        finishCase();
                prev = keys.get(i);
            }
            switchJava.startDefault().add(EXCEPTION).finishCase();
            return method.add(switchJava);
        }
    }
    private class StatUtil{
        private Keywords.DATATYPE[] listOrder;
        private final int[] stats;
        private final int[][] baseIndices;
        private int iStat, iBase;

        public StatUtil( ListTableNumGen numGen) {
            listOrder = numGen.getListOrder();
            stats = new int[listOrder.length * 3];
            baseIndices = new int[listOrder.length][];
            iStat = 0;
            iBase = 0;
        }

        public void addStats(CategoryNode[] nodes){
            int totalSize = 0;
            for(CategoryNode node : nodes){
                totalSize += node.size() + 1; // plus one for category enu
            }
            stats[iStat++] = totalSize;
            stats[iStat++] = nodes[0].getValFirst();
            stats[iStat++] = nodes[nodes.length -1].getValLast();
        }
        public void addNulls(){
            iStat +=3;
            iBase++;
        }

        public void addBaseIndices(CategoryNode[] nodes){
            int[] categoryIndices = new int[nodes.length];
            int i = 0;
            for(CategoryNode node : nodes){
                categoryIndices[i++] = node.getCategoryEnum();
            }
            baseIndices[iBase++] = categoryIndices;
        }

        public ArrayList<String> genStats(ListTableNumGen numGen){
            FormatUtil formatUtil = new FormatUtil();
            ClassJava classJava = (ClassJava)new ClassJava.ClassJavaBuilder().
                setImports(
                    new ClassJava.ImportBuilder().setPathPackages("enums").
                            setName("DATATYPE").build(),
                    new ClassJava.ImportBuilder().setPathPackages("enums").
                            setName("DATATYPE").setStatic().setWildcard().build()
                ).
                setPathPackages(PATH_PACKAGE).setVisibility(PUBLIC_).
                setName(STAT_CLASS_NAME).build().
                    add(
                        CommentJava.quickComment("Pass enu value to get type:"),
                        genMethodFlagTypeByRange(),
                        CommentJava.quickComment("Pass base index (category name) to get type:"),
                        genMethodFlagTypeByBaseIndex(),
                        CommentJava.quickComment("Store Settings:"),
                        genMethodGetW("getWRow", numGen.getWRow()),
                        genMethodGetW("getWCol", numGen.getWCol()),
                        genMethodGetW("getWVal", numGen.getWVal()),
                        CommentJava.quickComment("Stats by type:"),
                        genMethodGetByDatatype("getSize", 0),
                        genMethodGetByDatatype("getLowIndex", 1),
                        genMethodGetByDatatype("getHighIndex", 2)
                    );
                //addGenMethodGetX(classJava);
                classJava.finish(formatUtil);
            return formatUtil.finish();
        }
        private IMethod genMethodFlagTypeByRange(){
            MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                    setReturnType("DATATYPE").setName("flagTypeByRange").setStatic().
                    setParams("int index").
                    build();
            for (int i = 2, j = 0; i < stats.length; i+=3, j++) {
                method.add(
                        new ControlJava.ControlBuilder().setIf(
                            new ConditionJava.ConditionBuilder().build().add(
                                String.format(RANGE_TEST_FORMAT, stats[i-1], stats[i])
                            )
                        ).build().add(
                            String.format(RETURN_DATATYPE, listOrder[j])
                        )
                );
            }
            return method.add(EXCEPTION);
        }

        private IMethod genMethodFlagTypeByBaseIndex(){
            MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                    setReturnType("DATATYPE").setName("flagTypeByBaseIndex").setStatic().
                    setParams("int index").
                    build();
            SwitchJava switchJava = (SwitchJava)new SwitchBuilder().setTestObject("index").setNoBreaks().build();

            for(int i = 0; i < baseIndices.length; i++){
                if(baseIndices[i] != null){
                    for(int j = 0; j < baseIndices[i].length; j++){
                        switchJava.startCase(String.format(VAL_FORMAT, baseIndices[i][j]));

                        System.out.printf("%d: %d: %X\n", i, j, baseIndices[i][j]);
                    }
                    switchJava.add(
                        String.format(RETURN_DATATYPE, listOrder[i])
                    ).finishCase();
                }

            }
            switchJava.startDefault().add(EXCEPTION).finishCase();
            return method.add(switchJava);
        }

        private IMethod genMethodGetW(String methodName, int w){
            return new MethodJava.MethodBuilder().
                    setReturnType("int").setName(methodName).setStatic().build().
                    add(String.format("return %d;", w)); // more readable if not in hex
        }

        private IMethod genMethodGetByDatatype(String methodName, int index){
            MethodJava method = (MethodJava) new MethodJava.MethodBuilder().
                    setReturnType("int").setName(methodName).setParams("DATATYPE datatype").setStatic().build();
            SwitchJava switchJava = (SwitchJava)new SwitchBuilder().setTestObject("datatype").setNoBreaks().build();
            for (Keywords.DATATYPE datatype : listOrder) {
                //System.out.printf("--- %s: %d: %s: %d\n", methodName, index, datatype.toString(), stats[index]);
                switchJava.startCase(datatype.toString()).
                        add(
                                String.format(RETURN_INT, stats[index])
                        ).
                        finishCase();
                index += 3;
            }
            switchJava.startDefault().add(EXCEPTION).finishCase();
            return method.add(switchJava);
        }


    }
}
