package compile.symboltable;

import compile.basics.Keywords;

public interface ISymbolNode {
    void setName(String name);
    String getName();
    void setType(Keywords.HANDLER type);
    Keywords.HANDLER getType();
}
