package toksource;

import static compile.basics.Keywords.STATUS_FORMAT;

import interfaces.ILifeCycle;
import toksource.interfaces.ITextSource;
import toksource.interfaces.ITextWordOrLine;

/**Placeholder class
 * @author Dave Swanson */
public class NullSource implements ITextSource, ITextWordOrLine, ILifeCycle {

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
        return String.format(STATUS_FORMAT, "", 0, 0);
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
