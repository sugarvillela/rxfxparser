/*
 */
package commons;

import java.util.ArrayList;

/**Very simple functions for stuff Java does not do well, or do at all
 *
 * @author Dave Swanson
 */
public class Commons {
    private Commons(){}
//    private static Commons C=null;
//    public static Commons getInstance(){
//        return (C==null)? ( C = new Commons() ) : C;
//    }
    public interface Result{
        public boolean success();
        public int intVal();
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
    
    public static Result equals(CharSequence[] A, CharSequence[] B){
        final int len = A.length;
        if(A.length != B.length){
            return new Result(){
                @Override
                public boolean success(){
                    return false;
                }
                @Override
                public int intVal(){
                    return -1;
                }
            };
        }
        for(int i=0; i<A.length; i++){
            System.out.println(A[i]+"=="+B[i]);
            if(!A[i].equals(B[i])){
                System.out.println("not equal");
                final int failIndex = i;
                return new Result(){
                    @Override
                    public boolean success(){
                        return false;
                    }
                    @Override
                    public int intVal(){
                        return failIndex;
                    }
                };
            }
        }
        return new Result(){
            public int val = len;
            @Override
            public boolean success(){
                return true;
            }
            @Override
            public int intVal(){
                return len;
            }
        };
    }
    public static boolean equals(CharSequence[] A, CharSequence[] B, boolean verbose){
        if(A.length != B.length){
            System.out.printf("bad length: %d, %d\n",A.length, B.length);
            return false;
        }
        for(int i=0; i<A.length; i++){
            System.out.println(A[i]+"=="+B[i]);
            if(!A[i].equals(B[i])){
                System.out.println("not equal");
                return false;
            }
        }
        return true;
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
    
    public static <T> void disp( ArrayList<T> arr ){
        if( arr==null ){
            return;
        }
        disp(arr, "");
    }
    public static <T> void disp( ArrayList<T> arr, String label ){
        if( arr==null || arr.isEmpty() ){
            System.out.println("\nDisplay: NULL or EMPTY array:" + label );
            return;
        }
        System.out.println("\nDisplay: " + label + ": " + arr.size() );
        for( T elem : arr ){
            System.out.println( "\t" + elem );
        }
        System.out.println("End Display: " + label+"\n");
    }
    public static <T> void disp( T[] arr ){
        disp(arr, "");
    }
    public static <T> void disp( T[] arr, String label ){
        if( arr==null || arr.length==0 ){
            System.out.println("\nDisplay: NULL or EMPTY array:" + label );
            return;
        }
        System.out.println("\nDisplay: " + label + ": " + arr.length );
        for( T elem : arr ){
            System.out.println( "\t" + elem );
        }
        System.out.println("End Display: " + label+"\n");
    }

    public static int boolInt(boolean bool){// Java can't cast bool to int
        return bool? 1 : 0;
    }
    public static String objStr(Object obj){//safe toString() for nullable object
        return(obj==null)? "" : obj.toString();
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
}
