package namegen;

import runstate.Glob;

public class NameGenTest {
    public static void globalNames() {
        NameGenRx nameGen = Glob.NAME_GEN_RX;
        String name;

        for (int i = 0; i < 10; i++) {
            System.out.println();

            name = nameGen.getRx(NameGenRx.RX_FUN);
            System.out.println("        " + name);
            nameGen.incRx(NameGenRx.RX_FUN);

            nameGen.incFx(NameGenRx.FX_WORD);
            name = nameGen.getFx(NameGenRx.FX_WORD);

            System.out.println("    " + name);

            if(i%5==0){
                nameGen.incRx(NameGenRx.RX_WORD);
                name = nameGen.getRx(NameGenRx.RX_WORD);
                System.out.println("    " + name);
            }
            if(i%3==0){
                nameGen.incRx(NameGenRx.RX_PATTERN);
                name = nameGen.getRx(NameGenRx.RX_PATTERN);
                System.out.println(name);

                nameGen.incFx(NameGenRx.FX_PATTERN);
                name = nameGen.getFx(NameGenRx.FX_PATTERN);
                System.out.println(name);

                name = nameGen.getRx(NameGenRx.RX_WORD);
                System.out.println("    " + name);
            }

        }
    }
}
