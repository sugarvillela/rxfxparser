package commons;

/** Expects all input to be validated
 * @throws IllegalStateException
 * @throws NumberFormatException
 * @throws StringIndexOutOfBoundsException
 */
public class RangeUtil {
    private int low, high;
    private String lowStr, highStr;

    public void rangeToInt(String delimiter, String text){
        String[] tok = text.split(delimiter);
        if(tok.length != 2){
            throw new IllegalStateException("Range input '" + text + "'should have one occurrence of delimiter " + delimiter);
        }
        low = Integer.parseInt(tok[0]);
        high = Integer.parseInt(tok[1]);
    }

    public void rangeToString(String delimiter, String text){
        String[] tok = text.split(delimiter);
        if(tok.length != 2){
            throw new IllegalStateException("Range input '" + text + "'should have one occurrence of delimiter " + delimiter);
        }
        lowStr = tok[0];
        highStr = tok[1];
    }

    public int rangeBelowToInt(String text){
        return Integer.parseInt(text.substring(1));
    }

    public String rangeBelowToString(String text){
        return text.substring(1);
    }

    public int rangeAboveToInt(String text){
        return Integer.parseInt(text.substring(0, text.length() - 1));
    }

    public String rangeAboveToString(String text){
        return text.substring(0, text.length() - 1);
    }

    public String unwrap(String text){
        return text.substring(1, text.length() - 1);
    }

    public int unwrapInt(String text){
        return Integer.parseInt(text.substring(1, text.length() - 1));
    }

    public int getLow() {
        return low;
    }
    public int getHigh() {
        return high;
    }

    public String getLowStr() {
        return lowStr;
    }

    public String getHighStr() {
        return highStr;
    }

}
