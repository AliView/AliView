package aliview.gui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.text.DefaultEditorKit.PasteAction;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import utils.OSNativeUtils;
import utils.nexus.CharSet;
import utils.nexus.CharSets;
import aliview.AliView;
import aliview.AliViewWindow;
import aliview.GeneticCode;
import aliview.alignment.Alignment;
import aliview.alignment.AlignmentEvent;
import aliview.alignment.AlignmentListener;
import aliview.color.ColorScheme;
import aliview.color.ColorSchemeFactory;
import aliview.externalcommands.CommandItem;
import aliview.gui.pane.CharPixels;
import aliview.importer.FileFormat;
import aliview.sequencelist.AlignmentDataEvent;
import aliview.sequencelist.AlignmentDataListener;
import aliview.sequencelist.AlignmentSelectionEvent;
import aliview.sequencelist.AlignmentSelectionListener;
import aliview.sequencelist.FilePage;
import aliview.sequencelist.FileSequenceAlignmentListModel;
import aliview.settings.Settings;
import aliview.settings.SettingsListener;

public class AliViewJMenuBar extends JMenuBar implements AlignmentListener, AlignmentDataListener, AlignmentSelectionListener, SettingsListener{
	private static final Logger logger = Logger.getLogger(AliViewJMenuBar.class);
	private AliViewWindow aliViewWindow;
	private ArrayList<AbstractButton> editFunctions = new ArrayList<AbstractButton>();
	private ArrayList<AbstractButton> alwaysAvailableFunctions = new ArrayList<AbstractButton>();
	private ArrayList<AbstractButton> loadedAlignmentFunctions = new ArrayList<AbstractButton>();
	private ArrayList<AbstractButton> nucleotideFunctions = new ArrayList<AbstractButton>();
	private ArrayList<AbstractButton> aminoAcidFunctions = new ArrayList<AbstractButton>();
	private ArrayList<AbstractButton> hasSelectionFunctions = new ArrayList<AbstractButton>();
	private ArrayList<AbstractButton> undoFunctions = new ArrayList<AbstractButton>();
	private ArrayList<AbstractButton> reorderAndDeleteFunctions = new ArrayList<AbstractButton>();
	private JMenu mnRecentFiles;
	private JMenu mnExternal;
	private JMenu mnFilePages;
	private JMenu mnGeneticCodeCode;
	private JMenuItem undoButton;
	private JMenuItem redoButton;
	private ButtonModel toggleTranslationButtonModel;
	private ButtonModel highlightConsButtonModel;
	private ButtonModel highlightNonConsButtonModel;
	private ButtonModel highlightDiffButtonModel;
	private ButtonModel decFontSizeButtonModel;
	private ButtonModel incFontSizeButtonModel;
	private ButtonModel drawCoonPosOnRulerButtonModel;
	private ButtonModel nonCodingButtonModel;
	private ButtonModel showAACodeButtonMoes;
	private ButtonModel coding1ButtonModel;
	private ButtonModel coding0ButtonModel;
	private ButtonModel coding2ButtonModel;
	private ButtonModel transOnePosButtonModel;
	private ButtonModel copyAsFastaButtonModel;
	private ButtonModel copyAsCharatersButtonModel;
	private ButtonModel realignSelectedBlockButtonModel;
	private ButtonModel realignSelectedSequencesButtonModel;
	private ButtonModel pasteAsFastaButtonModel;
	private ButtonModel editModeButtonModel;
	private boolean menuLock;
	private ButtonModel copyNameButtonModel;
	private ButtonModel renameButtonModel;
	private JMenu mnSelectCharset;
	private ButtonModel countCodonButtonModel;
	private ButtonModel addEmptySeqButtonModel;

	/*
	 * 
	 *  TODO Currently the MenuBar is containing both the GUI and all the Actions
	 *  The button models are shared with Popup menu, ToolBar etc.
	 *  this could be separated into menu and then separate Actions
	 *  The menu buttons are also taking responsibility for when "Actions" are enabled/disabled
	 * 
	 * 
	 */
	public AliViewJMenuBar(AliViewWindow aliViewWin){
		super();
		aliViewWindow = aliViewWin;

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		this.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AliView.createNewWindow();
			}

		});
		mntmNew.setIcon(AppIcons.getNewIcon());
		mntmNew.setAccelerator(OSNativeUtils.getNewFileAccelerator());
		mnFile.add(mntmNew);
		alwaysAvailableFunctions.add(mntmNew);


		JMenuItem mntmOpenFile = new JMenuItem("Open File");
		mntmOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AliView.openAlignmentFileViaChooser(aliViewWindow.getParent());
			}

		});
		mntmOpenFile.setAccelerator(OSNativeUtils.getOpenFileAccelerator());
		mnFile.add(mntmOpenFile);
		alwaysAvailableFunctions.add(mntmOpenFile);

		mnFile.add(new JSeparator());

		mnRecentFiles = new JMenu("Recent Files");
		mnFile.add(mnRecentFiles);	
		rebuildRecentFilesSubmenu();
		alwaysAvailableFunctions.add(mnRecentFiles);

		mnFile.add(new JSeparator());

		JMenuItem mntmReloadFile = new JMenuItem("Reload file");
		mntmReloadFile.setAccelerator(OSNativeUtils.getReloadKeyAccelerator());
		mntmReloadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.reloadCurrentFile();
			}
		});
		mnFile.add(mntmReloadFile);
		loadedAlignmentFunctions.add(mntmReloadFile);

		mnFile.add(new JSeparator());

		JMenuItem saveFileMenu = new JMenuItem("Save");
		saveFileMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				aliViewWindow.saveAlignmentFile();
			}
		});
		saveFileMenu.setAccelerator(OSNativeUtils.getSaveFileAccelerator());
		mnFile.add(saveFileMenu);
		loadedAlignmentFunctions.add(saveFileMenu);

		JMenuItem saveAlignmentAsFastaNebu = new JMenuItem("Save as Fasta");
		saveAlignmentAsFastaNebu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.FASTA, false);
			}
		});
		mnFile.add(saveAlignmentAsFastaNebu);
		loadedAlignmentFunctions.add(saveAlignmentAsFastaNebu);


		JMenuItem mntmSaveAlignmentAsNexus = new JMenuItem("Save as Nexus");
		mntmSaveAlignmentAsNexus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.NEXUS, false);
			}

		});
		mnFile.add(mntmSaveAlignmentAsNexus);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsNexus);

		JMenuItem mntmSaveAlignmentAsSimpleNexus = new JMenuItem("Save as Nexus (Illegal name chars replaced by _ (e.g. for MrBayes)");
		mntmSaveAlignmentAsSimpleNexus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.NEXUS_SIMPLE, false);
			}
		});
		mnFile.add(mntmSaveAlignmentAsSimpleNexus);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsSimpleNexus);

		JMenuItem mntmSaveAlignmentAsNexusCodon = new JMenuItem("Save as codonpos Nexus (codonpos as charsets - excluded removed)");
		mntmSaveAlignmentAsNexusCodon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.NEXUS_CODONPOS_CHARSET, false);
			}

		});
		mnFile.add(mntmSaveAlignmentAsNexusCodon);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsNexusCodon);
		nucleotideFunctions.add(mntmSaveAlignmentAsNexusCodon);

		JMenuItem mntmSaveAlignmentAsPhylipRelaxedPadded = new JMenuItem("Save as Phylip (full names & padded)");
		mntmSaveAlignmentAsPhylipRelaxedPadded.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.PHYLIP_RELAXED_PADDED_AKA_LONG_NAME_SEQUENTIAL, false);
			}

		});
		mnFile.add(mntmSaveAlignmentAsPhylipRelaxedPadded);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsPhylipRelaxedPadded);
		
		
		JMenu menuPhylipSubmenu = new JMenu("Save as Phylip (other format versions)");
		mnFile.add(menuPhylipSubmenu);
		
		
		JMenuItem mntmSaveAlignmentAsPhylipRelaxedPaddedInterleaved = new JMenuItem("Save as Phylip (full names, padded & interleaved)");
		mntmSaveAlignmentAsPhylipRelaxedPaddedInterleaved.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.PHYLIP_RELAXED_PADDED_INTERLEAVED_AKA_LONG_NAME_INTERLEAVED, false);
			}

		});
		menuPhylipSubmenu.add(mntmSaveAlignmentAsPhylipRelaxedPaddedInterleaved);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsPhylipRelaxedPaddedInterleaved);
		

		JMenuItem mntmSaveAlignmentAsPhylip = new JMenuItem("Save as Phylip (full names)");
		mntmSaveAlignmentAsPhylip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.PHYLIP_RELAXED, false);
			}

		});
		menuPhylipSubmenu.add(mntmSaveAlignmentAsPhylip);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsPhylip);

		JMenuItem mntmSaveAlignmentAsPhylipStrictSequential = new JMenuItem("Save as Phylip (strict 10 char names)");
		mntmSaveAlignmentAsPhylipStrictSequential.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.PHYLIP_STRICT_SEQUENTIAL_AKA_SHORT_NAME_SEQUENTIAL, false);
			}

		});
		menuPhylipSubmenu.add(mntmSaveAlignmentAsPhylipStrictSequential);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsPhylipStrictSequential);

		JMenuItem mntmSaveAlignmentAsClustal = new JMenuItem("Save as Clustal (aln)");
		mntmSaveAlignmentAsClustal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.CLUSTAL, false);
			}

		});
		mnFile.add(mntmSaveAlignmentAsClustal);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsClustal);

		JMenuItem mntmSaveAlignmentAsMSF = new JMenuItem("Save as MSF/GCG");
		mntmSaveAlignmentAsMSF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.MSF, false);
			}

		});
		mnFile.add(mntmSaveAlignmentAsMSF);
		loadedAlignmentFunctions.add(mntmSaveAlignmentAsMSF);

		mnFile.add(new JSeparator());

		JMenuItem mntmSaveSelAsFasta = new JMenuItem("Save selection as Fasta");
		mntmSaveSelAsFasta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveSelectionAsFastaFileViaChooser();
			}
		});
		mnFile.add(mntmSaveSelAsFasta);
		hasSelectionFunctions.add(mntmSaveSelAsFasta);

		mnFile.add(new JSeparator());

		JMenuItem mntmSaveTranslatedAlignmentAsFasta = new JMenuItem("Save Translated alignment (Amino Acid) as Fasta");
		mntmSaveTranslatedAlignmentAsFasta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.FASTA_TRANSLATED_AMINO_ACID, false);
			}

		});
		mnFile.add(mntmSaveTranslatedAlignmentAsFasta);
		loadedAlignmentFunctions.add(mntmSaveTranslatedAlignmentAsFasta);
		nucleotideFunctions.add(mntmSaveTranslatedAlignmentAsFasta);
		/*
		JMenuItem mntmSaveTranslatedAlignmentAsPhylip = new JMenuItem("Save Translated alignment (Amino Acid) as Phylip");
		mntmSaveTranslatedAlignmentAsPhylip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.PHYLIP_TRANSLATED_AMINO_ACID, false);
			}

		});
		mnFile.add(mntmSaveTranslatedAlignmentAsPhylip);
		loadedAlignmentFunctions.add(mntmSaveTranslatedAlignmentAsPhylip);
		nucleotideFunctions.add(mntmSaveTranslatedAlignmentAsPhylip);

		JMenuItem mntmSaveTranslatedAlignmentAsNexus = new JMenuItem("Save Translated alignment (Amino Acid) as Nexus");
		mntmSaveTranslatedAlignmentAsNexus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.NEXUS_TRANSLATED_AMINO_ACID, false);
			}

		});
		mnFile.add(mntmSaveTranslatedAlignmentAsNexus);
		loadedAlignmentFunctions.add(mntmSaveTranslatedAlignmentAsNexus);
		nucleotideFunctions.add(mntmSaveTranslatedAlignmentAsNexus);
		 */

		mnFile.add(new JSeparator());

		JMenuItem mntmSaveFastaIndex = new JMenuItem("Save fasta index file (.fai)");
		mntmSaveFastaIndex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveFastaIndex();
			}
		});
		mnFile.add(mntmSaveFastaIndex);
		loadedAlignmentFunctions.add(mntmSaveFastaIndex);


		mnFile.add(new JSeparator());

		JMenuItem mntmExportAlignmentAsImage = new JMenuItem("Export alignment as image");
		mntmExportAlignmentAsImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.IMAGE_PNG, true);
			}
		});
		mnFile.add(mntmExportAlignmentAsImage);
		loadedAlignmentFunctions.add(mntmExportAlignmentAsImage);

		mnFile.add(new JSeparator());
		
		JMenuItem mntmExportRaxMLFile = new JMenuItem("Export RaxML partition file");
		mntmExportRaxMLFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.exportRaxMLFile();
			}
		});
		mnFile.add(mntmExportRaxMLFile);
		loadedAlignmentFunctions.add(mntmExportRaxMLFile);

		mnFile.add(new JSeparator());
		
