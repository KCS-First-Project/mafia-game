package network.View.server;

public class ClientHandlerResponseView {

    private static final String FAIL_TO_LISTEN_MESSAGE_PREFIX = "[IOException]";

    public static void failToListenMessage(String errorMessage) {
        System.out.println(FAIL_TO_LISTEN_MESSAGE_PREFIX + errorMessage);
    }

    public static void printLineRead(String message) {
        System.out.println("lineRead: " + message);
    }

    public static void cleanup() {
        System.out.println("Terminating ClientHandler");
    }

    public static void printHandleUnknownCommand(String command) {
        System.out.printf("ChatCommand %s \n", command);
    }
}
