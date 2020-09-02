package compile.basics;

import codegen.Widget;
import commons.Dev;
import compile.parse.Class_Parser;
import compile.scan.Class_Scanner;
import compile.symboltable.SymbolTable_Enu;
import erlog.Erlog;
import toksource.ScanNodeSource;
import toksource.TextSource_file;
import toksource.TokenSource;
import unique.Unique;

import java.text.SimpleDateFormat;
import java.util.Date;

import static compile.basics.Keywords.INTERIM_FILE_EXTENSION;
import static compile.basics.Keywords.SOURCE_FILE_EXTENSION;

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
        unique = new Unique();
    }

    private Base_Stack currStack;
    private int wrow, wval;
    private String inName, projName;
    private String initTime;
    private final Unique unique;

    public void initFromProperties(String path){// TODO load from properties file
    
    }
    public void init(String inName, String projName){
        this.inName = inName;
        this.projName = projName;
        initTime = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(new Date());
        SymbolTable_Enu symbolTable_enu = SymbolTable_Enu.getInstance();
        symbolTable_enu.onCreate();
        symbolTable_enu.onQuit();
//        Class_Scanner.init(
//            new TokenSource(
//                new TextSource_file(inName + SOURCE_FILE_EXTENSION)
//            )
//        );
//        Class_Parser.init(
//            new ScanNodeSource(
//                new TextSource_file(this.inName + INTERIM_FILE_EXTENSION)
//            )
//        );
    }

    public String getInitTime(){ return this.initTime; }

    public void setWRow(int wrow){ this.wrow = wrow; }
    public int  getWRow(){ return wrow; }
    public void setWVal(int wval){ this.wval = wval; }
    public int  getWVal(){ return wval; }

    public String getInName(){
        return this.inName;
    }
    public void setProjName(String projName){ 
        System.out.println("CompileInitializer.setProjName: " + projName);
        this.projName = projName; 
    }
    public String getProjName(){ return this.projName; }

    public String genAnonName(Keywords.HANDLER type){
        String anon = String.format("Anon_%s_%s", type.toString(), unique.toString());
        return anon;
    }
    public void setCurrParserStack(Base_Stack currStack){
        this.currStack = currStack;
    }
    public Base_Stack getCurrParserStack(){
        return currStack;
    }
}
