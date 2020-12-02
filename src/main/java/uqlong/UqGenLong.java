package uqlong;

public interface UqGenLong {
    void rewind();
    void rewind(long setStart);
    long curr();
    long next();
    boolean hasNext();
    void newCol();
    void newRow();
}
