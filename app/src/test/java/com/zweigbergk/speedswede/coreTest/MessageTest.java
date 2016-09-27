package com.zweigbergk.speedswede.coreTest;

import com.zweigbergk.speedswede.core.Message;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class MessageTest {
    private Message message;
    private long timeStamp;

    @Before
    public void initialize() {
        this.timeStamp = (new Date()).getTime();
        this.message = new Message("initialName", "initialMessage", timeStamp);
    }

    @Test
    public void initialMessageContent() {
        assertTrue(this.message.getName().equals("initialName") &&
            this.message.getText().equals("initialMessage"));
    }

    @Test
    public void setGetName() {
        String testName = "testName";
        this.message.setName(testName);
        assertTrue(this.message.getName().equals(testName));
    }

    @Test
    public void setGetTest() {
        String testText = "testText";
        this.message.setText(testText);
        assertTrue(this.message.getText().equals(testText));
    }

    @Test
    public void copyTextFrom() {
        String otherText = "otherText";
        Message otherMessage = new Message("name", otherText);
        this.message.copyTextFrom(otherMessage);
        assertTrue(this.message.getText().equals(otherText));
    }

    @Test
    public void getTimestamp() {
        assertTrue(this.message.getTimeStamp() == this.timeStamp);
    }
    
}
