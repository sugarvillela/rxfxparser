package runstate;

import codegen.Widget;
import langdef.Keywords;
import translators.list.ListJava;
import commons.Dev;
import compile.implstack.Base_Stack;
import compile.scan.Class_Scanner;
import compile.scan.factories.Factory_ScanItem;
import listtable.ListTable;
import erlog.Erlog;
import toksource.TextSource_file;
import toksource.TokenSource;
import toksource.interfaces.ChangeListener;
import toksource.interfaces.ChangeNotifier;
import toksource.interfaces.ITextStatus;

import java.io.File;
import java.util.ArrayList;

import static langdef.Keywords.SOURCE_FILE_EXTENSION;

/**
 *
 * @author Dave Swanson
 */
// TODO refactor this into smaller classes
// TODO rename LIST datatypes to FLAG
public class RunState implements ChangeListener {
    private static RunState instance;
    public static RunState getInstance(){
        return (instance == null)? (instance = new RunState()) : instance;
    }

    private final ArrayList<ChangeListener> listeners;
    private final Erlog er;
    private Base_Stack activeParserStack, pausedParserStack;
    private ITextStatus pausedTextStatusReporter;
    private boolean newListSet, scanOnly, parseOnly; // arg flags
    private String inName, projName;
    private String genPath, genPackage;

    private RunState(){
        Dev.dispOn();
        Erlog.initErlog(Erlog.DISRUPT|Erlog.USESYSOUT);
        Widget.setDefaultLanguage(Widget.JAVA);
        er = Erlog.get(this);
        listeners = new ArrayList<>();
        newListSet = false;
        parseOnly = false;
        //genPath = "C:\\Users\\daves\\OneDrive\\Documents\\GitHub\\SemanticAnalyzer\\src\\main\\java\\generated";//laptop
        genPath = "C:\\Users\\Dave Swanson\\OneDrive\\Documents\\GitHub\\SemanticAnalyzer\\src\\main\\java\\generated";//desktop
        genPackage = "generated";
        inName = "semantic1";
    }

    public void initFromProperties(String path){// TODO load from properties file

    }
    public void init(String[] args){
        if(args != null && args.length != 0){
            inName = (args[0].endsWith(SOURCE_FILE_EXTENSION))?
                    args[0].substring(0, args[0].length() - SOURCE_FILE_EXTENSION.length()) : args[0];
            if(args.length > 1){
                readArgs(args);
            }
        }
        projName = inName;

        System.out.printf("inName = %s, outName = %s, newEnuFile = %b \n", inName, projName, newListSet);

        this.addChangeListener(er);
        this.addChangeListener(Glob.SCAN_NODE_FACTORY);
        this.addChangeListener(Glob.SYMBOL_TABLE);
        if(!parseOnly){
            this.scan();
        }
//        if(!scanOnly){
//            this.parse();
//        }
        //Erlog.finish();
    }
    private void scan(){
        Glob.TEXT_SNIFFER.setStateSleep();

        Factory_ScanItem.init();
        Factory_ScanItem.enterPreScanMode();

        Class_Scanner.init(
                new TokenSource(
                        new TextSource_file(inName + SOURCE_FILE_EXTENSION)
                )
        );
        Class_Scanner scanner = Class_Scanner.getInstance();
        setActiveParserStack(scanner);
        scanner.readFile();

        if(!Glob.LIST_TABLE.isInitialized()){
            Glob.LIST_TABLE.initLists();
        }
        Glob.LIST_TABLE.persist();

        System.out.println("Pre-Scan Complete");

        Glob.TEXT_SNIFFER.setStateWake();
        Factory_ScanItem.enterScanMode();

        Class_Scanner.init(
                new TokenSource(
                        new TextSource_file(inName + SOURCE_FILE_EXTENSION)
                )
        );
        scanner = Class_Scanner.getInstance();
        setActiveParserStack(scanner);
        scanner.readFile();

        scanner.persist();
        System.out.println("Scan Complete");
    }
    private void parse(){
        System.out.println("ListTable build or rebuild");

        //Glob.LIST_TABLE.disp();
        Glob.LIST_TABLE.getNumGen().initCategoryNodes();
        //listTable.getNumGen().disp();
//
        ListJava listTranslator = new ListJava();
        listTranslator.translate();
//
//            System.out.println("Begin Parse");

//            Class_Parser.init(
//                    new ScanNodeSource(
//                            new TextSource_file(this.inName + INTERIM_FILE_EXTENSION)
//                    )
//            );
//            Class_Parser parser = Class_Parser.getInstance();
//            setCurrParserStack(parser);
//            parser.onCreate();
    }
    private void readArgs(String[] args){
        for(int i = 1; i < args.length; i++){
            switch(args[i]){
                case "-n": // lists in source file; don't use a stored rxlx list file
                    newListSet = true;
                    break;
                case "-p": // parse only
                    parseOnly = true;
                    break;
                case "-s": // parse only
                    scanOnly = true;
                    break;
                default:
                    if(args[i].startsWith("-")){
                        er.set("Unknown argument", args[i]);
                    }
                    this.projName = args[i];
            }
        }

        if(parseOnly){
            if(newListSet){
                er.set("-n with -p: list tables will not be created");
            }
            if(scanOnly){
                er.set("-s with -p: nothing will happen");
            }
        }
    }

    public String getInName(){
        return (inName == null)? Keywords.DEFAULT_PROJ_NAME : inName;
    }

    public void setProjName(String projName){
        this.projName = projName; 
    }
    public String getProjName(){ return this.projName; }

    public String getGenPath(String... dirs){
        return (dirs == null || dirs.length == 0)?
                genPath :
                genPath  + File.separator + String.join(File.separator, dirs);
    }
    public String getGenPackage(String... dirs){
        return (dirs == null || dirs.length == 0)?
                genPackage :
                genPackage  + "." + String.join(".", dirs);
    }

//    public String genAnonName(Keywords.DATATYPE type){
//        String anon = String.format("Anon_%s_%s", type.toString(), unique.toString());
//        return anon;
//    }
    public void setActiveParserStack(Base_Stack currStack){
        this.activeParserStack = currStack;
    }
    public Base_Stack getActiveParserStack(){
        return activeParserStack;
    }
    public void pauseActiveParserStack(Base_Stack newActiveParserStack, ITextStatus newTextStatusReporter){
        pausedParserStack = activeParserStack;
        activeParserStack = newActiveParserStack;
        pausedTextStatusReporter = Erlog.getTextStatusReporter();
        Erlog.setTextStatusReporter(newTextStatusReporter);
    }
    public void pauseActiveParserStack(Base_Stack tempStack){
        pausedParserStack = activeParserStack;
        activeParserStack = tempStack;
        pausedTextStatusReporter = null;
    }
    public void resumeCurrParserStack(){
        activeParserStack = pausedParserStack;
        if(pausedTextStatusReporter != null){
            Erlog.setTextStatusReporter(pausedTextStatusReporter);
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

    public void setNewListSet(boolean newListSet){
        this.newListSet = newListSet;
    }
    public boolean isNewListSet(){
        return newListSet;
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
