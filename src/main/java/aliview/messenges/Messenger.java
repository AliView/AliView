package aliview.messenges;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import utils.DialogUtils;

import aliview.AliViewWindow;
import aliview.AminoAcid;
import aliview.gui.AliViewJMenuBar;
import aliview.settings.Settings;

public class Messenger {
	private static final String LF = System.getProperty("line.separator");
	private static final Logger logger = Logger.getLogger(Messenger.class);
	public static final Message SAVE_NOT_POSSIBLE_TRY_SAVE_AS = new Message("Not possible to save, try Save as....", "Sorry....");
	public static final Message NO_SELECTION = new Message("Nothing selected...", "No selection");	
	public static Message NO_FASTA_IN_CLIPBOARD = new Message("Could not find fasta sequences in clipboard (name has to start with >)", "No fasta sequences");
	public static Message NO_FASTA_OR_FILE_IN_CLIPBOARD = new Message("Could not find fasta sequences (name has to start with >)" + LF +
			                                                           "- or a valid alignment file name in clipboard.", "No sequences");
	public static Message ALIGNER_SOMETHING_PROBLEM_ERROR = new Message("Something did not work out when aligning", "Problem when aligning");
	public static Message NO_FULLY_SELECTED_SEQUENCES = new Message("There are no fully selected sequences - if you only want to realign" + LF +
			                                                        "a part of sequence, select \"Realign selected block\" instead.", "No selected sequence");
	public static Message COULD_NOT_OPEN_HELP_IN_BROWSER = new Message("Could not open help file in browser, help is available at: " + Settings.getAliViewHelpWebPage(), "Problem when aligning");
	public static Message OUT_OF_MEMORY_ERROR = new Message("Out of memory error - you can probably still save your work as it is." + LF +
			                                         //       "One source of this error is the number of undo-steps preserved - you can change this in Settings." + LF +
															"If you want to increase memory available for AliView:  " + Settings.getAliViewHelpWebPage(), "Out of memory");
	public static Message ONLY_VIEW_WHEN_FILESEQUENCES = new Message("Edit capabilities are limited when large alignment is read from file" + LF + 
			                        								 "You can delete and rearange sequences." + LF + 
			                        								 "If you need full editing capabilities then you can increase " + LF + 
			                        								 "AliView memory settings under menu \"Preferences\". Memory " + LF +
			                        								 "needed is 2 x file size if files are to be read into memory." + LF +
			                        								 " ", "Limited edit capabilities");
	
	public static final Message LIMITED_UNDO_CAPABILITIES = new Message("The size of the alignment prevents Undo functionality when editing." + LF + 
			 															"- Don't forget to 'Save As' every once in a while....", "Undo function disabled");
			
	
	public static void main(String[] args) {
		boolean cbxSelected = showOKOnlyMessageWithCbx(ONLY_VIEW_WHEN_FILESEQUENCES, true, null);
		logger.info("cbxSelected" + cbxSelected);
	}
	
	public static void showGeneralErrorMessage(Error error, JFrame parentFrame) {
		 Message GENERAL_ERROR = new Message("Error (you can probably still save work as it is)" + LF +
	              "Description: " + error.getMessage(), "Error");
		 showOKOnlyMessage(GENERAL_ERROR, parentFrame);	
	}
	
	public static void showGeneralExceprionMessage(Exception exception, JFrame parentFrame) {
		 Message GENERAL_ERROR = new Message("Exception (you can probably still save work as it is)" + LF +
	              "Description: " + exception.getMessage(), "Exception");
		 showOKOnlyMessage(GENERAL_ERROR, parentFrame);	
	}
	
	public static void showOKOnlyMessage(Message message, JFrame parentFrame) {
		
		final JDialog dialog = new JDialog(parentFrame);
		dialog.setTitle(message.title);
		dialog.setModal(true);
		dialog.setAlwaysOnTop(true);
	//	dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
		
		JOptionPane optPane = new JOptionPane(message.text,JOptionPane.INFORMATION_MESSAGE);
		optPane.addPropertyChangeListener(new PropertyChangeListener()
		   {
		      public void propertyChange(PropertyChangeEvent e)
		      {
		         if (e.getPropertyName().equals("value"))
		         {
		            switch ((Integer)e.getNewValue())
		            {
		               case JOptionPane.OK_OPTION:
		                  //user clicks OK
		                  break;
		               case JOptionPane.CANCEL_OPTION:
		                  //user clicks CANCEL
		                  break;                       
		            }
		            dialog.dispose();
		         }
		      }
		   });
		
		dialog.setContentPane(optPane);
		dialog.pack();
		dialog.setLocationRelativeTo(parentFrame);
		dialog.setVisible(true);
		
		//JOptionPane.showMessageDialog(aliViewWindow,message.text,message.title, JOptionPane.INFORMATION_MESSAGE);	
	}
	
	
	
	
   public static boolean showOKOnlyMessageWithCbx(Message message, boolean cbxSelected, AliViewWindow aliViewWindow) {
		
		final JDialog dialog = new JDialog(aliViewWindow);
		dialog.setTitle(message.title);
		dialog.setModal(true);
		dialog.setAlwaysOnTop(true);
		dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
		JCheckBox cbx = new JCheckBox("Don't show this message again");
		cbx.setSelected(cbxSelected);
		cbx.setFocusPainted(false);
		JOptionPane optPane = new JOptionPaneWithCheckbox(cbx, message.text,JOptionPane.INFORMATION_MESSAGE);
		optPane.addPropertyChangeListener(new PropertyChangeListener()
		   {
		      public void propertyChange(PropertyChangeEvent e)
		      {
		         if (e.getPropertyName().equals("value"))
		         {
		            switch ((Integer)e.getNewValue())
		            {
		               case JOptionPane.OK_OPTION:
		                  //user clicks OK
		                  break;
		               case JOptionPane.CANCEL_OPTION:
		                  //user clicks CANCEL
		                  break;                       
		            }
		            dialog.dispose();
		         }
		      }
		   });
		
		dialog.setContentPane(optPane);
		dialog.pack();
		dialog.setLocationRelativeTo(aliViewWindow);
		dialog.setVisible(true);
		
		return cbx.isSelected();
		
	}
   	
}


class Message{
	String title;
	String text;
	
	public Message(String text, String title) {
		this.title = title;
		this.text = text;
	}
	public Message(String text) {
		this(text,"");
	}
}

