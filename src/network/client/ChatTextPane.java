package network.client;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import network.View.client.ChatTextPaneResponseView;
import network.constant.ChatCommandUtil;

public class ChatTextPane extends JTextPane {
    private static final long serialVersionUID = 1L;
    AttributeSet normalAttrSet;
    AttributeSet whisperAttrSet;
    AttributeSet enterExitAttrSet;

    private int linesToHold = 20;
    private int maxLines = 40;
    private boolean recordRemovedMsg = true;

    public ChatTextPane() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        normalAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.DARK_GRAY);

        //attSet = sc.addAttribute(attSet, StyleConstants.FontFamily, "Lucida Console");
        normalAttrSet = sc.addAttribute(normalAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        sc = new StyleContext();
        enterExitAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE.darker());
        enterExitAttrSet = sc.addAttribute(enterExitAttrSet, StyleConstants.FontFamily, "Lucida Console");
        enterExitAttrSet = sc.addAttribute(enterExitAttrSet, StyleConstants.Italic, true);
        enterExitAttrSet = sc.addAttribute(enterExitAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        sc = new StyleContext();
        whisperAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.PINK);
        //whisperAttrSet = sc.addAttribute(whisperAttrSet, StyleConstants.FontFamily, "Lucida Console");
        whisperAttrSet = sc.addAttribute(whisperAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
    }


    public void append(String msg, String command) {
        AttributeSet attrset = setAttrsetByCommand(command);
        appendWithStyle(msg, attrset);
        trimDocumentToMaxLines();
    }

    private AttributeSet setAttrsetByCommand(String command) {
        if (command.equals(ChatCommandUtil.WHISPER.getCommand())) {
            return whisperAttrSet;
        } else if (command.equals(ChatCommandUtil.ENTER_ROOM.getCommand()) ||
                command.equals(ChatCommandUtil.EXIT_ROOM.getCommand())) {
            return enterExitAttrSet;
        } else {
            return normalAttrSet;
        }
    }

    private void appendWithStyle(String msg, AttributeSet attrset) {
        try {
            Document doc = getDocument();
            doc.insertString(doc.getLength(), msg, attrset);
        } catch (BadLocationException e) {
            ChatTextPaneResponseView.failToGetDoc(e.getMessage());
        }
    }

    private void trimDocumentToMaxLines() {
        Document doc = getDocument();
        int count = doc.getDefaultRootElement().getElementCount();
        if (count >= maxLines) {
            try {
                removeOldLines(doc, count);
            } catch (BadLocationException e) {
                ChatTextPaneResponseView.failToGetDoc(e.getMessage());
            }
        }
    }

    private void removeOldLines(Document doc, int count) throws BadLocationException {
        int line = count - linesToHold - 1;
        Element map = doc.getDefaultRootElement();
        Element lineElem = map.getElement(line);
        int endOffset = lineElem.getEndOffset();
        /// hide the implicit break at the end of the document
        endOffset = ((line == count - 1) ? (endOffset - 1) : endOffset);

        doc.remove(0, endOffset);
    }

//    public void setRecordRemovedMsg(boolean rrm) {
//        recordRemovedMsg = rrm;
//    }

}
