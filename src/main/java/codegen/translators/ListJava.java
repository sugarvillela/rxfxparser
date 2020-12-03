package codegen.translators;

import codegen.Widget;
import codegen.genjava.*;
import codegen.interfaces.IMethod;
import codegen.interfaces.IText;
import codegen.ut.FormatUtil;
import codegen.ut.GenFileUtil;
import commons.Commons;
import compile.basics.Keywords;
import codegen.ut.NameGen;
import listtable.ListTable;
import listtable.ListTableNumGen;

import java.util.ArrayList;

import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;
import static compile.basics.Keywords.DATATYPE.*;
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

    private NameGen nameGen;// "time":"

    public void translate(){
        ListTableNumGen numGen = ListTable.getInstance().getNumGen();
        nameGen = NameGen.getInstance();
        GenFileUtil genFileUtil = new GenFileUtil();
        String className, fileExt = "." + Widget.getFileExt();
        ArrayList<String> toFile;
        StatUtil statUtil = new StatUtil(numGen);
        CategoryNode[] nodes;

        nodes = numGen.categoryNodesByType(LIST_STRING);
        if(nodes.length > 0){
            className = nameGen.className(LIST_STRING.toString());
            toFile = genList(nodes, className);
            Commons.disp(toFile, "LIST_STRING");
            genFileUtil.persist(toFile, PATH_PACKAGE, className + fileExt);
            toFile.clear();

            statUtil.addStats(nodes);
            statUtil.addBaseIndices(nodes);
        }
        else{
            statUtil.addNulls();
        }

        nodes = numGen.categoryNodesByType(LIST_NUMBER);
        if(nodes.length > 0){
            className = nameGen.className(LIST_NUMBER.toString());
            toFile = genList(nodes, className);
            Commons.disp(toFile, "LIST_NUMBER");
            genFileUtil.persist(toFile, PATH_PACKAGE, className + fileExt);
            toFile.clear();

            statUtil.addStats(nodes);
            statUtil.addBaseIndices(nodes);
        }
        else{
            statUtil.addNulls();
        }

        nodes = numGen.categoryNodesByType(LIST_DISCRETE);
        if(nodes.length > 0){
            className = nameGen.className(LIST_DISCRETE.toString());
            toFile = genCompositeList(nodes, className);
            Commons.disp(toFile, "LIST_DISCRETE");
            genFileUtil.persist(toFile, PATH_PACKAGE, className + fileExt);
            toFile.clear();

            statUtil.addStats(nodes);
            statUtil.addBaseIndices(nodes);
        }
        else{
            statUtil.addNulls();
        }

        nodes = numGen.categoryNodesByType(LIST_SCOPES);
        if(nodes.length > 0){
            className = nameGen.className(LIST_SCOPES.toString());
            toFile = genCompositeList(nodes, className);
            Commons.disp(toFile, "LIST_SCOPES");
            genFileUtil.persist(toFile, PATH_PACKAGE, className + fileExt);
            toFile.clear();

            statUtil.addStats(nodes);
            statUtil.addBaseIndices(nodes);
        }
        else{
            statUtil.addNulls();
        }

        nodes = numGen.categoryNodesByType(LIST_VOTE);
        if(nodes.length > 0){
            className = nameGen.className(LIST_VOTE.toString());
            toFile = genCompositeList(nodes, className);
            Commons.disp(toFile, "LIST_VOTE");
            genFileUtil.persist(toFile, PATH_PACKAGE, className + fileExt);
            toFile.clear();

            statUtil.addStats(nodes);
            statUtil.addBaseIndices(nodes);
        }
        else{
            statUtil.addNulls();
        }

        nodes = numGen.categoryNodesByType(LIST_BOOLEAN);
        if(nodes.length > 0){
            className = nameGen.className(LIST_BOOLEAN.toString());
            toFile = genCompositeList(nodes, className);
            Commons.disp(toFile, "LIST_BOOLEAN");
            genFileUtil.persist(toFile, PATH_PACKAGE, className + fileExt);
            toFile.clear();

            statUtil.addStats(nodes);
            statUtil.addBaseIndices(nodes);
        }
        else{
            statUtil.addNulls();
        }

        toFile = statUtil.genStats(numGen);
        Commons.disp(toFile, "stats");
        genFileUtil.persist(toFile, PATH_PACKAGE, STAT_CLASS_NAME + fileExt);
    }

    private ArrayList<String> genList(CategoryNode[] nodes, String className){
        FormatUtil formatUtil = new FormatUtil();
        new ClassJava.ClassJavaBuilder().setPathPackages(PATH_PACKAGE).
                setVisibility(PUBLIC_).setName(className).build().
                add(
                    genListContent(nodes)
                ).add(
                    genMethodCategoryByRange(nodes)
                ).add(
                    genMethodCategoryByBaseIndex(nodes)
                ).add(
                    genMethodBaseIndexByRange(nodes)
                ).add(
                    genMethodOffset(nodes[0])
                ).
                finish(formatUtil);
        return formatUtil.finish();
    }

    private ArrayList<String> genCompositeList(CategoryNode[] nodes, String className){
        FormatUtil formatUtil = new FormatUtil();
        new ClassJava.ClassJavaBuilder().setPathPackages(PATH_PACKAGE).
            setVisibility(PUBLIC_).setName(className).build().
                add(genListContent(nodes)).
                add(genMethodCategoryByRange(nodes)).
                add(genMethodCategoryByBaseIndex(nodes)).
                add(genMethodBaseIndexByRange(nodes)).
                add(genMethodCompositeOffset(nodes[0])).
                finish(formatUtil);
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

    private IMethod genMethodCategoryByRange(CategoryNode[] nodes){
        MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                setReturnType("String").setName("categoryByRange").setStatic().
                setParams("int index").
                build();
        for (CategoryNode node : nodes) {
            method.add(
                    new ControlJava.ControlBuilder().setIf(
                            new ConditionJava.ConditionBuilder().build().add(
                                    String.format(RANGE_TEST_FORMAT, node.getRangeLow(), node.getRangeHi())
                            )
                    ).build().add(
                            String.format(RETURN_STRING, node.getCategoryName())
                    )
            );
        }
        return method.add(EXCEPTION);
    }

    private IMethod genMethodBaseIndexByRange(CategoryNode[] nodes){
        MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                setReturnType("int").setName("baseIndexByRange").setStatic().
                setParams("int index").
                build();
        for (CategoryNode node : nodes) {
            method.add(
                    new ControlJava.ControlBuilder().setIf(
                            new ConditionJava.ConditionBuilder().build().add(
                                    String.format(RANGE_TEST_FORMAT, node.getRangeLow(), node.getRangeHi())
                            )
                    ).build().add(
                            String.format(RETURN_INT, node.getCategoryEnum())
                    )
            );
        }
        return method.add(EXCEPTION);
    }

    private IMethod genMethodCategoryByBaseIndex(CategoryNode[] nodes){
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

    private IMethod genMethodOffset(CategoryNode node){
        return new MethodJava.MethodBuilder().
            setReturnType("int").setName("offset").setStatic().
            build().add(
                String.format(RETURN_INT, node.getCategoryEnum())
            );
    }

    private IMethod genMethodCompositeOffset(CategoryNode node){
        return new MethodJava.MethodBuilder().
            setReturnType("int").setName("offset").setStatic().
            build().add(
                String.format(RETURN_INT, node.getRowOffset())
            );
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
            stats[iStat++] = nodes[0].getRangeLow();
            stats[iStat++] = nodes[nodes.length -1].getRangeHi();
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
                    new ClassJava.ImportBuilder().setPathPackages("code").
                            setName("DATATYPE").build(),
                    new ClassJava.ImportBuilder().setPathPackages("code").
                            setName("DATATYPE").setStatic().setWildcard().build()
                ).
                setPathPackages(PATH_PACKAGE).setVisibility(PUBLIC_).
                setName(STAT_CLASS_NAME).build().
                    add(
                        new CommentJava.CommentBuilder().build().add("Pass enu value to get type:"),
                        genMethodFlagTypeByRange(),
                        new CommentJava.CommentBuilder().build().add("Pass base index (category name) to get type:"),
                        genMethodFlagTypeByBaseIndex(),
                        new CommentJava.CommentBuilder().build().add("Store Settings:"),
                        genMethodGetW("getWRow", numGen.getWRow()),
                        genMethodGetW("getWCol", numGen.getWCol()),
                        genMethodGetW("getWVal", numGen.getWVal()),
                        new CommentJava.CommentBuilder().build().add("Stats by type:"),
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
