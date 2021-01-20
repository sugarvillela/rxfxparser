/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xcode.lemmatizer;

import commons.Commons;
import xcode.lemmatizer.*;

/**
 *
 * @author admin
 */
public class Lemmatizer_ {
    public static void starts(){
        System.out.println("StartsWell???");
        Prefix pref = new Prefix();
        String[] words = {"zzyx","apple","pfister","oompah","lynch","ypyppp","yeah","prunes","pyrite","yrank"};
        for(String word : words){
            System.out.printf("%s... %b\n",word, pref.startsWell(word));
        }
    }
    public static void ends(){
        System.out.println("EndsWell???");
        Suffix suff = new Suffix();
        String[] words = {"kipr","apple","stipl","egg","robyn","mulch","lewd","lisp","down","oggohg","to"};
        for(String word : words){
            System.out.printf("%s... %b\n",word, suff.endsWell(word));
        }
    }    
    public static void prefix(){
        Prefix pref = new Prefix();
        String origText="reoverunderheat";
        pref.set(origText);
        System.out.printf("\nFor origText: %s, have prefix = %b\n", origText, pref.haveAffix());
        Commons.disp( pref.get(), "root list returned" );
    }
    public static void tracebackP(){
        Traceback_P trace = new Traceback_P();
        String prefText = "reoverunder";
        trace.set(prefText);
        Commons.disp( trace.get(), "prefix list returned" );
    }
    public static void suffix(){
        Suffix suff = new Suffix();//anatomically vs automatically
        String[] words = {
            "anatomically"//,"implicationists","simply","muddy","atrocities","automatically"
        };
        for(int i=0; i<words.length; i++){
            suff.set(words[i]);
            System.out.printf("\nFor origText: %s, have suffix = %b\n", words[i], suff.haveAffix());
            Commons.disp( suff.get(), "\nroot list returned" );
        }
    }
    public static void tracebackS(){
        Traceback_S trace = new Traceback_S();
        String[] words = {
            "implicationists","simply","muddy","atrocities","anatomically","automatically"
        };
        String[] roots = {
            "imply","simple","mud","atrocity","anatomy","automatic"
        };
        for(int i=0; i<words.length; i++){
            trace.setTargetRoot(roots[i]);
            trace.set(words[i]);
            System.out.printf("\nFor word %s, root %s, have suffix = %b\n", words[i], roots[i], trace.haveAffix());
            Commons.disp( trace.get(), "\nsuff list returned" );
        }
    }
    public static void lemmatizer(){
        Lemmatizer lem = new Lemmatizer();
        String[] words = {
            "nonimplicationists"//,"simply","muddy","atrocities"
        };
        String[] roots = {
            "imply","simple","mud","atrocity"
        };
        for(int i=0; i<words.length; i++){
            lem.set(words[i]);
            System.out.printf("\nFor origText: %s, have suffix = %b\n", words[i], lem.haveAffix());
            Commons.disp( lem.get(), "\nroot list returned" );
//            trace.setTargetRoot(roots[i]);
//            trace.set(words[i]);
//            System.out.printf("\nFor root word: %s, have suffix = %b\n", roots[i], trace.haveAffix());
//            Commons.disp( trace.get(), "\nsuff list returned" );
        }
    }
    public static void LemmTest(){
        String origText="overeatery";
        String rootText="eat";
        Lemmatizer lem = new Lemmatizer();
        System.out.printf("\nFor origText containing %s:\n", rootText);
        lem.set( origText );
        if( lem.haveAffix()){
            Commons.disp( lem.get(), "roots list returned" );
            lem.traceback( rootText, origText );
            Commons.disp( lem.getPref(), "pref list returned" );
            Commons.disp( lem.getSuff(), "suff list returned" );
        }
        origText="undersecretary";
        rootText="secretary";
        System.out.printf("\nFor origText containing %s:\n", rootText);
        lem.set( origText );
        if( lem.haveAffix() ){
            Commons.disp( lem.get(), "roots list returned" );
            lem.traceback( rootText, origText );
            Commons.disp( lem.getPref(), "pref list returned" );
            Commons.disp( lem.getSuff(), "suff list returned" );
        }
        origText="disproportionately";
        rootText="proportion";
        System.out.printf("\nFor origText containing %s:\n", rootText);
        lem.set( origText );
        if( lem.haveAffix() ){
            Commons.disp( lem.get(), "roots list returned" );
            lem.traceback( rootText, origText );
            Commons.disp( lem.getPref(), "pref list returned" );
            Commons.disp( lem.getSuff(), "suff list returned" );
        }
        origText="undying";
        rootText="die";
        System.out.printf("\nFor origText containing %s:\n", rootText);
        lem.set( origText );
        if( lem.haveAffix() ){
            Commons.disp( lem.get(), "roots list returned" );
            lem.traceback( rootText, origText );
            Commons.disp( lem.getPref(), "pref list returned" );
            Commons.disp( lem.getSuff(), "suff list returned" );
        }
    }
}
