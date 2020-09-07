package toksource.interfaces;

import interfaces.ILifeCycle;

/**Exposes status indicator for error reporting
 * @author Dave Swanson
 */
public interface ITextStatus{
    int getRow();
    int getCol();
    String readableStatus();
    boolean isEndLine();
}
