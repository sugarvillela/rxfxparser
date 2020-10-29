
package compile.basics;

import compile.basics.Base_StackItem;

/**Stack or stack item
 * @author Dave Swanson */
public interface IStackComponent {
    void push( Base_StackItem nuTop );
    void pop();
    Base_StackItem getTop();
    void setTop(Base_StackItem nuTop);
    int getStackSize();
    void disp();
    String getDebugName();
}
