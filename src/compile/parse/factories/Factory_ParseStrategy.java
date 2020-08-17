/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compile.parse.factories;

import compile.parse.Base_ParseItem;
import compile.scan.Base_ScanItem;
import compile.basics.Base_Stack;
import compile.parse.Class_Parser;
import compile.scan.Class_Scanner;
import static compile.scan.factories.Factory_Strategy.getStrategy;
import compile.basics.Keywords.HANDLER;
import static compile.basics.Keywords.HANDLER.RX_WORD;
import static compile.basics.Keywords.HANDLER.TARGLANG_INSERT;
import static compile.basics.Keywords.SOURCE_CLOSE;
import static compile.basics.Keywords.SOURCE_OPEN;
import static compile.basics.Keywords.TARGLANG_INSERT_CLOSE;
import static compile.basics.Keywords.TARGLANG_INSERT_OPEN;
import static compile.basics.Keywords.USERDEF_OPEN;
import static compile.basics.Keywords.HANDLER.USER_DEF_LIST;
import static compile.basics.Keywords.KWORD.HI;
import static compile.basics.Keywords.KWORD.LO;
import compile.basics.Keywords.CMD;
import compile.scan.factories.Factory_Strategy;
/**
 *
 * @author newAdmin
 */
public class Factory_ParseStrategy {
    public static ParseStrategy getParseStrategy(CMD cmd){
        switch(cmd){
            
        }
        return null;
    }
    public static Factory_ParseStrategy.ParseStrategy[] setStrategies( Factory_Strategy.StrategyEnum... enums){
        ParseStrategy[] currentActions = new ParseStrategy[enums.length];
        for(int i = 0; i < enums.length; i++){
            //currentActions[i] = getParseStrategy(enums[i]);
        }
        return currentActions;
    } 
    public static abstract class ParseStrategy{
        Base_Stack P;
        
        public ParseStrategy(){
            P = Class_Parser.getInstance();
        }
        public abstract boolean go(String text, Base_ParseItem context);
    }
}
