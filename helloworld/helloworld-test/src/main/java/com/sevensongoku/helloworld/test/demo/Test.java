package com.sevensongoku.helloworld.test.demo;

import java.util.Arrays;

import static com.sevensongoku.helloworld.utils.StringUtils.println;

public class Test {
    static Test test = new Test();

    public static void main(String[] args) {
        println(Arrays.toString(test.twoSum(new int[] {2, 7, 11, 15}, 9)));
    }

    public int[] twoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length-1; i++) {
            for (int j = i + 1; j <nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[] {nums[i], nums[j]};
                }
            }
        }
        return null;
    }
}
