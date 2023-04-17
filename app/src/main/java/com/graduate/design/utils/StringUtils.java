package com.graduate.design.utils;

import java.util.Random;

public class StringUtils {
    private static final char[] charTable;
    private static final Random random = new Random();
    static {
        charTable = new char[62];
        int index = 0;
        for (int i = 0; i < 26; i++) {
            charTable[index++] = (char)('a' + i);
            charTable[index++] = (char)('A' + i);
        }
        for (int i = 0; i < 10; i++) {
            charTable[index++] = (char) ('0' + i);
        }
    }
    public static String getRandomCharSet(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int ran = random.nextInt(62);
            stringBuilder.append(charTable[ran]);
        }
        return stringBuilder.toString();
    }

    public static String getRandomName(int length, String suffix) {
        return getRandomCharSet(length) + suffix;
    }
}
