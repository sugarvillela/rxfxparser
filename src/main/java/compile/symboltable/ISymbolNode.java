package compile.symboltable;

import langdef.Keywords;

public interface                                                                                                                                                                       ISymbolNode {
    void setName(String name);
    String getName();
    void setType(Keywords.DATATYPE type);
    Keywords.DATATYPE getType();
}
