package network.View.server;

public class GroupManagerResponseView {
    
    public static void countActiveClients(int size) {
        System.out.println("Active clients count: " + size);
    }

    public static String createMessage(String command, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.delete(0, sb.length());
        sb.append("[");
        sb.append(command);
        sb.append("]");
        sb.append(msg);
        return sb.toString();
    }
}
