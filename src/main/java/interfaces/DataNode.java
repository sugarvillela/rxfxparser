package interfaces;

public abstract class DataNode {
    public abstract String readableContent();

    @Override
    public abstract String toString();  // force subclass implement toString
}
