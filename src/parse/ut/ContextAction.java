package parse.ut;

import erlog.Erlog;
import java.util.Arrays;
import parse.Class_Scanner;
import parse.Factory_Context;
import parse.Keywords;
import static parse.Keywords.COMMENT_TEXT;
import static parse.Keywords.HANDLER.TARGLANG_INSERT;
import static parse.Keywords.SOURCE_CLOSE;
import static parse.Keywords.TARGLANG_INSERT_CLOSE;
import static parse.Keywords.TARGLANG_INSERT_OPEN;
import static parse.Keywords.USERDEF_OPEN;
import toksource.interfaces.ITextStatus;

/**A hodgepodge of actions to make the handlers in Factory_Context more readable
 *
 * @author Dave Swanson
 */
public class ContextAction {
    private final Class_Scanner P;
    private final ITextStatus status;
    private Keywords.HANDLER[] allowedHandlers;// children handlers to instantiate

    public ContextAction(){
        P = Class_Scanner.getInstance();
        status = ((Class_Scanner)P).getTokenSource();
    }
    
    public final void setAllowedHandlers(Keywords.HANDLER[] allowedHandlers){
        this.allowedHandlers = allowedHandlers;
    }

    // detect connected symbol; false on symbol alone
    public final boolean TestIsUserDef(String text){
        return text.startsWith(USERDEF_OPEN) && 
                !text.equals(USERDEF_OPEN);
    }
    
    public final boolean TestIsEndLine(){
        return status.isEndLine();
    }
    
    /*=====Actions that report boolean========================================*/
    
    // sets a parse error
    public final boolean assertGoodHandler(Keywords.HANDLER handler){
        if(handler != null && Arrays.asList(allowedHandlers).contains(handler)){
            return true;
        }
        else{
            Erlog.getCurrentInstance().set( handler + " not allowed here");
            return false;
        }
    }
    
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
        if( status.isEndLine() ){
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
        if( keyword != null ){
            if(Arrays.asList(allowedHandlers).contains(keyword)){
                P.push( Factory_Context.get(keyword) );
                return true;
            }
            else{
                P.back(text);
                P.pop();
                return false;
            }
        }
        Erlog.getCurrentInstance().set( "Unknown keyword: " + text );
        return false;
    } 

}
