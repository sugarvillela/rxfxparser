package uq;

public interface UqGenComposite extends UqGen{
    int getWRow();
    int getWCol();
    int getWVal();
    UqGen getRowGen();
    UqGen getColGen();
    int currRowCol();
}
