package aliview.subprocesses;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import aliview.gui.AppIcons;
import aliview.settings.Settings;


public class SubProcessWindow{
	
	private JDialog dialog;
	private static final Logger logger = Logger.getLogger(SubProcessWindow.class);
	private Process subProcess;
	private JTextArea consoleTextArea;
	//JScrollPane scrollPane;
	private boolean subProcessDestrouedByUser = false;
	private Dimension preferredSize = new Dimension(500,350);
	private JFrame parentFrame;
	private JCheckBox closeAutomaticCbx = new JCheckBox("Default checkbox message");

	public SubProcessWindow(JFrame parentFrame){
		init(parentFrame, false);
	}
	
	public SubProcessWindow(JFrame parentFrame, boolean withAutoCloseWhenDoneCbx) {
		init(parentFrame, true);
	}
	
	public static SubProcessWindow getAlignmentProgressWindow(JFrame parentFrame, boolean autoCloseWhenDoneCbxSelected){
		
		SubProcessWindow procWin = new SubProcessWindow(parentFrame, true);
		
		procWin.closeAutomaticCbx.setText("Close this type of progress window automatically when done");
		procWin.closeAutomaticCbx.setSelected(autoCloseWhenDoneCbxSelected);
		procWin.closeAutomaticCbx.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();
				Settings.getHideAlignmentProgressWindowWhenDone().putBooleanValue(box.isSelected());
			}
		});
	
		return procWin;
	}
	
	
	public void init(JFrame parentFrame, boolean withAutocloseBox){
		
		this.parentFrame = parentFrame;
		dialog = new JDialog(parentFrame);
		
		consoleTextArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(consoleTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {		
			public void windowClosing(WindowEvent e) {
				
				// destroy subprocess if there is one
				if(subProcess != null){
					logger.info("destroy-subprocess");
					
					// On windows there is a risk that closing stream command takes a long
					// time to return or blocks - therefore do it in a separate thread
					
					// Even better - Dont close streams - it might block on windows
					
//					Thread thread = new Thread(new Runnable(){
//						public void run(){
//								try {
//									subProcess.getInputStream().close();
//									subProcess.getOutputStream().close();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}	
//							}
//					});
//					thread.start();
					
							
					logger.info("before destroy");
					subProcessDestrouedByUser = true;
					subProcess.destroy();		
					logger.info("now after destroy");
				}
				// and close window
				dialog.dispose();
			}
		});
		
		dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		
		
		if(withAutocloseBox){
			closeAutomaticCbx.setHorizontalAlignment(SwingConstants.CENTER);
			dialog.getContentPane().add(closeAutomaticCbx, BorderLayout.SOUTH);
		}
		
		
		dialog.setTitle("Sub Process Window");
		dialog.setIconImage(AppIcons.getProgramIconImage());
		dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(SubProcessWindow.class.getResource("/img/alignment_ico_128x128.png")));
			
	}
	/*
	public void init(){
		
		frame = new JFrame();
		
		consoleTextArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(consoleTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {		
			public void windowClosing(WindowEvent e) {
				if(subProcess != null){
					logger.info("destroy-subprocess");
					try {
						subProcess.getInputStream().close();
						subProcess.getOutputStream().close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					subProcess.destroy();
					subProcessDestrouedByUser = true;
				}
				frame.dispose();
			}
		});
		
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setTitle("Sub Process Window");
		frame.setIconImage(AppIcons.getProgramIconImage());
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(SubProcessWindow.class.getResource("/img/alignment_ico_128x128.png")));
	}
	*/
	
	public void show(){
		dialog.setPreferredSize(this.preferredSize);
		dialog.pack();
		// center
		if(parentFrame != null){
			int newX = parentFrame.getX() + parentFrame.getWidth()/2 - dialog.getPreferredSize().width/2;
			int newY = parentFrame.getY() + parentFrame.getHeight()/2 - dialog.getPreferredSize().height/2;
			dialog.setLocation(newX, newY);
		}
		dialog.setVisible(true);
	}
	
	public void setPreferredSize(Dimension prefSize){
		this.preferredSize = prefSize;
	}	
	
	public SubProcessWindow(Process subprocess) {
		super();
		this.subProcess = subprocess;
	}

	public boolean wasSubProcessDestrouedByUser() {
		return subProcessDestrouedByUser;
	}

	public void setActiveProcess(Process subProcess){
		this.subProcess = subProcess;	
	}	
	
	/*
	public void centerLocationToThisComponent(Component parent){
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth()/2 - dialog.getPreferredSize().width/2;
			int newY = parent.getY() + parent.getHeight()/2 - dialog.getPreferredSize().height/2;
			dialog.setLocation(newX, newY);
		}
	}
	*/
	
	public void placeFrameupperLeftLocationOfThis(Component parent){
		if(parent != null){
			int newX = parent.getX() + 100;
			int newY = parent.getY() + 100;
			dialog.setLocation(newX, newY);
		}
	}	
	
	
	public void appendOutput(final String output){
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
			    consoleTextArea.append(output);
				// Make it scroll to end
				consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
			    }
		  });
	}
	
	public void setMessage(final String output){
		setOutput(output);
	}
	
	public void setOutput(final String output){
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
			    consoleTextArea.setText(output);
				// Make it scroll to end
				consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
			    }
		  });
	}
		

	public void setTitle(String title) {
		dialog.setTitle(title);
		
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		dialog.setAlwaysOnTop(alwaysOnTop);
		
	}

	public void dispose() {
		if(dialog != null){
			dialog.dispose();		
		}
	}

	public Component getFrame() {
		return dialog;
	}

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub	
	}

	public boolean isCloseWhenDoneCbxSelected() {
		if(closeAutomaticCbx != null){
			return closeAutomaticCbx.isSelected();
		}else{
			return false;
		}
	}

	public void setCloseWhenDoneCbxSelection(boolean booleanValue) {
		if(closeAutomaticCbx != null){
			closeAutomaticCbx.setSelected(booleanValue);
		}
	}
	
	public void setCloseWhenDoneCbxText(String text) {
		if(closeAutomaticCbx != null){
			closeAutomaticCbx.setText(text);
		}
	}
	
}
