package network.View.client;

public class ChatTextPaneResponseView {

    private static final String FAIL_TO_GET_DOC = "[BadLocationException]";

    public static void failToGetDoc(String errorMessage) {
        System.out.println(FAIL_TO_GET_DOC + errorMessage);
    }

}
