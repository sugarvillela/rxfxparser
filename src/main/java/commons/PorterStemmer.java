package commons;

import toktools.TK;

/**
 * This is a simple implementation of the Porter stemming algorithm, defined here:
 * <a href="http://tartarus.org/martin/PorterStemmer/def.txt">http://tartarus.org/martin/PorterStemmer/def.txt</a>
 * <p>
 * This implementation has not been tuned for high performance on large amounts of text. It is
 * a simple (naive perhaps) implementation of the rules.
 */
public class PorterStemmer {
    static String word;
    
    /**
     * @param setWord the word to stem
     * @return the stem of the word, in lowercase.
     */
    public static String stem(String setWord){
        word = setWord.toLowerCase().replaceAll("[^a-z0-9]", "").trim();
        //System.out.println(word);
        if (word.length() < 3){
            return word;
        }
        stemStep1a();
        stemStep1b();
        stemStep1c();
        stemStep2();
        stemStep3();
        stemStep4();
        stemStep5a();
        stemStep5b();        
        return word;
    }
    /**
     * @param words array of words to stem; modifies input array
     * @return stemmed words; may be shorter if any symbol-only words
     */
    public static String[] stem(String[] words){
        boolean remove = false;
        for(int i=0; i<words.length; i++){
            if( (words[i]=stem(words[i])).isEmpty() ){
                words[i]=null;
                remove = true;
            }
        }
        if(remove){
            return Commons.copyNonNull(words);
        }
        return words;
    }
    public static String[] stemAll(String sentence){
        return stem(TK.toArr(' ', sentence.replaceAll("[^a-zA-z0-9]", " ").trim()));
    }
    static String[] getList2a(){
        return new String[]{
                "ational",
                "tional",
                "enci",
                "anci",
                "izer",
                "bli", // the published algorithm specifies abli instead of bli.
                "alli",
                "entli",
                "eli",
                "ousli",
                "ization",
                "ation",
                "ator",
                "alism",
                "iveness",
                "fulness",
                "ousness",
                "aliti",
                "iviti",
                "biliti",
                "logi", // the published algorithm doesn't contain this
        };
    }
    static String[] getList2b(){
        return new String[]{
                "ate",
                "tion",
                "ence",
                "ance",
                "ize",
                "ble", // the published algorithm specifies able instead of ble
                "al",
                "ent",
                "e",
                "ous",
                "ize",
                "ate",
                "ate",
                "al",
                "ive",
                "ful",
                "ous",
                "al",
                "ive",
                "ble",
                "log" // the published algorithm doesn't contain this
        };
    }
    static String[] getList3a(){
        return new String[]{
                "icate",
                "ative",
                "alize",
                "iciti",
                "ical",
                "ful",
                "ness",
        };
    }
    static String[] getList3b(){
        return new String[]{
                "ic",
                "",
                "al",
                "ic",
                "ic",
                "",
                "",
        };
    }
    static String[] getList4(){
        return new String[]{
                "al",
                "ance",
                "ence",
                "er",
                "ic",
                "able",
                "ible",
                "ant",
                "ement",
                "ment",
                "ent",
                "ion",
                "ou",
                "ism",
                "ate",
                "iti",
                "ous",
                "ive",
                "ize",
        };
    }     
    
    static void stemStep1a() {
        if (word.endsWith("sses")) {// SSES -> SS
            word = word.substring(0, word.length() - 2);
        }
        else if (word.endsWith("ies")) {// IES  -> I
            word = word.substring(0, word.length() - 2);
        }
        else if (!word.endsWith("ss") && word.endsWith("s")) {// S    ->
            word = word.substring(0, word.length() - 1);
        }
    }

    static void stemStep1b() {
        if (word.endsWith("eed")) {// (m>0) EED -> EE
            String stem = word.substring(0, word.length() - 1);
            String letterTypes = getLetterTypes(stem);
            int m = getM(letterTypes);
            if (m > 0){
                word = stem;
            }
        }
        else if (word.endsWith("ed")) {// (*v*) ED  ->
            String stem = word.substring(0, word.length() - 2);
            String letterTypes = getLetterTypes(stem);
            if (letterTypes.contains("v")) {
                word = stem;
                step1b2();
            }
        }
        else if (word.endsWith("ing")) {// (*v*) ING ->
            String stem = word.substring(0, word.length() - 3);
            String letterTypes = getLetterTypes(stem);
            if (letterTypes.contains("v")) {
                word = stem;
                step1b2();
            }
        }
    }

    static void step1b2() {
        if (word.endsWith("at")) {// AT -> ATE
            word = word + "e";
        }
        else if (word.endsWith("bl")) {// BL -> BLE
            word = word + "e";
        }
        else if (word.endsWith("iz")) {// IZ -> IZE
            word = word + "e";
        } 
        else {
            // (*d and not (*L or *S or *Z))
            // -> single letter
            char lastDoubleConsonant = getLastDoubleConsonant(word);
            if (lastDoubleConsonant != 0 &&
                    lastDoubleConsonant != 'l'
                    && lastDoubleConsonant != 's'
                    && lastDoubleConsonant != 'z') {
                word = word.substring(0, word.length() - 1);
            }
            // (m=1 and *o) -> E
            else {
                String letterTypes = getLetterTypes(word);
                int m = getM(letterTypes);
                if (m == 1 && isStarO(word)) {
                    word = word + "e";
                }
            }
        }
    }

