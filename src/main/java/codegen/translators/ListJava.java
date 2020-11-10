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
import compile.symboltable.ListTable;
import compile.symboltable.ListTableNumGen;

import java.util.ArrayList;

import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;
import static compile.basics.Keywords.DATATYPE.*;
import static codegen.genjava.SwitchJava.*;
import static compile.symboltable.ListTableNumGen.*;

public class ListJava {
    private static final String KEY_VAL_FORMAT = "public static final int %s = 0x0%X;";
    private static final String VAL_FORMAT = "0x0%X";
    private static final String RANGE_TEST_FORMAT = "0x0%X <= index && index <= 0x0%X";
    private static final String RETURN_STRING = "return \"%s\";";
    private static final String RETURN_INT = "return 0x0%X;";
    private static final String STAT_CLASS_NAME = "ListStats";
    private static final String SUBPACKAGE = "lists";
    private NameGen nameGen;

    public void translate(){
        ListTableNumGen numGen = ListTable.getInstance().getNumGen();
        nameGen = NameGen.getInstance();
        GenFileUtil genFileUtil = new GenFileUtil();
        String className, fileExt = "." + Widget.getFileExt();
        ArrayList<String> outList;
        StatUtil statUtil = new StatUtil();

        KeyValNode[] nodes = numGen.keyValMapAsArray(LIST_BOOLEAN);
        if(nodes.length > 0){
            className = nameGen.className(LIST_BOOLEAN.toString());
            outList = genListBool(nodes, className);
            Commons.disp(outList, "LIST_BOOLEAN");
            genFileUtil.persist(outList, SUBPACKAGE, className + fileExt);
            outList.clear();

            statUtil.addStats(nodes);
        }
        else{
            statUtil.addNulls();
        }

        nodes = numGen.keyValMapAsArray(LIST_DISCRETE);
        if(nodes.length > 0){
            className = nameGen.className(LIST_DISCRETE.toString());
            outList = genListDiscrete(nodes, className);
            Commons.disp(outList, "LIST_DISCRETE");
            genFileUtil.persist(outList, SUBPACKAGE, className + fileExt);
            outList.clear();

            statUtil.addStats(nodes);
        }
        else{
            statUtil.addNulls();
        }

        nodes = numGen.keyValMapAsArray(LIST_NUMBER);
        if(nodes.length > 0){
            className = nameGen.className(LIST_NUMBER.toString());
            outList = genList(nodes, className);
            Commons.disp(outList, "LIST_NUMBER");
            genFileUtil.persist(outList, SUBPACKAGE, className + fileExt);
            outList.clear();

            statUtil.addStats(nodes);
        }
        else{
            statUtil.addNulls();
        }

        nodes = numGen.keyValMapAsArray(LIST_STRING);
        if(nodes.length > 0){
            className = nameGen.className(LIST_STRING.toString());
            outList = genList(nodes, className);
            Commons.disp(outList, "LIST_STRING");
            genFileUtil.persist(outList, SUBPACKAGE, className + fileExt);
            outList.clear();

            statUtil.addStats(nodes);
        }
        else{
            statUtil.addNulls();
        }
        outList = statUtil.genStats();
        Commons.disp(outList, "stats");
        genFileUtil.persist(outList, SUBPACKAGE, STAT_CLASS_NAME + fileExt);
    }
    private ArrayList<String> genListBool(KeyValNode[] nodes, String className){
        FormatUtil formatUtil = new FormatUtil();

        new ClassJava.ClassJavaBuilder().setSubPackages(SUBPACKAGE).
                setVisibility(PUBLIC_).setName(className).build().
                add(
                    genBoolContent(nodes)
                ).add(
                    genBool_rangeToString(nodes)
                ).add(
                    genBool_rangeToBaseIndex(nodes)
                ).add(
                    genBool_baseIndexToString(nodes)
                ).finish(formatUtil);
        //Commons.disp(formatUtil.finish());
        return formatUtil.finish();
    }
    private IText genBoolContent(KeyValNode[] nodes){
        TextJava textJava = (TextJava)new TextJava.TextBuilder().build();
        for (ListTableNumGen.KeyValNode node : nodes) {
            //inner.getValue().disp();
            System.out.println("\nCategory: " + node.getGroupName());
            textJava.add(String.format(KEY_VAL_FORMAT, node.getGroupName(), node.getRowNumber()));
            node.rewind();
            while(node.hasNext()){
                textJava.add(String.format(KEY_VAL_FORMAT, node.nextKey(), node.nextVal()));
            }
            textJava.add("");
        }
        return textJava;
    }
    private IMethod genBool_rangeToString(KeyValNode[] nodes){
        MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                setReturnType("String").setName("rangeToString").setStatic().
                setParams("int index").
                build();
        for (ListTableNumGen.KeyValNode node : nodes) {
            method.add(
                new ControlJava.ControlBuilder().setIf(
                    new ConditionJava.ConditionBuilder().build().add(
                        String.format(RANGE_TEST_FORMAT, node.getRangeLow(), node.getRangeHi())
                    )
                ).build().add(
                    String.format(RETURN_STRING, node.getGroupName())
                )
            );
        }
        return method.add("return null;");
    }
    private IMethod genBool_rangeToBaseIndex(KeyValNode[] nodes){
        MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                setReturnType("int").setName("rangeToBaseIndex").setStatic().
                setParams("int index").
                build();
        for (ListTableNumGen.KeyValNode node : nodes) {
            method.add(
                    new ControlJava.ControlBuilder().setIf(
                            new ConditionJava.ConditionBuilder().build().add(
                                    String.format(RANGE_TEST_FORMAT, node.getRangeLow(), node.getRangeHi())
                            )
                    ).build().add(
                            String.format(RETURN_INT, node.getRowNumber())
                    )
            );
        }
        return method.add("return -1;");
    }
    private IMethod genBool_baseIndexToString(KeyValNode[] nodes){
        MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                setReturnType("String").setName("baseIndexToString").setStatic().
                setParams("int index").
                build();
        SwitchJava switchJava = (SwitchJava)new SwitchBuilder().setTestObject("index").setNoBreaks().build();
        for (ListTableNumGen.KeyValNode node : nodes) {
            switchJava.startCase(String.format(VAL_FORMAT, node.getRowNumber())).
            add(
                String.format(RETURN_STRING, node.getGroupName())
            ).
            finishCase();
        }
        switchJava.startDefault().add("return null;").finishCase();
        return method.add(switchJava);
    }

