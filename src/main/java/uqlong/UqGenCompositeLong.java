package uqlong;

public interface UqGenCompositeLong extends UqGenLong{
    int getWRow();
    int getWCol();
    int getWVal();
    UqGenLong getRowGen();
    UqGenLong getColGen();
    long currRowCol();
    long curRowOffset();
}
