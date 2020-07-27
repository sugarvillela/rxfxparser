package parse.interfaces;

/**Use for event-based method calls
 * @author Dave Swanson */
public interface ILifeCycle {
    // Event-based
    public void onCreate(); // call if object exists before push
    public void onPush();   // call immediately after push
    public void onPop();    // call just before pop
    public void onQuit();   // call when program/task is finished 
}