    private ArrayList<String> genListDiscrete(KeyValNode[] nodes, String className){
        FormatUtil formatUtil = new FormatUtil();
        new ClassJava.ClassJavaBuilder().setSubPackages(SUBPACKAGE).
                setVisibility(PUBLIC_).setName(className).build().
                add(
                    genDiscreteContent(nodes)
                ).add(
                    genDiscrete_rangeToString(nodes)
                ).
                finish(formatUtil);
        return formatUtil.finish();
    }
    private IText genDiscreteContent(KeyValNode[] nodes){
        TextJava textJava = (TextJava)new TextJava.TextBuilder().build();
        for (ListTableNumGen.KeyValNode node : nodes) {
            //inner.getValue().disp();
            System.out.println("\nCategory: " + node.getGroupName());
            //textJava.add(String.format(KEY_VAL_FORMAT, node.getGroupName(), node.getColNumber()));
            node.rewind();
            while(node.hasNext()){
                textJava.add(String.format(KEY_VAL_FORMAT, node.nextKey(), node.nextVal()));
            }
            textJava.add("");
        }
        return textJava;
    }
    private IMethod genDiscrete_rangeToString(KeyValNode[] nodes){
        MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                setReturnType("String").setName("rangeToString").setStatic().
                setParams("int index").
                build();
        for (ListTableNumGen.KeyValNode node : nodes) {
            method.add(
                    new ControlJava.ControlBuilder().setIf(
                            new ConditionJava.ConditionBuilder().build().add(
                                    String.format(RANGE_TEST_FORMAT, node.getRangeLow(), node.getRangeHi())
                            )
                    ).build().add(
                            String.format(RETURN_STRING, node.getGroupName())
                    )
            );
        }
        return method.add("return null;");
    }

