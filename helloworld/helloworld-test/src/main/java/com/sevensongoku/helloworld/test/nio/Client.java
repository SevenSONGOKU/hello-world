package com.sevensongoku.helloworld.test.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client {
    private SocketChannel socketChannel;

    public void start() throws IOException {
        Selector selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 55555));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new ChannelHandler(selector)).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            socketChannel.write(Charset.forName("UTF-8").encode(socketChannel.getLocalAddress() + ": " + scanner.nextLine()));
        }
    }

    public void close() throws IOException {
        socketChannel.close();
    }
}
