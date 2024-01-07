package com.mafiachat.client;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import java.util.*;
import java.util.List;


@SuppressWarnings("serial")
public class ChatUserList extends JList {
	public ChatUserList() {
		super(new DefaultListModel());
		this.setCellRenderer(new CellRenderer());
		DefaultListModel model = (DefaultListModel) getModel();
		model.addElement(null);

	}

	public void addNewUsers(ArrayList <ChatUser> users) { // 리스투 추가 함수 추가
		DefaultListModel newModel = new DefaultListModel();
		for(ChatUser user: users) {
			newModel.addElement(user);
		}
		setModel(newModel);
	}

	public List<ChatUser> getUsers() {
		DefaultListModel model = (DefaultListModel) getModel();
		List<ChatUser> users = new ArrayList<>();
		for (int i = 0; i < model.getSize(); i++) {
			Object element = model.getElementAt(i);
			if (element instanceof ChatUser) {
				users.add((ChatUser) element);
			}
		}
		return users;
	}
	class CellRenderer extends JLabel implements ListCellRenderer {
		public CellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(
				JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setText(value == null? "": value.toString());
			if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }      
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
		}
	}

}
