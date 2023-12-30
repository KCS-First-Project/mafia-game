package network.server;

import static network.View.server.MafiaServerResponseView.failToStartServer;
import static network.View.server.MafiaServerResponseView.printServerListeningMessage;
import static network.View.server.MafiaServerResponseView.printServerThreadRun;
import static network.View.server.MafiaServerResponseView.serverShutDown;
import static network.View.server.MafiaServerResponseView.terminateServer;
import static network.constant.Port.PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MafiaServer implements Runnable {
    private final ServerSocket serverSocket;

    private ClientHandler clientHandler;

    public MafiaServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        printServerListeningMessage(PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownHook()));
    }

    private void shutdownHook() {
        try {
            serverSocket.close();
            GroupManager.closeAllMessageHandlers(clientHandler.getGroupManager().getClientGroup());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                printServerThreadRun(socket);
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            terminateServer(e.getMessage());
        }
        serverShutDown();
    }

    public static void main(String[] args) {
        try {
            Runnable run = new MafiaServer();
            new Thread(run).start();
        } catch (IOException e) {
            failToStartServer(e.getMessage());
        }
    }
}
