package codegen.translators.rx;

import codegen.genjava.ClassJava;
import codegen.genjava.MethodJava;
import codegen.interfaces.IClass;
import codegen.interfaces.IMethod;
import codegen.translators.interfaces.RxFunGen;
import compile.sublang.factories.PayNodes;

import static codegen.interfaces.enums.VISIBILITY.PUBLIC_;

public abstract class RxFunJava {
    public static abstract class RxFunGenBase implements RxFunGen {
        protected final String extends_;

        public RxFunGenBase(String extends_) {
            this.extends_ = extends_;
        }

        @Override
        public IClass classBody(String className, PayNodes.RxPayNode payNode){
            return new ClassJava.ClassJavaBuilder().
                    setVisibility(PUBLIC_).setStatic().setInner().
                    setName(className).setExtends(extends_).build().
                    add(
                            //CommentJava.quickComment("generated class " + instanceName ),
                            construct(className, payNode)
                    );
        }
    }

    public static class CategoryParam extends RxFunGenBase{
        public CategoryParam(String extends_) {
            super(extends_);
        }

        @Override
        public IMethod construct(String className, PayNodes.RxPayNode payNode){
            return new MethodJava.MethodBuilder().
                    setIsConstructor().
                    setName(className).
                    build().
                    add(String.format("super(%s);", payNode.uDefCategory));
        }
    }
    public static class SingleIntParam extends RxFunGenBase{
        public SingleIntParam(String extends_) {
            super(extends_);
        }

        @Override
        public IMethod construct(String className, PayNodes.RxPayNode payNode){
            return new MethodJava.MethodBuilder().
                    setIsConstructor().
                    setName(className).
                    build().
                    add(String.format("super(%d);", payNode.values[0]));
        }
    }

    public static class DoubleIntParam extends RxFunGenBase{
        public DoubleIntParam(String extends_) {
            super(extends_);
        }

        @Override
        public IMethod construct(String className, PayNodes.RxPayNode payNode){
            int len = payNode.values.length;
            return new MethodJava.MethodBuilder().
                setIsConstructor().setName(className).build().
                add(
                    (len > 1)?
                        String.format("super(%d, %d);", payNode.values[0], payNode.values[1]) :
                        String.format("super(%d);", payNode.values[0])
                );
        }
    }

    public static class StringParam extends RxFunGenBase{
        public StringParam(String extends_) {
            super(extends_);
        }

        @Override
        public IMethod construct(String className, PayNodes.RxPayNode payNode){
            return new MethodJava.MethodBuilder().
                    setIsConstructor().setName(className).build().
                    add(String.format("super(\"%s\");", payNode.item));
        }
    }

    public static class EmptyClass extends RxFunGenBase{
        public EmptyClass(String extends_) {
            super(extends_);
        }

        @Override
        public IClass classBody(String className, PayNodes.RxPayNode payNode) {
            return new ClassJava.ClassJavaBuilder().
                setVisibility(PUBLIC_).setStatic().setInner().
                    setName(className).setExtends(extends_).build();
        }

        @Override
        public IMethod construct(String className, PayNodes.RxPayNode payNode){
            return null;
        }
    }

}
