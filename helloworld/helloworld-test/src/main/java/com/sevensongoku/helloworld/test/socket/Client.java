package com.sevensongoku.helloworld.test.socket;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

import static com.sevensongoku.helloworld.utils.StringUtils.println;

public class Client {

    public static void main(String[] args) {
        try {
            File f = new File("test.txt");
            f.createNewFile();
            println(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String fileName = scanner.nextLine();
            new Thread(new ClientSocket(fileName)).start();
        }
    }

    static class ClientSocket implements Runnable {
        private String fileName;

        public ClientSocket(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void run() {
            println(Thread.currentThread().getName() + "start...");
            File file = new File(this.fileName);
            if (!file.exists()) {
                println("文件不存在：" + this.fileName);
                return;
            }
            try {
                Socket socket = new Socket("localhost", 55555);
                OutputStream out = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(out);
                pw.println(this.fileName);
                pw.flush();
                pw.println(file.length());
                pw.flush();

                Scanner scanner = new Scanner(socket.getInputStream());
                long responseSize = scanner.nextLong();

                BufferedOutputStream output = new BufferedOutputStream(out);
                FileInputStream input = new FileInputStream(file);
                byte[] bytes = new byte[2048];
                int length = 0;
                while ((length = input.read(bytes)) != -1) {
                    output.write(bytes, 0, length);
                    output.flush();
                }


                responseSize = scanner.nextLong();

                input.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            println(Thread.currentThread().getName() + "end...");
        }
    }
}
