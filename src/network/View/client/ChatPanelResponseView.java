package network.View.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatPanelResponseView {
    private PrintWriter writer;
    private StringBuilder msgBuilder = new StringBuilder();

    private ChatPanelResponseView(Socket socket) throws IOException {
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public static ChatPanelResponseView createChatPanelResponseView(Socket socket) throws IOException {
        return new ChatPanelResponseView(socket);
    }

    public void write(String message) {
        writer.println(message);
    }

    public void sendMessage(String command, String msg) {
        String message = createMessage(command, msg);
        writer.println(message);
    }

    public String createMessage(String command, String msg) {
        msgBuilder.delete(0, msgBuilder.length());
        msgBuilder.append("[");
        msgBuilder.append(command);
        msgBuilder.append("]");
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }
}
