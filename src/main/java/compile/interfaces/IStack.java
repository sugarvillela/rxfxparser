
package compile.interfaces;

import compile.implitem.Base_StackItem;

/**Stack or stack item
 * @author Dave Swanson */
public interface IStack {
    void push( Base_StackItem nuTop );
    void pop();
    Base_StackItem getTop();
    void setTop(Base_StackItem nuTop);
    int getStackSize();

    void readFile(); // call if object exists before push
    void persist();   // call when program/task is finished
}
