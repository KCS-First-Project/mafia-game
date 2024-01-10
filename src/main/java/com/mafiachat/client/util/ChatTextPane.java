package com.mafiachat.client.util;

import com.mafiachat.protocol.Command;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * 글꼴 Setting
 */
public class ChatTextPane extends JTextPane {
    AttributeSet normalAttrSet;
    AttributeSet whisperAttrSet;
    AttributeSet enterExitAttrSet;

    public ChatTextPane() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        normalAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.DARK_GRAY);

        normalAttrSet = sc.addAttribute(normalAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        sc = new StyleContext();
        enterExitAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED.darker());
        enterExitAttrSet = sc.addAttribute(enterExitAttrSet, StyleConstants.FontFamily, "Lucida Console");
        enterExitAttrSet = sc.addAttribute(enterExitAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        sc = new StyleContext();
        whisperAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.PINK);
        whisperAttrSet = sc.addAttribute(whisperAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
    }

    public void append(String msg, Command command) {
        AttributeSet attrset;
        if (command.equals(Command.SYSTEM)) {
            attrset = enterExitAttrSet;
        } else if (command.equals(Command.EXIT_ROOM) || command.equals(Command.ENTER_ROOM)) {
            attrset = whisperAttrSet;
        } else {
            attrset = normalAttrSet;
        }
        Document doc = getDocument();
        try {
            doc.insertString(doc.getLength(), msg + "\n", attrset);
            setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}