package main.java.com.mafiachat.client;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import main.java.com.mafiachat.client.domain.StatusComponent;
import main.java.com.mafiachat.client.event.ChatStatusListener;
import main.java.com.mafiachat.client.event.ConnectionManager;

@SuppressWarnings("serial")
public class StatusBar extends JPanel implements ChatStatusListener {
    private static final String SET_STATUS_TEXT = "Initialized ...";
    private static final String USER_NAME = "User Name: ";

    private StatusComponent statusComponent;
    private static volatile StatusBar statusBar; //Thread Safety : .getStatusBar()  메서드에 이중 검사 잠금 패턴 적용

    private ConnectionManager connectionManager;

    private StatusBar(ConnectionManager connectionManager) {
        super(new GridBagLayout());
        statusComponent = new StatusComponent();
        this.connectionManager = connectionManager;
        this.add(statusComponent, statusComponent.getConstraints());
        statusComponent.setStatusText(SET_STATUS_TEXT);
    }

    public static StatusBar getStatusBar(ConnectionManager connectionManager) { //duble-checked locking
        if (statusBar == null) { //1. null check
            synchronized (StatusBar.class) {
                if (statusBar == null) { // 2. 동기화 블록 안에서 null check
                    statusBar = new StatusBar(connectionManager);
                }
            }
        }
        return statusBar;
    }

    public void changeByChatName() {
        statusComponent.setStatusText(USER_NAME + connectionManager.getName());
    }

    @Override
    public void chatStatusChanged(Object obj) {
        SwingUtilities.invokeLater(() -> statusComponent.setStatusText(obj.toString()));
    }
}
