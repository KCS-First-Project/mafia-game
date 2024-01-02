package main.java.com.mafiachat.client;

import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import main.java.com.mafiachat.client.domain.ChatUser;

@SuppressWarnings("serial")
public class ChatUserList extends JList<ChatUser> {
    private DefaultListModel<ChatUser> model;

    public ChatUserList() {
        model = new DefaultListModel<>();
        setModel(model);
        setCellRenderer(new CellRenderer());
    }

    public void addNewUsers(List<ChatUser> users) {
        for (ChatUser user : users) {
            model.addElement(user);
        }
    }

    class CellRenderer extends JLabel implements ListCellRenderer<ChatUser> {
        public CellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends ChatUser> list, ChatUser value, int index,
                boolean isSelected, boolean cellHasFocus) {
            setText(value == null ? "" : value.toString());
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            return this;
        }
    }
}
