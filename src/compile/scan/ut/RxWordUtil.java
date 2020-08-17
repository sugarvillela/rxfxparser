package compile.scan.ut;

import erlog.Erlog;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RxWordUtil {
    private static RxWordUtil instance;
    
    private RxWordUtil(){}
    
    public static RxWordUtil getInstance(){
        return (instance == null)? (instance = new RxWordUtil()) : instance;
    }

    private final Pattern RANGE_PATTERN = Pattern.compile("\\{[0-9]+((-[0-9]+)?|-?)\\}$");
    private final Pattern DUP_SYMBOLS = Pattern.compile(".*(&&|\\|\\||==|~~).*");
    private final Pattern BAD_BRACKET = Pattern.compile(".*(\\)\\(|\\}\\{).*");
    private final String MAX = "1024";
    private String truncated, low, high;
    
    public boolean findAndSetRange(String text){
        //System.out.printf( "\ngetRange %s\n", text );
        int lastPos = text.length()-1;
        switch (text.charAt(lastPos)){
            case '*':
                truncated = text.substring(0, lastPos);
                low = "0";
                high = MAX;
                return true;
            case '+':
                truncated = text.substring(0, lastPos);
                low = "1";
                high = MAX;
                return true;
            case '?':
                truncated = text.substring(0, lastPos);
                low = "0";
                high = "1";
                return true;
            case '}':
                return setRangeFromCurlys(text);
            default:
                low = "1";
                high = "1";
                return false;
        }
    }
    public String getTruncated(){
        return truncated;
    }
    public String getLowRange(){
        return low;
    }
    public String getHighRange(){
        return high;
    }
    private boolean setRangeFromCurlys(String text){
        Matcher matcher = RANGE_PATTERN.matcher(text);
        if(matcher.find()){
            truncated = matcher.replaceFirst("");
            text = matcher.group();
            text = text.substring(1, text.length() - 1);
            String[] toks = text.split("-");
            System.out.println(text);
            System.out.println(toks.length);

            if(toks.length == 1){
                if(toks[0].length() < text.length()){
                    low = toks[0];
                    high = MAX;
                }
                else{
                    low = high = text;
                }
            }
            else{
                low = toks[0];
                high = toks[1];
            }
            return true;    

        }
        else{
            Erlog.get(this).set("Bad number format", text);
            return false;
        }
    }
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
}
