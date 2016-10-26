package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class HashMapTest {

    HashMap<String, Integer> test;

    @Before
    public void setUp() throws Exception {
        test = new HashMap<>();

        assertTrue(test.isEmpty());

        assertEquals(0, test.size());
    }

    @After
    public void tearDown() throws Exception {
        test = null;
    }


    @Test
    public void values() throws Exception { //FEL
        test = new HashMap<>();
        Set<Integer> testSet = new HashSet<>();
        for(int i=65;i<=90;i++) {
            testSet.add(i);
            test.put(String.valueOf((char) i) , i);
        }

        java.util.Set<String> keySet = test.keySet();
        java.util.Set<Integer> valueSet = new HashSet<Integer>();

        for(String i: keySet) {
            valueSet.add(test.get(i));
        }


        assertEquals(testSet, valueSet);

    }

    @Test
    public void keys() throws Exception {
        test = new HashMap<>();
        Set<String> testSet = new HashSet<>();
        for(int i=65;i<=90;i++) {
            testSet.add(String.valueOf((char) i));
            test.put(String.valueOf((char) i) , i);
        }

        assertEquals(testSet, test.keySet());
    }


    @Test
    public void invert() throws Exception {
        test = new HashMap<>();

        test.put("a" , 1);
        test.put("b" , 2);
        test.put("c" , 3);
        test.put("d" , 4);

        HashMap<Integer, String> testInvert = (HashMap<Integer, String>) test.invert();

        assertEquals(testInvert.get(1), "a");
        assertEquals(testInvert.get(2), "b");
        assertEquals(testInvert.get(3), "c");
        assertEquals(testInvert.get(4), "d");


    }

    @Test
    public void create() throws Exception {
        test = new HashMap<>();

        Collection<String> keys = new ArrayList<>();
        Collection<Integer> values = new ArrayList<>();

        HashMap<String, Integer> hashMapCreateTest = new HashMap<>();

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

        hashMapCreateTest = (HashMap<String, Integer>) HashMap.create(keys, values);

        assertEquals(hashMapCreateTest, test);

    }

    @Test
    public void putList() throws Exception {
        test = new HashMap<>();
        HashMap<String, Integer> hashMapPutListTest = new HashMap<>();

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