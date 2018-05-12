package aliview.messenges;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import utils.DialogUtils;
import aliview.AliView;
import aliview.AliViewWindow;
import aliview.AminoAcid;
import aliview.gui.AliViewJMenuBar;
import aliview.settings.Settings;

public class Messenger {
	private static final String LF = System.getProperty("line.separator");
	private static final Logger logger = Logger.getLogger(Messenger.class);
	public static final Message SAVE_NOT_POSSIBLE_TRY_SAVE_AS = new Message("Not possible to save, try Save as....", "Sorry....");
	public static final Message NO_SELECTION = new Message("Nothing selected...", "No selection");	
	public static final Message NO_FASTA_IN_CLIPBOARD = new Message("Could not find fasta sequences in clipboard (name has to start with >)", "No fasta sequences");
	public static final Message CLIPBOARD_EMPTY = new Message("Clipboard is empty", "No sequences");
	public static final Message NO_FASTA_IN_CLIPBOARD_PASTE_ANYWAY = new Message("Could not find fasta sequences in clipboard (name has to start with >)" + LF + 
			"- Paste anyway?", "No fasta sequences - Paste anyway?");
	public static final Message NO_FASTA_OR_FILE_IN_CLIPBOARD = new Message("Could not find fasta sequences (name has to start with >)" + LF +
			"- or a valid alignment file name in clipboard.", "No sequences");
	public static final Message ALIGNER_SOMETHING_PROBLEM_ERROR = new Message("Something did not work out when aligning.", "Problem when aligning");
	public static final Message NO_FULLY_SELECTED_SEQUENCES = new Message("There are no fully selected sequences - if you only want to realign" + LF +
			"a part of sequence, select \"Realign selected block\" instead.", "No selected sequence");
	public static final Message COULD_NOT_OPEN_HELP_IN_BROWSER = new Message("Could not open help file in browser, help is available at: " + Settings.getAliViewHelpWebPage(), "Problem when aligning");
	public static final Message OUT_OF_MEMORY_ERROR = new Message("Out of memory error - you can probably still save your work as it is." + LF +
			//       "One source of this error is the number of undo-steps preserved - you can change this in Settings." + LF +
			"If you want to increase memory available for AliView, see:  " + Settings.getAliViewHelpWebPage(), "Out of memory");
	public static final Message ONLY_VIEW_WHEN_FILESEQUENCES = new Message("Edit capabilities are limited when large alignment is read from file" + LF + 
			"You can delete and rearange sequences." + LF + 
			"If you need full editing capabilities then you can increase " + LF + 
			"AliView memory settings under menu \"Preferences\". Memory " + LF +
			"needed is 2 x file size if files are to be read into memory." + LF +
			" ", "Limited edit capabilities");

	public static final Message MUSCLE_PROFILE_INFO_MESSAGE = new Message("MUSCLE \"-profile\" command is used for adding new sequences" + LF + 
			"to the alignment. The performance of this method might be" + LF +
			"less favourable than some other add sequences algorithms." + LF + 
			"See for example Löytynoja et al (2012) and Katoh & Frith (2012)" + LF +
			"One option is to download and install MAFFT and then use the" + LF +
			"MAFFT -addfragments algorithm instead.",
			"Add sequences with MUSCLE profile");

	public static final Message LIMITED_UNDO_CAPABILITIES = new Message("The size of the alignment prevents Undo functionality when editing." + LF + 
			"- Don't forget to 'Save As' every once in a while....", "Undo function disabled");

