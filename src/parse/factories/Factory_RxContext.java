/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse.factories;

import erlog.Erlog;
import java.util.ArrayList;
import parse.Base_Context;
import parse.Keywords;
import static parse.Keywords.HANDLER.ATTRIB;
import toktools.TK;
import toktools.Tokens;

/**
 *
 * @author newAdmin
 */
public class Factory_RxContext{
    private final static Tokens T = TK.getInstance("", "('", TK.IGNORESKIP );
    
    public static Base_Context get( Keywords.HANDLER h ){
        switch(h){
            case RX:
                break;
            default:
                Erlog.get("Factory_RxContext").set("Blame developer");
        }
        return null;
    }
    public static class Rx extends Base_Context{
        public Rx(Keywords.HANDLER setH ){
            this.h = setH;
            setAllowedHandlers(new Keywords.HANDLER[]{ ATTRIB });
        }
        
        @Override
        public void pushPop(String text) {
            if( 
                action.popAll(text)        || 
                action.pushComment(text)   ||
                action.pushTargLang(text)
            ){}
            
        }

    }
    public static class TreeNode{
        public ArrayList<TreeNode> nodes;
        
        public TreeNode(){
            nodes = new ArrayList<>();
        }
        public void addChild(TreeNode node){
            
        }
    }
    
}
