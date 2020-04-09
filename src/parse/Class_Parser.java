/*
 */
package parse;

import parse.Keywords.HANDLER;
import parse.Keywords.CMD;

import codegen.*;
import toksource.TokenSourceImpl;
import toksource.StringSource_list;
import java.util.ArrayList;
import java.util.Iterator;
import toksource.TokenSource;
import unique.*;

/**
 *
 * @author Dave Swanson
 */
public class Class_Parser extends Base_Stack {
    
//    public Handler top;     // stack; handlers are linked nodes
//    public int stackSize;       // changes on push, pop
//    public Stringsource_file fin;        // file to be parsed
    public Unique uq;           // unique number/string generator
    private Widget[] widgets;   // output generator
    private Base_Gen[] handlers;
    private Widget baseWidget;
//    private String title;       // outFile name = title_handler.extension
    
    public int wrow, wval;      // for itr_struct bitpack
    public int initRow;         //?
//    private final Erlog er;     // logs, notifies, quits or all 3
//    private String backText;    // repeat lines
    
    private static Class_Parser staticInstance;
    
    public Class_Parser(){
        generalInit();
    }
    private Class_Parser(String filename){
        setFile(filename, "rxlx");
        fin = new TokenSourceImpl( filename );
        if( !fin.hasData() ){
            er.set( "Bad input file name: "+filename );
            return;
        }
        generalInit();
    }
    private Class_Parser(ArrayList<Object> setContent){
        fin = new StringSource_list( setContent );
//        if( !fin.hasData() ){
//            er.set( "Bad input content" );
//            return;
//        }
        generalInit();
    }
    private void generalInit(){
        widgets = new Widget[HANDLER.NUM_HANDLERS.ordinal()]; //TODO remove
        uq = new Unique();                          //TODO remove
        // defaults
        handlers = new Base_Gen[HANDLER.NUM_HANDLERS.ordinal()];
        
        wrow = 8;
        wval = 4;
    }
    
    // Singleton pattern
    public static Class_Parser getInstance(){
        return staticInstance;
    }
    public static Class_Parser getInstance( String filename ){
        return (staticInstance = new Class_Parser(filename));
    }
    public static void killInstance(){
        staticInstance = null;
    }
    
    public Base_Gen getGen(ScanNode s){
        int i = s.h.ordinal();
        if(handlers[i] != null){
            return handlers[i];
        }
        switch(s.h){
            case TARGLANG_BASE:
                baseWidget = Widget.getNewWidget();
                return ( handlers[i] = new Gen_targetLang(baseWidget));
            case ENUB:
                return ( handlers[i] = new Gen_ENUB( 
                        Widget.getNewWidget(), 
                        new Uq_enumgen( wrow ), 
                        "ENUB"
                ));
            case ENUD:
                return ( handlers[i] = new Gen_ENUD( 
                        Widget.getNewWidget(), 
                        new Uq_enumgen( wrow, wval ), 
                        "ENUD"
                ));
            case SCOPE:
                return ( handlers[i] = new Gen_targetLang(Widget.getNewWidget()));
            case RX:
                return ( handlers[i] = new Gen_targetLang(Widget.getNewWidget()));
            case FX:
                return ( handlers[i] = new Gen_targetLang(Widget.getNewWidget()));
            case SRCLANG:
                return ( handlers[i] = new Gen_targetLang(baseWidget));
            case ATTRIB:
                return ( handlers[i] = new Gen_targetLang(Widget.getNewWidget()));
            case USERDEF:
                return new Gen_USERDEF(s.data);//don't save the instance
            default:
                setEr("rxlx file contains unknown handler name: "+s.cmd);
                return null;//fail loudly
        }
    }
    public void doCMD(ScanNode s){
        CMD cmd = s.cmd;
        switch (cmd){
            case ADD_TO:
                top.add(s.data);
                break;
            case PUSH:
                push( getGen(s) );
                break;
            case POP:
                pop();
                break;
            default:
                setEr("rxlx file contains unknown command: "+s.cmd);
                break;
        }
    }
    // widgets is a store for any widgets made by handlers
    // widgets[0] is the default writer
    public Widget getWidget(HANDLER h){//TODO remove
        int i = h.ordinal();
        if(widgets[i] == null){
            widgets[i]=Widget.getNewWidget();
        }
        return widgets[i];
    }
    public void setAttrib( String key, String val ){
        switch (key){
            case "title":
                title = val;
                if(Boolean.parseBoolean(val)){
                    //onFinish();
                }
                break;
            case "done":
                if(Boolean.parseBoolean(val)){
                    //onFinish();
                }
                break;
            case "wrow":
                wrow = Integer.parseInt(val);
                break;
            case "wval":
                wval = Integer.parseInt(val);
                break;
            case "initRow":
                initRow = Integer.parseInt(val);
                break;
            default:
                setEr("Parser.setAttrib: unknown attrib: " + key );
        }
    }
    public void setAttrib( String key, int val ){
        switch (key){
            case "initRow":
                initRow = val;
                break;
            default:
                setEr("Parser.setAttrib: unknown attrib: " + key );
        }
    }
    public String getAttrib_str( String key ){
        return "";
    }
    public int getAttrib_int( String key ){//
        switch (key){
            case "initRow":
                return initRow;
            default:
                setEr("Parser.getAttrib_int: unknown attrib: " + key );
                return 0;
        }
    }

