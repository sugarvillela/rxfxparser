package compile.scan.ut;

import erlog.Erlog;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static compile.basics.Keywords.RX_MAX_RANGE;

public class RxRangeUtil {
    private static RxRangeUtil instance;
    
    private RxRangeUtil(){
        MAX = String.valueOf(RX_MAX_RANGE);
        RANGE_PATTERNS = new Pattern[]{
                Pattern.compile("^.+\\{([0-9]+)\\}$"),
                Pattern.compile("^.+\\{[-]([0-9]+)\\}$"),
                Pattern.compile("^.+\\{([0-9]+)[-]\\}$"),
                Pattern.compile("^.+\\{([0-9]+)[-]([0-9]+)\\}$")
        };
    }
    
    public static RxRangeUtil getInstance(){
        return (instance == null)? (instance = new RxRangeUtil()) : instance;
    }

    private final int NUM = 0, BELOW = 1, ABOVE = 2, RANGE = 3;
    private final Pattern[] RANGE_PATTERNS;
    private final String MAX;
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
                return setRangeFromCurly(text);
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
    private boolean setRangeFromCurly(String text){
        Matcher matcher;
        for(int paramType = 0; paramType < RANGE_PATTERNS.length; paramType++){
            matcher = RANGE_PATTERNS[paramType].matcher(text);
            if(matcher.find()){
                switch(paramType){
                    case NUM:
                        low = high = matcher.replaceAll("$1");
                        truncated = text.substring(0, text.length() - low.length() - 2);
                        return true;
                    case BELOW:
                        low = "0";
                        high = matcher.replaceAll("$1");
                        truncated = text.substring(0, text.length() - high.length() - 3);
                        return true;
                    case ABOVE:
                        low = matcher.replaceAll("$1");
                        high = MAX;
                        truncated = text.substring(0, text.length() - low.length() - 3);
                        return true;
                    case RANGE:
                        low = matcher.replaceAll("$1");
                        high = matcher.replaceAll("$2");
                        truncated = text.substring(0, text.length() - low.length()  - high.length() - 3);
                        return true;
                }
            }
        }
        Erlog.get(this).set("Expected range in standard format... Example: {1-2} or {5}", text);
        return false;
    }

}
