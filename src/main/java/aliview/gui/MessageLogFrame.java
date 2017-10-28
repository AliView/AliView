package aliview.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.settings.Settings;

public class MessageLogFrame extends JFrame{
	private static final Logger logger = Logger.getLogger(MessageLogFrame.class);
	private static final String LF = System.getProperty("line.separator");
	private JTextArea messageArea;
	private AliViewWindow aliViewWindow;

	public MessageLogFrame(AliViewWindow aliViewWin) {
		this.aliViewWindow = aliViewWin;
		messageArea = new JTextArea();

		refreshLog();

		JScrollPane scrollPane = new JScrollPane(messageArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.getContentPane().add(scrollPane, BorderLayout.CENTER);


		JButton btnLogStatistics = new JButton("Log statistics");
		btnLogStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				aliViewWindow.createStats();
				refreshLog();
			}
		});

		JButton btnRequestGC = new JButton("Request GC");
		btnRequestGC.setToolTipText("Request Java Garbage Collection");
		btnRequestGC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				aliViewWindow.requestGB();
				refreshLog();
			}
		});

		JButton refreshButton = new JButton("Refresh log");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshLog();	
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		buttonPanel.add(btnLogStatistics);
		buttonPanel.add(btnRequestGC);
		buttonPanel.add(refreshButton);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		this.setPreferredSize(new Dimension(600,400));
		this.pack();
		this.setTitle("log");
		this.setIconImage(AppIcons.getProgramIconImage());
		this.centerLocationToThisComponent(aliViewWindow);
	}

	protected void refreshLog() {
		try {
			aliViewWindow.flushAllLogs();
			File logFile = new File( System.getProperty("user.home"), File.separator + Settings.getAliViewUserDataSubdir() + File.separator + Settings.getLogfileName());
			logger.info("logFile=" + logFile);
			String message = FileUtils.readFileToString(logFile);
			messageArea.setText(logFile.getAbsolutePath() + LF + message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void placeFrameupperLeftLocationOfThis(Component parent){
		if(parent != null){
			int newX = parent.getX() + 150;
			int newY = parent.getY() + 100;
			this.setLocation(newX, newY);
		}
	}

	public void centerLocationToThisComponent(Component parent){
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth()/2 - this.getWidth()/2;
			int newY = parent.getY() + parent.getHeight()/2 - this.getHeight()/2;
			this.setLocation(newX, newY);
		}
	}
}
