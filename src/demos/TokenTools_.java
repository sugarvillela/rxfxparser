
package demos;

import toktools.*;

import commons.Commons;
import java.util.ArrayList;
/**
 *
 * @author Dave Swanson
 */
public class TokenTools_ {
    public static void map1(){
        System.out.println( Mapper.map1("Here's the sentence to be tokenized"));
        System.out.println( Mapper.map1(1));
        System.out.println( Mapper.map1(2));
    }
    public static void map2(){
        /* Split on |, keep index 1, split on - and return (0, 20) */
        String text = "enum|0-20"; 
        String map = "|1-";
        System.out.printf( "text=%s, map=%s\n", text, map );
        
        Commons.disp(Mapper.map2( text, map ));

//        /* Get stuff in parenth */
        text = "foo(stuff in parenth)";
        map = "(1)0";
        System.out.printf( "==================================================" );
        System.out.printf( "text=%s, map=%s\n", text, map );
        Commons.disp(Mapper.map2( text, map ));
//        
//        /* Split on colon and return item 2 of comma-separated list */
        text = "intro: item 0, item 1, item 2, item 3";
        map = ":1,2";
        System.out.printf( "==================================================" );
        System.out.printf( "text=%s, map=%s\n", text, map );
        Commons.disp(Mapper.map2( text, map ));
//        
        /* Simple tokenize */
        text = "0,1,2,3,4,5,6";
        map = ",";
        System.out.printf( "==================================================" );
        System.out.printf( "text=%s, map=%s\n", text, map );
        Commons.disp(Mapper.map2( text, map ));
//        
        /* Non-existent delimiter or index>= size of split returns array */
        text = "0,1,2,3,4,5,6";
        map = "-";
        System.out.printf( "==================================================" );
        System.out.printf( "text=%s, map=%s\n", text, map );
        Commons.disp(Mapper.map2( text, map ));
//        
        /* get foo *and* stuff in parenth */
        text = "foo(stuff in parenth)";
        System.out.printf( "==================================================" );
        System.out.printf( "text=%s, map=%s\n", text, map );
        Commons.disp(Mapper.map2( text, map ));
    }
    public static void map3(){
        /* Get every item in string */
        //Commons common = Commons.getInstance();
        String text = "a|b-c,d"; 
        String map = "|01-01,*";
        
        System.out.printf( "text=%s, map=%s\n", text, map );
        ArrayList<String> returnme =  new ArrayList<>();
        Mapper.map3( returnme, text, map, 55, 0 );
        Commons.disp(returnme);
        System.out.printf( "==================================================" );
        /* Get second to last item */
//        $text = "one,two,three,four,five";
//        /* Get second to last item */
//        L::og($text);
//        $map = ",_1,_1,_1,0";
//        L::og($map);
//        System.out.printf( "\nDone\n".implode(", ", TK::map3( $text, $map ))."" );
//        L::disp();
//        /* Get last two items */
//        $map = ",_1,_1,_1,";
//        L::og($map);
//        System.out.printf( "\nDone\n".implode(", ", TK::map3( $text, $map ))."" );
//        L::disp();
//        /* Get first and last item */
//        $map = ",01,_1,_1,_1,1";
//        L::og($map);
//        System.out.printf( "\nDone\n".implode(", ", TK::map3( $text, $map ))."" );
//        L::disp();
    }
    public static void tokens_simple_toList(){
        String text = "__First half_second half__third half_____fourth half__";
        Commons.disp(TK.toList('_', text), text );
        text = "I eat coconuts   all year long ";
        Commons.disp(TK.toList(' ', 5, text), text );
        text = "I eat coconuts all year long except Christmas";
        Commons.disp(TK.toList(' ', 2, text), text );
    }
    public static void tokens_simple_toArr(){
        Tokens_simple tk = new Tokens_simple();
        String text = "__First half_second half__third half_____fourth half__";
        Commons.disp(TK.toArr('_', text), text );
        text = "I eat coconuts   all year long ";
        Commons.disp(TK.toArr(' ', 5, text), text );
        text = "I eat coconuts all year long except Christmas";
        Commons.disp(TK.toArr(' ', 2, text), text );
    }
    public static void tokens_special(){
        boolean keepSkip1 =     true;
        boolean keepSkip2 =     true;
        boolean keepSkip3 =     true;
        //boolean skipOut =       false;
        boolean manyDelims =    true;
        boolean delimsIn =      true;
        
        String text;
        ArrayList<String> t;
        if( keepSkip1 ){
            text="Comment_=_\"This_is_my_comment_(in_quotes!)\"";
            System.out.printf( "\n=============================\nOrig text:\n%s\n", text );
            t = TK.toList(
                "_",                            //delimiter
                "(\"",                          //skip symbol
                TK.SYMBOUT,                      //flag to remove skip symbole, TK.DELIMIN|TK.SYMBOUT
                text                           //string input
            );
            Commons.disp( t, "\nTokens:" );
        }
        if( keepSkip2 ){
            text="Comment_=_(This_is_my_comment_\"in_parentheses!\")_so_yeah";
            System.out.printf( "\n=============================\nOrig text:\n%s\n", text );
            t = TK.toList(
                "_",                            //delimiter
                "(\"",                          //skip symbol
                TK.SYMBOUT,                      //flag to remove skip symbole, TK.DELIMIN|TK.SYMBOUT
                text                           //string input
            );
            Commons.disp( t, "\nTokens:" );
        }
        if( keepSkip3 ){
            text="These_parentheses_are(connected_without_a_delimiter_before!)or_after";//or_after
            System.out.printf( "\n=============================\nOrig text:\n%s\n", text );
            t = TK.toList(
                "_",                            //delimiter
                "(\"",                          //skip symbol
                TK.SYMBOUT,                      //flag to remove skip symbole, TK.DELIMIN|TK.SYMBOUT
                text                           //string input
            );
            Commons.disp( t, "\nTokens:" );
        }
//        if( skipOut ){
//            text="Comment_=_(This_is_my_comment_\"in_parentheses!\")_so_yeah";
//            System.out.printf( "\n=============================\nOrig text:\n%s\n", text );
//            t = TK.toList(
//                "_",                            //delimiter
//                text,                           //string input
//                "(\"",                          //skip symbol
//                TK.SYMBOUT|TK.SKIPOUT                      //flag to remove skip symbole, TK.DELIMIN|TK.SYMBOUT
//            );
//            Commons.disp( t, "\nTokens:" );
//        }
        if( manyDelims ){
            text="||We_!got!-lots_of-delimiters";
            
            System.out.printf( "\n=============================\nOrig text:\n%s\n", text );
            t = TK.toList(
                "|_!-",                         //delimiter
                "(\"",                          //skip symbol
                0,                              //default flags
                text                            //string input
            );
            Commons.disp( t, "\nTokens:" );
        }
        if( delimsIn ){
            //text="NoDelimiters";
            //text="myPatternIsThis&myWillIsThat";
            text="^text.in=scratch_(flag.poss|pos.det)_!pos.adj|pos.gerund*_head$";
            System.out.printf( "\n=============================\nOrig text:\n%s\n", text );
            t = TK.toList(
                "_.*+?!&|=()^$",                            //delimiter
                "(\"",                          //skip symbol
                TK.DELIMIN,                      //flag to give delim own element
                text                           //string input
            );
            Commons.disp( t, "\nTokens:" );
        }
    }
    public static void tokens_wSkipHold_skip(){
        Tokens tk = TK.getInstance(
            " ", "(\"", TK.SYMBOUT|TK.SKIPOUT
        );//new Tokens_special();//
       
        String text1="This is a sentence(with secret text) with words \"you can't see\"";      

        tk.parse(text1);
        Commons.disp( tk.getTokens(), "Tokens:" );
        Commons.disp( tk.getSkips(), "Skips:" );
    }
    public static void tokens_wSkipHold_hold(){
        Tokens tk = TK.getInstance("_", "(\"", TK.HOLDOVER);
       
        String text1="This_is_a_series_(that_starts";
        String text2="on_one_line)_and_ends";
 
        tk.parse(text1);
        Commons.disp( tk.getTokens(), "\nTokens 1:" );

        tk.parse(text2);
        Commons.disp( tk.getTokens(), "\nTokens 2:" );
    }

    public static void tokens_byGroup(){
        Tokens_byGroup tk = new Tokens_byGroup(
            new String[]{"<=>", "!", " "},
            "",
            0
        );
        String text = "mildew<=tacos && !frankfurters";
        tk.parse(text);
        Commons.disp( tk.getTokens(), text );
    }
}
