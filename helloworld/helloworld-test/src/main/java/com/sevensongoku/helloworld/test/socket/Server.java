package com.sevensongoku.helloworld.test.socket;

import com.sevensongoku.helloworld.utils.StringUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

import static com.sevensongoku.helloworld.utils.StringUtils.*;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(55555);
        while (true) {
            Socket socket = serverSocket.accept();
            InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
            println(address.getHostName() + ":" + address.getPort() + "connect...");

            InputStream in = socket.getInputStream();
            Scanner scanner = new Scanner(in);
            String fileName = scanner.nextLine();
            if (StringUtils.isEmpty(fileName)) {
                fileName = formatDate2(new Date());
            }
            StringBuilder sb = new StringBuilder(fileName);
            int index = sb.lastIndexOf(".");
            if (index == -1) {
                index = sb.length() - 1;
            }
            fileName = sb.insert(index, "_" + System.currentTimeMillis()).toString();
            File file = new File(fileName + ".tmp");
            if (!file.createNewFile()) {
                throw new RuntimeException("文件创建失败：" + file.getName());
            }
            long fileSize = scanner.nextLong();

            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println(fileSize);
            pw.flush();

            try (BufferedInputStream input = new BufferedInputStream(in);
                 FileOutputStream output = new FileOutputStream(file)) {
                byte[] bytes = new byte[2048];
                int length = 0;
                while ((length = input.read(bytes)) != -1) {
                    output.write(bytes, 0, length);
                    output.flush();
                    if (file.length() >= fileSize) {
                        pw.println(file.length());
                        pw.flush();
                        break;
                    }
                }
                output.close();
                file.renameTo(new File(fileName));
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                file.deleteOnExit();
            }

            println(address.getHostName() + ":" + address.getPort() + "finish...");
        }
    }
}
