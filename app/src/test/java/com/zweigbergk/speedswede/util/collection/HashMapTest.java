package com.zweigbergk.speedswede.util.collection;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class HashMapTest {

    HashMapExtension<String, Integer> test;

    @Before
    public void setUp() throws Exception {
        test = new HashMapExtension<>();

        assertTrue(test.isEmpty());

        assertEquals(0, test.size());
    }

    @After
    public void tearDown() throws Exception {
        test = null;
    }


    @Test
    public void values() throws Exception {
        test = new HashMapExtension<>();
        Set<Integer> testSet = new HashSetExtension<>();
        for(int i=65;i<=90;i++) {
            testSet.add(i);
            test.put(String.valueOf((char) i) , i);
        }

        Set<String> keySet = test.keySet();
        Set<Integer> valueSet = new HashSetExtension<>();

        for(String i: keySet) {
            valueSet.add(test.get(i));
        }


        assertEquals(testSet, valueSet);

    }

    @Test
    public void keys() throws Exception {
        test = new HashMapExtension<>();
        Set<String> testSet = new HashSet<>();
        for(int i=65;i<=90;i++) {
            testSet.add(String.valueOf((char) i));
            test.put(String.valueOf((char) i) , i);
        }

        assertEquals(testSet, test.keySet());
    }


    @Test
    public void invert() throws Exception {
        test = new HashMapExtension<>();

        test.put("a" , 1);
        test.put("b" , 2);
        test.put("c" , 3);
        test.put("d" , 4);

        HashMapExtension<Integer, String> testInvert = (HashMapExtension<Integer, String>) test.invert();

        assertEquals(testInvert.get(1), "a");
        assertEquals(testInvert.get(2), "b");
        assertEquals(testInvert.get(3), "c");
        assertEquals(testInvert.get(4), "d");


    }

    @Test
    public void create() throws Exception {
        test = new HashMapExtension<>();

        CollectionExtension<String> keys = new ArrayListExtension<>();
        CollectionExtension<Integer> values = new ArrayListExtension<>();

        HashMapExtension<String, Integer> hashMapCreateTest;

        test.put("a", 1);
        test.put("b", 2);
        test.put("c", 3);
        test.put("d", 4);

        keys.add("a");
        keys.add("b");
        keys.add("c");
        keys.add("d");

        values.add(1);
        values.add(2);
        values.add(3);
        values.add(4);

        hashMapCreateTest = (HashMapExtension<String, Integer>) HashMapExtension.create(keys, values);

        assertEquals(hashMapCreateTest, test);

    }

    @Test
    public void putList() throws Exception {
        test = new HashMapExtension<>();
        HashMapExtension<String, Integer> hashMapPutListTest = new HashMapExtension<>();

        List<String> keys = new ArrayList<String>();

        List<Integer> values = new ArrayList<Integer>();

        test.put("a", 1);
        test.put("b", 2);
        test.put("c", 3);
        test.put("d", 4);

        keys.add("a");
        keys.add("b");
        keys.add("c");
        keys.add("d");

        values.add(1);
        values.add(2);
        values.add(3);
        values.add(4);

        hashMapPutListTest = hashMapPutListTest.putList(keys, values);

        assertEquals(hashMapPutListTest, test);

    }

}