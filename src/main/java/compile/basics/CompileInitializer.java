package compile.basics;

import codegen.Widget;
import commons.Dev;
import compile.scan.Class_Scanner;
import compile.scan.factories.Factory_ScanItem;
import compile.symboltable.SymbolTable;
import compile.symboltable.TextSniffer;
import erlog.Erlog;
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
        parseOnly = false;
        wrow = 5;
        wcol = 3;
        wval = 4;
    }

    private final ArrayList<ChangeListener> listeners;
    private final Erlog er;
    //private final Unique unique;
    private Base_Stack currStack, pausedStack;
    private ITextStatus pausedStatusReporter;
    private boolean newEnumSet, parseOnly; // arg flags
    private String inName, projName;
    private String initTime;
    private int wrow, wval, wcol;


    public void initFromProperties(String path){// TODO load from properties file

    }
    public void init(String[] args){
        if(args == null || args.length == 0){
            inName = "semantic1";
        }
        else{
            inName = (args[0].endsWith(SOURCE_FILE_EXTENSION))?
                    args[0].substring(0, args[0].length() - SOURCE_FILE_EXTENSION.length()) : args[0];
            if(args.length > 1){
                readArgs(args);
            }
        }
        projName = inName;

        System.out.printf("inName = %s, outName = %s, newEnuFile = %b \n", inName, projName, newEnumSet);

        this.addChangeListener(er);
        this.addChangeListener(Factory_Node.getInstance());
        this.addChangeListener(SymbolTable.getInstance());

        if(!parseOnly){
            TextSniffer.init();
            TextSniffer.getInstance().sleep();

            Factory_ScanItem.init();
            Factory_ScanItem.enterPreScanMode();

            Class_Scanner.init(
                    new TokenSource(
                            new TextSource_file(inName + SOURCE_FILE_EXTENSION)
                    )
            );
            Class_Scanner scanner = Class_Scanner.getInstance();
            setCurrParserStack(scanner);
            scanner.onCreate();

//            if(ListTable.getInstance() != null){
//                ListTable.getInstance().persist();
//            }
            System.out.println("Pre-Scan Complete");

            TextSniffer.getInstance().wake();
            Factory_ScanItem.enterScanMode();

            Class_Scanner.init(
                    new TokenSource(
                            new TextSource_file(inName + SOURCE_FILE_EXTENSION)
                    )
            );
            scanner = Class_Scanner.getInstance();
            setCurrParserStack(scanner);
            scanner.onCreate();

            scanner.onQuit();
            System.out.println("Scan Complete");

            SymbolTable.killInstance();
            TextSniffer.killInstance();
        }
        //ListTable listTable = ListTable.getInstance();
        //listTable.disp();
        //listTable.getNumGen().gen();
        //listTable.getNumGen().disp();
//
//        Class_Parser.init(
//            new ScanNodeSource(
//                new TextSource_file(this.inName + INTERIM_FILE_EXTENSION)
//            )
//        );
//        Class_Parser parser = Class_Parser.getInstance();
//        parser.onCreate();
        //Erlog.finish();
    }
    private void readArgs(String[] args){
        for(int i = 1; i < args.length; i++){
            switch(args[i]){
                case "-n": // lists in source file; don't use a stored rxlx list file
                    newEnumSet = true;
                    break;
                case "-p": // parse only
                    parseOnly = true;
                    break;
                default:
                    if(args[i].startsWith("-")){
                        er.set("Unknown argument", args[i]);
                    }
                    this.projName = args[i];
            }
        }
        if(newEnumSet && parseOnly){

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
    public void pauseCurrParserStack(Base_Stack tempStack, ITextStatus tempStatusReporter){
        pausedStack = currStack;
        currStack = tempStack;
        pausedStatusReporter = Erlog.getTextStatusReporter();
        Erlog.setTextStatusReporter(tempStatusReporter);
    }
    public void pauseCurrParserStack(Base_Stack tempStack){
        pausedStack = currStack;
        currStack = tempStack;
        pausedStatusReporter = null;
    }
    public void resumeCurrParserStack(){
        currStack = pausedStack;
        if(pausedStatusReporter != null){
            Erlog.setTextStatusReporter(pausedStatusReporter);
        }
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
