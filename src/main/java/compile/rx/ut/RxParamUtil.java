package compile.rx.ut;

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
    private String truncated, paramText;
    private int paramType;

    public void findAndSetParam(String text){
        identifyPattern(text);
        if(getParamType().isFun){
            if(types[paramType] == CONST_PAR){
                readConstant();
            }
            RxValidator validator = RxValidator.getInstance();
            validator.assertRxFunction(truncated);
            if(types[paramType] == RANGE_PAR){
                validator.assertValidRange(paramText);
            }
        }
    }

    public Keywords.PAR getParamType(){
        //Commons.disp(Keywords.PAR.values(), "Par values, paramType = " + paramType);
        return Keywords.PAR.fromInt(paramType);
    }

    public String getTruncated(){
        return truncated;
    }

    public String getParamText(){
        return paramText;
    }

    public Keywords.RX_FUN getFunType(){// already validated as fun
        return Keywords.RX_FUN.fromString(truncated);
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
                    paramText = matcher.replaceAll("$1");
                    truncated = text.substring(0, text.length() - paramText.length() -2);
                    //param = param.substring(1, param.length() - 1);

                }
                else{
                    truncated = "";
                    paramText = "";
                }
                return;
            }
        }
        Erlog.get(this).set("Syntax error", text);
    }

    private void readConstant(){
        String read = ConstantTable.getInstance().readConstant(paramText);
        if(read == null){
            Erlog.get(this).set("Undefined constant", paramText);
        }
        else{
            System.out.println("param="+ paramText + ", read="+read);
            identifyPattern(String.format("%s(%s)", truncated, read));
        }
    }
}
