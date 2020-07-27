
package parse.interfaces;

/**
 *
 * @author Dave Swanson
 */
public interface IContext {
    // Fancy setters
    public void pushPop( String text );
    public void add(Object obj);                // enum CMD
    public void setAttrib(String key, Object value);// enum KEY
    //public Object getAttrib(String key);           // enum KEY 
}
