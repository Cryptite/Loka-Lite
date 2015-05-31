package com.cryptite.lite.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static int secondsSince(Long time) {
        if (time == 0) return 0;

        return (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time);
    }

    public static int minutesSince(Long time) {
        if (time == 0) return 0;

        return (int) TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time);
    }

    public static int hoursSince(Long time) {
        if (time == 0) return 0;

        return (int) TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - time);
    }

    public static int daysSince(Long time) {
        if (time == 0) return 0;

        return (int) TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - time);
    }

    public static String minutesUntil(Long time, int start) {
        int seconds = (start * 60) - secondsSince(time);
        if (seconds <= 60) {
            return seconds + " seconds";
        }

        return (seconds / 60) + "m " + (seconds % 60) + "s";
    }
}