    static void stemStep1c() {
        if (word.endsWith("y")) {
            String stem = word.substring(0, word.length() - 1);
            String letterTypes = getLetterTypes(stem);
            if (letterTypes.contains("v")){
                word = stem + "i";
                //System.out.println("1c true");
            }
            else{
                //System.out.println("1c false");
            }
        }
    }

    static void stemStep2() {
        String[] A = getList2a();
        String[] B = getList2b();
        // (m>0) ATIONAL ->  ATE
        // (m>0) TIONAL  ->  TION
        for (int i = 0; i < A.length; i++) {
            if (word.endsWith(A[i])) {
                String stem = word.substring(0, word.length() - A[i].length());
                String letterTypes = getLetterTypes(stem);
                int m = getM(letterTypes);
                if (m > 0){
                    word = stem + B[i];
                }
                return;
            }
        }
    }

    static void stemStep3() {
        String[] A = getList3a();
        String[] B = getList3b();
        // (m>0) ICATE ->  IC
        // (m>0) ATIVE ->
        for (int i = 0; i < A.length; i++) {
            if (word.endsWith(A[i])) {
                String stem = word.substring(0, word.length() - A[i].length());
                String letterTypes = getLetterTypes(stem);
                int m = getM(letterTypes);
                if (m > 0){
                    word = stem + B[i];
                }
                return;
            }
        }
    }

    static void stemStep4() {
        String[] suffixes = getList4();
        // (m>1) AL    ->
        // (m>1) ANCE  ->
        for(String suffix : suffixes) {
            if (word.endsWith(suffix)) {
                String stem = word.substring(0, word.length() - suffix.length());
                String letterTypes = getLetterTypes(stem);
                int m = getM(letterTypes);
                if (m > 1) {
                    if (suffix.equals("ion")) {
                        if (stem.charAt(stem.length() - 1) == 's' || stem.charAt(stem.length() - 1) == 't') {
                            word = stem;
                            return;
                        }
                    } else {
                        word = stem;
                        return;
                    }
                }
                return;
            }
        }
    }

    static void stemStep5a() {
        if (word.endsWith("e")) {
            String stem = word.substring(0, word.length() - 1);
            String letterTypes = getLetterTypes(stem);
            int m = getM(letterTypes);
            // (m>1) E     ->
            if (m > 1) {
                word = stem;
            }
            // (m=1 and not *o) E ->
            else if (m == 1 && !isStarO(stem)) {
                word = stem;
            }
        }
    }
    String stemStep5s(String input) {
        if (input.endsWith("e")) {
            String stem = input.substring(0, input.length() - 1);
            String letterTypes = getLetterTypes(stem);
            int m = getM(letterTypes);
            // (m>1) E     ->
            if (m > 1) {
                return stem;
            }
            // (m=1 and not *o) E ->
            if (m == 1 && !isStarO(stem)) {
                return stem;
            }
        }
        return input;
    }    

    static void stemStep5b() {
        // (m > 1 and *d and *L) -> single letter
        String letterTypes = getLetterTypes(word);
        int m = getM(letterTypes);
        if (m > 1 && word.endsWith("ll")) {
            word = word.substring(0, word.length() - 1);
        }
    }

    static char getLastDoubleConsonant(String input) {
        if (input.length() < 2) return 0;
        char lastLetter = input.charAt(input.length() - 1);
        char penultimateLetter = input.charAt(input.length() - 2);
        if (lastLetter == penultimateLetter && getLetterType((char) 0, lastLetter) == 'c') {
            return lastLetter;
        }
        return 0;
    }

    // *o  - the stem ends cvc, where the second c is not W, X or Y (e.g.
    //                                                              -WIL, -HOP)
    static boolean isStarO(String input) {
        if (input.length() < 3) return false;

        char lastLetter = input.charAt(input.length() - 1);
        if (lastLetter == 'w' || lastLetter == 'x' || lastLetter == 'y') return false;

        char secondToLastLetter = input.charAt(input.length() - 2);
        char thirdToLastLetter = input.charAt(input.length() - 3);
        char fourthToLastLetter = input.length() == 3 ? 0 : input.charAt(input.length() - 4);
        return getLetterType(secondToLastLetter, lastLetter) == 'c'
                && getLetterType(thirdToLastLetter, secondToLastLetter) == 'v'
                && getLetterType(fourthToLastLetter, thirdToLastLetter) == 'c';
    }

    static String getLetterTypes(String input) {
        StringBuilder letterTypes = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char letter = input.charAt(i);
            char previousLetter = i == 0 ? 0 : input.charAt(i - 1);
            char letterType = getLetterType(previousLetter, letter);
            if (letterTypes.length() == 0 || letterTypes.charAt(letterTypes.length() - 1) != letterType) {
                letterTypes.append(letterType);
            }
        }
        return letterTypes.toString();
    }

    static int getM(String letterTypes) {
        if (letterTypes.length() < 2) return 0;
        if (letterTypes.charAt(0) == 'c') {
            return (letterTypes.length() - 1) / 2;
        }
        return letterTypes.length() / 2;
    }

    static char getLetterType(char previousLetter, char letter) {
        switch (letter) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return 'v';
            case 'y':
                if (previousLetter == 0 || getLetterType((char) 0, previousLetter) == 'v') {
                    return 'c';
                }
                return 'v';
            default:
                return 'c';
        }
    }

    
}

