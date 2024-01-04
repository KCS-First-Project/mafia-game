package com.mafiachat.client;
import java.awt.Color;
import java.text.SimpleDateFormat;

import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
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
	private boolean recordRemovedMsg = true;
	private JProgressBar curActiveProgressBar;
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
}