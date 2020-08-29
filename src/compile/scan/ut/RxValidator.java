package compile.scan.ut;

import compile.basics.Keywords.FUNCT;
import erlog.Erlog;
import java.util.regex.Matcher;
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
    
    public boolean assertValidRxWord(String text){
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
    
    public boolean findAndSetParam(String text){
        return(
            getParamFromBraces(text) && assertRxFunction(truncated)
        );
    }
    public String getTruncated(){
        return truncated;
    }
    public String getParam(){
        return param;
    }
    public boolean getParamFromBraces(String text){
        Matcher matcher = FUNCT_BRACES.matcher(text);
        if(matcher.find()){
            truncated = matcher.replaceFirst("");
            String braces = matcher.group();
            if(braces.length() > 2){
                param = braces.substring(1, braces.length() - 1);
            }
            else{
                param = null; 
            }
            System.out.println(truncated);
            System.out.println(param);
            return true;
        }
        System.out.println("nope");
        return false;
    }
    public boolean assertRxFunction(String text){
        for(FUNCT f : FUNCT.values()){
            if(f.toString().equals(text)){
                return true;
            }
        }
        Erlog.get(this).set( "Invalid RX Function name", text);
        return false;
    }
    
    // TODO
    public boolean assertFieldName(String text){
        // TODO get list of field names like trait.numeric 
        // from ENU definition or file
        return true;
    }
}
