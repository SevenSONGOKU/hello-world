package com.sevensongoku.helloworld.test.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

import static com.sevensongoku.helloworld.utils.StringUtils.*;

public class Server {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public void close() throws IOException {
        selector.keys().forEach(key -> {
            try {
                key.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverSocketChannel.close();
    }

    public void start() throws IOException {
        selector = Selector.open();

        serverSocketChannel = ServerSocketChannel.open()
                .bind(new InetSocketAddress(55555));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        println("server started...");

        while (true) {
            if (selector.select() == 0) continue;

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();

                // accept handle
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }

                // read handle
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }
            }
        }
    }

    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        socketChannel.write(Charset.forName("UTF-8").encode("client connecting..."));
        println(socketChannel.getRemoteAddress() + "connecting...");
    }

    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String request = EMPTY;
        while (socketChannel.read(buffer) > 0) {
            buffer.flip();
            request += Charset.forName("UTF-8").decode(buffer);
        }
        socketChannel.register(selector, SelectionKey.OP_READ);

        braodCast(selector, socketChannel, request);
    }

    private void braodCast(Selector selector, SocketChannel sourceChannel, String request) {
        selector.keys().forEach(key -> {
            SelectableChannel targetChannel = key.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != sourceChannel) {
                try {
                    ((SocketChannel)targetChannel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
