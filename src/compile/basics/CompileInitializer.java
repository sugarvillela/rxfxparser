package compile.basics;

import codegen.Widget;
import commons.Dev;
import compile.parse.Class_Parser;
import compile.scan.Class_Scanner;
import erlog.Erlog;

/**
 *
 * @author Dave Swanson
 */
public class CompileInitializer {
    private static CompileInitializer instance;
    
    public static CompileInitializer getInstance(){
        return (instance == null)?
                (instance = new CompileInitializer()) : instance;
    }
    private CompileInitializer(){
        wrow = 8;
        wval = 4;
        Dev.dispOn();
        Widget.setDefaultLanguage(Widget.PHP);
        Erlog.initErlog(Erlog.DISRUPT|Erlog.USESYSOUT);
    }
    
    private int wrow, wval;
    private String inName, projName;
    public void initFromProperties(String path){// TODO load from properties file
    
    }
    public void init(String inName, String projName){
        this.inName = inName;
        this.projName = projName;
        Class_Scanner.init(this.inName, this.inName);
        Class_Parser.init(this.inName, this.projName);
    }
    public void setWRow(int wrow){ this.wrow = wrow; }
    public int  getWRow(){ return wrow; }
    public void setWVal(int wval){ this.wval = wval; }
    public int  getWVal(){ return wval; }
    
    public void setProjName(String projName){ 
        System.out.println("CompileInitializer.setProjName: " + projName);
        this.projName = projName; 
    }
    public String getProjName(){ return this.projName; }
    
}
