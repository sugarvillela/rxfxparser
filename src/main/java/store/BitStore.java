package store;

public interface BitStore {
    /**Reads indexing info at left side of int word and sets seek variable(s)
     * @param integer a generated integer enum: see class UqDiscreteGen */
    void seek(int integer);
    int getSeekRow();
    int getSeekCol();
    void set(int integer);
    boolean isSet(int integer);
    int getState(int integer);      // in-place, value only
    int getEnum(int integer);       // in-place value with row, col included
    int getNumber(int integer);     // value only, right shifted to true value
    void drop(int integer);
    int numNonZero(int integer);
    boolean anyNonZero(int integer);

    void disp();
}
