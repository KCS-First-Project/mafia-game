package com.mafiachat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class TestClient {
    private final BufferedReader reader;
    private final PrintWriter writer;

    private Logger logger = Logger.getLogger(TestClient.class.getSimpleName());

    public TestClient(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        OutputStream os = socket.getOutputStream();
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        InputStream is = socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(is));
        logger.info("TestClient created");
    }

    public void write(String msg) {
        writer.println(msg);
    }

    public String read() throws IOException {
        return reader.readLine();
    }
}