//		JMenuItem mntmGenoPheno = new JMenuItem("Genotype > phenotype");
//		mntmGenoPheno.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				aliViewWindow.genotype2phenotype();
//			}
//		});
//		mnFile.add(mntmGenoPheno);
//		alwaysAvailableFunctions.add(mntmGenoPheno);
//		
//		mnFile.add(new JSeparator());

		JMenuItem mntmPrint = new JMenuItem("Print");
		mntmPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.printAlignment();
			}
		});
		mnFile.add(mntmPrint);
		mntmPrint.setAccelerator(OSNativeUtils.getPrintAccelerator());
		loadedAlignmentFunctions.add(mntmPrint);


		mnFile.add(new JSeparator());
		/*
		JMenuItem mntmDebug = new JMenuItem("Start debug");
		mntmDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.startDebug();
			}
		});
		mnFile.add(mntmDebug);
		alwaysAvailableFunctions.add(mntmDebug);
		 */
		JMenuItem mntmLogFile = new JMenuItem("Show message log");
		mntmLogFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.showMessageLog();
			}


		});
		mnFile.add(mntmLogFile);
		alwaysAvailableFunctions.add(mntmLogFile);

		mnFile.add(new JSeparator());

		
		// show stats
		
		JMenuItem mntmShowStats = new JMenuItem("Show statistics");
		mntmShowStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.createStats();
			}
		});
		mnFile.add(mntmShowStats);
		alwaysAvailableFunctions.add(mntmShowStats);
		 
		// end show stats		
				
				

		// Mac has its own menu item
		if(OSNativeUtils.isAnythingButMac()){
			JMenuItem mntmExit = new JMenuItem("Exit");
			mntmExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AliView.quitProgram();
				}
			});
			mntmExit.setIcon(AppIcons.getQuitIcon());
			mnFile.add(mntmExit);
			alwaysAvailableFunctions.add(mntmExit);
		}


		JMenu mnEdit = new JMenu("Edit");
		mnEdit.setMnemonic(KeyEvent.VK_E);
		this.add(mnEdit);

		undoButton = new JMenuItem("Undo");
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.getUndoControler().undo();
			}
		});
		undoButton.setAccelerator(OSNativeUtils.getUndoKeyAccelerator());
		undoButton.setIcon(AppIcons.getUndoIcon()); 
		mnEdit.add(undoButton);
		undoFunctions.add(undoButton);

		redoButton = new JMenuItem("Redo");
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.getUndoControler().redo();
			}
		});
		redoButton.setAccelerator(OSNativeUtils.getRedoKeyAccelerator());
		redoButton.setIcon(AppIcons.getRedoIcon());
		mnEdit.add(redoButton);
		undoFunctions.add(redoButton);

		mnEdit.add(new JSeparator());
		JMenuItem mntmCopySelectionAsFasta = new JMenuItem("Copy selection as fasta");
		mntmCopySelectionAsFasta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.copySelectionAsFasta();
			}
		});
		mntmCopySelectionAsFasta.setAccelerator(OSNativeUtils.getCopySelectionAsFastaKeyAccelerator());
		copyAsFastaButtonModel = mntmCopySelectionAsFasta.getModel();
		mnEdit.add(mntmCopySelectionAsFasta);
		hasSelectionFunctions.add(mntmCopySelectionAsFasta);


		JMenuItem mntmCopySelectionAsNucleotides = new JMenuItem("Copy selection as characters");
		mntmCopySelectionAsNucleotides.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.copySelectionAsNucleotides();
			}
		});
		mntmCopySelectionAsNucleotides.setAccelerator(OSNativeUtils.getCopyKeyAccelerator());
		copyAsCharatersButtonModel = mntmCopySelectionAsNucleotides.getModel();
		mnEdit.add(mntmCopySelectionAsNucleotides);
		hasSelectionFunctions.add(mntmCopySelectionAsNucleotides);

		JMenuItem mntmCopyName = new JMenuItem("Copy name(s) only");
		mntmCopyName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.copyNames();
			}
		});
		//mntmCopyName.setAccelerator(OSNativeUtils.getCopyKeyAccelerator());
		copyNameButtonModel = mntmCopyName.getModel();
		mnEdit.add(mntmCopyName);
		hasSelectionFunctions.add(mntmCopyName);

		JMenuItem mntmPasteClipboard = new JMenuItem("Paste (fasta-sequences)");
		mntmPasteClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.pasteFasta();
			}
		});
		mntmPasteClipboard.setAccelerator(OSNativeUtils.getPasteKeyAccelerator());
		pasteAsFastaButtonModel = mntmPasteClipboard.getModel();
		mnEdit.add(mntmPasteClipboard);
		editFunctions.add(mntmPasteClipboard);
		alwaysAvailableFunctions.add(mntmPasteClipboard);


		mnEdit.add(new JSeparator());

		JMenuItem mntmRename = new JMenuItem("Rename sequence");
		mntmRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.renameFirstSelected();
			}
		});
		mntmRename.setAccelerator(OSNativeUtils.getRenameKeyAccelerator());
		renameButtonModel = mntmRename.getModel();
		mnEdit.add(mntmRename);
		editFunctions.add(mntmRename);
		hasSelectionFunctions.add(mntmRename);

		mnEdit.add(new JSeparator());

		JMenuItem mntmAddFromFile = new JMenuItem("Add sequences from file");
		mntmAddFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.addSequencesFromFile(0);
			}
		});
		mnEdit.add(mntmAddFromFile);
		editFunctions.add(mntmAddFromFile);
		
		JMenuItem mntmAddNewSeq = new JMenuItem("Add new empty sequence");
		mntmAddNewSeq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.addNewSequence();
			}
		});
		addEmptySeqButtonModel = mntmAddNewSeq.getModel();
		mnEdit.add(mntmAddNewSeq);
		editFunctions.add(mntmAddNewSeq);

		mnEdit.add(new JSeparator());

		JCheckBoxMenuItem mntmEditMode = new JCheckBoxMenuItem("Edit mode");
		mntmEditMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem cbxItem = (JCheckBoxMenuItem) e.getSource();
				aliViewWindow.setEditMode(cbxItem.isSelected());
			}
		});
		editModeButtonModel = mntmEditMode.getModel();
		mnEdit.add(mntmEditMode);
		editFunctions.add(mntmEditMode);
		loadedAlignmentFunctions.add(mntmEditMode);


		JMenuItem mntmClearSelectedItem = new JMenuItem("Clear selected bases");
		mntmClearSelectedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.clearSelectedBases();
			}
		});
		mntmClearSelectedItem.setIcon(AppIcons.getClearIcon());
		mntmClearSelectedItem.setAccelerator(OSNativeUtils.getClearKeyAccelerator());
		mnEdit.add(mntmClearSelectedItem);
		editFunctions.add(mntmClearSelectedItem);
		hasSelectionFunctions.add(mntmClearSelectedItem);

		JMenuItem mntmDeleteSelectedItem = new JMenuItem("Delete selected");
		mntmDeleteSelectedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.deleteSelected();
			}
		});
		mntmDeleteSelectedItem.setAccelerator(OSNativeUtils.getDeleteKeyAccelerator());
		mnEdit.add(mntmDeleteSelectedItem);
		reorderAndDeleteFunctions.add(mntmDeleteSelectedItem);


		JMenuItem mntmRemoveVerticalGaps = new JMenuItem("Delete gap-only columns");
		mntmRemoveVerticalGaps.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				aliViewWindow.deleteVerticalGaps();

			}
		});
		//mntmRemoveVerticalGaps.setAccelerator(KeyStroke.getKeyStroke(??));
		mnEdit.add(mntmRemoveVerticalGaps);
		editFunctions.add(mntmRemoveVerticalGaps);
		loadedAlignmentFunctions.add(mntmRemoveVerticalGaps);

		JMenuItem mntmRemoveAllGaps = new JMenuItem("Delete all gaps in all sequences");
		mntmRemoveAllGaps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.deleteAllGaps();

			}
		});
		mnEdit.add(mntmRemoveAllGaps);
		editFunctions.add(mntmRemoveAllGaps);
		loadedAlignmentFunctions.add(mntmRemoveAllGaps);

		JMenuItem mntmTrim = new JMenuItem("Trim sequences");
		mntmTrim.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				aliViewWindow.trimSequences();

			}
		});
		mnEdit.add(mntmTrim);
		editFunctions.add(mntmTrim);
		loadedAlignmentFunctions.add(mntmTrim);

		JMenuItem mntmDeleteExcluded = new JMenuItem("Delete excluded bases");
		mntmDeleteExcluded.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.deleteExludedBases();
			}
		});
		mnEdit.add(mntmDeleteExcluded);
		editFunctions.add(mntmDeleteExcluded);
		loadedAlignmentFunctions.add(mntmDeleteExcluded);

		JMenuItem mntmDeleteEmpty = new JMenuItem("Delete empty sequences");
		mntmDeleteEmpty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.deleteEmptySequences();
			}
		});
		mnEdit.add(mntmDeleteEmpty);
		editFunctions.add(mntmDeleteEmpty);
		loadedAlignmentFunctions.add(mntmDeleteEmpty);

		mnEdit.add(new JSeparator());

		JMenuItem mntmFind = new JMenuItem("Find");
		mntmFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aliViewWindow.find();
			}
		});
		mnEdit.add(mntmFind);
		loadedAlignmentFunctions.add(mntmFind);
		
		JMenuItem mntmFindDupes = new JMenuItem("Find and select duplicate sequences");
		//mntmFindDupes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmFindDupes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aliViewWindow.selectDuplicates();
			}
		});
		mnEdit.add(mntmFindDupes);
		loadedAlignmentFunctions.add(mntmFindDupes);

		JMenuItem mntmFindClipboardNames = new JMenuItem("Find sequence names from clipboard");
		mntmFindClipboardNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmFindClipboardNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aliViewWindow.findNamesFromClipboard();
			}
		});
		mnEdit.add(mntmFindClipboardNames);
		loadedAlignmentFunctions.add(mntmFindClipboardNames);

		mnEdit.add(new JSeparator());

		JMenuItem mntmMerger = new JMenuItem("Merge 2 selected sequences");
		mntmMerger.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmMerger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.merge2SelectedSequences();
			}
		});
		mnEdit.add(mntmMerger);
		editFunctions.add(mntmMerger);
		loadedAlignmentFunctions.add(mntmMerger);

		mnEdit.add(new JSeparator());		

		JMenuItem mntmRevCompSequences = new JMenuItem("Reverse Complement Selected Sequences");
		mntmRevCompSequences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				logger.info("action perf mntmRevCompSequences");
				aliViewWindow.reverseComplementSelectedSequences();
			}

		});
		mnEdit.add(mntmRevCompSequences);
		editFunctions.add(mntmRevCompSequences);
		loadedAlignmentFunctions.add(mntmRevCompSequences);


		JMenuItem mntmRevCompAlignment = new JMenuItem("Reverse Complement Alignment");
		mntmRevCompAlignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				logger.info("action perf");
				aliViewWindow.reverseComplementAlignment();
			}

		});
		mnEdit.add(mntmRevCompAlignment);
		editFunctions.add(mntmRevCompAlignment);
		loadedAlignmentFunctions.add(mntmRevCompAlignment);

		JMenuItem mntmCompAlignment = new JMenuItem("Complement Alignment");
		mntmCompAlignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				logger.info("action perf");
				aliViewWindow.complementAlignment();
			}

		});
		mnEdit.add(mntmCompAlignment);
		editFunctions.add(mntmCompAlignment);
		loadedAlignmentFunctions.add(mntmCompAlignment);

		JMenuItem mntmRevComp = new JMenuItem("Reverse Complement Clipboard");
		mntmRevComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				logger.info("action perf");
				aliViewWindow.reverseComplementClipboard();
			}
		});
		mnEdit.add(mntmRevComp);
		alwaysAvailableFunctions.add(mntmRevComp);
		
		mnEdit.add(new JSeparator());
		
		JMenuItem mntmEditCharsets = new JMenuItem("Edit charsets");
		mntmEditCharsets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aliViewWindow.editCharsets();
			}
		});
		mnEdit.add(mntmEditCharsets);
		loadedAlignmentFunctions.add(mntmEditCharsets);
		

		mnEdit.add(new JSeparator());

		// Mac has its own menu item
		if(OSNativeUtils.isAnythingButMac()){
			JMenuItem mntmPreferences = new JMenuItem("Preferences");
			mntmPreferences.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					aliViewWindow.openPreferencesGeneral();
				}
			});
			mnEdit.add(mntmPreferences);
		}
		JMenu mnSelection = new JMenu("Selection");
		mnSelection.setMnemonic(KeyEvent.VK_S);
		this.add(mnSelection);


		JMenuItem mntmSelectAll = new JMenuItem("Select all");
		mntmSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.selectAll();
			}
		});
		mntmSelectAll.setAccelerator(OSNativeUtils.getSelectAllKeyAccelerator());
		mnSelection.add(mntmSelectAll);
		loadedAlignmentFunctions.add(mntmSelectAll);

		JMenuItem mntmClearSelection = new JMenuItem("De-select");
		mntmClearSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.clearSelection();
			}
		});
		mnSelection.add(mntmClearSelection);
		hasSelectionFunctions.add(mntmClearSelection);

