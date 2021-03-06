package commons;

import java.io.File;

/** Some experiments, unfinished */
public class PathUtil {
    private static PathUtil instance;

    public static PathUtil getInstance(){
        return (instance == null)? (instance = new PathUtil()) : instance;
    }

    private String path = null;

    private PathUtil(){
        setGenPath();
    }
    public final void setGenPath(String... dirs){// path to genObj
//        ClassLoader cl = GenObjAdaptor.class.getClassLoader();
//        System.out.println("ClassLoader = " + cl.toString());
//        URL u = cl.getResource(".");
//        System.out.println("Url=" + u);
//        String dest = u.toString();//GenObjAdaptor.class.getCanonicalName();
//
//        this.path = dest;
    }
    public final void setUserPath(String... dirs){
        String userHome = System.getProperty("user.home");
        this.path = (dirs.length == 0)?
                userHome :
                userHome + File.separator + String.join(File.separator, dirs);
    }

    public final void setNullPath(){
        this.path = null;
    }

    public String fixPath(String fileName){
        return (path == null)? fileName : path + File.separator + fileName;
    }

    public String fixPackage(String... dirs){
//        String dest = GenObjAdaptor.class.getCanonicalName();
//        int lastIndex = dest.lastIndexOf(".");
//        return (dirs == null || dirs.length == 0)?
//                dest.substring(0, lastIndex) :
//                dest.substring(0, lastIndex) + "." + String.join(".", dirs);
        return null;
    }

    public static void demo(){
        PathUtil pathUtil = new PathUtil();
        System.out.println(pathUtil.fixPath("myFile"));

        pathUtil.setGenPath("bork", "munch");
        System.out.println(pathUtil.fixPath("myFile"));

        pathUtil.setUserPath();
        System.out.println(pathUtil.fixPath("myFile"));

        pathUtil.setUserPath("bork", "munch");
        System.out.println(pathUtil.fixPath("myFile"));

        pathUtil.setNullPath();
        System.out.println(pathUtil.fixPath("myFile"));

        System.out.println(pathUtil.fixPackage("bork", "munch"));
    }
}
