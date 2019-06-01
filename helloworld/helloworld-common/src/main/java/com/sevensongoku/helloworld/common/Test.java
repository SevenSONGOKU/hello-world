package com.sevensongoku.helloworld.common;

import com.sevensongoku.helloworld.utils.StringUtils;

import static com.sevensongoku.helloworld.utils.StringUtils.*;

public class Test {

    public static void main(String[] args) {
//        println(getMinSteps(randomWord(), randomWord()));
        println(getMinSteps("abc", "cde"));
        // a b c f
        // c d e f
    }

    // 有问题！！！
    static int getMinSteps(String word1, String word2) {
        println(word1);
        println(word2);
        // 由s1变到s2与s2变到s1结果是一样的，所以假设s1.length < s2.length
        String s1, s2;
        if (word1.length() > word2.length()) {
            s1 = word2;
            s2 = word1;
        } else {
            s1 = word1;
            s2 = word2;
        }

        int steps = 0;
        for (int i = s1.length(); i > 0; i--) {
            for (int j = 0; j <= s1.length() - i; j++) {
                String s = StringUtils.substring(s1, j, i);
                if (s2.contains(s)) {
                    return s2.length() - s1.length() + steps;
                }
            }
            steps++;
        }

        println(steps);
        return -1;
    }
}
