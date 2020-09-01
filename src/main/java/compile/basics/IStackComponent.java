
package compile.basics;

import compile.basics.Base_StackItem;

/**Stack or stack item
 * @author Dave Swanson */
public interface IStackComponent {
    public void push( Base_StackItem nuTop );
    public void pop();
    public Base_StackItem getTop();
    public int getStackSize();
    public void disp();
    public String getDebugName();
}
