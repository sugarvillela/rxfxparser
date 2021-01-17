package codegen.translators.interfaces;

import codegen.interfaces.IClass;
import codegen.interfaces.IMethod;
import compile.sublang.factories.PayNodes;

public interface RxFunGen {
    IClass classBody(String className, PayNodes.RxPayNode payNode);
    IMethod construct(String className, PayNodes.RxPayNode payNode);
}
