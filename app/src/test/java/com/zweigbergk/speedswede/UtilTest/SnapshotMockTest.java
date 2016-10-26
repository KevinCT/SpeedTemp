package com.zweigbergk.speedswede.UtilTest;

import com.zweigbergk.speedswede.mock.ISnapshot;
import com.zweigbergk.speedswede.mock.SnapshotMock;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class SnapshotMockTest {

    private ISnapshot storeMock;

    private static final String FRUITS = "fruits";
    private static final String GREENS = "greens";
    private static final String MEAT = "meat";
    private static final String BANANA = "banana";
    private static final String APPLE = "apple";
    private static final String PRICE = "price";
    private static final int EXPENSIVE = 249;


    @Before
    public void reset() {
        storeMock = new SnapshotMock();
    }

    @Test
    public void testCorrectValue() {
        storeMock.child(FRUITS).child(BANANA).child(PRICE).setValue(25);

        //Try normal assigning
        assertTrue(storeMock.child(FRUITS).child(BANANA).child(PRICE).getValue().equals(25));

        //Try changing value
        storeMock.child(FRUITS).child(BANANA).child(PRICE).setValue(47);
        assertTrue(storeMock.child(FRUITS).child(BANANA).child(PRICE).getValue().equals(47));

        //Try clearing all childs after FRUITS
        storeMock.child(FRUITS).setValue(null);

        assertTrue(storeMock.child(FRUITS).child(BANANA).child(PRICE).getValue() == null);
        assertTrue(storeMock.child(FRUITS).child(BANANA).getValue() == null);
    }

    @Test
    public void testSize() {
        storeMock.child(FRUITS).child(APPLE).child(PRICE);
        assertTrue(storeMock.getChildrenCount() == 0);

        reset();

        storeMock.child(FRUITS).child(APPLE).child(PRICE).setValue(EXPENSIVE);
        assertTrue(storeMock.getChildrenCount() == 1);

        reset();

        storeMock.child(FRUITS).setValue("Fruits!");
        storeMock.child(GREENS).setValue("Greens!");
        //Child is not assigned, and so should not count towards children count
        storeMock.child(MEAT);

        assertTrue(storeMock.getChildrenCount() == 2);
    }
}
