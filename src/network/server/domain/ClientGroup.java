//package network.server.domain;
//
//import java.util.Vector;
//import network.server.MessageHandler;
//
//public class ClientGroup {
//    private Vector<MessageHandler> clients;
//
//    private ClientGroup() {
//        clients = new Vector<>();
//    }
//
//    public static ClientGroup createClientGroup() {
//        return new ClientGroup();
//    }
//
//    public Vector<MessageHandler> getClients() {
//        return clients;
//    }
//
//    public synchronized int getActiveClientCount() {
//        return clients.size();
//    }
//
//    public synchronized void addClient(MessageHandler messageHandler) {
//        clients.add(messageHandler);
//    }
//
//    public synchronized void removeClient(MessageHandler messageHandler) {
//        clients.remove(messageHandler);
//    }
//
//    public synchronized void clearClient() {
//        clients.clear();
//    }
//}