	public static final Message NO_PRIMERS_FOUND = new Message("Could not find any primers in selected region with current settings." + LF + 
			"Try adjusting \"Find primer settings\" or try another selection." + LF + 
			"Note that characters n or ? will greatly reduce the possibility" + LF + 
			"of finding primers", "No primers found");
	public static final Message EDIT_MODE_QUESTION = new Message("Edit key/menu pressed (or mouse edit), " + LF + "do you want to allow edits?" + LF + "", "Edit mode?");
	public static final Message FILE_OPEN_NOT_EXISTS = new Message("Could not open file (does not exist).", "File not found");
	public static final Message COMPLEMENT_FUNCTION_ERROR = new Message("Error in reverse complement function: ", "Problem");
	public static final Message FILE_SAVE_ERROR = new Message("Error saving file: ", "File save problem");
	public static final Message ERROR_PASTE = new Message("Error pasting: ", "Problem");
	public static final Message UNDO_REDO_PROBLEM = new Message("Error in undo/redo function: ", "Problem");
	public static final Message ERROR_RUNNING_EXTERNAL_COMMAND = new Message("Error running external command: ", "Problem");
	public static final Message ERROR_PRINTING = new Message("Error in print function: ");
	public static final Message OPEN_LARGE_FILE_ERROR = new Message("Error when trying to open large file:" + LF +
			"One source of problem could be if you are running a 32-bit OS." + LF +
			"Opening large files are mainly tested on 64-bit OS" + LF +
			"Another problem could be if your Java version is 32-bit although" + LF + 
			"your OS is 64-bit, in this case you could download and install latest 64-bit Java." + LF +
			"Another solution is if you can increase memory for AliView so that alignment is" + LF +
			"loaded into memory and not residing on file. See setting memory in Program Preferences.", "Problem");
	public static final Message FILE_ERROR = new Message("Error when reading file:", "Problem");
	public static final Message ALIGNMENT_META_READ_ERROR = new Message("Error during import alignment metadata: ", "Problem");
	public static final Message ALIGNMENT_IMPORT_ERROR = new Message("Could not Import alignment: ", "Import problem");
	public static final Message TO_BIG_SELECTION_FOR_COPY = new Message("Selection is to big to Copy", "To bil selection");
	public static final Message NO_FASTA_INDEX_COULD_BE_SAVED = new Message("Could not save Fasta index file: Alignment has to be indexed" + LF + 
			"from file when loaded and in Fasta format", "Problem");
	public static final Message REALIGN_EVERYTHING = new Message("Are you sure you want to realign the whole alignment?", "Realign everything");
	public static final Message DELETE_VERTICAL_GAPS = new Message("Are you sure you want to delete all vertical gaps?", "Delete vertical gaps?");
	public static final Message DELETE_ALL_GAPS = new Message("Are you sure you want to delete all gaps in all sequences?", "Delete ALL gaps?");
	public static final Message DELETE_SELECTED_SEQUENCES = new Message("Are you sure you want to delete all selected sequences?", "Delete selected Sequences?");
	public static final Message DELETE_SELECTED_BASES = new Message("Are you sure you want to clear all selected positions?", "Clear selected positions?");
	public static final Message PHENOTYPE_IMAGE_OPEN_ERROR = new Message("Could not create Phenotype from Image file. Wrong file type? (png,jpg)", "Wrong file");
	public static final Message SUGGEST_ALIGN_AS_TRANSLATED = new Message("If you want to align Nucleotides as translated Amino Acids, please use function:" + LF +			                                                              "Realign everything as Translated Amino Acids", "Can not realign");
	public static final Message MULTI_FILE_DROP_WARNING = new Message("More than 10 files dropped at once - Is this what you intended? " , "Multiple files dropped");
	public static final Message FAILED_SEQUENCE_DETECTION = new Message("Sequence type could not be automatically detected for" + LF +
			"this alignment file. Please set sequence type manually:" + LF +
			"Menu \"Edit\" -> \"Set Alignment Sequence Type\"", "Unknown sequence type");
	public static final Message SUBPROCESS_CANCELLED_SUCCESSFULLY = new Message("Subprocess cancelled sucessfully", "Subprocess cancelled");
	public static final Message PAD_OR_TRIM_ALIGNMENT_TO_EQUAL_LENGTH = new Message("Sequences are of different length." + LF +
			"Sometimes alignments with different length sequences" + LF + 
			"cause problems in other programs. Do you want to pad" + LF + 
			"sequences with gap(s) at end to equal length?", "Sequences are of different length");

	private static int lastSelectedOption = -1;
	private static boolean showedMaxJPanelSizeMessageOnceThisSession;


	public static void main(String[] args) {
		boolean cbxSelected = showOKOnlyMessageWithCbx(MUSCLE_PROFILE_INFO_MESSAGE, true, null);
		logger.info("cbxSelected" + cbxSelected);
	}

	public static void showGeneralErrorMessage(Error error, JFrame parentFrame) {
		Message GENERAL_ERROR = new Message("Error (you can probably still save work as it is)" + LF +
				"Description: " + error.getMessage(), "Error");
		showOKOnlyMessage(GENERAL_ERROR, parentFrame);	
	}

