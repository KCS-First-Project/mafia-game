package com.mafiachat.util;

import java.io.*;
import java.net.Socket;

public class TestClient{
    private final BufferedReader reader;
    private final PrintWriter writer;

    public TestClient(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        OutputStream os = socket.getOutputStream();
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        InputStream is = socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(is));
        System.out.println("TestClient created");
    }

    public void write(String msg) {
        writer.println(msg);
    }

    public String read() throws IOException {
        return reader.readLine();
    }
}
