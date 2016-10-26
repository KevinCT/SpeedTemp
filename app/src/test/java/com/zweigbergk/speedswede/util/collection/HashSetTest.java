package com.zweigbergk.speedswede.util.collection;

import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class HashSetTest {
    HashSetExtension<String> test;

    @Before
    public void setUp() throws Exception {
        test = new HashSetExtension<>();

        assertTrue(test.isEmpty());

        assertEquals(0, test.size());
    }

    @After
    public void tearDown() throws Exception {
        test = null;
    }

    @Test
    public void union() throws Exception {
        HashSetExtension<String> testUnion = new HashSetExtension<>();
        HashSetExtension<String> testUnion2 = new HashSetExtension<>();

        testUnion2.add("a");
        testUnion2.add("b");
        testUnion2.add("c");
        testUnion2.add("d");
        testUnion2.add("e");
        testUnion2.add("f");

        test.add("a");
        test.add("b");
        test.add("c");

        testUnion.add("d");
        testUnion.add("e");
        testUnion.add("f");

        testUnion = (HashSetExtension<String>) testUnion.union(test);

        assertEquals(testUnion2, testUnion);

    }

    @Test
    public void intersect() throws Exception {
        test =  new HashSetExtension<>();
        HashSetExtension<String> testIntersect = new HashSetExtension<>();
        HashSetExtension<String> testIntersect2 = new HashSetExtension<>();

        test.add("a");
        test.add("b");
        test.add("c");
        test.add("e");
        test.add("f");
        test.add("g");
        test.add("h");

        testIntersect2.add("a");
        testIntersect2.add("c");
        testIntersect2.add("e");
        testIntersect2.add("g");
        testIntersect2.add("k");

        testIntersect.add("c");
        testIntersect.add("a");
        testIntersect.add("g");
        testIntersect.add("e");

        test = (HashSetExtension<String>) test.intersect(testIntersect2);

        assertEquals(test, testIntersect);

    }

    @Test
    public void difference() throws Exception {
        test =  new HashSetExtension<>();
        HashSetExtension<String> testIntersect = new HashSetExtension<>();
        HashSetExtension<String> testIntersect2 = new HashSetExtension<>();

        test.add("a");
        test.add("b");
        test.add("c");
        test.add("e");
        test.add("f");
        test.add("g");
        test.add("h");

        testIntersect2.add("a");
        testIntersect2.add("c");
        testIntersect2.add("e");
        testIntersect2.add("g");
        testIntersect2.add("k");

        testIntersect.add("b");
        testIntersect.add("f");
        testIntersect.add("h");

        test = (HashSetExtension<String>) test.difference(testIntersect2);

        assertEquals(test, testIntersect);
    }


}