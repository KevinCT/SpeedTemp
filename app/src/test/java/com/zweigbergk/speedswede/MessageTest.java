package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.core.Message;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertTrue;

public class MessageTest {
    private Message message;
    static final String ID = "abc123";
    static final String CONTENT = "This is the content";

    @Before
    public void initialize() {
        this.message = new Message(ID, CONTENT);
    }

    @Test
    public void testEquals() {
        Message otherMessage = new Message(ID, CONTENT);
        assertTrue(this.message.equals(otherMessage));
    }

}
