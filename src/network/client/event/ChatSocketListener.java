package network.client.event;

import java.io.IOException;

public interface ChatSocketListener {
    public void socketClosed();

    public void socketConnected(java.net.Socket s) throws IOException;
}
