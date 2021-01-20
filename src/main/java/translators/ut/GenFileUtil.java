package translators.ut;

import runstate.RunState;
import runstate.Glob;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GenFileUtil {

    public boolean persist(ArrayList<String> content, String... subpath){
        //Commons.disp(content);
        String path = RunState.getInstance().getGenPath(subpath);
        System.out.println("GenFileUtil: path = " + path);
        try(
            BufferedWriter file = new BufferedWriter(new FileWriter(path))
        ){
            file.write("// Generated file, do not edit");
            file.newLine();
            file.write("// Last write: " + Glob.TIME_INIT);
            file.newLine();

            for (String line: content) {
                file.write(line);
                file.newLine();
            }
            file.close();
            return true;
        }
        catch(IOException e){
            System.out.println("GenFileUtil: exception = " + e);
            System.exit(-1);
        }
        return false;
    }

}