//		JMenuItem mntmInvertSelection = new JMenuItem("Invert selection");
//		mntmInvertSelection.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				aliViewWindow.invertSelection();
//			}
//		});
//		//mntmInvertSelection.setAccelerator(OSNativeUtils.getSelectAllKeyAccelerator());
//		mntmInvertSelection.setMnemonic(KeyEvent.VK_I);
//		mnSelection.add(mntmInvertSelection);
//		hasSelectionFunctions.add(mntmInvertSelection);
		
		JMenuItem mntmExpandSelectionRight = new JMenuItem("Expand selection Right");
		mntmExpandSelectionRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.expandSelectionRight();
			}
		});
		mntmExpandSelectionRight.setAccelerator(OSNativeUtils.getSelectionExpandRightKeyAccelerator());
		mnSelection.add(mntmExpandSelectionRight);
		hasSelectionFunctions.add(mntmExpandSelectionRight);
		
		JMenuItem mntmExpandSelectionLeft = new JMenuItem("Expand selection Left");
		mntmExpandSelectionLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.expandSelectionLeft();
			}
		});
		mntmExpandSelectionLeft.setAccelerator(OSNativeUtils.getSelectionExpandLeftKeyAccelerator());
		mnSelection.add(mntmExpandSelectionLeft);
		hasSelectionFunctions.add(mntmExpandSelectionLeft);
		
		
		mnSelection.add(new JSeparator());


		// This way of binding key action to menu buttom makes repeat work much faster
		// http://stackoverflow.com/questions/9622260/java-swing-actionlistener-much-slower-than-keylistener
		AbstractAction meveSelUpAction = new AbstractAction("Move selected sequences up"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getMoveSelectionUpKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.moveSelectedUp();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getMoveSelectionUpKeyAccelerator(), "meveSelUpAction");
		aliViewWin.getRootPane().getActionMap().put("meveSelUpAction", meveSelUpAction);   
		JMenuItem mntmMoveSelectionUp = new JMenuItem(meveSelUpAction);
		mnSelection.add(mntmMoveSelectionUp);
		mntmMoveSelectionUp.setIcon(AppIcons.getGoUpIcon());
		hasSelectionFunctions.add(mntmMoveSelectionUp);
		reorderAndDeleteFunctions.add(mntmMoveSelectionUp);

		// This way of binding key action to menu buttom makes repeat work much faster
		// http://stackoverflow.com/questions/9622260/java-swing-actionlistener-much-slower-than-keylistener
		AbstractAction moveSelDownAction = new AbstractAction("Move selected sequences down"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getMoveSelectionDownKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.moveSelectedDown();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getMoveSelectionDownKeyAccelerator(), "moveSelDownAction");
		aliViewWin.getRootPane().getActionMap().put("moveSelDownAction", moveSelDownAction);   
		JMenuItem mntmMoveSelectionDown = new JMenuItem(moveSelDownAction);
		mntmMoveSelectionDown.setIcon(AppIcons.getGoDownIcon());
		mnSelection.add(mntmMoveSelectionDown);
		hasSelectionFunctions.add(mntmMoveSelectionDown);
		reorderAndDeleteFunctions.add(mntmMoveSelectionDown);


		JMenuItem mntmMoveSelectionToTop = new JMenuItem("Move selected sequences to top");
		mntmMoveSelectionToTop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.moveSelectedToTop();
			}
		});
		mntmMoveSelectionToTop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_MASK));
		mntmMoveSelectionToTop.setIcon(AppIcons.getGoTopIcon());
		mnSelection.add(mntmMoveSelectionToTop);
		hasSelectionFunctions.add(mntmMoveSelectionToTop);
		reorderAndDeleteFunctions.add(mntmMoveSelectionToTop);

		JMenuItem mntmMoveSelectionToBottom = new JMenuItem("Move selected sequences to bottom");
		mntmMoveSelectionToBottom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.moveSelectedToBottom();
			}
		});
		mntmMoveSelectionToBottom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_MASK));
		mntmMoveSelectionToBottom.setIcon(AppIcons.getGoBottomIcon());
		mnSelection.add(mntmMoveSelectionToBottom);
		hasSelectionFunctions.add(mntmMoveSelectionToBottom);
		reorderAndDeleteFunctions.add(mntmMoveSelectionToBottom);

		mnSelection.add(new JSeparator());

		JMenuItem mntmAddExcludes = new JMenuItem("Add or Remove selection to Excludes/Exset");
		mntmAddExcludes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.addOrRemoveSelectionToExcludes();
			}
		});
		mntmAddExcludes.setAccelerator(OSNativeUtils.getAddOrRemoveExcludesKeyAccelerator());
		mnSelection.add(mntmAddExcludes);
		hasSelectionFunctions.add(mntmAddExcludes);
		/*
		JMenuItem mntmRemoveExcludes = new JMenuItem("Remove selection from Excludes/Exset");
		mntmRemoveExcludes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.removeSelectionFromExcludes();
			}
		});
		mnSelection.add(mntmRemoveExcludes);
		hasSelectionFunctions.add(mntmRemoveExcludes);
		 */
		mnSelection.add(new JSeparator());

		mnSelectCharset = new JMenu("Select Charset");
		//		mntmSelectCharset.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				aliViewWindow.setSelectionAsCoding(0);
		//			}
		//		});
		mnSelection.add(mnSelectCharset);
		mnSelectCharset.setEnabled(false);


		mnSelection.add(new JSeparator());

		JMenuItem mntmSetSelectionAsCoding0 = new JMenuItem("Set selection as coding (selection starting with codon position=1)");
		mntmSetSelectionAsCoding0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.setSelectionAsCoding(1);
			}
		});
		mnSelection.add(mntmSetSelectionAsCoding0);
		coding0ButtonModel=mntmSetSelectionAsCoding0.getModel();
		hasSelectionFunctions.add(mntmSetSelectionAsCoding0);
		nucleotideFunctions.add(mntmSetSelectionAsCoding0);

		JMenuItem mntmSetSelectionAsCoding1 = new JMenuItem("Set selection as coding (selection starting with codon position=2)");
		mntmSetSelectionAsCoding1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.setSelectionAsCoding(2);
			}
		});
		coding1ButtonModel=mntmSetSelectionAsCoding1.getModel();
		mnSelection.add(mntmSetSelectionAsCoding1);
		hasSelectionFunctions.add(mntmSetSelectionAsCoding1);
		nucleotideFunctions.add(mntmSetSelectionAsCoding1);

		JMenuItem mntmSetSelectionAsCoding2 = new JMenuItem("Set selection as coding (selection starting with codon position=3)");
		mntmSetSelectionAsCoding2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.setSelectionAsCoding(3);
			}
		});
		coding2ButtonModel=mntmSetSelectionAsCoding2.getModel();
		mnSelection.add(mntmSetSelectionAsCoding2);
		hasSelectionFunctions.add(mntmSetSelectionAsCoding2);
		nucleotideFunctions.add(mntmSetSelectionAsCoding2);

		JMenuItem mntmSetSelectionAsNonCoding = new JMenuItem("Set selection as Non-coding");
		mntmSetSelectionAsNonCoding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.setSelectionAsNonCoding();
			}
		});
		nonCodingButtonModel = mntmSetSelectionAsNonCoding.getModel();
		mnSelection.add(mntmSetSelectionAsNonCoding);
		hasSelectionFunctions.add(mntmSetSelectionAsNonCoding);
		nucleotideFunctions.add(mntmSetSelectionAsNonCoding);



		JMenu mnViewMenu = new JMenu("View");
		mnViewMenu.setMnemonic(KeyEvent.VK_V);
		this.add(mnViewMenu);

		// This way of binding key action to menu buttom makes repeat work much faster
		AbstractAction decFontSizeAction = new AbstractAction("Decrease Font Size"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getDecreaseFontSizeKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.zoomOut();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getDecreaseFontSizeKeyAccelerator(), "decfontsize");
		aliViewWin.getRootPane().getActionMap().put("decfontsize", decFontSizeAction);   
		JMenuItem mntmDecreaseFontSize = new JMenuItem(decFontSizeAction);
		mntmDecreaseFontSize.setAction(decFontSizeAction); 
		decFontSizeButtonModel = mntmDecreaseFontSize.getModel();
		mnViewMenu.add(decFontSizeAction);
		loadedAlignmentFunctions.add(mntmDecreaseFontSize);


		// This way of binding key action to menu buttom makes repeat work much faster
		AbstractAction incFontSizeAction = new AbstractAction("Increase Font Size"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getIncreaseFontSizeKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.zoomIn();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getIncreaseFontSizeKeyAccelerator(), "incfontsize");
		aliViewWin.getRootPane().getActionMap().put("incfontsize", incFontSizeAction);   
		JMenuItem mntmIncreaseFontSize = new JMenuItem(incFontSizeAction);
		mntmIncreaseFontSize.setAction(incFontSizeAction); 
		incFontSizeButtonModel = mntmIncreaseFontSize.getModel();
		mnViewMenu.add(incFontSizeAction);
		loadedAlignmentFunctions.add(mntmIncreaseFontSize);


		mnViewMenu.add(new JSeparator());

		JCheckBoxMenuItem highlightCons = new JCheckBoxMenuItem("Highlight consensus characters");
		highlightCons.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				JCheckBoxMenuItem  btn = (JCheckBoxMenuItem ) e.getSource();
				aliViewWindow.setHighlightConsensus(btn.isSelected());
			}
		});
		ButtonGroup buttonGroupOneViewAtATime = new NoneSelectedButtonGroup();
		buttonGroupOneViewAtATime.add(highlightCons);
		highlightConsButtonModel = highlightCons.getModel();
		mnViewMenu.add(highlightCons);
		loadedAlignmentFunctions.add(highlightCons);

		JCheckBoxMenuItem highlightNonCons = new JCheckBoxMenuItem("Highlight Non-consensus characters");
		highlightNonCons.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				JCheckBoxMenuItem  btn = (JCheckBoxMenuItem ) e.getSource();
				aliViewWindow.setHighlightNonConsensus(btn.isSelected());
			}
		});
		buttonGroupOneViewAtATime.add(highlightNonCons);
		highlightNonConsButtonModel = highlightNonCons.getModel();
		mnViewMenu.add(highlightNonCons);
		loadedAlignmentFunctions.add(highlightNonCons);

		JCheckBoxMenuItem highlightDiff = new JCheckBoxMenuItem("Highlight diff from a selected sequence");
		highlightDiff.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				JCheckBoxMenuItem  btn = (JCheckBoxMenuItem ) e.getSource();
				aliViewWindow.setHighlightDiff(btn.isSelected());
			}
		});
		buttonGroupOneViewAtATime.add(highlightDiff);
		highlightDiffButtonModel = highlightDiff.getModel();
		mnViewMenu.add(highlightDiff);
		loadedAlignmentFunctions.add(highlightDiff);

		mnViewMenu.add(new JSeparator());	

		JMenuItem mntmSortSequencesByName = new JMenuItem("Sort sequences by name");
		mntmSortSequencesByName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.sortSequencesByName();
			}
		});
		mnViewMenu.add(mntmSortSequencesByName);
		reorderAndDeleteFunctions.add(mntmSortSequencesByName);
		loadedAlignmentFunctions.add(mntmSortSequencesByName);

		JMenuItem mntmSortSequencesByCharColumn = new JMenuItem("Sort sequences by character in selected column");
		mntmSortSequencesByCharColumn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.sortSequencesByCharColumn();
			}
		});
		mnViewMenu.add(mntmSortSequencesByCharColumn);
		reorderAndDeleteFunctions.add(mntmSortSequencesByCharColumn);
		loadedAlignmentFunctions.add(mntmSortSequencesByCharColumn);


		mnViewMenu.add(new JSeparator());

		JCheckBoxMenuItem mntmToggleTranslation = new JCheckBoxMenuItem("Show as translation");
		mntmToggleTranslation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				JCheckBoxMenuItem btn = (JCheckBoxMenuItem ) e.getSource();
				aliViewWindow.setShowTranslation(btn.isSelected());
			}
		});
		mntmToggleTranslation.setAccelerator(OSNativeUtils.getToggleTranslationKeyAccelerator());
		buttonGroupOneViewAtATime.add(highlightNonCons);
		toggleTranslationButtonModel = mntmToggleTranslation.getModel();
		mnViewMenu.add(mntmToggleTranslation);
		nucleotideFunctions.add(mntmToggleTranslation);
		loadedAlignmentFunctions.add(mntmToggleTranslation);

		JCheckBoxMenuItem mntmToggleTranslationOnePos = new JCheckBoxMenuItem("Show as translation (1 pos per AminoAcid)");
		mntmToggleTranslationOnePos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.mntmToggleTranslationOnePos();
			}
		});
		mntmToggleTranslationOnePos.setAccelerator(OSNativeUtils.getToggleTranslationOnePosKeyAccelerator());
		transOnePosButtonModel = mntmToggleTranslationOnePos.getModel();
		mnViewMenu.add(mntmToggleTranslationOnePos);
		nucleotideFunctions.add(mntmToggleTranslationOnePos);
		loadedAlignmentFunctions.add(mntmToggleTranslationOnePos);


		JCheckBoxMenuItem mntmToggleAminoAcidCode = new JCheckBoxMenuItem("Show as Amino acid code (when show translate)");
		mntmToggleAminoAcidCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.toggleDrawAminoAcidCode();
			}
		});
		mntmToggleAminoAcidCode.setAccelerator(OSNativeUtils.getToggleTranslateShowAACodeKeyAccelerator());
		showAACodeButtonMoes = mntmToggleAminoAcidCode.getModel();
		mnViewMenu.add(mntmToggleAminoAcidCode);
		nucleotideFunctions.add(mntmToggleAminoAcidCode);
		loadedAlignmentFunctions.add(mntmToggleAminoAcidCode);
		
		JCheckBoxMenuItem mntmToggleTranslateShowBoth = new JCheckBoxMenuItem("Show both aminoacid and nucleotides when translating");
		mntmToggleTranslateShowBoth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.toggleTranslationShowBoth();
			}
		});
		mntmToggleTranslateShowBoth.setAccelerator(OSNativeUtils.getToggleTranslateShowBothKeyAccelerator());
		mnViewMenu.add(mntmToggleTranslateShowBoth);
		nucleotideFunctions.add(mntmToggleTranslateShowBoth);
		loadedAlignmentFunctions.add(mntmToggleTranslateShowBoth);

		JCheckBoxMenuItem mntmToggleIgnoreGapInTranslation = new JCheckBoxMenuItem("Ignore gaps when translating");
		mntmToggleIgnoreGapInTranslation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.toggleIgnoreGapInTranslation();
			}
		});
		mntmToggleIgnoreGapInTranslation.setAccelerator(OSNativeUtils.getToggleIgnoreGapInTranslationKeyAccelerator());
		mnViewMenu.add(mntmToggleIgnoreGapInTranslation);
		nucleotideFunctions.add(mntmToggleIgnoreGapInTranslation);
		loadedAlignmentFunctions.add(mntmToggleIgnoreGapInTranslation);

		JMenuItem mntmToggleDrawCodonPos = new JMenuItem("Show codonpositions on ruler");
		mntmToggleDrawCodonPos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.toggleDrawCodonpos();
			}
		});
		mnViewMenu.add(mntmToggleDrawCodonPos);
		drawCoonPosOnRulerButtonModel = mntmToggleDrawCodonPos.getModel();
		nucleotideFunctions.add(mntmToggleDrawCodonPos);
		loadedAlignmentFunctions.add(mntmToggleDrawCodonPos);

		// Create Menu and submenu for gen-code-alternatives
		mnGeneticCodeCode = new JMenu("Select genetic code for translation");
		for(final GeneticCode aCode: GeneticCode.allCodesArray){
			JMenuItem genCodeSubM = new JMenuItem("" + aCode.transTable + ". " + aCode.name);
			genCodeSubM.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					aliViewWindow.setGeneticCode(aCode);
				}
			});
			mnGeneticCodeCode.add(genCodeSubM);
		}
		mnViewMenu.add(mnGeneticCodeCode);
		alwaysAvailableFunctions.add(mnGeneticCodeCode);

		JMenuItem mntmIncreaseReadingFrame = new JMenuItem("Reading frame +1");
		mntmIncreaseReadingFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				aliViewWindow.incReadingFrame();
			}
		});
		mntmIncreaseReadingFrame.setAccelerator(OSNativeUtils.incReadingFrameKeyAccelerator());
		mnViewMenu.add(mntmIncreaseReadingFrame);
		nucleotideFunctions.add(mntmIncreaseReadingFrame);
		loadedAlignmentFunctions.add(mntmIncreaseReadingFrame);

		JMenuItem mntmDecreaseReadingFrame = new JMenuItem("Reading frame -1");
		mntmDecreaseReadingFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.decReadingFrame();
			}
		});
		mntmDecreaseReadingFrame.setAccelerator(OSNativeUtils.decReadingFrameKeyAccelerator());
		mnViewMenu.add(mntmDecreaseReadingFrame);
		nucleotideFunctions.add(mntmDecreaseReadingFrame);
		loadedAlignmentFunctions.add(mntmDecreaseReadingFrame);


		JMenuItem mntmCountStopCodons = new JMenuItem("Count stop codons");
		mntmCountStopCodons.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.countStopCodons();
			}
		});
		countCodonButtonModel=mntmCountStopCodons.getModel();
		mntmCountStopCodons.setAccelerator(OSNativeUtils.countStopCodonsKeyAccelerator());
		mnViewMenu.add(mntmCountStopCodons);
		loadedAlignmentFunctions.add(mntmCountStopCodons);

		mnViewMenu.add(new JSeparator());
		
		JCheckBoxMenuItem mntmShowCharsetsRuler = new JCheckBoxMenuItem("Show charsets ruler (if charsets are defined)");
		boolean isShowCharsetRuler = Settings.getShowCharsetRuler().getBooleanValue();
		if(isShowCharsetRuler){
			mntmShowCharsetsRuler.setSelected(true);
		}
		mntmShowCharsetsRuler.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				JCheckBoxMenuItem btn = (JCheckBoxMenuItem ) e.getSource();
				aliViewWindow.showCharsetsRuler(btn.isSelected());	
			}
		});
		//mntmFontCase.setAccelerator(OSNativeUtils.getToggleTranslationKeyAccelerator());
		//buttonGroupOneViewAtATime.add(highlightNonCons);
		//toggleTranslationButtonModel = mntmToggleTranslation.getModel();
		mnViewMenu.add(mntmShowCharsetsRuler);
		alwaysAvailableFunctions.add(mntmShowCharsetsRuler);
		
		mnViewMenu.add(new JSeparator());
		
		
		JCheckBoxMenuItem mntmFontCase = new JCheckBoxMenuItem("Always display as Upper case characters");
		mntmFontCase.setMnemonic(KeyEvent.VK_U);
		int fontCase = Settings.getFontCase().getIntValue();
		if(fontCase == CharPixels.CASE_UPPER){
			mntmFontCase.setSelected(true);
		}
		mntmFontCase.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				JCheckBoxMenuItem btn = (JCheckBoxMenuItem ) e.getSource();
				aliViewWindow.setFontCaseUpper(btn.isSelected());	
			}
		});
		//mntmFontCase.setAccelerator(OSNativeUtils.getToggleTranslationKeyAccelerator());
		//buttonGroupOneViewAtATime.add(highlightNonCons);
		//toggleTranslationButtonModel = mntmToggleTranslation.getModel();
		mnViewMenu.add(mntmFontCase);
		alwaysAvailableFunctions.add(mntmFontCase);
		
		
		// create ColorMenu and submenu
		JMenu mnColorScheme = new JMenu("Colors");
		JMenu mnNucleotideSub = new JMenu("Nucleotide");
		mnColorScheme.add(mnNucleotideSub);
		ButtonGroup colorSchemeGroup = new ButtonGroup();
		ColorScheme settingsColorScheme = Settings.getColorSchemeNucleotide();
		for(final ColorScheme aScheme: ColorSchemeFactory.getNucleotideColorSchemes()){
			JCheckBoxMenuItem colorSubmenu = new JCheckBoxMenuItem(aScheme.getName());
			colorSubmenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					aliViewWindow.setColorSchemeNucleotide(aScheme);
				}
			});
			colorSchemeGroup.add(colorSubmenu);
			// if this is the one from settings then select button
			if(settingsColorScheme == aScheme){
				colorSubmenu.setSelected(true);
			}
			mnNucleotideSub.add(colorSubmenu);
		}
		JMenu mnAASub = new JMenu("Amino Acid");
		mnColorScheme.add(mnAASub);
		ButtonGroup colorSchemeGroupAA = new ButtonGroup();
		ColorScheme settingsColorSchemeAA = Settings.getColorSchemeAminoAcid();
		for(final ColorScheme aScheme: ColorSchemeFactory.getAAColorSchemes()){
			JCheckBoxMenuItem colorSubmenu = new JCheckBoxMenuItem(aScheme.getName());
			colorSubmenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					aliViewWindow.setColorSchemeAminoAcid(aScheme);
				}
			});
			colorSchemeGroupAA.add(colorSubmenu);
			// if this is the one from settings then select button
			if(settingsColorSchemeAA == aScheme){
				colorSubmenu.setSelected(true);
			}
			mnAASub.add(colorSubmenu);
		}
		mnColorScheme.setIcon(AppIcons.getColorsIcon());
		mnViewMenu.add(mnColorScheme);
		alwaysAvailableFunctions.add(mnColorScheme);


		JMenu mnAlign = new JMenu("Align");
		mnAlign.setMnemonic(KeyEvent.VK_A);
		this.add(mnAlign);



		//
		//	    Automatic alignment functions
		//
		JMenuItem mntmAlignInCurrent = new JMenuItem("Add and align sequences from clipboard (fasta)");
		mntmAlignInCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				aliViewWindow.addAndAlignSeqFromClipboard();
			}
		});
		mnAlign.add(mntmAlignInCurrent);
		editFunctions.add(mntmAlignInCurrent);
		loadedAlignmentFunctions.add(mntmAlignInCurrent);

		/*
		 * 
		 * TODO This has to be looked over so it works with thread
		 * 
		 */
		//
		//		JMenuItem mntmAlignInMultiple = new JMenuItem("Add and align multiplesequences from clipboard one by one (fasta)");
		//		mntmAlignInMultiple.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//
		//				aliViewWindow.addAndAlignMultipleSeqFromClipOneByOne();
		//
		//			}
		//		});
		//		mnAlign.add(mntmAlignInMultiple);
		//		editFunctions.add(mntmAlignInMultiple);

		JMenuItem mntmAlignInFromFile = new JMenuItem("Add and align sequences from file (fasta)");
		mntmAlignInFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.addAndAlignSeqFromFile();
			}
		});
		mnAlign.add(mntmAlignInFromFile);
		editFunctions.add(mntmAlignInFromFile);
		loadedAlignmentFunctions.add(mntmAlignInFromFile);


		JMenuItem mntmReAlignSelection = new JMenuItem("Realign selected block");
		mntmReAlignSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				aliViewWindow.reAlignSelectionInSeparateThread();
			}
		});
		mnAlign.add(mntmReAlignSelection);
		realignSelectedBlockButtonModel = mntmReAlignSelection.getModel();
		editFunctions.add(mntmReAlignSelection);
		hasSelectionFunctions.add(mntmReAlignSelection);

		JMenuItem mntmReAlignSelectedSequence = new JMenuItem("Realign selected sequence(s)");
		mntmReAlignSelectedSequence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.reAlignSelectedSequences();
			}
		});
		mnAlign.add(mntmReAlignSelectedSequence);
		realignSelectedSequencesButtonModel = mntmReAlignSelectedSequence.getModel();
		editFunctions.add(mntmReAlignSelectedSequence);
		loadedAlignmentFunctions.add(mntmReAlignSelectedSequence);

		JMenuItem mntmAlign = new JMenuItem("Realign everything");
		mntmAlign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.reAlignEverythingWithDefaultProgram();
			}
		});
		//mntmAlign.setIcon(AppIcons.getAlignIcon());
		mnAlign.add(mntmAlign);
		editFunctions.add(mntmAlign);
		editFunctions.add(mntmAlign);
		loadedAlignmentFunctions.add(mntmAlign);

		JMenuItem mntmAlignAsAA = new JMenuItem("Realign everything as Translated Amino Acids");
		mntmAlignAsAA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.reAlignEverythingAsTranslatedAA();
			}
		});
		//mntmAlign.setIcon(AppIcons.getAlignIcon());
		mnAlign.add(mntmAlignAsAA);
		editFunctions.add(mntmAlignAsAA);
		loadedAlignmentFunctions.add(mntmAlignAsAA);
		nucleotideFunctions.add(mntmAlignAsAA);

		mnAlign.add(new JSeparator());

		JMenu mnuAlignerSettings = new JMenu("Change default Aligner program");
		mnAlign.add(mnuAlignerSettings);
		alwaysAvailableFunctions.add(mnuAlignerSettings);

		// Create submenu 1
		JMenuItem mntmALLAlignerSettings = new JMenuItem("for realigning all (or selected blocks)");
		mntmALLAlignerSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.editAlignerALLSettings();
			}
		});
		mnuAlignerSettings.add(mntmALLAlignerSettings);
		// Create submenu 2
		JMenuItem mntmADDAlignerSettings = new JMenuItem("when pasting new sequences (profile-alignment)");
		mntmADDAlignerSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.editAlignerADDSettings();
			}
		});
		mnuAlignerSettings.add(mntmADDAlignerSettings);



		//
		//	    Manual alignment functions
		//

		mnAlign.add(new JSeparator());


		// This way of binding key action to menu buttom makes repeat work much faster
		// http://stackoverflow.com/questions/9622260/java-swing-actionlistener-much-slower-than-keylistener
		AbstractAction moveselrightAction = new AbstractAction("Move selected positions right"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getMoveSelectedRightKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.moveSelectionRight();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getMoveSelectedRightKeyAccelerator(), "moveselright");
		aliViewWin.getRootPane().getActionMap().put("moveselright", moveselrightAction);  
		JMenuItem mntmMoveSelectionRight = new JMenuItem(moveselrightAction);
		mnAlign.add(mntmMoveSelectionRight);
		mntmMoveSelectionRight.setIcon(AppIcons.getMoveRightIcon());
		hasSelectionFunctions.add(mntmMoveSelectionRight);
		editFunctions.add(mntmMoveSelectionRight);


		// This way of binding key action to menu buttom makes repeat work much faster
		// http://stackoverflow.com/questions/9622260/java-swing-actionlistener-much-slower-than-keylistener
		AbstractAction moveselleftAction = new AbstractAction("Move selected positions left"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getMoveSelectedLeftKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.moveSelectionLeft();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getMoveSelectedLeftKeyAccelerator(), "moveselleft");
		aliViewWin.getRootPane().getActionMap().put("moveselleft", moveselleftAction);   
		JMenuItem mntmMoveSelectionLeft = new JMenuItem(moveselleftAction);
		mntmMoveSelectionLeft.setAction(moveselleftAction); 
		mnAlign.add(mntmMoveSelectionLeft);
		mntmMoveSelectionLeft.setIcon(AppIcons.getMoveLeftIcon());
		editFunctions.add(mntmMoveSelectionLeft);
		hasSelectionFunctions.add(mntmMoveSelectionLeft);



		// This way of binding key action to menu buttom makes repeat work much faster
		// http://stackoverflow.com/questions/9622260/java-swing-actionlistener-much-slower-than-keylistener
		AbstractAction insertGapMRAction = new AbstractAction("Insert Gap move right"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getInsertGapMoveRightKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.insertGapMoveRight();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getInsertGapMoveRightKeyAccelerator(), "insertGapMRAction");
		aliViewWin.getRootPane().getActionMap().put("insertGapMRAction", insertGapMRAction);   
		JMenuItem mntmInsertGapMoveRight = new JMenuItem(insertGapMRAction);
		mnAlign.add(mntmInsertGapMoveRight);
		editFunctions.add(mntmInsertGapMoveRight);
		hasSelectionFunctions.add(mntmInsertGapMoveRight);

		// This way of binding key action to menu buttom makes repeat work much faster
		// http://stackoverflow.com/questions/9622260/java-swing-actionlistener-much-slower-than-keylistener
		AbstractAction insertGapMoveLeftAction = new AbstractAction("Insert Gap move left"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getInsertGapMoveLeftKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.insertGapMoveLeft();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getInsertGapMoveLeftKeyAccelerator(), "insertGapMoveLeftAction");
		aliViewWin.getRootPane().getActionMap().put("insertGapMoveLeftAction", insertGapMoveLeftAction); 
		JMenuItem mntmInsertGapMoveLeft = new JMenuItem(insertGapMoveLeftAction);
		mnAlign.add(mntmInsertGapMoveLeft);
		editFunctions.add(mntmInsertGapMoveLeft);
		hasSelectionFunctions.add(mntmInsertGapMoveLeft);

		// This way of binding key action to menu buttom makes repeat work much faster
		// http://stackoverflow.com/questions/9622260/java-swing-actionlistener-much-slower-than-keylistener
		AbstractAction deleteGapMoveLeftAction = new AbstractAction("Delete Gap at left"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getDeleteGapMoveLeftKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.deleteGapMoveLeft();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getDeleteGapMoveLeftKeyAccelerator(), "deleteGapMoveLeftAction");
		aliViewWin.getRootPane().getActionMap().put("deleteGapMoveLeftAction", deleteGapMoveLeftAction); 
		JMenuItem mntmDeleteGapMoveLeft = new JMenuItem(deleteGapMoveLeftAction);
		mnAlign.add(mntmDeleteGapMoveLeft);
		editFunctions.add(mntmDeleteGapMoveLeft);
		hasSelectionFunctions.add(mntmDeleteGapMoveLeft);


		// This way of binding key action to menu buttom makes repeat work much faster
		// http://stackoverflow.com/questions/9622260/java-swing-actionlistener-much-slower-than-keylistener
		AbstractAction deleteGapMoveRightAction = new AbstractAction("Delete Gap at right"){
			{
				putValue(ACCELERATOR_KEY, OSNativeUtils.getDeleteGapMoveRightKeyAccelerator());
			}
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.deleteGapMoveRight();
			}
		};
		aliViewWin.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(OSNativeUtils.getDeleteGapMoveRightKeyAccelerator(), "deleteGapMoveRightAction");
		aliViewWin.getRootPane().getActionMap().put("deleteGapMoveRightAction", deleteGapMoveRightAction); 
		JMenuItem mntmDeleteGapMoveRight = new JMenuItem(deleteGapMoveRightAction);
		mnAlign.add(mntmDeleteGapMoveRight);
		editFunctions.add(mntmDeleteGapMoveRight);
		hasSelectionFunctions.add(mntmDeleteGapMoveRight);


		/*

	JMenuItem mntmReAlignSelectionMafft = new JMenuItem("Realign selection with mafft");
	mntmReAlignSelectionMafft.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			aliView.pushUndoState();
			aliView.reAlignSelectionWithMafft();
		}
	});
	mnAlign.add(mntmReAlignSelectionMafft);

		 */

		JMenu mnPrimer = new JMenu("Primer");
		mnPrimer.setMnemonic(KeyEvent.VK_P);
		this.add(mnPrimer);

		JMenuItem mntmPrimerInCurrent = new JMenuItem("Find primer in current selection");
		mntmPrimerInCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.findPrimerInCurrentSelection();
			}
		});
		mnPrimer.add(mntmPrimerInCurrent);
		hasSelectionFunctions.add(mntmPrimerInCurrent);
		nucleotideFunctions.add(mntmPrimerInCurrent);

		JMenuItem mntmFindPrimerSettings = new JMenuItem("Find primer settings");
		mntmFindPrimerSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.findPrimerSettings();
			}
		});
		mnPrimer.add(mntmFindPrimerSettings);
		alwaysAvailableFunctions.add(mntmFindPrimerSettings);


		// Menu with External Commands
		mnExternal = new JMenu("External commands");
		mnExternal.setMnemonic(KeyEvent.VK_E);
		this.add(mnExternal);
		rebuildExternalCommandsSubmenu();

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		this.add(mnHelp);

		JMenuItem mntmHelpOpen = new JMenuItem("Help");
		mntmHelpOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.openHelp();
			}
		});
		mnHelp.add(mntmHelpOpen);
		alwaysAvailableFunctions.add(mntmHelpOpen);

		//mnHelp.add(new JSeparator());

		JMenuItem mntmCheckNew = new JMenuItem("Check for new version/version history");
		mntmCheckNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.checkNewVersion();
			}
		});
		mnHelp.add(mntmCheckNew);
		alwaysAvailableFunctions.add(mntmCheckNew);
		/*
		JMenuItem mntmVersionHistory = new JMenuItem("Check version history");
		mntmVersionHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.displayVersionHistory();
			}
		});
		mnHelp.add(mntmVersionHistory);
		alwaysAvailableFunctions.add(mntmVersionHistory);
		 */
		JMenuItem mntmReportBug = new JMenuItem("Report bug/feature request");
		mntmReportBug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.openBugReportPage();
			}
		});
		mnHelp.add(mntmReportBug);
		alwaysAvailableFunctions.add(mntmReportBug);

		mnHelp.add(new JSeparator());

		// Mac has its own menu item
		if(OSNativeUtils.isAnythingButMac()){
			JMenuItem mntmAbout = new JMenuItem("About");
			mntmAbout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {		
					aliViewWindow.showAbout();
				}
			});
			mnHelp.add(mntmAbout);
			alwaysAvailableFunctions.add(mntmAbout);
		}
	}

	private void rebuildExternalCommandsSubmenu(){
		mnExternal.removeAll();

		// get saved commands (or default will be served by settings if none)
		ArrayList<CommandItem> cmdItems = Settings.getExternalCommands();	
		for(final CommandItem anItem: cmdItems){; 
		if(anItem.isActivated() && anItem.getName() != null && anItem.getName().length() > 0){
			JMenuItem nextCommandItem = new JMenuItem(anItem.getName());
			nextCommandItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					aliViewWindow.runExternalCommand(anItem);
				}
			});
			nextCommandItem.setSelected(anItem.isActivated());
			alwaysAvailableFunctions.add(nextCommandItem);
			mnExternal.add(nextCommandItem);
		}
		}
		mnExternal.add(new JSeparator());

		JMenuItem mntmEditExternal = new JMenuItem("Edit external command");
		mntmEditExternal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aliViewWindow.editExternalCommands();
			}
		});

		mnExternal.add(mntmEditExternal);
		alwaysAvailableFunctions.add(mntmEditExternal);
	}

	private void rebuildRecentFilesSubmenu(){
		logger.info("rebuildRecentFilesSubmenu()");
		mnRecentFiles.removeAll();
		// submenu
		for(final File nextFile: Settings.getRecentFiles()){
			JMenuItem nextItem = new JMenuItem(nextFile.getAbsolutePath());
			nextItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AliView.openAlignmentFile(nextFile);
				}

			});
			mnRecentFiles.add(nextItem);
		}
		mnRecentFiles.invalidate();
		mnRecentFiles.repaint();
	}


	public void rebuildSelectCharsetsSubmenu(){
		logger.info("rebuildSelectCharsetsSubmenu()" + aliViewWindow.getAlignment().getAlignmentMeta().getCharsets());
		mnSelectCharset.removeAll();

		CharSets charsets = aliViewWindow.getAlignment().getAlignmentMeta().getCharsets();
		// submenu
		for(final CharSet aCharset: charsets){
			JMenuItem nextItem = new JMenuItem(aCharset.getName());
			nextItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					aliViewWindow.selectAll(aCharset);
				}
			});
			mnSelectCharset.add(nextItem);
		}
		if(charsets != null && charsets.size() > 0){
			mnSelectCharset.setEnabled(true);
		}else{
			mnSelectCharset.setEnabled(false);
		}

		mnSelectCharset.invalidate();
		mnSelectCharset.repaint();
	}


	public final void setEditFunctionsEnabled(boolean enable){
		setAllEnabled(editFunctions, enable);
	}	

	public final void setAlwaysFunctionsEnabled(boolean enable){
		setAllEnabled(alwaysAvailableFunctions, enable);
	}

	public final void setLoadedFunctionsEnabled(boolean enable){
		setAllEnabled(loadedAlignmentFunctions, enable);
	}

	public final void setNucleotideFunctionsEnabled(boolean enable){
		setAllEnabled(nucleotideFunctions, enable);
	}

	public final void setAAFunctionsEnabled(boolean enable){
		setAllEnabled(aminoAcidFunctions, enable);
	}

	public final void setHasSelectionFunctionsEnabled(boolean enable){
		setAllEnabled(hasSelectionFunctions, enable);
	}

	public final void setReorderFunctionsEnabled(boolean enable){
		setAllEnabled(reorderAndDeleteFunctions, enable);
	}

	public void setUndoButtonEnabled(boolean b) {
		this.undoButton.setEnabled(b);	
	}
	public void setRedoButtonEnabled(boolean b) {
		this.redoButton.setEnabled(b);	
	}

	private void setAllEnabled(ArrayList<AbstractButton> comps, boolean enable) {
		if(!menuLock){
			for(AbstractButton comp: comps){
				comp.setEnabled(enable);
				Component[] subComponents = comp.getComponents();
				if(subComponents != null && subComponents.length>0){
					setEnabledAllJMenuButtonsRecursive(subComponents, enable);
				}
			}
		}
	}

	public void setMenuLock(boolean lock){

		logger.info("lock" + lock);

		if(lock){
			// first make sure we can change buttons
			menuLock = false;
			disableAllButEssentialButtons();
			// now lock;
			menuLock = true;
		}else{
			menuLock = false;
			updateAllMenuEnabled();
		}
	}

	private void disableAllRegisterdMenusAndSubs(){
		boolean enable = false;
		setAllEnabled(editFunctions, enable);
		setAllEnabled(alwaysAvailableFunctions, enable);
		setAllEnabled(loadedAlignmentFunctions, enable);
		setAllEnabled(nucleotideFunctions, enable);
		setAllEnabled(aminoAcidFunctions, enable);
		setAllEnabled(hasSelectionFunctions, enable);
		setAllEnabled(undoFunctions, enable);
		setAllEnabled(reorderAndDeleteFunctions, enable);		
	}

	public void updateAllMenuEnabled() {

		this.disableAllButEssentialButtons();		

		if(! aliViewWindow.isEmpty()){
			logger.info("alignment.isAAAlignment()" + aliViewWindow.getAlignment().isAAAlignment());
			logger.info("alignment.isNucleotideAlignment()" + aliViewWindow.getAlignment().isNucleotideAlignment());

			this.setLoadedFunctionsEnabled(true);
			this.setReorderFunctionsEnabled(true);
			this.setUndoButtonEnabled(!aliViewWindow.isUndoStackEmpty());
			this.setRedoButtonEnabled(!aliViewWindow.isRedoStackEmpty());

			if(aliViewWindow.getAlignment().isEditable()){
				this.setEditFunctionsEnabled(true);
				logger.info("editFunctions" + true);
			}

			if(aliViewWindow.getAlignment().isNucleotideAlignment()){
				this.setNucleotideFunctionsEnabled(true);
				this.setHasSelectionFunctionsEnabled(aliViewWindow.getAlignment().hasSelection());
				this.setAAFunctionsEnabled(false);
			}
			if(aliViewWindow.getAlignment().isAAAlignment()){
				this.setAAFunctionsEnabled(true);
				this.setHasSelectionFunctionsEnabled(aliViewWindow.getAlignment().hasSelection());
				this.setNucleotideFunctionsEnabled(false);
			}

			if(!aliViewWindow.getAlignment().isEditable()){
				this.setEditFunctionsEnabled(false);
			}
		}	
	}

	public void disableAllButEssentialButtons() {
		disableAllRegisterdMenusAndSubs();
		setAlwaysFunctionsEnabled(true);
	}

	private void setEnabledAllJMenuButtonsRecursive(Component[] allComps, boolean isEnabled){
		for(Component comp: allComps){
			comp.setEnabled(isEnabled);
			if(comp instanceof AbstractButton){			
				Component[] subcomps = ((AbstractButton) comp).getComponents();
				if(subcomps != null && subcomps.length > 0){
					setEnabledAllJMenuButtonsRecursive(subcomps, isEnabled);
				}
			}
		}
	}

	public void createDynamicLoadFilePages(final FileSequenceAlignmentListModel seqList, List<FilePage> pages) {
		logger.info("beforemenu");
		/*
		if(mnFilePages == null){
			mnFilePages = new JMenu("Load more sequences from file");
			this.add(mnFilePages);
		}else{
			mnFilePages.removeAll();
		}
		ButtonGroup bg = new ButtonGroup();
		for(final FilePage page: pages){
			String startIndexPrefix = "";
			if(page.startIndex != 0){
				startIndexPrefix = "~";
			}
			JCheckBoxMenuItem nextItem = new JCheckBoxMenuItem("Load sequence "+ startIndexPrefix + page.startIndex + " to ~" + page.endIndex);
			nextItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					seqList.loadMoreSequencesFromFile(page);
				}
			});
			bg.add(nextItem);
			mnFilePages.add(nextItem);
			logger.info("seqList.getActivePage()" + seqList.getActivePage());
			if(seqList.getActivePage() == page){
				nextItem.setSelected(true);
			}
		}
		*/
	}

	public void removeDynamicLoadFilePagesMenu(){
		if(mnFilePages != null){
			this.remove(mnFilePages);
		}
	}
	public ButtonModel getToggleTranslationButtonModel() {
		return toggleTranslationButtonModel;
	}

	public ButtonModel getHighlightConsButtonModel() {
		return highlightConsButtonModel;
	}

	public ButtonModel getHighlightDiffTraceButtonModel() {
		return highlightDiffButtonModel;
	}

	public ButtonModel getHighlightNonConsButtonModel() {
		return highlightNonConsButtonModel;
	}

	public ButtonModel getShowAACodeButtonModel() {
		return showAACodeButtonMoes;
	}

	public ButtonModel getIncFontSizeButtonModel() {
		return incFontSizeButtonModel;
	}

	public ButtonModel getDecFontSizeButtonModel() {
		return decFontSizeButtonModel;
	}

	public ButtonModel getCoding0ButtonModel() {
		return coding0ButtonModel;
	}

	public ButtonModel getCoding1ButtonModel() {
		return coding1ButtonModel;
	}

	public ButtonModel getCoding2ButtonModel() {
		return coding2ButtonModel;
	}

	public ButtonModel getCodingNoneButtonModel() {
		return nonCodingButtonModel;
	}

	public ButtonModel getDrawCoonPosOnRulerButtonModel() {
		return drawCoonPosOnRulerButtonModel;
	}

	public ButtonModel getTransOnePosButtonModel() {
		return transOnePosButtonModel;
	}

	public ButtonModel getCountCodonButtonModel() {
		return countCodonButtonModel;
	}



	/*
	 * 
	 * 
	 * AlignmentListener
	 * 
	 * 
	 */
	public void newSequences(AlignmentEvent alignmentEvent) {
		this.updateAllMenuEnabled();
	}

	public void alignmentMetaChanged(AlignmentEvent alignmentEvent) {

	}
	
	//
	// AlignmentDataListener
	//
	public void intervalAdded(ListDataEvent e) {
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}

	public void intervalRemoved(ListDataEvent e) {
        if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}  	
	
	public void contentsChanged(ListDataEvent e) {
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}
	
	public void contentsChanged(AlignmentDataEvent e) {
		//this.updateAllMenuEnabled();
	}
	
	
	//
	// AlignmentSelectionListener
	//
	public void selectionChanged(AlignmentSelectionEvent e) {
		if(aliViewWindow.getAlignment().isNucleotideAlignment()){
			setHasSelectionFunctionsEnabled(aliViewWindow.getAlignment().hasSelection());
			setAAFunctionsEnabled(false);
			setEditFunctionsEnabled(aliViewWindow.getAlignment().isEditable());
		}
		if(aliViewWindow.getAlignment().isAAAlignment()){
			setHasSelectionFunctionsEnabled(aliViewWindow.getAlignment().hasSelection());
			setNucleotideFunctionsEnabled(false);
			setEditFunctionsEnabled(aliViewWindow.getAlignment().isEditable());
		}
	}
	
	public void recentFilesChanged() {
		rebuildRecentFilesSubmenu();
	}

	public void alignAllCmdsChanged() {
	}

	public void alignAddCmdsChanged() {
	}

	public void externalCmdsChanged() {
		rebuildExternalCommandsSubmenu();
	}

	public ButtonModel getCopyAsFastaButtonModel() {
		return copyAsFastaButtonModel;
	}

	public ButtonModel getCopyAsCharactersButtonModel() {
		return copyAsCharatersButtonModel;
	}

	public ButtonModel getRealignSelectedBlock() {
		return realignSelectedBlockButtonModel;
	}

	public ButtonModel getRealignSelectedSequences() {
		return realignSelectedSequencesButtonModel;
	}

	public ButtonModel getPasteAsFastaButtonModel() {
		return pasteAsFastaButtonModel;
	}

	public void editModeChanged() {
		logger.info("aliViewWindow.isEditMode()" + aliViewWindow.isEditMode());
		editModeButtonModel.setSelected(aliViewWindow.isEditMode());
	}

	public ButtonModel getCopyNameButtonModel() {
		return copyNameButtonModel;
	}

	public ButtonModel getRenameButtonModel() {
		return renameButtonModel;
	}
	
	public ButtonModel getAddNewSequenceButtonModel() {
		return addEmptySeqButtonModel;
	}
	
	

}
