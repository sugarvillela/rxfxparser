package compile.parse.factories;

import compile.parse.Base_ParseItem;

public class Factory_ParseStrategy {

    public enum PushEnum{
        ON_PUSH_                 (new OnPush_()),                         //

        ;

        public final ParseStrategy strategy;
        PushEnum(ParseStrategy strategy) {
            this.strategy = strategy;
        }
    }
    public enum PopEnum{
        ON_POP_                  (new OnPop_()),                          //

        ;
        public final ParseStrategy strategy;

        PopEnum(ParseStrategy strategy) {
            this.strategy = strategy;
        }
    }

    public static abstract class ParseStrategy{
        public abstract boolean go(String text, Base_ParseItem context);
    }
    public static class OnPush_ extends ParseStrategy{
        @Override
        public boolean go(String text, Base_ParseItem context){
            return false;
        }
    }
    public static class OnPop_ extends ParseStrategy{
        @Override
        public boolean go(String text, Base_ParseItem context){
            return false;
        }
    }
}
