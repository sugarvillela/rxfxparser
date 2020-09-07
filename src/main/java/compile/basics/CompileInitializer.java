package compile.basics;

import codegen.Widget;
import commons.Dev;
import compile.scan.Class_Scanner;
import compile.scan.PreScanner;
import compile.symboltable.SymbolTable_Enu;
import erlog.Erlog;
import toksource.ScanNodeSource;
import toksource.TextSource_file;
import toksource.TokenSource;
import unique.Unique;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        Dev.dispOn();
        Erlog.initErlog(Erlog.DISRUPT|Erlog.USESYSOUT);
        Widget.setDefaultLanguage(Widget.PHP);
        initTime = (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")).format(new Date());
        unique = new Unique();
        er = Erlog.get(this);
        newEnumSet = false;
        wrow = 8;
        wval = 4;
    }

    private final Erlog er;
    private final Unique unique;
    private Base_Stack currStack;
    private boolean newEnumSet;
    private String inName, projName;
    private String initTime;
    private int wrow, wval;

    public void initFromProperties(String path){// TODO load from properties file

    }
    public void init(String[] args){
        inName = (args.length > 0)? args[0] : "semantic";
        if(inName.endsWith(SOURCE_FILE_EXTENSION)){
            inName = inName.substring(0, inName.length() - SOURCE_FILE_EXTENSION.length());
            System.out.println(SOURCE_FILE_EXTENSION + " extension not needed.");
        }

        projName = inName;
        if(args.length > 1){
            readArgs(args);
        }
        System.out.printf("inName = %s, outName = %s, newEnuFile = %b \n", inName, projName, newEnumSet);

        PreScanner.init(
            new TokenSource(
                new TextSource_file(inName + SOURCE_FILE_EXTENSION)
            )
        );
        PreScanner preScanner = PreScanner.getInstance();
        preScanner.onCreate();
        Class_Scanner.init(
            new TokenSource(
                new TextSource_file(inName + SOURCE_FILE_EXTENSION)
            )
        );
        Class_Scanner scanner = Class_Scanner.getInstance();
        scanner.onCreate();
        scanner.onQuit();
//        Class_Parser.init(
//            new ScanNodeSource(
//                new TextSource_file(this.inName + INTERIM_FILE_EXTENSION)
//            )
//        );
    }
    private void readArgs(String[] args){
        for(int i = 1; i < args.length; i++){
            switch(args[i]){
                case "-n": // start with new
                    newEnumSet = true;
                    break;
                default:
                    if(args[i].startsWith("-")){
                        er.set("Unknown argument", args[i]);
                    }
                    this.projName = args[i];
            }
        }
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

    public void setNewEnumSet(boolean newEnumSet){
        this.newEnumSet = newEnumSet;
    }
    public boolean isNewEnumSet(){
        return newEnumSet;
    }
    private void deleteMe(){
        SymbolTable_Enu.init(null);
        SymbolTable_Enu enu = SymbolTable_Enu.getInstance();
        ArrayList<Factory_Node.ScanNode> nodes = new ArrayList<>();
        nodes.add(Factory_Node.newScanNode("Line 16 Word 0,PUSH,ENUB,NULL,NULL"));
        nodes.add(Factory_Node.newScanNode("Line 17 Word 0,SET_ATTRIB,ENUB,DEF_NAME,POS"));
        nodes.add(Factory_Node.newScanNode("Line 18 Word 0,ADD_TO,ENUB,NULL,verb"));
        nodes.add(Factory_Node.newScanNode("Line 21 Word 0,POP,ENUB,NULL,NULL"));
        enu.readList(nodes);
        enu.onQuit();
    }
}
