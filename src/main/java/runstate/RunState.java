package runstate;

import codegen.Widget;
import codegen.translators.list.ListJava;
import commons.Dev;
import codegen.namegen.NameGenSimple;
import compile.basics.Base_Stack;
import compile.basics.Factory_Node;
import compile.scan.Class_Scanner;
import compile.scan.factories.Factory_ScanItem;
import listtable.ListTable;
import compile.symboltable.SymbolTable;
import compile.symboltable.TextSniffer;
import erlog.Erlog;
import toksource.TextSource_file;
import toksource.TokenSource;
import toksource.interfaces.ChangeListener;
import toksource.interfaces.ChangeNotifier;
import toksource.interfaces.ITextStatus;

import java.io.File;
import java.util.ArrayList;

import static compile.basics.Keywords.SOURCE_FILE_EXTENSION;

/**
 *
 * @author Dave Swanson
 */
// TODO refactor this into smaller classes
// TODO rename LIST datatypes to FLAG
public class RunState implements ChangeListener {
    private static RunState instance;
    
    public static RunState getInstance(){
        return (instance == null)?
                (instance = new RunState()) : instance;
    }
    private RunState(){
        Dev.dispOn();
        Erlog.initErlog(Erlog.DISRUPT|Erlog.USESYSOUT);
        Widget.setDefaultLanguage(Widget.JAVA);
        er = Erlog.get(this);
        listeners = new ArrayList<>();
        newEnumSet = false;
        parseOnly = false;
        //genPath = "C:\\Users\\daves\\OneDrive\\Documents\\GitHub\\SemanticAnalyzer\\src\\main\\java\\generated";//laptop
        genPath = "C:\\Users\\Dave Swanson\\OneDrive\\Documents\\GitHub\\SemanticAnalyzer\\src\\main\\java\\generated";//desktop
        genPackage = "generated";
        staticState = StaticState.init();
    }

    private final StaticState staticState;
    private final ArrayList<ChangeListener> listeners;
    private final Erlog er;
    //private final Unique unique;
    private Base_Stack currStack, pausedStack;
    private ITextStatus pausedStatusReporter;
    private boolean newEnumSet, scanOnly, parseOnly; // arg flags
    private String inName, projName;
    private String genPath, genPackage;


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

            if(ListTable.getInstance() != null){
                ListTable.getInstance().persist();
            }

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
        if(!scanOnly){
            NameGenSimple.init(projName);
            System.out.println("ListTable build or rebuild");

            ListTable listTable = ListTable.getInstance();
            //listTable.disp();
            listTable.getNumGen().initCategoryNodes();
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
            if(newEnumSet){
                er.set("-n with -p: list tables will not be created");
            }
            if(scanOnly){
                er.set("-s with -p: nothing will happen");
            }
        }
    }

    public String getInName(){
        return this.inName;
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
