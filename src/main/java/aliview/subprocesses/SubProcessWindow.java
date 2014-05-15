package aliview.subprocesses;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import aliview.gui.AppIcons;


public class SubProcessWindow{
	
	private JFrame frame;
	private static final Logger logger = Logger.getLogger(SubProcessWindow.class);
	private Process subProcess;
	private JTextArea consoleTextArea;
	//JScrollPane scrollPane;
	private boolean subProcessDestrouedByUser = false;
	private Dimension preferredSize = new Dimension(500,350);
	
	public SubProcessWindow(){
		init();
	}
	
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
	
	public void show(){
		frame.setPreferredSize(this.preferredSize);
		frame.pack();
		frame.setVisible(true);
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
	
	public void centerLocationToThisComponent(Component parent){
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth()/2 - frame.getPreferredSize().width/2;
			int newY = parent.getY() + parent.getHeight()/2 - frame.getPreferredSize().height/2;
			frame.setLocation(newX, newY);
		}
	}	
	
	public void placeFrameupperLeftLocationOfThis(Component parent){
		if(parent != null){
			int newX = parent.getX() + 100;
			int newY = parent.getY() + 100;
			frame.setLocation(newX, newY);
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
		frame.setTitle(title);
		
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		frame.setAlwaysOnTop(alwaysOnTop);
		
	}

	public void dispose() {
		if(frame != null){
			frame.dispose();		
		}
	}

	public Component getFrame() {
		return frame;
	}

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub	
	}

	
}
