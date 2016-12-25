/*
 * Copyright (C) 2008 SKLSDE(State Key Laboratory of Software Development and Environment, Beihang University)., All Rights Reserved.
 */
package edu.buaa.edu.wordsimilarity;

import junit.framework.TestCase;


/**
 * DOCUMENT ME!
 *
 * @author Yingqiang Wu
 * @version 1.0
  */
public class WordSimilarityTests extends TestCase {
    public void test_loadGlossary(){
        WordSimilarity.loadGlossary();
    }
    /**
     * test the method {@link WordSimilarity#disPrimitive(String, String)}.
     */
    public void test_disPrimitive(){
        int dis = WordSimilarity.disPrimitive("雇用", "争斗");
        System.out.println("雇用 and 争斗 dis : "+ dis);
    }
    
    public void test_simPrimitive(){
        double simP = WordSimilarity.simPrimitive("雇用", "争斗");
        System.out.println("雇用 and 争斗 sim : "+ simP);
    }
    public void test_simWord(){
        String word1 = "牛";
        String word2 = "猪";
        double sim = WordSimilarity.simWord(word2, word1);
        System.out.println(sim);
    }
    
}
