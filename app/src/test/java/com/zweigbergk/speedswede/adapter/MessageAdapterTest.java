package com.zweigbergk.speedswede.adapter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static com.zweigbergk.speedswede.Constants.SWEDISH;
import static org.junit.Assert.*;

public class MessageAdapterTest {

    MessageAdapter test;

    @Before
    public void setUp() throws Exception {
        test = new MessageAdapter(new Locale(SWEDISH));
    }

    @After
    public void tearDown() throws Exception {
        test = null;
    }

    @Test
    public void clear() throws Exception {
        test.clear();
        assertTrue(test.getItemCount() == 0);
    }

    @Test
    public void onListChanged() throws Exception {

    }

    @Test
    public void addEventCallback() throws Exception {

    }

    @Test
    public void removeEventCallback() throws Exception {

    }

    @Test
    public void onCreateViewHolder() throws Exception {

    }

    @Test
    public void onBindViewHolder() throws Exception {

    }

    @Test
    public void getItemCount() throws Exception {

    }

    @Test
    public void getItemViewType() throws Exception {

    }

}