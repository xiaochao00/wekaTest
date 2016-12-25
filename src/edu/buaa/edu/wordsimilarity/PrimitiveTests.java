/*
 * Copyright (C) 2008 SKLSDE(State Key Laboratory of Software Development and Environment, Beihang University)., All Rights Reserved.
 */
package edu.buaa.edu.wordsimilarity;

import java.util.List;

import junit.framework.TestCase;


/**
 * DOCUMENT ME!
 *
 * @author Yingqiang Wu
 * @version 1.0
  */
public class PrimitiveTests extends TestCase {
    /**
     * test the method {@link Primitive#getParents(String)}.
     */
    public void test_getParents(){
        String primitive = "¹¥´ò";
        List<Integer> list = Primitive.getParents(primitive);
        for(Integer i : list){
            System.out.println(i);
        }
    }
}
