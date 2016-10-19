package com.zweigbergk.speedswede.util.collection;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by FEngelbrektsson on 19/10/16.
 */
public class CollectionsTest {

    @Test
    public static void testEmptySet() {
        Set<Object> testSet = Collections.emptySet();
        testSet.add(1);
        testSet.add(2);
        testSet.add(3);
        testSet.add(4);
        testSet.add(5);
        testSet.add(6);
        testSet.add(7);
        testSet.add(8);
        testSet.add(9);

        assertTrue(testSet.contains(1));
    }

    @Test
    public static void testEmptyList() {
        List<Object> testList = Collections.emptyList();
        testList.add(1);
        testList.add(2);
        testList.add(3);
        testList.add(4);
        testList.add(5);
        testList.add(6);
        testList.add(7);
        testList.add(8);
        testList.add(9);
        assertTrue(testList.contains(1));
    }
}