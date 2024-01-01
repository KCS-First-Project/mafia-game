package network.client;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class CommandButton extends JButton {
    static final String CMD_DISCONNECT = "Disconnect";
    static final String CMD_CONNECT = "Connect";

    public CommandButton() {
        this(CMD_CONNECT);
    }

    public CommandButton(String labelCmd) {
        this(labelCmd, labelCmd);
    }

    public CommandButton(String label, String cmd) {
        super(label);
        setActionCommand(cmd);
    }

    public void toButton(String cmd) {
        setActionCommand(cmd);
        setText(getActionCommand());
    }
}
