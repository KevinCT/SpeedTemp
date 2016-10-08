package com.zweigbergk.speedswede.util;

import com.zweigbergk.speedswede.core.Message;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class Time {
    private static  String getTimeFromDate(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return f.format(date);
    }

    private static String getDayAndMonthFromDate(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("MMM dd", Locale.getDefault());
        return f.format(date);
    }

    public static String formatMessageDate(Message message) {
        return message.isFromToday() ?
                getTimeFromDate(message.getDateSent()) :
                getDayAndMonthFromDate(message.getDateSent());
    }
}
