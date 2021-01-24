package runstate;

import compile.scan.ut.RxTargLangUtil;
import compile.scan.ut.WordRangeUtil;
import compile.symboltable.ConstantTable;
import compile.symboltable.SymbolTable;
import compile.symboltable.SymbolTest;
import compile.symboltable.TextSniffer;
import listtable.ListTable;
import namegen.NameGenRx;
import namegen.NameGenSimple;
import scannode.ScanNodeFactory;
import sublang.LogicTreeFx;
import sublang.LogicTreeRx;
import sublang.TreeBuildUtil;
import sublang.rxfun.OutTypeUtil;
import sublang.ut.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Glob {
    public static final String          TIME_INIT;
    public static final ScanNodeFactory SCAN_NODE_FACTORY;
    public static final SymbolTest      SYMBOL_TEST;        // TODO eliminate?

    public static final ListTable       LIST_TABLE;
    public static final SymbolTable     SYMBOL_TABLE;
    public static final ConstantTable   CONSTANT_TABLE;

    public static final TextSniffer     TEXT_SNIFFER;

    public static final WordRangeUtil   WORD_RANGE_UTIL;    // TODO eliminate?
    public static final RxTargLangUtil  RX_TARG_LANG_UTIL;

    public static final TreeBuildUtil   TREE_BUILD_UTIL;
    public static final LogicTreeRx     LOGIC_TREE_RX;
    public static final LogicTreeFx     LOGIC_TREE_FX;

    public static final OutTypeUtil     RX_FUN_UTIL;
    public static final ValidatorRx     VALIDATOR_RX;
    public static final ValidatorFx     VALIDATOR_FX;
    public static final RxParamUtil     RX_PARAM_UTIL;
    public static final FxParamUtil     FX_PARAM_UTIL;
    public static final FxAccessUtil    FX_ACCESS_UTIL;

    public static final NameGenSimple   NAME_GEN_SIMPLE;
    public static final NameGenRx       NAME_GEN_RX;

    static {
        TIME_INIT =         (new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")).format(new Date());
        SCAN_NODE_FACTORY = ScanNodeFactory.init();
        SYMBOL_TEST =       SymbolTest.init();

        LIST_TABLE =        ListTable.init();
        SYMBOL_TABLE =      SymbolTable.init();
        CONSTANT_TABLE =    ConstantTable.init();

        TEXT_SNIFFER =      TextSniffer.init();

        WORD_RANGE_UTIL =   WordRangeUtil.init();
        RX_TARG_LANG_UTIL = RxTargLangUtil.init();

        TREE_BUILD_UTIL =   TreeBuildUtil.init();
        LOGIC_TREE_RX =     LogicTreeRx.init();
        LOGIC_TREE_FX =     LogicTreeFx.init();

        RX_FUN_UTIL =       OutTypeUtil.init();
        VALIDATOR_RX =      ValidatorRx.init();
        VALIDATOR_FX =      ValidatorFx.init();
        RX_PARAM_UTIL =     RxParamUtil.init();
        FX_PARAM_UTIL =     FxParamUtil.init();
        FX_ACCESS_UTIL =    FxAccessUtil.init();

        NAME_GEN_SIMPLE =   NameGenSimple.init();
        NAME_GEN_RX =       NameGenRx.init();
        // FxParamUtil.init() Glob.FX_PARAM_UTIL
        // ValidatorFx.init() Glob.VALIDATOR_FX
    }
}
