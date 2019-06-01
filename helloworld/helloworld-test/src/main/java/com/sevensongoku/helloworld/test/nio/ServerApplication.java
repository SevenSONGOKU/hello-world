package com.sevensongoku.helloworld.test.nio;

import java.io.IOException;

public class ServerApplication {

    public static void main(String[] s) throws IOException {
        Server server = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        server.start();
    }
}
