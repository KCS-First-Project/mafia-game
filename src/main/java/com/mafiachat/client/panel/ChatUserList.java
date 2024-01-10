package com.mafiachat.client.panel;

import com.mafiachat.client.user.ChatUser;
import java.awt.Component;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


@SuppressWarnings("serial")
public class ChatUserList extends JList {
    public ChatUserList() {
        super(new DefaultListModel());
        this.setCellRenderer(new CellRenderer());
        DefaultListModel model = (DefaultListModel) getModel();
        model.addElement(null);
    }

    public void addNewUsers(List<ChatUser> users) {
        DefaultListModel newModel = new DefaultListModel();
        users.stream()
                .map(ChatUser::getName)
                .forEach(newModel::addElement);
        setModel(newModel);
    }

    class CellRenderer extends JLabel implements ListCellRenderer {
        public CellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            setText(Optional.ofNullable(value).map(Object::toString).orElse(""));
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

}
