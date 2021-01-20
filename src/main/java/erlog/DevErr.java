package erlog;

import compile.interfaces.Debuggable;

/** A separate reporting system for in-development errors.
 *  May be removed in release version, as all programming errors should be fixed, right? Right */
public class DevErr {
    private static DevErr instance;

    public static DevErr get(String source){// same erasure for string and object
        if(instance == null){
            instance = new DevErr();
        }
        instance.sourceName = source;
        return instance;
    }
    public static DevErr get(Debuggable debuggable){// same erasure for string and object
        return get(debuggable.getDebugName());
    }
    public static DevErr get(Object object){// same erasure for string and object
        return get(object.getClass().getSimpleName());
    }

    private final boolean MUTE = false;
    private String sourceName;

    private DevErr(){}

    public void kill( String message ){
        this.kill(message, null);
    }

    public void kill( String message, String at ){
        if(!MUTE){
            String text = (at == null)?
                    String.format("%s: %s", sourceName, message) :
                    String.format("%s: %s at '%s'", sourceName, message, at);
            throw new IllegalStateException(text);
        }
    }
    public void warn( String message, String at ){
        if(!MUTE){
            String text = (at == null)?
                    String.format("%s: %s", sourceName, message) :
                    String.format("%s: %s at '%s'", sourceName, message, at);
            System.err.println(text);
        }
    }
    public static void demo(){
        DevErr.get("OffendingClass").kill("Unknown yada yada in the hoohah", "offending bit of code");
    }
}
