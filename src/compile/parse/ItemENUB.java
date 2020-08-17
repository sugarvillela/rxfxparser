/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compile.parse;

import static compile.basics.Factory_Node.ScanNode.NULL_TEXT;
import compile.basics.IParseItem;
import erlog.Erlog;
import java.util.Iterator;
import unique.Uq_enumgen;

/**
 *
 * @author newAdmin
 */
public class ItemENUB  implements IParseItem{
    protected Erlog er;
    protected Iterator<Integer> itr;
    //protected Uq_enumgen uq = new Uq_enumgen(5);
    
    public ItemENUB(){
        er = Erlog.get(this);
        itr = (new Uq_enumgen(5)).iterator();
    }
    @Override
    public void addTo(Object object) {
        String enubName = (String)object;
        if(NULL_TEXT.equals(enubName)){
            er.set("ENUB no variable name", enubName);
        }
        int cur = itr.next();
        System.out.println(enubName + " = " + commons.BIT.str(cur));
    }

    @Override
    public void setAttrib(Object key, Object val) {}
    
}
