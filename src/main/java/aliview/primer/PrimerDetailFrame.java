package aliview.primer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.gui.AppIcons;

public class PrimerDetailFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(PrimerDetailFrame.class);
	JPanel mainPanel = new JPanel();
	private AliViewWindow aliViewWindow;
	JTextArea mainTextArea;
	private Font textFont = new Font(Font.MONOSPACED, Font.PLAIN, 11);
	
	public PrimerDetailFrame(AliViewWindow aliViewWin) {
		this.aliViewWindow = aliViewWin;
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setBackground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		mainTextArea = new JTextArea();
		mainTextArea.setText("This is demo text");
		mainTextArea.setEditable(false);
		mainTextArea.setFont(textFont);
		mainPanel.add(mainTextArea, BorderLayout.CENTER);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
			
		this.setTitle("Primer detail");
		this.setIconImage(AppIcons.getProgramIconImage());
		this.setPreferredSize(new Dimension(500,400));
		this.setLocation(150, 150);
		this.pack();
		this.setVisible(true);		
	}
	
	
	public void setText(String text){
		mainTextArea.setText(text);
		mainTextArea.setCaretPosition(0);
	}
}
