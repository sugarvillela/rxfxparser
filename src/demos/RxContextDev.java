package demos;

import java.util.ArrayList;
import toktools.TK;
import toktools.Tokens_special;
import unique.Unique;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RxContextDev {
    private static final Tokens_special T = new Tokens_special("", "('", TK.IGNORESKIP );
    private static final Unique uq = new Unique();
    private int maxLevel;
    private TreeNode root;
    public static final char AND = '&';
    public static final char OR = '|';
    public static final char EQUAL = '=';
    public static final char NOT = '~';
    public static final char OPAR = '(';
    public static final char CPAR = ')';
    public static final char QUOTE = '\'';
    
    public void testValidate(){
        RxValidation rxVal = new RxValidation();
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";
        System.out.println(text);
        System.out.println(rxVal.assertValidPairs(text));
        text = "dru='&'&LEN()=2";
        System.out.println(text);
        System.out.println(rxVal.assertValidPairs(text));
        text = "~(A=a&B='b)'&(C=c&D=d)|~(E=e&F=f)&'G'";
        System.out.println(text);
        System.out.println(rxVal.assertValidPairs(text));
        text = "dru='&'&LEN()=2'";
        System.out.println(text);
        System.out.println(rxVal.assertValidPairs(text));
        text = "())dru='&'&LEN(()=2'";
        System.out.println(text);
        System.out.println(rxVal.assertValidPairs(text));
    }
    public class RxValidation{
        public boolean assertValidPairs(String text){// even ( ) ratio
            int stackLevel = 0;
            boolean skip = false;
            for(int i=0; i<text.length(); i++){
                switch(text.charAt(i)){
                    case QUOTE:
                        skip = !skip;
                        break;
                    case OPAR:
                        if(!skip){
                            stackLevel++;
                        }
                        break;
                    case CPAR:
                        if(!skip){
                            stackLevel--;
                        }
                        break;
                }
                if(stackLevel < 0){
                    System.out.println("Bad parentheses");
                    return false;
                }
            }
            if(skip){
                System.out.println("Bad quotes");
            }
            if(stackLevel != 0){
                System.out.println("Bad parentheses");
            }
            return stackLevel == 0 && !skip;
        }
        public boolean assertSingleOccurrence(){
        Pattern p = Pattern.compile(".*(&&|\\|\\||==|~~).*");
        System.out.println(p.matcher("abc~~cded").matches()); 
        System.out.println(p.matcher("&&abc").matches()); 
        
        System.out.println(p.matcher("abc||").matches()); 
        //p = Pattern.compile(".*\\)\\(.*");//)( bad parenth
        //Pattern p = Pattern.compile("^[^{}]*\\{[0-9]+((-[0-9]+)?|-?)\\}$");  valid regex quantity
        //Pattern p = Pattern.compile("^.*\\{[0-9\\-]*\\}$");                   Allow invalid
            return false;
        }
        public void getQuantity(){
            Pattern p = Pattern.compile("\\{[0-9]+((-[0-9]+)?|-?)\\}$");
            Matcher matcher = p.matcher("~A=a&B='b'{5}");
            if(matcher.find()){
                System.out.println(matcher.start());
                System.out.println(matcher.group());//get quantity
                System.out.println(matcher.replaceFirst(""));//truncated
            }
            else{
                System.out.println("Not found"); 
            }
        }
    }
    public void testUnwrap(){
        TreeNode node = new TreeNode("(((ab)cde))", 0, ' ', 0);
        node.unwrap(OPAR, CPAR);
        node.payload = "'b'";
        node.unwrap(QUOTE, QUOTE);
        node.payload = "()";
        node.unwrap(OPAR, CPAR);
    }
    public void tokenize(){
        String text = "~(A=a&B='b')&(C=c&D=d)|~(E=e&F=f)&'G'";//"dru='&'&LEN()=2";
        System.out.println("root text: " + text);
        root = new TreeNode(text, 0, 'r', -1);
        boolean more;
        do{
            more = false;
            more |= root.split(AND);
            more |= root.split(OR);
            more |= root.split(EQUAL);
            root.negate();
            root.unwrap(OPAR, CPAR);
            root.unwrap(QUOTE, QUOTE);
        }while(more);
        
        System.out.println("root text: " + text);
        System.out.println("maxLevel: " + maxLevel);
        System.out.println("\nDisplay: ");
        //root.dispLeaves();
        breadthFirst();
    }
    public void setMaxLevel(int nuMax){
        if(nuMax > maxLevel){
            maxLevel = nuMax;
        }
    }
    public void breadthFirst(){
        //ArrayList<TreeNode>[] levels = new ArrayList<TreeNode>[maxLevel+1];
        maxLevel++;
        ArrayList<TreeNode>[] levels = new ArrayList[maxLevel];
        for(int i = 0; i < maxLevel; i++){
            levels[i] = new ArrayList<>();
        }
        root.breadthFirst(levels);
        for(int i = 0; i < maxLevel; i++){
            System.out.println("\nLevel: " + i);
            for(TreeNode node : levels[i]){
                node.disp();
            }
        }
    }
    public class TreeNode{
        public ArrayList<TreeNode> nodes;
        public String payload;
        public char op, parentOp;
        public boolean not;
        public int level, id, parentId;
        
        public TreeNode(String payload, int level, char parentOp, int parentId){
            this.payload = payload;
            this.level = level;
            this.parentOp = parentOp;
            this.parentId = parentId;
            this.id = uq.next();
            this.op = 'v';
            this.not = false;
            setMaxLevel(level);
        }
        public boolean go(char opSymbol){
            switch(opSymbol){
                case AND:
                case OR:
                case EQUAL:
                    return split(opSymbol);
                case NOT:
                    negate();
                    return false;
                case OPAR:
                    unwrap(OPAR, CPAR);
                    return false;
                case QUOTE:
                    unwrap(QUOTE, QUOTE);
                    return false;
                default:
                    return false;
            }
        }

        public boolean split(char delim){
            if(payload == null){
                if(nodes != null){
                    boolean more = false;
                    for(TreeNode node : nodes){
                        more |= node.split(delim);
                    }
                    return more;
                }
            }
            else{
                //System.out.println(level + ": split: " + delim + ": " + payload);
                nodes = new ArrayList<>();
                T.setDelims(delim);
                T.parse(payload);
                ArrayList<String> tokens = T.getTokens();
                if(tokens.size() > 1){
                    //System.out.println(level + "-----new---- " + delim);
                    op = delim;
                    
                    //System.out.printf("ADD_TO %c%d: %c%d ... %s\n", parentOp, parentId, op, id, payload );
                    payload = null;
                    for(String token : tokens){
                        this.addChild(new TreeNode(token, level + 1, op, id));
                    }
                    return true;
                }
            }
            return false;
        }
        public void negate(){
            if(payload != null){
                int i = 0;
                while(payload.charAt(i) == '~'){
                    not = !not;
                    i++;
                }
                if(i > 0){
                    payload = payload.substring(i);
                    //System.out.println(level + ": negate: " + not + ": payload = " + payload);
                }
            }
            if(nodes != null){
                for(TreeNode node : nodes){
                    node.negate();
                }
            }
        }
        public void unwrap(char first, char last){
            if(payload != null){
                int i = 0, len = payload.length();
                //System.out.println(payload);
                while(payload.charAt(i) == first && last == payload.charAt(len - i - 1)){
                    //System.out.println(i + " : " + (len - i) + " : " + payload.charAt(i) + " : " + payload.charAt(len - i));
                    i++;
                }
                if(i > 0){
                    payload = payload.substring(i, len - i);
                    //System.out.println(level + ": unwrap: " + payload);
                }
            }
            if(nodes != null){
                for(TreeNode node : nodes){
                    node.unwrap(first, last);
                }
            }
        }
        public void addChild(TreeNode node){
            //System.out.println(level + ": addChild: " + node.payload);
            nodes.add(node);
        }
        public void disp(){
            String dispPayload = (payload == null)? "" : payload;
            String dispNot = not? "!" : " ";
            System.out.printf("%d: parent %c%d -> %s%c%d: %s\n", 
                level, parentOp, parentId, dispNot, op, id, dispPayload
            );
        }
        public void dispLeaves(){
            disp();
            if(nodes != null){
                for(TreeNode node : nodes){
                    node.dispLeaves();
                }
            }
        }
        public void breadthFirst(ArrayList<TreeNode>[] levels){
            levels[level].add(this);
            if(nodes != null){
                for(TreeNode node : nodes){
                    node.breadthFirst(levels);
                }
            }
        }

    }
}
