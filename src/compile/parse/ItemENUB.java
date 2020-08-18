/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compile.parse;

import compile.basics.CompileInitializer;
import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import compile.basics.Keywords.KWORD;
import compile.basics.Keywords.HANDLER;
import erlog.Erlog;
import unique.Uq_enumgen;
import unique.Enum_itr;

/**
 *
 * @author newAdmin
 */
public class ItemENUB extends Base_ParseItem{
    protected Enum_itr itr;
    protected String defName;
    protected int count;
    
    public ItemENUB(){}
    public ItemENUB(HANDLER h){
        this.h = h;
        this.debugName = h.toString();
        er = Erlog.get(this);
        itr = (Enum_itr)(new Uq_enumgen(CompileInitializer.getWRow())).iterator();
        count = 0;
    }
    @Override
    public void addTo(HANDLER handler, Object object) {
        String enubName = (String)object;
        if(NULL_TEXT.equals(enubName)){
            er.set("No variable name", enubName);
        }
        int cur = itr.next();
        System.out.printf("%s = 0x%x;\n", enubName, cur);
        System.out.println(commons.BIT.str(cur));
    }

    @Override
    public void setAttrib(KWORD key, String val) {
        switch(key){
            case DEF_NAME:
                defName = val;
                break;
            default:
                er.set("Unknown keyword", key.toString());
        }
    }

    @Override
    public void onPush() {
        System.out.println("ENUB onPush");
    }
    @Override
    public void onBeginStep(){
        if(count > 0){
            itr.newRow();
        }
        count++;
        System.out.println("ENUB onBeginStep: name = " + defName);
    }
    @Override
    public void onEndStep(){
        System.out.println("ENUB onEndStep: name = " + defName);
    }
    @Override
    public void onPop() {
        System.out.println("ENUB onPop");
    }
}
