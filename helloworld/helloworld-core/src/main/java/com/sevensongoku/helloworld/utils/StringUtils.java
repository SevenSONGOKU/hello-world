package com.sevensongoku.helloworld.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

public class StringUtils {
    public final static String EMPTY = "";

    public static int randomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static char randomUpChar() {
        return (char)randomInt(65, 90);
    }

    public static char randomLowChar() {
        return Character.toLowerCase(randomUpChar());
    }

    public static char randomChar() {
        Random random = new Random();
        if (random.nextBoolean()) {
            return randomUpChar();
        } else {
            return randomLowChar();
        }
    }

    public static String randomWord(int length) {
        char[] chars = new char[length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = randomChar();
        }
        return String.valueOf(chars);
    }

    public static String randomWord(int min, int max) {
        return randomWord(randomInt(min, max));
    }

    public static String randomWord() {
        return randomWord(5, 10);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.equals(EMPTY);
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isNotEmpty(Collection c) {
        return c != null && c.size() > 0
                && c.stream().filter(x -> x != null).findAny().isPresent();
    }

    public static boolean isEmpty(Collection c) {
        return !isNotEmpty(c);
    }

    public static String substring(String s, int beginIndex, int length) {
        return s.substring(beginIndex, beginIndex + length);
    }

    public static void print(String s) {
        System.out.print(s);
    }

    public static void print(Object o) {
        print(o.toString());
    }

    public static void println(String s) {
        System.out.println(s);
    }

    public static void println(Object o) {
        println(o.toString());
    }

    public static void println() { println(EMPTY); }

    public static String concat(Object... o) {
        String[] stringArray = (String[]) Arrays.stream(o)
                .map(Object::toString)
                .toArray();
        return String.join(EMPTY, stringArray);
    }

    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDate2(Date date) {
        return formatDate(date, "yyyyMMddHHmmss");
    }
}
