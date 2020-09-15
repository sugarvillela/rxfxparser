package compile.rx.ut;

import compile.basics.Keywords;
import compile.symboltable.ConstantTable;
import erlog.Erlog;

import java.util.regex.Matcher;

import static compile.basics.Keywords.RX_PARAM_TYPE.*;

public class RxParamUtil {
    private static RxParamUtil instance;

    private RxParamUtil(){
        types = Keywords.RX_PARAM_TYPE.values();
    }

    public static RxParamUtil getInstance(){
        return (instance == null)? (instance = new RxParamUtil()): instance;
    }

    private final Keywords.RX_PARAM_TYPE[] types;
    private String truncated, param;
    private int paramType;

    public void findAndSetParam(String text){
        identifyPattern(text);
        if(isFun()){
            if(types[paramType] == CONST_PARAM){
                readConstant();
            }
            RxValidator validator = RxValidator.getInstance();
            validator.assertRxFunction(truncated);
            if(types[paramType] == NUM_RANGE_PARAM){
                validator.assertValidRange(param);
            }
        }
    }

    public boolean isFun(){
        return types[paramType].isFun;
    }

    public Keywords.RX_PARAM_TYPE getParamType(){
        return Keywords.RX_PARAM_TYPE.fromInt(paramType);
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
            matcher = types[paramType].pattern.matcher(text);
            if(matcher.find()){
                if(types[paramType].isFun){
                    truncated = matcher.replaceFirst("");
                    param = matcher.group();
                    param = param.substring(1, param.length() - 1);

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
