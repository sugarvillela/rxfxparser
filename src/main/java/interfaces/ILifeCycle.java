package interfaces;

/**Use for event-based method calls
 * @author Dave Swanson */
public interface ILifeCycle {
    // Event-based
    void onCreate(); // call if object exists before push
    void onPush();   // call immediately after push/activate
    void onPop();    // call just before pop/deactivate
    void onQuit();   // call when program/task is finished
}
