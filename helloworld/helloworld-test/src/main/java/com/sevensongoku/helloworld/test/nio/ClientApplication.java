package com.sevensongoku.helloworld.test.nio;

import java.io.IOException;

public class ClientApplication {

    public static void main(String[] a) throws IOException {
        Client client = new Client();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        client.start();
    }
}
