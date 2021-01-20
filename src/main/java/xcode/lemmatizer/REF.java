package xcode.lemmatizer;

import commons.Commons;
import commons.tinymaps.CSVMap;
import java.util.HashMap;

/**Static data source for Lemmatizer operations
 *
 * @author Dave Swanson
 */
public class REF {
    /* Keep MAXWORDLEN updated with longest word in word list database */
    public static final int MAXWORDLEN=15;
    public static final int MINWORDLEN=2;
    /* Keep PREF and SUF updated from lists below */
    public static final int MAXPREFLEN=7;
    public static final int MAXSUFLEN=6;
    
    private static CSVMap lastResult = null;

    public static boolean inStarts( String chunk ){
        return Commons.binarySearch( STARTS, chunk )!=-1;
    }
    public static boolean inEnds( String chunk ){
        return Commons.binarySearch( ENDS, chunk )!=-1;
    }
    public static boolean isPrefix( String text ){
        return isPrefix(text,text.length());
    }
    public static boolean isPrefix( String text, int len ){
        return (len < PREFIXES.length && Commons.indexOf(text, PREFIXES[len]) != -1);
    }
    public static boolean isSuffix( String text ){
        // three states: 
        //   (1) null if no suffix, 
        //   (2) empty map if no substitutions
        //   (3) map with content
        // This function populates lastResult and checks null
        // False if null, true if empty or full LiteMap
        return SUFFIXES.containsKey(text) && ( lastResult = SUFFIXES.get(text) ) != null;
    }
    public static CSVMap getSuffix(){
        // returns map set by call to isSuffix, or last getSuffix(text)
        return lastResult;
    }
    public static CSVMap getSuffix( String text ){
        return ( lastResult = SUFFIXES.get(text) );
    }
    
    public static final String[] STARTS = {//double-consonant valid combos
        "bl", "br", "ch", "cl", "cr", "dr", 
        "dw", "fl", "fr", "gh", "gl", "gr", 
        "kl", "kn", "kr", "kw", "ll", "mn", 
        "ph", "pl", "pn", "pr", "ps", "pt", 
        "rh", "sc", "sh", "sk", "sl", "sm", 
        "sn", "sp", "sq", "st", "sw", "th", 
        "tr", "tw", "wh", "wr", "xr"
    };
    public static final String[] ENDS = {
        "bt", "ch", "ck", "ct", "gh", "ht", 
	"lb", "lc", "ld", "lf", "lk", "lm", 
        "lp", "lt", "mb", "mn", "mp", "nc", 
        "nd", "ng", "nk", "nt", "ph", "pt", 
        "rb", "rc", "rd", "rf", "rg", "rk", 
        "rl", "rm", "rn", "rp", "rt", "rv", 
        "sc", "sh", "sk", "sm", "sp", "st", 
        "th", "wb", "wd", "wk", "wl", "wn"
    };
    public static final String[][] PREFIXES = {
        {},
        {"a"},
        {"in","de","re","ir","il","co","em","im","en","un","bi"},
        {"ego","geo","mis","non","tri","ex-","uni","mid","pre","sub","dis","out"},
        {"fore","mega","meta","mono","deca","kilo","nano","poly","post","semi","theo","anti","mini","over","uber"},
        {"anthro","inter","after","super","hyper","ultra","centi","milli","micro","under","macro","extra","multi","quadr","trans"},
        {"thermo"},
        {"counter"}
    };

    public static final HashMap<String,CSVMap> SUFFIXES = new HashMap<>();
    
