//package network.server.domain;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//public class ClientCommunicationHandler {
//    private Socket socket;
//    private BufferedReader br;
//    private PrintWriter pw;
//
//    private ClientCommunicationHandler(Socket socket) throws IOException {
//        this.socket = socket;
//        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        pw = new PrintWriter(socket.getOutputStream(), true);
//    }
//
//    public static ClientCommunicationHandler createClientCommunicationHandler(Socket socket) throws IOException {
//        return new ClientCommunicationHandler(socket);
//    }
//
//    public Socket getSocket() {
//        return socket;
//    }
//
//
//    public void sendMessage(String msg) {
//        pw.println(msg);
//    }
//
//    public String getMessage() throws IOException {
//        return br.readLine();
//    }
//
//    public void close() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
