/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codegen;

import commons.Commons;
import java.util.ArrayList;
import parse.IParse;

/**
 *
 * @author newAdmin
 */
public class Gen_PHP implements IGen{
    @Override
    public void ENU_onCreate( Widget W, String className ){
        W.class_( "abstract class " + className );  // open 
    }
    @Override
    public void ENU_add( Widget W, String varName, int varVal ){
        W.line( String.format("const %s = 0x0%X;", varName, varVal ) );
    }
    @Override
    public void ENU_getGroupName_(Widget W, ArrayList<IParse.GroupNode> groups){
            Commons.disp(groups);
            W.blank();
            W.function_("getGroupName", "group name", "$enum");
                for( IParse.GroupNode g : groups ){
                    W.if_( 
                        String.format( "0x0%X >= $enum && $enum >= 0x0%X", g.e, g.s ) 
                    );
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