    static{
        SUFFIXES.put("aholic", new CSVMap(0) );// shopaholic
        SUFFIXES.put("cation", new CSVMap( "cate","ion" ) );// + y implication vs education
        SUFFIXES.put("ssible", new CSVMap( "ss", "ible","t", "ible") );// permissible admissible...pretty irregular NEW!!
        SUFFIXES.put("arian", new CSVMap( "ary", "an","y", "an") );//totalitarian, authoritarian, sectarian vs veterinarian...can"t be exclusive NEW!!!
            //"arily", new LiteMap( "ary", "ly") );//momentarily 
        SUFFIXES.put("ative", new CSVMap( "ate", "ive", "e", "ive") ); //initiative vs representative vs restorative
        SUFFIXES.put("ctive", new CSVMap( "ce", "ive","ct", "ive") );//deductive seductive vs interactive
        SUFFIXES.put("ility", new CSVMap( "le", "ity","ile", "ity") );//acceptability agility
        SUFFIXES.put("ition", new CSVMap( "ish", "ion") );//demolition vs partition, ignition ok by max
        SUFFIXES.put("itive", new CSVMap( "it", "ive","ish", "ive") );//exhibitive punitive vs additive 
        SUFFIXES.put("itize", new CSVMap( "e", "ize","it", "ize","ity", "ize") );//digitize prioritize vs sensitize "ite", "ize"
        SUFFIXES.put("itude", new CSVMap( "ite", "ude") );//infinitude vs exactitude //roots
        SUFFIXES.put("iture", new CSVMap( "ish", "ure") );//furniture vs expenditure
        SUFFIXES.put("ocate", new CSVMap( "oke", "ate") );// provocate
        SUFFIXES.put("ssion", new CSVMap( "ss", "ion","", "ion") );//+de +t procession vs admission vs compression...very irregular NEW!! No, finds process, procede
        SUFFIXES.put("uous", new CSVMap( "ue", "ous") );//continuous vs incestuous
        SUFFIXES.put("able", new CSVMap( "e", "able","ate", "able") );//demonstrable vs lovable
        SUFFIXES.put("ally", new CSVMap( "al", "ly") );//anatomically vs automatically
        SUFFIXES.put("ator", new CSVMap( "e", "or", "ate", "or") );//inflamator-y circulator vs signatory  circulator
        SUFFIXES.put("ation", new CSVMap( "ate", "ion") );//violation vs determination
        SUFFIXES.put("cant", new CSVMap( "cate", "ant") );//lubricant vs applicant
        SUFFIXES.put("cian", new CSVMap( "c", "ian") );//electrician vs beautician
        SUFFIXES.put("ence", new CSVMap( "ent", "ence","e", "ence") );//different vs adherence
        SUFFIXES.put("iage", new CSVMap( "y", "age") );// marriage vs verbiage
        SUFFIXES.put("ible", new CSVMap(0) );//no rule  word or root
        SUFFIXES.put("ical", new CSVMap( "ic", "al","y", "al","e", "al") );// +y astronomical vs acoustical NEW!!! and kill cal
        SUFFIXES.put("ings", new CSVMap(0) );// no rule!
        SUFFIXES.put("ious", new CSVMap( "ion", "ous","y", "ous","e", "ous") );//+ion fallacious vs religious, +e spacious
        SUFFIXES.put("itor", new CSVMap( "it", "or","e", "or") );//depositor vs competitor
        SUFFIXES.put("less", new CSVMap(0) );// no rule!
        SUFFIXES.put("like", new CSVMap(0) );
        SUFFIXES.put("ment", new CSVMap(0) );
        SUFFIXES.put("ness", new CSVMap(0) );//no rule ipos=adjective
        SUFFIXES.put("ress", new CSVMap(0) );//er or?
        SUFFIXES.put("ship", new CSVMap(0) );//No rule
        SUFFIXES.put("sion", new CSVMap( "se", "ion","de", "ion","d", "ion") );//+d fusion vs comprehension
        SUFFIXES.put("sive", new CSVMap( "s", "ive","se", "ive","de", "ive","re", "ive") );//+ convulsive abrasive, elusive + re adhesive vs repulsive obsessive
        SUFFIXES.put("sual", new CSVMap( "se", "al","t", "al") );// consensual vs sensual
        SUFFIXES.put("tize", new CSVMap( "t", "ize") );// vs dramatize
        SUFFIXES.put("ture", new CSVMap( "t", "ure") );//moisture vs mixture NEW!!!
            //"ate", new LiteMap(0) );//all roots
        SUFFIXES.put("age", new CSVMap( "e", "age") );//assemblage usage
        SUFFIXES.put("ant", new CSVMap( "ate", "ant") );// consultant vs participant
        SUFFIXES.put("ary", new CSVMap( "ar", "y") );//burglary vs legendary
        SUFFIXES.put("ate", new CSVMap( "e", "ate") );//all roots but in-between words can exist: inflamate
        SUFFIXES.put("ent", new CSVMap( "end", "ent") );//dependent vs ascent
        SUFFIXES.put("ess", new CSVMap(0) );//No rule
        SUFFIXES.put("est", new CSVMap( "e", "est") );//closest
        SUFFIXES.put("ful", new CSVMap(0) );
        SUFFIXES.put("ial", new CSVMap( "ia", "al","e", "al") );//bacterial vs partial vs official
        SUFFIXES.put("ian", new CSVMap( "ia", "an") );//civilian vs armenian // add countries to database
        SUFFIXES.put("ify", new CSVMap( "e", "ify", "y", "ify", "ic", "ify") );//glorify electric vs humidify
        SUFFIXES.put("ile", new CSVMap( "e", "ile") );//servile vs infantile
            //"ily", new LiteMap( "y", "ly") );//happily
        SUFFIXES.put("ing", new CSVMap( "e", "ing") );//behaving
        SUFFIXES.put("ion", new CSVMap( "e", "ion") );//ignition
        SUFFIXES.put("ior", new CSVMap( "e", "ior") );//behavior
        SUFFIXES.put("ish", new CSVMap(0) );//no rule
        SUFFIXES.put("ism", new CSVMap( "e", "ism") );
        SUFFIXES.put("ist", new CSVMap( "e", "ist") );//no rule
        SUFFIXES.put("ity", new CSVMap( "e", "ity") );//sanity
        SUFFIXES.put("ive", new CSVMap( "e", "ive") );//creative
        SUFFIXES.put("ium", new CSVMap(0) );//crematorium, consortium...mostly roots
        SUFFIXES.put("ize", new CSVMap( "e", "ize","ive", "ize","y", "ize") );// no rule
        SUFFIXES.put("ous", new CSVMap( "e", "ous", "y", "ous") );// virtuous adulterous vs cavernous roots!!!
        SUFFIXES.put("ual", new CSVMap( "ue", "al") );// consensual vs sexual
        SUFFIXES.put("ure", new CSVMap( "e", "ure") );//Forclosure
        SUFFIXES.put("al", new CSVMap(0) );//accidental
        SUFFIXES.put("an", new CSVMap( "a", "an") );//republican vs american
        SUFFIXES.put("ar", new CSVMap( "e", "ar") );//burglar
        SUFFIXES.put("ed", new CSVMap( "e", "ed") );//closed
        SUFFIXES.put("en", new CSVMap( "e", "en") );//chasten enliven forgiven
        SUFFIXES.put("er", new CSVMap( "e", "er") );//closer freezer
        SUFFIXES.put("es", new CSVMap( "e", "s") );//freezes
        SUFFIXES.put("ic", new CSVMap( "y", "ic", "ia", "ic") );//comedic anemic vs idiotic
        SUFFIXES.put("ly", new CSVMap(0) );
        SUFFIXES.put("or", new CSVMap( "e", "or") );//indicator
        SUFFIXES.put("s", new CSVMap(0) );
        SUFFIXES.put("y", new CSVMap(0) );
    }
    

    
    /* These are not in suffix table yet
        itize=ize
        ocate=ate
        ssible
        ility
        ssion
        trism
        cant
        ical
        sive
        ture
     * ate
    //"oholic", new LiteMap( "ohol", "aholic") );// shopaholic
     */
}
