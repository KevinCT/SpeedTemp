package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.util.AbuseFilter;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class AbuseFilterTest {

    @Test
    public void testFilter() {
        String testString = "Oh fuck this is a very bad message";
        String filteredString = "Oh *** this is a very bad message";

        assertTrue((AbuseFilter.filterMessage(testString)).equals(filteredString));
    }

}
