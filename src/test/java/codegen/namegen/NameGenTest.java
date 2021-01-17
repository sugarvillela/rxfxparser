package codegen.namegen;

public class NameGenTest {
    public static void globalNames() {
        NameGenRxFx nameGen = new NameGenRxFx();
        String name;

        for (int i = 0; i < 10; i++) {
            System.out.println();

            name = nameGen.getRx(NameGenRxFx.RX_FUN);
            System.out.println("        " + name);
            nameGen.incRx(NameGenRxFx.RX_FUN);

            nameGen.incFx(NameGenRxFx.FX_WORD);
            name = nameGen.getFx(NameGenRxFx.FX_WORD);

            System.out.println("    " + name);

            if(i%5==0){
                nameGen.incRx(NameGenRxFx.RX_WORD);
                name = nameGen.getRx(NameGenRxFx.RX_WORD);
                System.out.println("    " + name);
            }
            if(i%3==0){
                nameGen.incRx(NameGenRxFx.RX_PATTERN);
                name = nameGen.getRx(NameGenRxFx.RX_PATTERN);
                System.out.println(name);

                nameGen.incFx(NameGenRxFx.FX_PATTERN);
                name = nameGen.getFx(NameGenRxFx.FX_PATTERN);
                System.out.println(name);

                name = nameGen.getRx(NameGenRxFx.RX_WORD);
                System.out.println("    " + name);
            }

        }
    }
}
