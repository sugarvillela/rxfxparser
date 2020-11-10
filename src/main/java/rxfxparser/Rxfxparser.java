package rxfxparser;

import codegen.genjava.ClassJava;
import commons.Commons;
import compile.basics.CompileInitializer;
import demos.Codegen_;

/**Code generator and translator for RXFX language
 *
 * @author Dave Swanson
 */
public class Rxfxparser {

    public static void main(String[] args) {
        System.out.println("Running...");
        //Commons.disp(Commons.randomContent(10));
        //demos.NameGen_.importTable();
        //Codegen_.widget_java();
        //FormatUtil.demo();


        CompileInitializer.getInstance().init(new String[]{"semantic1", "-p"});//
        //store.TestStore.listTable();
        //uq.UqTest.uqBoolGenNewRow();
        //commons.BIT.binStrToInt("0000_1111_0000_1111_0000_1111_0000_1111");
    }
    
}
