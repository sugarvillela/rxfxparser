package runstate;

import codegen.namegen.NameGenRxFx;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StaticState {
    private static StaticState instance;

    public static StaticState init(){
        return (instance = new StaticState());
    }
    public static StaticState getInstance(){
        return instance;
    }

    private final String initTime;
    private final NameGenRxFx nameGenRxFx;

    private StaticState() {
        initTime = (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")).format(new Date());
        nameGenRxFx = new NameGenRxFx();
    }

    public String getInitTime(){ return this.initTime; }

    public NameGenRxFx getNameGenRxFx(){ return this.nameGenRxFx; }
}