    @Override
    public void onPush(){
//        String next;
//        backText = null;
//        // start with a base language handler
//        fin.setLineGetter();
//        push( new Handler_targetLang(title) );
//       
//        while( fin.hasNext() ){
//            if( backText == null ){
//                next = fin.next();
//            }
//            else{
//                next = backText;
//                backText = null;
//            }
//            if( next.isEmpty() && stackSize>1  ){
//                //System.out.println( "=====EMPTY TEXT======"+top.name );
//                continue;
//            }
//            //System.out.println( next ); 
//            top.pushPop( next );
//        }
//        finish();
    }
    @Override
    public void onQuit(){
        //System.out.println( "parser onQuit" ); 
        String wErr;
        for(Widget w : widgets ){
            if( w !=null && ( wErr = w.finish() )!= null ){
                er.set( wErr ); 
            }
        }
    }
    
    public abstract class Base_Gen extends Base_StackItem {
        protected Widget W;
        protected IGen rxfx;
        public Base_Gen(){
            P = Class_Parser.getInstance();
            rxfx = baseWidget.getSpecializedCodeGenerator();
        }
        @Override
        public void pushPop(String text) {}
        @Override
        public void setAttrib(String key, Object value){
            if(below != null){
                below.setAttrib(key, value);
            }
        }
        @Override
        public abstract void add( Object obj );
        
        @Override
        public ArrayList<ScanNode> getScanNodeList(){
            return null;
//            if(nodes==null){
//                commons.Erlog.getInstance().set(
//                    "Developer: scan node list not initialized in "+this.getClass().getSimpleName()
//                );
//            }
//            return nodes;
        }
        @Override
        public Base_StackItem getTop(){
            return P.getTop();
        }
        @Override
        public TokenSource getTokenSource(){
            if(fin==null){
                commons.Erlog.getInstance().set(
                    "Developer: StringSource not initialized in "+this.getClass().getSimpleName()
                );
            }
            return fin;
        }
    }
    public class Gen_targetLang extends Base_Gen{
        public Gen_targetLang( Widget setW ){
            W = setW;
        }
        @Override
        public void add( Object obj ) {
            W.add((String)obj);
        }
    }
    public class Gen_sourceLang extends Base_Gen{
        public Gen_sourceLang( Widget setW ){
            W = setW;
        }
        @Override
        public void add( Object obj ) {}
    }
    public class Gen_ENUB extends Base_Gen{
        protected Uq_enumgen uq;
        protected Iterator<Integer> itr;
        protected ArrayList<GroupNode> groups;    // Accumulate group names
        protected int iGroup, curr;
        
