package uq;

public interface UqGen {
    void rewind();
    void rewind(int setStart);
    int curr();
    int next();
    boolean hasNext();
    void newCol();
    void newRow();
    int getHalt(); // for debug
}
