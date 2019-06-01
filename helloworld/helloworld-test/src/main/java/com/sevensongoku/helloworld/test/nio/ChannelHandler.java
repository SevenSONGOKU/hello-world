package com.sevensongoku.helloworld.test.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import static com.sevensongoku.helloworld.utils.StringUtils.*;

public class ChannelHandler implements Runnable {
    Selector selector;

    public ChannelHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (selector.select() == 0) continue;

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    // read handle
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        println(request);
    }
}
