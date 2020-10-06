package uq;

public interface UniqueItr {
    void rewind();
    int curr();
    int next();
    boolean hasNext();
    void newCol();
    void newRow();
}
