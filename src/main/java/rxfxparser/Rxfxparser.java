package rxfxparser;

import demos.*;
import compile.basics.CompileInitializer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Code generator and translator for RXFX language
 *
 * @author Dave Swanson
 */
public class Rxfxparser {

    public static void main(String[] args) {
        System.out.println("Running...");

        //CompileInitializer.getInstance().init(args);
        uq.UqTest.uqDiscreteGen();
    }
    
}
