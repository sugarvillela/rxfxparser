package codegen.namegen;

public class NameGenRxFx {
    public static final int RX_PATTERN = 0;
    public static final int RX_WORD = 1;
    public static final int RX_FUN_PATTERN = 2;
    public static final int RX_FUN = 3;
    public static final int FX_PATTERN = 4;
    public static final int FX_WORD = 5;
    private static final int ANY = -1;
    private final Generator rxGen, fxGen;

    public NameGenRxFx() {
        rxGen = new Generator(
            RX_PATTERN, "RX_PATTERN_%03d",
            new Generator(
                    RX_WORD, "RX_WORD_%03d_%02d",
                    new Generator(
                            RX_FUN_PATTERN, "RX_FUN_PATTERN_%03d_%02d_%02d",
                            new Generator(
                                    RX_FUN,"RX_FUN_%03d_%02d_%02d_%02d", null
                            )
                    )
            )
        );
        fxGen = new Generator(
                FX_PATTERN, "FX_PATTERN_%03d",
                new Generator(
                        FX_WORD, "FX_WORD_%03d_%02d", null
                )
        );
    }

    public void incRx(int type){
        rxGen.increment(type);
    }
    public void incFx(int type){
        fxGen.increment(type);
    }

    public String getRx(int type){
        return rxGen.get(type);
    }
    public String getFx(int type){
        return rxGen.get(type);
    }

    private static class Generator {
        private final int type;
        private final String format;
        private final Generator below;
        private int curr;

        private Generator(int type, String format, Generator below) {
            this.type = type;
            this.format = format;
            this.below = below;
            curr = 0;
        }

        private void increment(int type){
            if(type == ANY || type == this.type){
                curr++;
                if(below != null){
                    below.reset();
                }
            }
            else if(below != null){
                below.increment(type);
            }
        }
        private void reset(){
            curr = 0;
            if(below != null){
                below.reset();
            }
        }

        private String get(int type){// rxPattern
            if(this.type == type){
                return String.format(format, curr);
            }
            else{
                return below.get(type, curr);
            }
        }
        private String get(int type, int rxPattern){// rxWord
            if(this.type == type){
                return String.format(format, rxPattern, curr);
            }
            else{
                return below.get(type, rxPattern, curr);
            }
        }
        private String get(int type, int rxPattern, int rxWord){// rxFunPattern
            if(this.type == type){
                return String.format(format, rxPattern, rxWord, curr);
            }
            else{
                return below.get(type, rxPattern, rxWord, curr);
            }
        }
        private String get(int type, int rxPattern, int rxWord, int rxFunPattern){// rxFun
            return String.format(format, rxPattern, rxWord, rxFunPattern, curr);
        }
    }
}
