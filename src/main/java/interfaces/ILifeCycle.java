package interfaces;

/**Use for event-based method calls
 * @author Dave Swanson */
public interface ILifeCycle {
    // Event-based
    public void onCreate(); // call if object exists before push
    public void onPush();   // call immediately after push/activate
    public void onBeginStep();
    public void onEndStep();   // call to terminate an intermediate step
    public void onPop();    // call just before pop/deactivate
    public void onQuit();   // call when program/task is finished 
}
