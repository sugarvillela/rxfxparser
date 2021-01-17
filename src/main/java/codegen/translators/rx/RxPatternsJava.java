package codegen.translators.rx;

import codegen.Widget;
import codegen.genjava.*;
import codegen.interfaces.IClass;
import codegen.interfaces.IMethod;
import codegen.namegen.NameGenRxFx;
import codegen.ut.FormatUtil;
import codegen.ut.GenFileUtil;
import commons.Commons;
import commons.Util_string;
import listtable.ListTableNumGen;
import runstate.StaticState;

import java.util.ArrayList;

import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;

public class RxPatternsJava {
    private static final String CLASS_NAME = "RxPatterns";
    private final RxFunPatternUtil rxFunPatternUtil;

    private NameGenRxFx nameGen;
    private GenFileUtil genFileUtil;
    private String fileExt;
    private ArrayList<String> nameList;
    private FormatUtil formatUtil;

    public RxPatternsJava() {
        rxFunPatternUtil = new RxFunPatternUtil();
    }


    public void translate(){
        nameGen = StaticState.getInstance().getNameGenRxFx();
        genFileUtil = new GenFileUtil();
        fileExt = "." + Widget.getFileExt();

        nameList = new ArrayList<>();
        formatUtil = new FormatUtil();
        rxFunPatternUtil.classBody().finish(formatUtil);
        Commons.disp(nameList, "nameList");
        Commons.disp(formatUtil.finish(), "output");

    }
//    private ArrayList<String>  genClassFile(){
//
//    }

    private class RxFunUtil{
        private static final String EXTENDS = "StoreGetString";// TODO map functions to extends clauses

        IClass classBody(){
            String instanceName = nameGen.getRx(NameGenRxFx.RX_FUN);
            String className = Util_string.toPascalCase(instanceName);
            nameList.add(instanceName);
            nameList.add(className);

            return new ClassJava.ClassJavaBuilder().
                    setVisibility(PUBLIC_).setStatic().setName(className).setExtends(EXTENDS).build().
                    add(
                            //CommentJava.quickComment("generated class " + instanceName ),
                            construct(className)
                    );
        }
        IMethod construct(String className){
            return new MethodJava.MethodBuilder().
                    setIsConstructor().setName(className).build().
                    add(
                            CommentJava.quickComment("initLeft(new StoreGetString(IN));" ),
                            CommentJava.quickComment("initCompare(COMPARE_EQUAL);" ),
                            CommentJava.quickComment("initRight(new valContainerObject(\"have\"));" )
                    );
        }
    }

    private class RxFunPatternUtil{
        private static final String EXTENDS = "RxFunPatternBase";

        IClass classBody(){
            String instanceName = nameGen.getRx(NameGenRxFx.RX_FUN_PATTERN);
            String className = Util_string.toPascalCase(instanceName);
            nameList.add(instanceName);
            nameList.add(className);

            return new ClassJava.ClassJavaBuilder().
                setVisibility(PUBLIC_).setStatic().setName(className).setExtends(EXTENDS).build().
                add(
                        //CommentJava.quickComment("generated class " + instanceName ),
                        construct(className)
                );
        }
        IMethod construct(String className){
            return new MethodJava.MethodBuilder().
                setIsConstructor().setName(className).build().
                add(
                    CommentJava.quickComment("initLeft(new StoreGetString(IN));" ),
                        CommentJava.quickComment("initCompare(COMPARE_EQUAL);" ),
                        CommentJava.quickComment("initRight(new valContainerObject(\"have\"));" )
                );
        }
    }


}

