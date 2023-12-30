package network.View.server;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MafiaServerResponseView {
    private static final String TERMINATE_PREFIX = "[TerminateServer] ";
    private static final String TERMINATE_SUFFIX = "Terminating ChatServer.";
    private static final String UNKNOWN_HOST_PREFIX = "[UnknownHostException] ";
    private static final String UNKNOWN_HOST_SUFFIX = "Error retrieving the local host address.";
    private static final String FAIL_TO_START_SERVER_PREFIX = "[Fail to Start Server] ";

    public static void printServerListeningMessage(int port) {
        try {
            String serverAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.printf("ChatServer[%s] is listening on port %d\n", serverAddress, port);
        } catch (UnknownHostException e) {
            System.out.println(UNKNOWN_HOST_PREFIX + e.getMessage() + UNKNOWN_HOST_SUFFIX);
        }
    }

    public static void printServerThreadRun(Socket socket) {
        System.out.format("Client[%s] accepted\n", socket.getInetAddress().getHostName());
    }

    public static void terminateServer(String errorMessage) {
        System.out.println(TERMINATE_PREFIX + errorMessage + TERMINATE_SUFFIX);
    }

    public static void serverShutDown() {
        System.out.println("ChatServer shut down");
    }

    public static void failToStartServer(String errorMessage) {
        System.out.println(FAIL_TO_START_SERVER_PREFIX + errorMessage);
    }
}