        public Gen_ENUB( Widget setW, Uq_enumgen setUQ, String setClassName ){
            W = setW;                       //  Set up file writer
            W.setFileName(setClassName);
            rxfx.ENU_onCreate(W, setClassName);// Write opening code
            uq = setUQ;                     //  Set up number generator
            itr = uq.iterator();            //  uq needs an iterator
            groups = new ArrayList<>();     //  to hold names, start, end range
            iGroup = -1;                    //  destination index
            curr = itr.next();              //  have first number ready
        }
        @Override
        public void setAttrib(String key, Object value){
            switch(key){
                case "DEF_NAME":  // called on USERDEF push to set name
                    iGroup++;
                    groups.add( new GroupNode( (String)value, curr ) );
                    break;
            }
        }
        @Override
        public void add( Object obj ) {
            rxfx.ENU_add( W, (String)obj, curr );
            this.groups.get( iGroup ).e = curr;
            curr = itr.next();
        }
        @Override
        public void onQuit() {
            rxfx.ENU_getGroupName_(W, groups);
            rxfx.ENU_onQuit(W);
        }
    }
    public class Gen_ENUD extends Gen_ENUB{
        public Gen_ENUD(  Widget setW, Uq_enumgen setUQ, String setClassName  ){
            super( setW, setUQ, setClassName );
        }
        @Override
        public void setAttrib(String key, Object value){
            switch(key){
                case "DEF_NAME":
                    if(iGroup != -1){
                        uq.newCol();
                        this.groups.get( iGroup ).e = curr;
                        curr = itr.next();
                    }
                    iGroup++;
                    groups.add( new GroupNode( (String)value, curr ) );
                    break;
            }
        }
    }
    public class Gen_USERDEF extends Base_Gen{
        private final String defName;
        public Gen_USERDEF( String setName ){
            defName = setName;
        }
        @Override
        public void onPush(){
            below.setAttrib("DEF_NAME", defName);
        }
        @Override
        public void add( Object obj ) {
            below.add(obj);
        }
    }
    public class Gen_ATTR extends Base_Gen{
        @Override
        public void add( Object obj ) {
            String[] tok = ((String)obj).split("=");
            this.below.setAttrib( tok[0], tok[1] );
        }
    }
    
    
    public void test_Gen_ENUB(){
        getGen(new ScanNode(CMD.PUSH, HANDLER.TARGLANG_BASE));
        Gen_ENUB g = (Gen_ENUB)getGen(new ScanNode(CMD.PUSH, HANDLER.ENUB));
        Gen_USERDEF u = (Gen_USERDEF)getGen(new ScanNode(CMD.PUSH, HANDLER.USERDEF, "Wowee"));
        u.below = g;
        u.onPush();
        u.add("donkey");
        u.add("fritter");
        u.add("incompetent");
        Gen_USERDEF u2 = (Gen_USERDEF)getGen(new ScanNode(CMD.PUSH, HANDLER.USERDEF, "Fantastique"));
        u2.below = g;
        u2.onPush();
        u2.add("jeepers");
        u2.add("awesome");
        u2.add("coom");
        g.onQuit();
    }
    public void test_Gen_ENUD(){
        getGen(new ScanNode(CMD.PUSH, HANDLER.TARGLANG_BASE));
        Gen_ENUD g = (Gen_ENUD)getGen(new ScanNode(CMD.PUSH, HANDLER.ENUD));
        g.onCreate();
        Gen_USERDEF u = (Gen_USERDEF)getGen(new ScanNode(CMD.PUSH, HANDLER.USERDEF, "Wowee"));
        u.below = g;
        u.onPush();
        u.add("donkey");
        u.add("fritter");
        u.add("incompetent");
        Gen_USERDEF u2 = (Gen_USERDEF)getGen(new ScanNode(CMD.PUSH, HANDLER.USERDEF, "Fantastique"));
        u2.below = g;
        u2.onPush();
        u2.add("jeepers");
        u2.add("awesome");
        u2.add("coom");
        g.onQuit();
    }
}
