// Generated file, do not edit
package genobj;

public Semantic1ListBooleanGen000 {
    public static final int STATE = 0x01;
    public static final int MORE = 0x02;
    public static final int DONE = 0x04;
    
    public static final int COLORS = 0x010000001;
    public static final int RED = 0x010000002;
    public static final int BLUE = 0x010000004;
    public static final int GREEN = 0x010000008;
    public static final int ORANGE = 0x010000010;
    public static final int FUSCIA = 0x010000020;
    public static final int PURPLE = 0x010000040;
    public static final int VIOLET = 0x010000080;
    
    public static final int POS = 0x020000001;
    public static final int VERB = 0x020000002;
    public static final int NOUN = 0x020000004;
    public static final int ADJECTIVE = 0x020000008;
    public static final int ADVERB = 0x020000010;
    public static final int LINKING = 0x020000020;
    public static final int ARTICLE = 0x020000040;
    public static final int DETERMINER = 0x020000080;
    public static final int MODAL = 0x020000100;
    
    public String getCategory (int index) {
        if (
            0x01 <= index && index <= 0x04
        ) {
            return "STATE";
        }
        if (
            0x010000001 <= index && index <= 0x010000080
        ) {
            return "COLORS";
        }
        if (
            0x020000001 <= index && index <= 0x020000100
        ) {
            return "POS";
        }
        return null;
    }
}
