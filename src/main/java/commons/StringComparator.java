package commons;

public class StringComparator {

    public interface Result{
        boolean success();
        int intVal();
    }

    public static Result equals_r(CharSequence[] A, CharSequence[] B){
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

    public static boolean equals(CharSequence[] A, CharSequence[] B){
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


}
