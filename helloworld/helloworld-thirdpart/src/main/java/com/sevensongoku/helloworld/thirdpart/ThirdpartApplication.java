package com.sevensongoku.helloworld.thirdpart;

import cn.hutool.core.lang.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThirdpartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThirdpartApplication.class);
        Console.log("============================");
        Console.log("start successfully");
        Console.log("============================");
    }
}
