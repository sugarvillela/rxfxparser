package codegen;

import commons.Commons;
import java.util.ArrayList;
import compile.basics.Factory_Node.GroupNode;
/**
 *
 * @author Dave Swanson
 */
public class Gen_Java implements IGen{
    @Override
    public void ENU_onCreate( Widget W, String className ){
        W.class_( "abstract class " + className );  // open 
    }
    @Override
    public void ENU_add( Widget W, String varName, int varVal ){
        W.line( String.format("const %s = 0x0%X;", varName, varVal ) );
    }
    @Override
    public void ENU_getGroupName_(Widget W, ArrayList<GroupNode> groups){
            Commons.disp(groups);
            W.blank();
            W.function_("getGroupName", "group name", "$enum");
                for( GroupNode g : groups ){
                    W.if_( g.e + ">= $enum && $enum >= " + g.s );
                    W.line("return '"+g.n+"';");
                    W.close();
                }
                W.line("return '';");
            W.close(); // close function
    }

    @Override
    public void ENU_onQuit(Widget W){
            W.close();  // close class
            W.disp();
    }
}
