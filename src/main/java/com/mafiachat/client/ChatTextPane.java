package com.mafiachat.client;
import java.awt.Color;
import java.text.SimpleDateFormat;

import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class ChatTextPane extends JTextPane{
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat formatter = new SimpleDateFormat("MMdd_HH_mm_ss");
	AttributeSet normalAttrSet;
	AttributeSet whisperAttrSet;
	AttributeSet enterExitAttrSet;
	private int linesToHold = 20;
	private int maxLines = 40;
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
	public void append(String msg) {
		AttributeSet attrset;
		attrset = normalAttrSet;
		Document doc = getDocument();
		int count = doc.getDefaultRootElement().getElementCount();
		try {
			doc.insertString(doc.getLength(), msg, attrset);
			System.out.println("line count: " + count);
			if(count >= maxLines) {
				int line = count - linesToHold - 1;
				Element map = getDocument().getDefaultRootElement();
	            Element lineElem = map.getElement(line);
	            int endOffset = lineElem.getEndOffset();
	            // hide the implicit break at the end of the document
	            endOffset = ((line == count - 1) ? (endOffset - 1) : endOffset);		
				System.out.println();
				doc.remove(0, endOffset);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}