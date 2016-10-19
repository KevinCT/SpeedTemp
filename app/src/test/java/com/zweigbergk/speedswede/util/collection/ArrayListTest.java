package com.zweigbergk.speedswede.util.collection;

import org.junit.Test;

import java.lang.reflect.Array;

import static org.junit.Assert.*;

/**
 * Created by FEngelbrektsson on 19/10/16.
 */
public class ArrayListTest {

    @Test
    public static void testUnion() {
        ArrayList<Object> iterable = new ArrayList<>();
        Set<Object> i = iterable.union(iterable);

        assertTrue(i == null);
    }

    @Test
    public static void testDifference() {
        ArrayList<Object> iterable = new ArrayList<>();
        Set<Object> i = iterable.difference(iterable);

        assertTrue(i == null);
    }

    @Test
    public static void testGetFirst() {
        ArrayList<Object> arre = new ArrayList<>();
        arre.add(1);
        arre.add(2);
        arre.add(3);
        arre.add(4);
        arre.add(5);
        arre.add(6);
        arre.add(7);
        arre.add(8);
        arre.add(9);

        assertTrue((int)arre.getFirst() == 1);
    }

    @Test
    public static void testGetLast() {
        ArrayList<Object> arre = new ArrayList<>();
        arre.add(1);
        arre.add(2);
        arre.add(3);
        arre.add(4);
        arre.add(5);
        arre.add(6);
        arre.add(7);
        arre.add(8);
        arre.add(9);

        assertTrue((int) arre.getLast() == 9);
    }

}