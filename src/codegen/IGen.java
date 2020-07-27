package codegen;

import java.util.ArrayList;
import parse.interfaces.IParse;

/**For RXFX-specific code generation using Widget
 * Need implementation for each target language
 * Method names reflect the name of the method or class being generated
 * @author newAdmin
 */
public interface IGen {
    // Generates ENUB and ENUD
    public void ENU_onCreate(Widget W, String name);
    public void ENU_add( Widget W, String varName, int varVal );
    public void ENU_getGroupName_(Widget W, ArrayList<IParse.GroupNode> Groups);
    public void ENU_onQuit(Widget W);
}