    private ArrayList<String> genList(KeyValNode[] nodes, String className){
        FormatUtil formatUtil = new FormatUtil();
        new ClassJava.ClassJavaBuilder().setSubPackages(SUBPACKAGE).
                setVisibility(PUBLIC_).setName(className).build().
                add(
                    genListContent(nodes)
                ).add(
                    genList_rangeToString(nodes)
                ).
                finish(formatUtil);
        return formatUtil.finish();
    }
    private IText genListContent(KeyValNode[] nodes){
        TextJava textJava = (TextJava)new TextJava.TextBuilder().build();
        for (ListTableNumGen.KeyValNode node : nodes) {
            //inner.getValue().disp();
            System.out.println("\nCategory: " + node.getGroupName());
            //textJava.add(String.format(KEY_VAL_FORMAT, node.getGroupName(), node.getColNumber()));
            node.rewind();
            while(node.hasNext()){
                textJava.add(String.format(KEY_VAL_FORMAT, node.nextKey(), node.nextVal()));
            }
            textJava.add("");
        }
        return textJava;
    }
    private IMethod genList_rangeToString(KeyValNode[] nodes){
        MethodJava method = (MethodJava)new MethodJava.MethodBuilder().
                setReturnType("String").setName("rangeToString").setStatic().
                setParams("int index").
                build();
        for (ListTableNumGen.KeyValNode node : nodes) {
            method.add(
                    new ControlJava.ControlBuilder().setIf(
                            new ConditionJava.ConditionBuilder().build().add(
                                    String.format(RANGE_TEST_FORMAT, node.getRangeLow(), node.getRangeHi())
                            )
                    ).build().add(
                            String.format(RETURN_STRING, node.getGroupName())
                    )
            );
        }
        return method.add("return null;");
    }

    private class StatUtil{
        private final Keywords.DATATYPE[] datatypes;
        private final int[] stats;
        private int index;

        public StatUtil() {
            datatypes = new Keywords.DATATYPE[]{LIST_BOOLEAN, LIST_DISCRETE, LIST_NUMBER, LIST_STRING};
            stats = new int[12];
            index = 0;
        }

        public void addStats(KeyValNode[] nodes){
            int totalSize = 0;
            for(KeyValNode node : nodes){
                totalSize += node.size();
            }
            stats[index++] = totalSize;
            stats[index++] = nodes[0].getRangeLow();
            stats[index++] = nodes[nodes.length -1].getRangeHi();
        }
        public void addNulls(){
            index+=3;
        }
        public ArrayList<String> genStats(){
            Commons.disp(stats, "genStats");

            FormatUtil formatUtil = new FormatUtil();
            ClassJava classJava = (ClassJava)new ClassJava.ClassJavaBuilder().
                setSubPackages(SUBPACKAGE).setVisibility(PUBLIC_).
                setName(STAT_CLASS_NAME).build();
            String methodName;
            index = 0;

            for(Keywords.DATATYPE datatype : datatypes){
                methodName = nameGen.functionName("getSize" + datatype);
                classJava.add(genStatsMethod(methodName));
                methodName = nameGen.functionName("getLowIndex" + datatype);
                classJava.add(genStatsMethod(methodName));
                methodName = nameGen.functionName("getHighIndex" + datatype);
                classJava.add(genStatsMethod(methodName));
            }
            classJava.finish(formatUtil);
            return formatUtil.finish();
        }
        private IMethod genStatsMethod(String methodName){
            return new MethodJava.MethodBuilder().
                    setReturnType("int").setName(methodName).setStatic().build().
                    add(String.format("return %d;", stats[index++]));
        }
    }
}
