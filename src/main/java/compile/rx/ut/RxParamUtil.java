package compile.rx.ut;

import commons.Commons;
import compile.basics.Keywords;
import compile.symboltable.ConstantTable;
import erlog.Erlog;

import java.util.regex.Matcher;

import static compile.basics.Keywords.PAR.*;

public class RxParamUtil {
    private static RxParamUtil instance;

    private RxParamUtil(){
        types = Keywords.PAR.values();
    }

    public static RxParamUtil getInstance(){
        return (instance == null)? (instance = new RxParamUtil()): instance;
    }

    private final Keywords.PAR[] types;
    private String truncated, param;
    private int paramType;

    public void findAndSetParam(String text){
        identifyPattern(text);
        if(isFun()){
            if(types[paramType] == CONST_PAR){
                readConstant();
            }
            RxValidator validator = RxValidator.getInstance();
            validator.assertRxFunction(truncated);
            if(types[paramType] == RANGE_PAR){
                validator.assertValidRange(param);
            }
        }
    }

    public boolean isFun(){
        return types[paramType].isFun;
    }

    public Keywords.PAR getParamType(){
        //Commons.disp(Keywords.PAR.values(), "Par values, paramType = " + paramType);
        return Keywords.PAR.fromInt(paramType);
    }

    public String getTruncated(){
        return truncated;
    }

    public String getParam(){
        return param;
    }

    private void identifyPattern(String text){
        Matcher matcher;
        for(paramType = 0; paramType < types.length; paramType++){
            if(types[paramType].pattern == null){
                continue;
            }
            matcher = types[paramType].pattern.matcher(text);
            if(matcher.find()){
                if(types[paramType].isFun){
                    param = matcher.replaceAll("$1");
                    truncated = text.substring(0, text.length() - param.length() -2);
                    //param = param.substring(1, param.length() - 1);

                }
                else{
                    truncated = "";
                    param = "";
                }
                return;
            }
        }
        Erlog.get(this).set("Syntax error", text);
    }

    private void readConstant(){
        String read = ConstantTable.getInstance().readConstant(param);
        if(read == null){
            Erlog.get(this).set("Undefined constant", param);
        }
        else{
            System.out.println("param="+param + ", read="+read);
            identifyPattern(String.format("%s(%s)", truncated, read));
        }
    }
}
