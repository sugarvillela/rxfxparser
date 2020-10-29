package codegen.interfaces;

public abstract class enums {
    //public static final int MARGIN = 70;           // Constant formatting value
    public static final String SEMICOLON = ";";
    public static final String ENDL = "\n";
    public static final String COMMENT_SHORT = "// ";
    public static final String COMMENT_OPEN = "/*";
    public static final String COMMENT_CLOSE = "*/";
    public static final String COMMENT_PY = "# ";

   public enum VISIBILITY{
       PUBLIC_          ("public"),
       PRIVATE_         ("private"),
       PROTECTED_       ("protected"),
       PACKAGE_PRIVATE  ("")
       ;

       private final String text;

       private VISIBILITY(String text){
           this.text = text;
       }
       @Override
       public String toString(){
           return text;
       }
    }
}
