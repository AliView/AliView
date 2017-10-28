package utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

public class FileUtilities {
	private static final Logger logger = Logger.getLogger(FileUtilities.class);
	private static int preferedWidth = 650;
	private static int preferedHeight = 450;

	public static File selectOpenFileViaChooser(File suggestedFile, Component parentComponent){

		File selectedFile = null;

		// If mac or windows open FileDialog
		// I have skipped using Native FileUtils in mac - it is sometimes crashing
		// Only for some Windos VM is it used
		if(OSNativeUtils.isRunningDefectJFilechooserJVM()) {
			// get Frame
			Component root = SwingUtilities.getRoot(parentComponent);
			Frame parentFrame = null;
			if (root instanceof Frame) {
				parentFrame = (Frame) root;
			}
			// use the native file dialog
			FileDialog dialog = new FileDialog(parentFrame, "Open",FileDialog.LOAD);
			dialog.setDirectory(suggestedFile.getParent());
			dialog.setVisible(true);
			String fileDirectory = dialog.getDirectory();
			String fileName = dialog.getFile();
			if(fileName != null){
				selectedFile = new File(fileDirectory, fileName);
			}
		}
		else{

			// Else JFileChooser
			// Set readOnly to avoid rename of file by slow double click
			Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);  

			JFileChooser fc = new JFileChooser(suggestedFile);
			fc.setPreferredSize(new Dimension(preferedWidth, preferedHeight));

			/*
			AbstractButton button = SwingUtilities.getDescendantOfType(AbstractButton.class,
				      fc, "Icon", UIManager.getIcon("FileChooser.detailsViewIcon"));
			button.doClick();
			 */

			int returnVal = fc.showOpenDialog(parentComponent);


			UIManager.put("FileChooser.readOnly", old);  


			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = fc.getSelectedFile();
			} else {
				selectedFile = null;
			}

			preferedWidth = fc.getSize().width;
			preferedHeight = fc.getSize().height;
		}

		System.out.println("selectedfile" + selectedFile);

		return selectedFile;

	}


	public static File selectSaveFileViaChooser(File suggestedFile, Component parentComponent){

		File selectedFile = null;	


		if(OSNativeUtils.isRunningDefectJFilechooserJVM()) {
			// get Frame
			Component root = SwingUtilities.getRoot(parentComponent);
			Frame parentFrame = null;
			if (root instanceof Frame) {
				parentFrame = (Frame) root;
			}
			// use the native file dialog on the mac
			FileDialog dialog = new FileDialog(parentFrame, "Save",FileDialog.SAVE);
			dialog.setDirectory(suggestedFile.getParent());
			dialog.setFile(suggestedFile.getName());
			dialog.setVisible(true);
			String fileName = dialog.getFile();
			String directory = dialog.getDirectory(); 
			if(fileName != null && directory != null){
				selectedFile = new File(directory, fileName);
			}
		}


		else{

			// Else JFileChooser

			// No good with for save filechooser because then you can not create dir
			// UIManager.put("FileChooser.readOnly", Boolean.TRUE); 

			// Cannot set file in constructor, then only directory and not also filename is suggested in chooser

			JFileChooser fc = new JFileChooser();

			// Additional field
			//			JTextField field = new JTextField("Hello, World");
			//			JPanel fcPanel = new JPanel();
			//			fcPanel.setLayout(new BorderLayout());
			//			fcPanel.add(fc, BorderLayout.CENTER);
			//			fcPanel.add(field, BorderLayout.SOUTH);
			// end additional

			fc.setSelectedFile(suggestedFile);
			fc.setPreferredSize(new Dimension(preferedWidth, preferedHeight));


			int returnVal = fc.showSaveDialog(parentComponent);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = fc.getSelectedFile();
			} else {
				selectedFile = null;
			}

			preferedWidth = fc.getSize().width;
			preferedHeight = fc.getSize().height;
		}

		return selectedFile;
	}
}
