package compile.basics;

import codegen.Widget;
import commons.Dev;
import compile.scan.Class_Scanner;
import compile.scan.PreScanner;
import compile.symboltable.ListTable;
import compile.symboltable.SymbolTable;
import erlog.Erlog;
import toksource.ScanNodeSource;
import toksource.TextSource_file;
import toksource.TokenSource;
import toksource.interfaces.ChangeListener;
import toksource.interfaces.ChangeNotifier;
import toksource.interfaces.ITextStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static compile.basics.Keywords.SOURCE_FILE_EXTENSION;

/**
 *
 * @author Dave Swanson
 */
public class CompileInitializer implements ChangeListener {
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
        //unique = new Unique();
        er = Erlog.get(this);
        listeners = new ArrayList<>();
        newEnumSet = false;
        wrow = 5;
        wcol = 3;
        wval = 4;
    }

    private final ArrayList<ChangeListener> listeners;
    private final Erlog er;
    //private final Unique unique;
    private Base_Stack currStack;
    private boolean newEnumSet;
    private String inName, projName;
    private String initTime;
    private int wrow, wval, wcol;


    public void initFromProperties(String path){// TODO load from properties file

    }
    public void init(String[] args){
        inName = (args == null || args.length == 0)?  "semantic1" : args[0];
        if(inName.endsWith(SOURCE_FILE_EXTENSION)){
            inName = inName.substring(0, inName.length() - SOURCE_FILE_EXTENSION.length());
            System.out.println(SOURCE_FILE_EXTENSION + " extension not needed.");
        }

        projName = inName;
        if(args.length > 1){
            readArgs(args);
        }
        System.out.printf("inName = %s, outName = %s, newEnuFile = %b \n", inName, projName, newEnumSet);

        this.addChangeListener(er);
        this.addChangeListener(Factory_Node.getInstance());
        this.addChangeListener(SymbolTable.getInstance());

//        PreScanner.init(
//            new TokenSource(
//                new TextSource_file(inName + SOURCE_FILE_EXTENSION)
//            )
//        );
//        PreScanner preScanner = PreScanner.getInstance();
//        preScanner.onCreate();
//        //System.out.println("\nConstantTable:");
//        //System.out.println(ConstantTable.getInstance());
//        //Factory_TextNode.getInstance().testItr();
//        Class_Scanner.init(
//            new TokenSource(
//                new TextSource_file(inName + SOURCE_FILE_EXTENSION)
//            )
//        );
//        Class_Scanner scanner = Class_Scanner.getInstance();
//        scanner.onCreate();
//        if(ListTable.getInstance() != null){
//            ListTable.getInstance().persist();
//        }
//        scanner.onQuit();
//        System.out.println("Scan Complete");
//
//        SymbolTable.killInstance();
//        TextSniffer.killInstance();
//
//        Class_Parser.init(
//            new ScanNodeSource(
//                new TextSource_file(this.inName + INTERIM_FILE_EXTENSION)
//            )
//        );
//        Class_Parser parser = Class_Parser.getInstance();
//        parser.onCreate();
//        Erlog.finish();
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
    public void setWCol(int wcol){ this.wcol = wcol; }
    public int  getWCol(){ return wcol; }
    public void setWVal(int wval){ this.wval = wval; }
    public int  getWVal(){ return wval; }

    public boolean fitToWVal(String numeric){// validate numeric before calling here
        int fit = (int)Math.pow(2, wval);
        return Integer.parseInt(numeric) < fit;
    }

    public String getInName(){
        return this.inName;
    }
    public void setProjName(String projName){ 
        //System.out.println("CompileInitializer.setProjName: " + projName);
        this.projName = projName; 
    }
    public String getProjName(){ return this.projName; }

//    public String genAnonName(Keywords.DATATYPE type){
//        String anon = String.format("Anon_%s_%s", type.toString(), unique.toString());
//        return anon;
//    }
    public void setCurrParserStack(Base_Stack currStack){
        this.currStack = currStack;
    }
    public Base_Stack getCurrParserStack(){
        return currStack;
    }

    public void addChangeListener(ChangeListener listener){
        listeners.add(listener);
    }
    public void removeChangeListener(ChangeListener listener){
        listeners.remove(listener);
    }
    @Override
    public void onTextSourceChange(ITextStatus textStatus, ChangeNotifier caller){
        for(ChangeListener listener : listeners){
            if(listener != null){
                listener.onTextSourceChange(textStatus, caller);
            }
        }
    }
    public void setNewEnumSet(boolean newEnumSet){
        this.newEnumSet = newEnumSet;
    }
    public boolean isNewEnumSet(){
        return newEnumSet;
    }

    private void deleteMe(){
//        ListTable.init(null);
//        ListTable listTable = ListTable.getInstance();
//        ArrayList<Factory_Node.ScanNode> nodes = new ArrayList<>();
//        nodes.add(Factory_Node.newScanNode("Line 16 Word 0,PUSH,ENUB,NULL,NULL"));
//        nodes.add(Factory_Node.newScanNode("Line 17 Word 0,SET_ATTRIB,ENUB,DEF_NAME,POS"));
//        nodes.add(Factory_Node.newScanNode("Line 18 Word 0,ADD_TO,ENUB,NULL,verb"));
//        nodes.add(Factory_Node.newScanNode("Line 21 Word 0,POP,ENUB,NULL,NULL"));
//        listTable.readList(nodes);
//        listTable.onQuit();
    }
}
