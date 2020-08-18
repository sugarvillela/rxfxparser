package toksource;

import toksource.interfaces.ITextSource;

/**Placeholder class
 * @author Dave Swanson */
public class NullSource implements ITextSource{

    @Override
    public void rewind() {}

    @Override
    public String next() {
        return "";
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean hasData() {
        return false;
    }

    @Override
    public int getRow() {
        return -1;
    }

    @Override
    public int getCol() {
        return -1;
    }

    @Override
    public String readableStatus() {
        return "";
    }

    @Override
    public void onCreate() {}

    @Override
    public void onPush() {}
    @Override
    public void onBeginStep(){}
    @Override
    public void onEndStep(){}
    
    @Override
    public void onPop() {}

    @Override
    public void onQuit() {}

    @Override
    public void setLineGetter() {}

    @Override
    public void setWordGetter() {}

    @Override
    public boolean isLineGetter() {
        return true;
    }

    @Override
    public boolean isWordGetter() {
        return false;
    }

    @Override
    public boolean isEndLine() {
        return false;
    }
    
}
