package parse.ut;

import erlog.Erlog;
import java.util.Arrays;
import parse.Class_Scanner;
import parse.factories.Factory_Context;
import parse.Keywords;
import static parse.Keywords.COMMENT_TEXT;
import static parse.Keywords.HANDLER.TARGLANG_INSERT;
import static parse.Keywords.SOURCE_CLOSE;
import static parse.Keywords.TARGLANG_INSERT_CLOSE;
import static parse.Keywords.TARGLANG_INSERT_OPEN;
import static parse.Keywords.USERDEF_OPEN;

/**A hodgepodge of actions to make the handlers in Factory_Context more readable
 *
 * @author Dave Swanson
 */
public class ContextAction {
    private static ContextAction instance;
    
    public static ContextAction getInstance(){
        return (instance == null)? (instance = new ContextAction()) : instance;
    }
    
    private final Class_Scanner P;
    private Keywords.HANDLER[] allowedHandlers;// children handlers to instantiate

    private ContextAction(){
        P = Class_Scanner.getInstance();
    }
    
    // detect connected symbol; false on symbol alone
    public final boolean TestIsUserDef(String text){
        return text.startsWith(USERDEF_OPEN) && 
                !text.equals(USERDEF_OPEN);
    }
    
    public final boolean TestIsEndLine(){
        return Erlog.getErlog().getTextStatusReporter().isEndLine();
    }
    
    /*=====Actions that report boolean========================================*/
       
    // push
    public final boolean pushUserDef(Keywords.HANDLER uDefHandler, String text){
        if(TestIsUserDef(text)){
            P.push( 
                Factory_Context.get(
                    uDefHandler, text.substring(USERDEF_OPEN.length())
                )
            );
            return true;
        }
        return false;
    }

    // detects connected or unconnected comment symbol
    public final boolean pushComment(String text){
        if(text.startsWith(COMMENT_TEXT)){// okay to discard text
            P.push( Factory_Context.get(Keywords.HANDLER.COMMENT) );
            return true;
        }
        return false;
    }
    
    public final boolean pushTargLang(String text){
        if( TARGLANG_INSERT_OPEN.equals(text) ){
            P.push( Factory_Context.get(TARGLANG_INSERT) );
            return true;
        }
        return false;
    }
    
    // pop
    public final boolean popOnTargLangClose(String text){
        if( TARGLANG_INSERT_CLOSE.equals(text) ){
            P.pop();
            return true;
        }
        return false;
    }
    public final boolean popOnEndLine(){
        if( Erlog.getErlog().getTextStatusReporter().isEndLine() ){
            P.pop();
            return true;
        }
        return false;
    }
    
    public final boolean popOnUserDef(String text){
        if( TestIsUserDef(text) ){
            P.back(text);
            P.pop();
            return true;
        }
        return false;
    }
    
    public final boolean popOnKeyword(String text){
        Keywords.HANDLER keyword = Keywords.HANDLER.get(text);
        if( keyword != null){
            P.back(text);//repeat keyword so next handler can push it
            P.pop();
            return true;
        }
        return false;
    }
    
    // Push/Pops: Call popAll first, if you call it...
    public final boolean popAll(String text){
        if(SOURCE_CLOSE.equals(text)){
            P.setLineGetter();
            P.popAllSource();
            return true;
        }
        return false;
    }

    // Err if not keyword; push if allowed; pop if not allowed
    public final boolean pushPopOrErr(String text){
        Keywords.HANDLER keyword = Keywords.HANDLER.get(text);
        if( keyword == null ){
            Erlog.get(this).set( "Unknown keyword", text );
            return false;
        }
        System.out.println("pushPopOrErr");
        if(allowedHandlers == null){
            System.out.println("allowedHandlers is null");
        }
        if(allowedHandlers != null && Arrays.asList(allowedHandlers).contains(keyword)){
            System.out.println("pushPopOrErr: push "+keyword.toString());
            P.push( Factory_Context.get(keyword) );
            return true;
        }
        else{
            System.out.println("pushPopOrErr: pop "+keyword.toString());
            P.back(text);
            P.pop();
            return false;
        }
    } 

}
