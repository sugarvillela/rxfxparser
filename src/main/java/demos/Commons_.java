/*
 */
package demos;

import commons.*;
import java.util.ArrayList;

/**
 *
 * @author newAdmin
 */
public class Commons_ {
    public static void trim(){
        String text = "___Trim_me__";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim( '_' ,text ));
        
        System.out.println( "==================================================" );
        text = "_____";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim( '_' ,text ));
        
        System.out.println( "==================================================" );
        text = "____a_";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim( '_' ,text ));
        
        System.out.println( "==================================================" );
        text = "_a";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim( '_' ,text ));
        
        System.out.println( "==================================================" );
        text = "a_";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim( '_' ,text ));        
        
        System.out.println( "==================================================" );
        text = "";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim( '_' ,text ));
        
        System.out.println( "==================================================" );
        text = "(['Trim_me'])";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim("(['])" ,text));
        
        System.out.println( "==================================================" );
        text = "(['a'])";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim("(['])" ,text));
        
        System.out.println( "==================================================" );
        text = "";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim("(['])" ,text));

        System.out.println( "==================================================" );
        text = "_a";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim( "_" ,text ));
        
        System.out.println( "==================================================" );
        text = "a_";
        System.out.println( "Input: " + text );
        System.out.println( "Output: " + Util_string.trim( "_" ,text ));  
        
        System.out.println( "==================================================" );
        String[] arr = new String[]{"(['a'])","","[]a", "([''])"};
        Commons.disp(arr, "Input: ");
        Commons.disp(Util_string.trim( "(['])" ,arr ), "Output: ");
        
        System.out.println( "==================================================" );
        ArrayList<String> list = new ArrayList<>();
        list.add("(['a'])");
        list.add("");
        list.add("[]a");
        list.add("([''])");
        Commons.disp(list, "Input: ");
        Commons.disp(Util_string.trim( "(['])" ,list ), "Output: ");
    }
    public static void caseChanges(){
        String[] strings = {
            "garbage_in_garbage_out",
            "muffin",
            "MuchoPoopy",
            "CAPTAINKirk_hAS_HaIr",
            "ENUM_VALUE_NON_INTEGER",
            "myValue_int",
            "Many____UnderscoresMake_Trouble"
        };
//        System.out.println("\nToPascal");
//        for(String str : strings){
//            //System.out.printf("%s: is all cap: %b\n", str, Util_string.isAllCap(str));
//            System.out.printf("%s: %s\n", str, Util_string.toPascalCase(str));
//        }
//        System.out.println("\nToCamel");
//        for(String str : strings){
//            System.out.printf("%s: %s\n", str, Util_string.toCamelCase(str));
//        }
        System.out.println("\nToSnake");
        for(String str : strings){
            //Util_string.toSnakeCase(str);
            System.out.printf("%s: %s\n", str, Util_string.toSnakeCase(str));
        }
    }
    public static void assertFileExt(){
        // case 1 Filename already done
        String f = "myFile.java";
        String ext = ".java";
        System.out.printf("f=%s, ext=%s, result=%s\n", f, ext, Commons.assertFileExt(f, ext));
        // case 2 Correct input
        f = "myFile";
        ext = "java";
        System.out.printf("f=%s, ext=%s, result=%s\n", f, ext, Commons.assertFileExt(f, ext));
        // case 3 One wrong
        f = "myFile";
        ext = ".java";
        System.out.printf("f=%s, ext=%s, result=%s\n", f, ext, Commons.assertFileExt(f, ext));
        // case 4 Both wrong
        f = "myFile.";
        ext = ".java";
        System.out.printf("f=%s, ext=%s, result=%s\n", f, ext, Commons.assertFileExt(f, ext));
        // case 5 try to spoof 'endsWith()
        f = "myjava";
        ext = "java";
        System.out.printf("f=%s, ext=%s, result=%s\n", f, ext, Commons.assertFileExt(f, ext));
        // case 6 Fixable, but why bother?
        f = "myFile.java.java";
        ext = ".java";
        System.out.printf("f=%s, ext=%s, result=%s\n", f, ext, Commons.assertFileExt(f, ext));
    }
}