	public static void showGeneralExceptionMessage(Exception exception, JFrame parentFrame) {
		Message GENERAL_ERROR = new Message("Exception (you can probably still save work as it is)" + LF +
				"Description: " + exception.getMessage(), "Exception");
		showOKOnlyMessage(GENERAL_ERROR, parentFrame);	
	}

	public static void showOKOnlyMessage(Message message, JFrame parentFrame) {
		showOKOnlyMessage(message, "",  parentFrame);
	}

	public static void showOKOnlyMessage(Message message) {
		showOKOnlyMessage(message, "",  AliView.getActiveWindow());
	}


	public static void showOKOnlyMessage(Message message, String appendMessageText) {
		showOKOnlyMessage(message, appendMessageText,  AliView.getActiveWindow());
	}

	public static void showOKOnlyMessage(Message message, String appendMessageText, JFrame parentFrame) {
		showOptionMessage(message, parentFrame, JOptionPane.OK_OPTION);
	}

	public static void showOKCancelMessage(Message message, JFrame parentFrame) {
		showOptionMessage(message, parentFrame, JOptionPane.OK_CANCEL_OPTION);
	}

	public static void showOptionMessage(Message message,JFrame parentFrame, int optionType) {

		final JDialog dialog = new JDialog(parentFrame);
		dialog.setTitle(message.title);
		// OBS DO NOT MODAL - Then there is problem in MAC when executed from error thread
		dialog.setModal(true);
		dialog.setAlwaysOnTop(true);
		JOptionPane optPane = new JOptionPane(message.text,JOptionPane.INFORMATION_MESSAGE, optionType);
		optPane.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				if (e.getPropertyName().equals("value"))
				{
					switch ((Integer)e.getNewValue())
					{
					case JOptionPane.OK_OPTION:
						lastSelectedOption = JOptionPane.OK_OPTION;
						break;
					case JOptionPane.CANCEL_OPTION:
						lastSelectedOption = JOptionPane.CANCEL_OPTION;
						break;                       
					}
					// dialog.setModal(false);
					dialog.dispose();
				}
			}
		});

		dialog.setContentPane(optPane);
		dialog.pack();
		dialog.setLocationRelativeTo(parentFrame);
		dialog.setVisible(true);
		//dialog.setModal(false);

	}

	public static boolean showYesNoCancelMessageWithCbx(Message message, boolean cbxSelected, JFrame parentFrame) {
		return  showOptionMessageWithCbx(message, cbxSelected, parentFrame, JOptionPane.YES_NO_CANCEL_OPTION);
	}

	public static boolean showOKOnlyMessageWithCbx(Message message, boolean cbxSelected, JFrame parentFrame) {
		return showOptionMessageWithCbx(message, cbxSelected, parentFrame, JOptionPane.OK_OPTION);
	}

	public static boolean showOKCancelMessageWithCbx(Message message, boolean cbxSelected, JFrame parentFrame) {
		return showOptionMessageWithCbx(message, cbxSelected, parentFrame, JOptionPane.OK_CANCEL_OPTION);
	}

	/*
	 * 
	 * TODO this method should maybe not be all static - due to problem with multiple dialogs and static variable (lastSelected)
	 * 
	 */
	public static boolean showOptionMessageWithCbx(Message message, boolean cbxSelected, JFrame parentFrame, int optionType) {

		final JDialog dialog = new JDialog(parentFrame);
		dialog.setTitle(message.title);
		// OBS DO NOT MODAL - Then there is problem in MAC when executed from error thread
		dialog.setModal(true);
		dialog.setAlwaysOnTop(true);
		JCheckBox cbx = new JCheckBox("Don't show this message again (can be undone in Preferences)");
		cbx.setSelected(cbxSelected);
		cbx.setFocusPainted(false);
		JOptionPane optPane = new JOptionPaneWithCheckbox(cbx, message.text,JOptionPane.INFORMATION_MESSAGE, optionType);
		optPane.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				if (e.getPropertyName().equals("value"))
				{
					switch ((Integer)e.getNewValue())
					{
					// OK and YES Options are the same
					case JOptionPane.OK_OPTION:
						lastSelectedOption = JOptionPane.OK_OPTION;
						break;
					case JOptionPane.NO_OPTION:
						lastSelectedOption = JOptionPane.NO_OPTION;
						break;
					case JOptionPane.CANCEL_OPTION:
						lastSelectedOption = JOptionPane.CANCEL_OPTION;
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
		//dialog.setModal(false);

		return cbx.isSelected();
	}

	public static int getLastSelectedOption() {
		return lastSelectedOption;
	}

	public static void showCountStopCodonMessage(int count, AliViewWindow aliViewWindow) {
		Message countMessage = new Message("Stop codons in alignment: " + count, "Stop codon count");
		showOKOnlyMessage(countMessage, aliViewWindow);
	}

	public static void showDuplicateSeqNamesMessage(ArrayList<String> dupeNames){

		boolean hideMessage = Settings.getHideDuplicateSeqNamesMessage().getBooleanValue();
		if(! hideMessage){

			String dupeString = "";
			int dupeCount = 0;
			for(String dupe: dupeNames){	   
				dupeString += dupe + LF;

				// Only list 10 names
				dupeCount ++;
				if(dupeCount == 10){
					break;
				}
			}

			String prep = "are";
			if(dupeCount == 1){
				prep = "is";
			}

			String dupeCountString = "";
			if(dupeCount == 10){
				dupeCountString = "at least 10";
			}else{
				dupeCountString = "" + dupeCount;
			}


			Message dupeMessage = new Message("There " + prep + " " + dupeCountString + " duplicate sequence name(s) in alignment, this might cause " + LF + 
					"unexpected problems when calling alignment programs for example." + LF + 
					"Duplicate sequences will be selected in alignment." + LF + 
					"Duplicate name(s) " + prep + ": " + dupeString, 
					"Duplicate sequence names");

			boolean hideMessageNextTime = showOKOnlyMessageWithCbx(dupeMessage, hideMessage, AliView.getActiveWindow());
			Settings.getHideDuplicateSeqNamesMessage().putBooleanValue(hideMessageNextTime);
		}


	}

	public static boolean showHideAlignmentProgramInvalidCharsInfoMessage(String invalidChars){

		boolean hideMessage =  Settings.getHideAlignmentProgramInvalidCharsInfoMessage().getBooleanValue();
		if(! hideMessage){

			Message invalCharMessage = new Message("Some aligners (e.g. Muscle, Mafft) are sensiteive to invalid characters," + LF +
					"the following were found: " + invalidChars + LF +
					"and you might need to replace them with X in your alignment.",
					"Problem characters");

			boolean hideMessageNextTime = showOKCancelMessageWithCbx(invalCharMessage, hideMessage, AliView.getActiveWindow());

			Settings.getHideAlignmentProgramInvalidCharsInfoMessage().putBooleanValue(hideMessageNextTime);
		}

		return hideMessage;


	}


	public static void showMaxJPanelSizeMessageOnceThisSession(){
		if(! showedMaxJPanelSizeMessageOnceThisSession){
			Message maxSizeMessage = new Message("The maximum size of the viewable area is 2147483647 pixels" + LF +
					"The current character-size is to large if you want to view" + LF + 
					"the last residues, try decreasing the character size if you" + LF +
					"are missing residues at the end of alignment", "Max size");
			showOKOnlyMessage(maxSizeMessage);
			showedMaxJPanelSizeMessageOnceThisSession = true;
		}

	}

	public static boolean askAllowEditMode() {

		boolean allowEditMode = false;
		boolean hideMessage = Settings.getHideAskBeforeEditMode().getBooleanValue();
		if(hideMessage){
			allowEditMode = true;
		}else{
			boolean hideMessageNextTime = showOKCancelMessageWithCbx(EDIT_MODE_QUESTION, hideMessage, AliView.getActiveWindow());
			Settings.getHideAskBeforeEditMode().putBooleanValue(hideMessageNextTime);
			if(getLastSelectedOption() == JOptionPane.OK_OPTION){
				allowEditMode = true;
			}
		}
		return allowEditMode;
	}


	public static void showEditCharsetExceptionMessage(Exception exception, JFrame parentFrame) {
		Message charsetExceptionMessage = new Message("Could not parse the charsets text:" + LF +
				exception.getMessage(), "Parse charset text exception");
		showOKOnlyMessage(charsetExceptionMessage, parentFrame);	
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

