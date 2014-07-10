package aliview.gui;

import javax.swing.ButtonGroup;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

import utils.OSNativeUtils;



public class AliToolBar extends JToolBar{

	public AliToolBar(final AliViewJMenuBar aliMenuBar, SearchPanel searchPanel, JPanel translationPanel){

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JToggleButton toggleButtonDiffTrace = new JToggleButton();//("Diff");
		toggleButtonDiffTrace.setFocusPainted(false);
		toggleButtonDiffTrace.setIcon(AppIcons.getDiffIcon());
		toggleButtonDiffTrace.setToolTipText("<html>Highlight difference from one selected \"trace\"-sequence<br>(Select trace sequence by right clicking on target)</html>");
		toggleButtonDiffTrace.setModel(aliMenuBar.getHighlightDiffTraceButtonModel());
		this.add(toggleButtonDiffTrace);
		
		JToggleButton toggleBtnNonCons = new JToggleButton();//("Non-Cons");
		toggleBtnNonCons.setFocusPainted(false);
		toggleBtnNonCons.setToolTipText("Highlight difference from majority rule consensus");
		toggleBtnNonCons.setIcon(AppIcons.getHighlightNonConsIcon());
		toggleBtnNonCons.setModel(aliMenuBar.getHighlightNonConsButtonModel());
		this.add(toggleBtnNonCons);
		
		JToggleButton toggleBtnCons = new JToggleButton();//("Cons");
		toggleBtnCons.setFocusPainted(false);
		toggleBtnCons.setToolTipText("Highlight majority rule consensus characters");
		toggleBtnCons.setIcon(AppIcons.getHighlightConsIcon());
		toggleBtnCons.setModel(aliMenuBar.getHighlightConsButtonModel());
		this.add(toggleBtnCons);
		
		JToggleButton toggleBtnTrans = new JToggleButton();//("Translate");
		toggleBtnTrans.setFocusPainted(false);
		toggleBtnTrans.setToolTipText("Translates nucleotide sequence to Amino Acids");
		toggleBtnTrans.setIcon(AppIcons.getTranslateIcon());
		toggleBtnTrans.setModel(aliMenuBar.getToggleTranslationButtonModel());
		this.add(toggleBtnTrans);
		
		this.add(new JToolBar.Separator());
		
		String keyName = OSNativeUtils.getStandardCommandModifierKeyName();
		
		JButton decFontSize = new JButton();//("");
		decFontSize.setFocusPainted(false);
		decFontSize.setToolTipText("Decrease font size - can also be done with Mouse-Wheel and " + keyName + "-button, or - key");
		decFontSize.setIcon(AppIcons.getDecFontSize());
		decFontSize.setModel(aliMenuBar.getDecFontSizeButtonModel());
		this.add(decFontSize);
		
		JButton incFontSize = new JButton();//("");
		incFontSize.setFocusPainted(false);
		incFontSize.setToolTipText("Increase font size - can also be done with Mouse-Wheel and " + keyName + "-button, or + key");
		incFontSize.setIcon(AppIcons.getIncFontSize());
		incFontSize.setModel(aliMenuBar.getIncFontSizeButtonModel());
		this.add(incFontSize);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue);
		
		this.add(translationPanel);
		
		Component horizontalGlue2 = Box.createHorizontalGlue();
		add(horizontalGlue2);
		
		this.add(searchPanel);
	}
}