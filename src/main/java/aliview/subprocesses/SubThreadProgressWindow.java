package aliview.subprocesses;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.gui.AppIcons;


public class SubThreadProgressWindow{

	private JFrame frame;
	private static final Logger logger = Logger.getLogger(SubThreadProgressWindow.class);
	private Thread subThread;
	private JTextArea consoleTextArea;
	private boolean subThreadInterruptedByUser = false;
	//	private Dimension PREF_SIZE = new Dimension(300,200);


	public SubThreadProgressWindow(){
		init();
	}

	public void init(){

		frame = new JFrame();
		//		frame.setPreferredSize(PREF_SIZE);
		consoleTextArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(consoleTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {		
			public void windowClosing(WindowEvent e) {
				if(subThread != null){
					subThread.interrupt();
					subThreadInterruptedByUser = true;
				}
				frame.dispose();
			}
		});

		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setTitle("Working");
		frame.setIconImage(AppIcons.getProgramIconImage());
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(SubThreadProgressWindow.class.getResource("/img/alignment_ico_128x128.png")));
	}

	public void pack(){
		frame.pack();
	}

	public void show(){
		frame.pack();
		frame.setVisible(true);
	}


	public SubThreadProgressWindow(Thread subthread) {
		super();
		this.subThread = subthread;
	}

	public boolean wasSubThreadInterruptedByUser() {
		return subThreadInterruptedByUser;
	}

	public void setActiveThread(Thread subThread){
		this.subThread = subThread;	
	}	

	public void centerLocationToThisComponentOrScreen(Component parent){
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth()/2 - frame.getPreferredSize().width/2;
			int newY = parent.getY() + parent.getHeight()/2 - frame.getPreferredSize().height/2;
			logger.info(parent.getX());
			logger.info(parent.getWidth());
			logger.info(frame.getWidth());
			setLocation(newX, newY);
		}
		else{
			centerLocationToCenterOfScreen();
		}
	}	

	public void setBottomRightRelativeThisComponent(Component parent) {
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth() - frame.getPreferredSize().width - 50;
			int newY = parent.getY() + parent.getHeight() - frame.getPreferredSize().height - 50;
			logger.info(parent.getX());
			logger.info(parent.getWidth());
			logger.info(frame.getWidth());
			setLocation(newX, newY);
		}
		else{
			centerLocationToCenterOfScreen();
		}
	}

	public void setTopRightRelativeThisComponent(Component parent) {
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth() - frame.getPreferredSize().width - 20;
			int newY = parent.getY() + 150;
			logger.info(parent.getX());
			logger.info(parent.getWidth());
			logger.info(frame.getWidth());
			setLocation(newX, newY);
		}
		else{
			centerLocationToCenterOfScreen();
		}
	}

	public void centerLocationToCenterOfScreen() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getPreferredSize().getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getPreferredSize().getHeight()) / 2);
		logger.info(frame.getPreferredSize());
		setLocation(x, y);
	}


	private void setLocation(final int x, final int y) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setLocation(x, y);
			}
		});
	}

	public void upperLeftLocationOfThisComponent(Component parent){
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + 100;
			int newY = parent.getY() + 100;
			setLocation(newX, newY);
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

	public void setInitialMessage(final String output){
		consoleTextArea.setText(output);
		// Make it scroll to end
		consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
	}

	public void setMessage(final String output){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				consoleTextArea.setText(output);
				// Make it scroll to end
				consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
			}
		});
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


	public void setTitle(final String title) {
		SwingUtilities.invokeLater(new Runnable() {		
			public void run() {
				frame.setTitle(title);		
			}
		});	
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

	public void setVisible(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {		
			public void run() {
				frame.setVisible(b);
			}
		});	
	}




}
