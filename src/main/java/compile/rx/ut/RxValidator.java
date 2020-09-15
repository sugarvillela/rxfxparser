package compile.rx.ut;

import compile.basics.Keywords;
import compile.basics.Keywords.FUNCT;
import erlog.Erlog;

import java.util.regex.Pattern;

public class RxValidator {
    private static RxValidator instance;
    
    private RxValidator(){}
    
    public static RxValidator getInstance(){
        return (instance == null)? (instance = new RxValidator()) : instance;
    } 

    private final Pattern DUP_SYMBOLS = Pattern.compile(".*(&&|\\|\\||==|~~).*");
    private final Pattern BAD_BRACKET = Pattern.compile(".*(\\)\\(|\\}\\{).*");
    private final Pattern FUNCT_BRACES = Pattern.compile("\\([0-9]*\\)$");
    private String truncated, param;
    
    public boolean assertValidRxWord(String text){// Early test on whole word
        if(DUP_SYMBOLS.matcher(text).matches()){
            Erlog.get(this).set( "Single & | ~ required", text);
            return false;
        }
        if(BAD_BRACKET.matcher(text).matches()){
            Erlog.get(this).set( "Invalid parentheses or braces", text);
            return false;
        }
        return true;
    }

    public boolean assertRxFunction(String text){
        FUNCT f = FUNCT.fromString(text);
        if(f == null){
            Erlog.get(this).set( "Invalid RX Function name", text);
            return false;
        }
        return true;
    }
    public boolean assertValidRange(String range){
        String[] toks = range.split("-");
        boolean good = false;
        if(toks.length == 2){
            try{
                good = Integer.parseInt(toks[0]) < Integer.parseInt(toks[1]);
            }catch(NumberFormatException e){ }
        }
        if(!good){
            Erlog.get(this).set( "Invalid RX Function range", range);
        }
        return good;
    }
    public boolean assertValidTests(Keywords.RX_PARAM_TYPE prev, Keywords.RX_PARAM_TYPE curr){
        return (
                (prev == Keywords.RX_PARAM_TYPE.CATEGORY && curr == Keywords.RX_PARAM_TYPE.CATEGORY_ITEM)
                );
    }
    
    // TODO
    public boolean assertFieldName(String text){
        // TODO get list of field names like trait.numeric 
        // from ENU definition or file
        return true;
    }
}
