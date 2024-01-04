package com.mafiachat.server.client;
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
