/*
 */
package commons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static compile.basics.Keywords.NULL_TEXT;

/**Very simple functions for stuff Java does not do well, or do at all
 *
 * @author Dave Swanson
 */
public class Commons {
    public static String timeString(){
        return (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(new Date());
    }

    public static String nullSafe(Object obj){//safe toString() for nullable object
        return (obj==null)? NULL_TEXT : obj.toString();
    }
    public static String nullSafe(String str){//safe toString() for nullable object
        return (str == null || str.isEmpty())? NULL_TEXT : str;
    }
    public static String nullSafe(Object[] objects){
        if(objects == null){
            return NULL_TEXT;
        }
        String[] out = new String[objects.length];
        for(int i = 0; i < objects.length; i++){
            out[i] = nullSafe(objects[i]);
        }
        return String.join("|", out);
    }
    public static String nullSafe(int[] objects){
        return (objects == null)? NULL_TEXT : Commons.join("|", objects);
    }

    public static String undoNullSafe(String text){
        return (text == NULL_TEXT)? null : text;
    }
    public static String[] undoNullSafe_stringArray(String text){
        if(NULL_TEXT.equals(text)){
            return null;
        }
        String[] toks = text.split("\\|");
        for(int i = 0; i < toks.length; i++){
            toks[i] = undoNullSafe(toks[i]);
        }
        return toks;
    }
    public static int[] undoNullSafe_intArray(String text){
        if(NULL_TEXT.equals(text)){
            return null;
        }
        String[] toks = text.split("\\|");
        int[] out = new int[toks.length];
        for(int i = 0; i < toks.length; i++){
            out[i] = Integer.parseInt(toks[i]);
        }
        return out;
    }
    public static int undoNullSafe_int(String text){
        try{
            return Integer.parseInt(text);
        }catch(NumberFormatException e){
            return 0;
        }
    }

    public static String join(String delimiter, Object[] objects){
        String[] out = new String[objects.length];
        for(int i = 0; i < objects.length; i++){
            out[i] = objects[i].toString();
        }
        return String.join(delimiter, out);
    }
    public static String join(String delimiter, int[] objects){
        String[] out = new String[objects.length];
        for(int i = 0; i < objects.length; i++){
            out[i] = String.valueOf(objects[i]);
        }
        return String.join(delimiter, out);
    }

    public static int indexOf( String needle, String[] haystack ){
        for(int i=0; i<haystack.length; i++){
            if( needle.equals(haystack[i]) ){
                return i;
            }
        }
        return -1;
    }
    public static int indexOf( int needle, int[] haystack ){
        for(int i=0; i<haystack.length; i++){
            if( needle == haystack[i] ){
                return i;
            }
        }
        return -1;
    }
    public static int indexOf( char needle, char[] haystack ){
        for(int i=0; i<haystack.length; i++){
            if( needle == haystack[i] ){
                return i;
            }
        }
        return -1;
    }
    public static int indexOf( String needle, ArrayList<String> haystack ){
        for(int i=0; i<haystack.size(); i++){
            if( needle.equals(haystack.get(i)) ){
                return i;
            }
        }
        return -1;
    }
    public static <T> int indexOf( T needle, ArrayList<T> haystack ){
        // for any non-string object or primitive
        for(int i=0; i<haystack.size(); i++){
            if( needle == haystack.get(i) ){
                return i;
            }
        }
        return -1;
    }

    public static int binarySearch( String[] haystack, String needle ){
        int left = 0, right = haystack.length - 1, mid, result; 
        while (left <= right) { 
            mid = left + (right - left) / 2; 
            result = needle.compareTo(haystack[mid]); 
            //System.out.printf("left=%d mid=%d right=%d %s result=%d\n", left, mid, right, haystack[mid], result);
            if (result == 0){       //found needle
                return mid;
            }
            else if (result > 0){   //greater, search right half
                left = mid + 1; 
            }
            else{                   //less, search left half
                right = mid - 1; 
            }  
        }
        return -1; 
    }

    public static ArrayList<String> getUQ(ArrayList<String> vals){
        ArrayList<String> nu = new ArrayList<>();
        for(String val : vals){
            if(!nu.contains(val)){
                nu.add(val);
            }
        }
        return nu;
    }
    public static String[] copyNonNull( String[] arr ){
        int i = 0;
        for( String str : arr ){
            if( str != null ){
                i ++;
            }
        }
        String[] out = new String[i];
        i = 0;
        for( String str : arr ){
            if( str != null ){
                out[i] = str;
                i ++;
            }
        }
        return out;
    }
    public static char[] copyNonNull( char[] arr ){
        int i = 0;
        for( char c : arr ){
            if( c != '\0' ){
                i ++;
            }
        }
        char[] out = new char[i];
        i = 0;
        for( char c : arr ){
            if( c != '\0' ){
                out[i] = c;
                i ++;
            }
        }
        return out;
    }

    public static <T> void disp( ArrayList<T> arr ){
        disp(arr, "generic array list");
    }
    public static <T> void disp( ArrayList<T> arr, String label ){
        if( arr==null || arr.isEmpty() ){
            System.out.println("\nDisplay: " + label + ": NULL or EMPTY array:");
            return;
        }
        System.out.println("\nDisplay: " + label + ": " + arr.size() );
        for( T elem : arr ){
            System.out.println( "\t" + elem );
        }
        System.out.println("End Display: " + label+"\n");
    }
    // For Strings or Objects
    public static <T> void disp( T[] arr ){
        disp(arr, "generic array");
    }
    public static <T> void disp( T[] arr, String label ){
        if( arr==null || arr.length==0 ){
            System.out.println("\nDisplay: " + label + ": NULL or EMPTY array:");
            return;
        }
        System.out.println("\nDisplay: " + label + ": " + arr.length );
        for( T elem : arr ){
            System.out.println( "\t" + elem );
        }
        System.out.println("End Display: " + label+"\n");
    }
    // For primitives
    public static void disp( char[] arr ){
        disp(arr, "char array");
    }
    public static void disp( char[] arr, String label ){
        if( arr==null || arr.length==0 ){
            System.out.println("\nDisplay: " + label + ": NULL or EMPTY array:");
            return;
        }
        System.out.println("\nDisplay: " + label + ": " + arr.length );
        for( char elem : arr ){
            System.out.println( "\t" + elem );
        }
        System.out.println("End Display: " + label+"\n");
    }
    public static void disp( int[] arr ){
        disp(arr, "int array");
    }
    public static void disp( int[] arr, String label ){
        if( arr==null || arr.length==0 ){
            System.out.println("\nDisplay: " + label + ": NULL or EMPTY array:");
            return;
        }
        System.out.println("\nDisplay: " + label + ": " + arr.length );
        for( int elem : arr ){
            System.out.println( "\t" + elem );
        }
        System.out.println("End Display: " + label+"\n");
    }
    public static int boolInt(boolean bool){// Java can't cast bool to int
        return bool? 1 : 0;
    }

    public static String assertFileExt( String f, String ext ){
        if( f.length() > 2 && f.endsWith(".") ){
            f = f.substring(0, f.length()-1);
        }
        if( ext.length() > 2 && ext.startsWith(".") ){
            ext = ext.substring(1);
        }
        if( f.length() > ext.length() && f.endsWith("."+ext) ){
            f = f.substring(0, f.length() - ext.length()-1);
        }
        return f + "." + ext;
    }

    public static String[] wrapAll(String[] strings, char oChar, char cChar){
        String[] out = new String[strings.length];
        for(int i = 0; i < strings.length; i++){
            out[i] = oChar + strings[i] + cChar;
        }
        return out;
    }
    public static String[] randomContent(int returnSize){
        String[] words = new String[]{
                "Interdum","et","malesuada","fames","ac","ante","ipsum","primis",
                "in","faucibus.","Proin","dui","tellus,","imperdiet","in",
                "felis","sodales,","molestie","facilisis","nulla.","Vestibulum",
                "purus","nibh,","aliquet","non","tortor","vitae,","fermentum",
                "elementum","eros.","Cras","rutrum","risus","nisi,","quis",
                "scelerisque","massa","mattis","nec.","Nullam","pellentesque",
                "nibh","commodo","ante","posuere,","iaculis","imperdiet","sapien",
                "vestibulum.","In","tempor","volutpat","pellentesque.","Nam","sed",
                "dolor","at","ex","sodales","tempor","pellentesque","lobortis","diam.",
                "Aenean","ut","nisl","at","est","mattis","consequat.","Vestibulum","ultrices",
                "vitae","turpis","et","vulputate.","Orci","varius","natoque","penatibus",
                "et","magnis","dis","parturient","montes,","nascetur","ridiculus","mus.",
                "Aenean","sodales","nulla","vel","nisl","se mper","dictum.","Nulla","rutrum",
                "condimentum","metus","eget","varius.","Maecenas","non","lorem","quis","metus",
                "porttitor","dignissim","nec","sit","amet","nisl.","Integer","neque","tortor,",
                "tempor","ut","dignissim","sed,","dictum","at","risus.","Vivamus","sed","sagittis","quam."
        };
        String[] out = new String[returnSize];
        int len = words.length;
        int start = (int)(Math.random() * (len - returnSize));
        for(int i = 0, j = start; i < returnSize; i++, j++){
            out[i] = words[j];
        }
        return out;
    }
}
