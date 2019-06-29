package com.sevensongoku.helloworld.test.parsestring;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static com.sevensongoku.helloworld.utils.StringUtils.*;

public class ParseMongoQuery {
    public static void main(String[] s) throws IOException {
        if (s.length <= 0) {
            println("args is empty...");
            new File("temp").createNewFile();
            return;
        }
        File f = new File("./helloworld-test/src/main/java/com/sevensongoku/helloworld/test/parsestring/" + s[0]);
        if (!f.exists()) {
            println(f.getName());
            println("file does not exists...");
            return;
        }
        Scanner scanner = new Scanner(new FileInputStream(f));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String content = scanner.nextLine().trim();
            if (isEmpty(content)) break;
            sb.append(content);
        }
        println(sb.toString());
        scanner.close();
    }
}
