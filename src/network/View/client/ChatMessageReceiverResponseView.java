package network.View.client;

public class ChatMessageReceiverResponseView {

    private static final String FAIL_TO_Receive_MESSAGE_PREFIX = "[IOException - Terminating ChatMessageReceiver]";

    public static void failToReceiveMessage(String errorMessage) {
        System.out.println(FAIL_TO_Receive_MESSAGE_PREFIX + errorMessage);
    }
}
