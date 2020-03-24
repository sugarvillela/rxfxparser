/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rxfxparser;

import commons.Erlog;

/**
 *
 * @author admin
 */
public class RxTest_base {
    public static void demo(){
        RxTest_base rx = new RxTest_base();
        rx.init("myPattern{3-15}");
        rx.init("myPattern{23-26}");
        rx.init("myPattern{5}");
    }
    protected int lo,  hi,  count;
    protected String pattern;
    protected Erlog er;
    
    public RxTest_base(){
        er = Erlog.getInstance();
    }
    public void reset(){
        this.count=0;
    }
    protected void trunc(){
        pattern = pattern.substring( 0, pattern.length()-1 );
    }
    protected void parseRange(){
        System.out.printf( "\nparseRange %s\n", pattern );
        int i = 0, j = 0;
        for(i=0; i<pattern.length()-1; i++){
            if(pattern.charAt(i)=='{'){
                System.out.printf( "found { at %d\n", i );
                break;
            }
        }
        for(j=i; j<pattern.length()-1; j++){
            if(pattern.charAt(j)=='-'){
                System.out.printf( "found - at %d\n", j );
                System.out.printf( "lo =%s\n", pattern.substring(i+1, j) );
                System.out.printf( "hi =%s\n", pattern.substring(j+1, pattern.length()-1) );
                System.out.printf( "pattern changed to %s\n", pattern.substring(0, i) );
                return;
                //lo = Integer.parseInt(pattern.substring(i, j));
            }
        }
        System.out.printf( "lo = hi=%s\n", pattern.substring(i+1, pattern.length()-1) );
        System.out.printf( "pattern changed to %s\n", pattern.substring(0, i) );
    }
    public void init( String str ) {
        if( str.length() == 0){
            er.set("RxTest: empty string");
            return;
        }
        pattern = str;
        int max = 1024;
        char lastChar = pattern.charAt(pattern.length()-1);
        switch ( lastChar ){
            case '*':
                trunc();
                this.lo = 0;
                this.hi = max;
                break;
            case '+':
                trunc();
                this.lo=1;
                this.hi = max;
                break;
            case '?':
                trunc();
                this.lo=0;
                this.hi=1;
                break;
            case '}':
                parseRange();
                break;
            default:
                this.lo=1;
                this.hi=1;
                break;
        }
        this.count=0;
    }
    public void inc(){
        this.count++;
    }
    public boolean inRange(){
        System.out.printf("inRange: count=%d, lo=%d, hi=%d\n", this.count, this.lo,this.hi);
        return this.hi >= this.count && this.count >= this.lo;
    }
    @Override
    public String toString(){
        return this.pattern;
    } 
    public boolean test( RxTest_base obj ){return false;}
}
