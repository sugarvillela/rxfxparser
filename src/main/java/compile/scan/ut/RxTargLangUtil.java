package compile.scan.ut;

import compile.scan.Base_ScanItem;

import java.util.regex.Pattern;

import static compile.basics.Keywords.*;

public class RxTargLangUtil {
    private static RxTargLangUtil instance;

    private RxTargLangUtil(){
        targRxOnSpecial = false;
        QUOTED = Pattern.compile(".*(\\)\\(|\\}\\{).*");
        FULL_WORD_PATTERN = Pattern.compile(".*(&&|\\|\\||==|~~).*");
        TARG_FUN = Pattern.compile(TARG + "\\(.+\\)");
    }

    public static RxTargLangUtil getInstance(){
        return (instance == null)? (instance = new RxTargLangUtil()) : instance;
    }

    private final Pattern QUOTED;
    private final Pattern FULL_WORD_PATTERN;
    private final Pattern TARG_FUN;

    private String text, truncated;
    boolean targRxOnSpecial;

    public void setTargRxOnSpecial(boolean set){
        targRxOnSpecial = set;
    }
    public boolean findRegexAndTruncate(String text, Base_ScanItem context){
        truncated = null;
        this.text = text;
        return (targRxOnSpecial && context.isSpecialScope())
                || findQuoted() || findTargOpenClose() || findFullWordPattern() || findTargFun();
    }
    public String getTruncated(){
        return truncated;
    }

    private boolean findQuoted(){
        if(QUOTED.matcher(text).matches()){
            truncated = text.substring(1, text.length() -1);
            return true;
        }
        return false;
    }
    private boolean findTargOpenClose(){
        if(text.startsWith(TARGLANG_INSERT_OPEN) && text.endsWith(TARGLANG_INSERT_CLOSE)){
            truncated = text.substring(TARGLANG_INSERT_OPEN.length(), text.length() - TARGLANG_INSERT_CLOSE.length());
            return true;
        }
        return false;
    }
    private boolean findFullWordPattern(){
        if(FULL_WORD_PATTERN.matcher(text).matches()){
            truncated = text;
            return true;
        }
        return false;
    }
    private boolean findTargFun(){
        if(TARG_FUN.matcher(text).matches()){
            truncated = text.substring(TARG.length() + 1, text.length() -1);
            return true;
        }
        return false;
    }
}
