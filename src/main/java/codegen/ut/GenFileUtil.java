package codegen.ut;

import compile.basics.CompileInitializer;
import genobj.GenObjAdaptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GenFileUtil {

    public boolean persist(String name, ArrayList<String> content){
        return persist(name, content, null);
    }
    public boolean persist(String name, ArrayList<String> content, String comment){
        try(
            BufferedWriter file = new BufferedWriter(new FileWriter(PathUtil.getInstance().fixPath(name)))
        ){
            file.write("# Generated file, do not edit");
            file.newLine();
//            file.write("# Last write: " + CompileInitializer.getInstance().getInitTime());
//            file.newLine();
            if(comment != null){
                file.write("# " + comment);
                file.newLine();
            }

            for (String line: content) {
                file.write(line);
                file.newLine();
            }
            file.close();
            return true;
        }
        catch(IOException e){
            return false;
        }
    }

}
