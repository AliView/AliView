package aliview;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import aliview.gui.AppIcons;

import utils.OSNativeUtils;


public abstract class AliAction extends AbstractAction {
	AliViewWindow aliViewWindow;

	public AliAction(String name, String actionCommandKey, String tooltip, KeyStroke accelerator, ImageIcon smallIcon){
		super(name, smallIcon);
		putValue(SHORT_DESCRIPTION, tooltip);
		putValue(ACCELERATOR_KEY, accelerator);
	    putValue(ACTION_COMMAND_KEY, actionCommandKey); 
	    //putValue(SMALL_ICON, smallIcon); 
	}



class NewAction extends AliAction{
	public NewAction(String name, String actionCommandKey, String tooltip,
			KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}

	public void actionPerformed(ActionEvent e) {
		AliView.createNewWindow();
	}
}

public AliAction newAction = new NewAction("New", "NewAction", null, null, AppIcons.getNewIcon());

class OpenFileAction extends AliAction{	
	public OpenFileAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}

	public void actionPerformed(ActionEvent e) {
		AliView.openAlignmentFileViaChooser(aliViewWindow.getParent());
	}
}
public AliAction openFileAction = new OpenFileAction("Open File", "OpenFileAction", null, OSNativeUtils.getOpenFileAccelerator(), null);

class ReloadFileAction extends AliAction{	
	public ReloadFileAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}

	public void actionPerformed(ActionEvent e) {
		aliViewWindow.reloadCurrentFile();
	}
}
public AliAction reloadFileAction = new ReloadFileAction("Reload File", "ReloadFileAction", null, OSNativeUtils.getReloadKeyAccelerator(), null);

class SaveFileAction extends AliAction{	
	public SaveFileAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentFile();
	}
}
public AliAction saveFileAction = new SaveFileAction("Save", "SaveFileAction", null, OSNativeUtils.getSaveFileAccelerator(), null);

class SaveAsFastaAction extends AliAction{	
	public SaveAsFastaAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.FASTA, false);
	}
}
public AliAction saveAsFastaAction = new SaveAsFastaAction("Save as Fasta", "SaveAsFastaAction", null, null, null);

class SaveAsNexusAction extends AliAction{	
	public SaveAsNexusAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}

	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.NEXUS, false);
	}
}
public AliAction saveAsNexusAction = new SaveAsNexusAction("Save as Nexus", "SaveAsNexusAction", null, null, null);

class SaveAsSimpleNexusAction extends AliAction{	
	public SaveAsSimpleNexusAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}

	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.NEXUS_SIMPLE, false);
	}
}
public AliAction saveAsSimpleNexusAction = new SaveAsSimpleNexusAction("Save as Nexus (Simplified Names - MrBayes)", "SaveAsSimpleNexusAction", null, null, null);

class SaveAsCodonposNexusAction extends AliAction{	
	public SaveAsCodonposNexusAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.NEXUS_CODONPOS_CHARSET, false);
	}
}
public AliAction saveAsCodonposNexusAction = new SaveAsCodonposNexusAction("Save as codonpos Nexus (codonpos as charsets - excluded removed)", "SaveAsCodonposNexusAction", null, null, null);

class SaveAsPhylipAction extends AliAction{	
	public SaveAsPhylipAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}

	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.PHYLIP, false);
	}
}
public AliAction saveAsPhylipAction = new SaveAsPhylipAction("Save as Phylip", "SaveAsPhylipAction", null, null, null);

class PrintAction extends AliAction{	
	public PrintAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}

	public void actionPerformed(ActionEvent e) {
		aliViewWindow.printAlignment();
	}
}
public AliAction printAction = new PrintAction("Print", "PrintAction", null, OSNativeUtils.getPrintAccelerator(), null);

class ExportAsImageAction extends AliAction{	
	public ExportAsImageAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}

	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.IMAGE_PNG, true);
	}
}
public AliAction exportAsImageAction = new ExportAsImageAction("Export alignment as image", "ExportAsImageAction", null, null, null);


class SaveSelectionAsFastaAction extends AliAction{	
	public SaveSelectionAsFastaAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveSelectionAsFastaFileViaChooser();
	}
}
public AliAction saveSelectionAsFastaAction = new SaveSelectionAsFastaAction("Save selection as Fasta", "SaveSelectionAsFastaAction", null, null, null);

class SaveTranslatedAsFastaAction extends AliAction{	
	public SaveTranslatedAsFastaAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.FASTA_TRANSLATED_AMINO_ACID, false);
	}
}
public AliAction saveTranslatedAsFastaAction = new SaveTranslatedAsFastaAction("Save Translated alignment (Amino Acid) as Fasta", "SaveTranslatedAsFastaAction", null, null, null);

class SaveTranslatedAsPhylipAction extends AliAction{	
	public SaveTranslatedAsPhylipAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.PHYLIP_TRANSLATED_AMINO_ACID, false);
	}
}
public AliAction saveTranslatedAsPhylipAction = new SaveTranslatedAsPhylipAction("Save Translated alignment (Amino Acid) as Phylip", "SaveTranslatedAsPhylipAction", null, null, null);

class SaveTranslatedAsNexusAction extends AliAction{	
	public SaveTranslatedAsNexusAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.saveAlignmentAsFileViaChooser(FileFormat.NEXUS_TRANSLATED_AMINO_ACID, false);
	}
}
public AliAction saveTranslatedAsNexusAction = new SaveTranslatedAsNexusAction("Save Translated alignment (Amino Acid) as Nexus", "SaveTranslatedAsNexusAction", null, null, null);

class DebugAction extends AliAction{	
	public DebugAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		Logger.getRootLogger().setLevel(Level.ALL);
	}
}
public AliAction debugAction = new DebugAction("Start debug", "DebugAction", null, null, null);

class ShowMessageAction extends AliAction{	
	public ShowMessageAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.showMessageLog();
	}
}
public AliAction showMessageAction = new ShowMessageAction("Show message log", "ShowMessageAction", null, null, null);

class AboutAction extends AliAction{	
	public AboutAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.showAbout();
	}
}
public AliAction aboutAction = new AboutAction("About", "AboutAction", null, null, null);

class ExitAction extends AliAction{	
	public ExitAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		AliView.quitProgram();
	}
}
public AliAction exitAction = new ExitAction("Exit", "ExitAction", null, null, AppIcons.getQuitIcon());

class UndoAction extends AliAction{	
	public UndoAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.getUndoControler().undo();
	}
}
public AliAction undoAction = new UndoAction("Undo", "UndoAction", null, OSNativeUtils.getUndoKeyAccelerator(), AppIcons.getUndoIcon());

class RedoAction extends AliAction{	
	public RedoAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.getUndoControler().redo();
	}
}
public AliAction redoAction = new RedoAction("Redo", "RedoAction", null, OSNativeUtils.getRedoKeyAccelerator(), AppIcons.getRedoIcon());

class CopySelectionAsFastaAction extends AliAction{	
	public CopySelectionAsFastaAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.copySelectionAsFasta();
	}
}
public AliAction copySelectionAsFastaAction = new CopySelectionAsFastaAction("Copy selection as fasta", "CopySelectionAsFastaAction", null, OSNativeUtils.getCopySelectionAsFastaKeyAccelerator(), null);

class CopySelectionAsCharactersAction extends AliAction{	
	public CopySelectionAsCharactersAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.copySelectionAsNucleotides();
	}
}
public AliAction copySelectionAsCharactersAction = new CopySelectionAsCharactersAction("Copy selection as characters", "CopySelectionAsCharactersAction", null, OSNativeUtils.getCopyKeyAccelerator(), null);

class PasteAction extends AliAction{	
	public PasteAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.pasteFasta();
	}
}
public AliAction pasteAction = new PasteAction("Paste (fasta-sequences)", "PasteAction", null, OSNativeUtils.getPasteKeyAccelerator(), null);

class AddFromFileAction extends AliAction{	
	public AddFromFileAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e) {
		aliViewWindow.addSequencesFromFile(0);
	}
}
public AliAction addFromFileAction = new AddFromFileAction("Add sequences from file", "AddFromFileAction", null, null, null);

class EditModeAction extends AliAction{	
	public EditModeAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource() instanceof AbstractButton){
			boolean isSelected = ((AbstractButton) e.getSource()).isSelected();
			putValue(Action.SELECTED_KEY, isSelected);
			aliViewWindow.setEditMode(isSelected);
		}
	}
}
public AliAction editModeAction = new EditModeAction("Edit mode", "EditModeAction", null, null, null);

class ClearSelectedCharactersAction extends AliAction{	
	public ClearSelectedCharactersAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.clearSelectedBases();
	}
}
public AliAction clearSelectedCharactersAction = new ClearSelectedCharactersAction("Clear selected characters", "ClearSelectedCharactersAction", null, OSNativeUtils.getClearKeyAccelerator(), AppIcons.getClearIcon());

class DeleteSelectedAction extends AliAction{	
	public DeleteSelectedAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.deleteSelected();
	}
}
public AliAction deleteSelectedAction = new DeleteSelectedAction("Delete selected", "DeleteSelectedAction", null, OSNativeUtils.getDeleteKeyAccelerator(), null);

class DeleteVerticalGapsAction extends AliAction{	
	public DeleteVerticalGapsAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.removeVerticalGaps();
	}
}
public AliAction deleteVerticalGapsAction = new DeleteVerticalGapsAction("Delete vertical gaps", "DeleteVerticalGapsAction", null, null, null);

class DeleteGapsAction extends AliAction{	
	public DeleteGapsAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.deleteAllGaps();
	}
}
public AliAction deleteGapsAction = new DeleteGapsAction("Delete all gaps in all sequences", "DeleteGapsAction", null, null, null);

class TrimAction extends AliAction{	
	public TrimAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.trimSequences();
	}
}
public AliAction trimAction = new TrimAction("Trim sequences", "TrimAction", null, null, null);


class DeleteExcludedAction extends AliAction{	
	public DeleteExcludedAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.deleteExludedBases();
	}
}
public AliAction deleteExcludedAction = new DeleteExcludedAction("Delete excluded bases", "DeleteExcludedAction", null, null, null);

class DeleteEmptyAction extends AliAction{	
	public DeleteEmptyAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.deleteEmptySequences();
	}
}
public AliAction deleteEmptyAction = new DeleteEmptyAction("Delete empty sequences", "DeleteEmptyAction", null, null, null);

class FindAction extends AliAction{	
	public FindAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.find();
	}
}
public AliAction findAction = new FindAction("Find", "FindAction", null, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), null);

class FindFromClipboardAction extends AliAction{	
	public FindFromClipboardAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.findNamesFromClipboard();
	}
}
public AliAction findFromClipboardAction = new FindFromClipboardAction("Find sequence names from clipboard", "FindFromClipboardAction", null, null, null);

class MergeTwoSeqAction extends AliAction{	
	public MergeTwoSeqAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.merge2SelectedSequences();
	}
}
public AliAction mergeTwoSeqAction = new MergeTwoSeqAction("Merge 2 selected sequences", "MergeTwoSeqAction", null, KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), null);


class RevCompSelectedAction extends AliAction{	
	public RevCompSelectedAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.reverseComplementSelectedSequences();
	}
}
public AliAction revCompSelectedAction = new RevCompSelectedAction("Reverse Complement Selected Sequences", "RevCompSelectedAction", null, null, null);

class RevCompAlimentAction extends AliAction{	
	public RevCompAlimentAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.reverseComplementAlignment();
	}
}
public AliAction revCompAlimentAction = new RevCompAlimentAction("Reverse Complement Alignment", "RevCompAlimentAction", null, null, null);

class ComplementAlimentAction extends AliAction{	
	public ComplementAlimentAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.complementAlignment();
	}
}
public AliAction complementAlimentAction = new ComplementAlimentAction("Complement Alignment", "ComplementAlimentAction", null, null, null);

class RevCompClipboardAction extends AliAction{	
	public RevCompClipboardAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.reverseComplementClipboard();
	}
}
public AliAction revCompClipboardAction = new RevCompClipboardAction("Reverse Complement Clipboard", "RevCompClipboardAction", null, null, null);

class PreferencesAction extends AliAction{	
	public PreferencesAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.openPreferencesGeneral();
	}
}
public AliAction preferencesAction = new PreferencesAction("Preferences", "PreferencesAction", null, null, null);

class SelectAllAction extends AliAction{	
	public SelectAllAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.selectAll();
	}
}
public AliAction selectAllAction = new SelectAllAction("Select all", "SelectAllAction", null, OSNativeUtils.getSelectAllKeyAccelerator(), null);

class ClearSelectionAction extends AliAction{	
	public ClearSelectionAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.clearSelection();
	}
}
public AliAction clearSelectionAction = new ClearSelectionAction("Clear selection", "ClearSelectionAction", null, null, null);

class MoveUpSelectionAction extends AliAction{	
	public MoveUpSelectionAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.moveSelectionUp();
	}
}
public AliAction moveUpSelectionAction = new MoveUpSelectionAction("Move selected sequences up", "MoveUpSelectionAction", null, OSNativeUtils.getMoveSelectionUpKeyAccelerator(), AppIcons.getGoUpIcon());

class MoveDownSelectionAction extends AliAction{	
	public MoveDownSelectionAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.moveSelectionDown();
	}
}
public AliAction moveDownSelectionAction = new MoveDownSelectionAction("Move selected sequences down", "MoveDownSelectionAction", null, OSNativeUtils.getMoveSelectionDownKeyAccelerator(), AppIcons.getGoDownIcon());

class MoveTopSelectionAction extends AliAction{	
	public MoveTopSelectionAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.moveSelectionToTop();
	}
}
public AliAction moveTopSelectionAction = new MoveTopSelectionAction("Move selected sequences to top", "MoveTopSelectionAction", null, KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_MASK), AppIcons.getGoTopIcon());

class MoveBottomSelectionAction extends AliAction{	
	public MoveBottomSelectionAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.moveSelectionToTop();
	}
}
public AliAction moveBottomSelectionAction = new MoveBottomSelectionAction("Move selected sequences to bottom", "MoveBottomSelectionAction", null, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_MASK), AppIcons.getGoBottomIcon());

class AddSelectionToExcludesAction extends AliAction{	
	public AddSelectionToExcludesAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.addSelectionToExcludes();
	}
}
public AliAction addSelectionToExcludesAction = new AddSelectionToExcludesAction("Add selection to Excludes/Exset", "AddSelectionToExcludesAction", null, OSNativeUtils.getAddExcludesKeyAccelerator(), null);

class RemoveSelectionFromExcludesAction extends AliAction{	
	public RemoveSelectionFromExcludesAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.removeSelectionFromExcludes();
	}
}
public AliAction removeSelectionFromExcludesAction = new RemoveSelectionFromExcludesAction("Remove selection from Excludes/Exset", "RemoveSelectionFromExcludesAction", null, null, null);

class SetSelectionCoding0Action extends AliAction{	
	public SetSelectionCoding0Action(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.setSelectionAsCoding(0);
	}
}
public AliAction setSelectionCoding0Action = new SetSelectionCoding0Action("Set selection as coding (selection starting with codon position=1)", "SetSelectionCoding0Action", "Set selection as coding 1-2-3", null, AppIcons.getCoding1Icon());

class SetSelectionCoding1Action extends AliAction{	
	public SetSelectionCoding1Action(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.setSelectionAsCoding(1);
	}
}
public AliAction setSelectionCoding1Action = new SetSelectionCoding1Action("Set selection as coding (selection starting with codon position=2)", "SetSelectionCoding1Action", "Set selection as coding 2-3-1", null, AppIcons.getCoding2Icon());

class SetSelectionCoding2Action extends AliAction{	
	public SetSelectionCoding2Action(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.setSelectionAsCoding(2);
	}
}
public AliAction setSelectionCoding2Action = new SetSelectionCoding2Action("Set selection as coding (selection starting with codon position=3)", "SetSelectionCoding2Action", "Set selection as coding 3-1-2", null, AppIcons.getCoding3Icon());

class SetSelectionNonCodingAction extends AliAction{	
	public SetSelectionNonCodingAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.setSelectionAsNonCoding();
	}
}
public AliAction setSelectionNonCodingAction = new SetSelectionNonCodingAction("Set selection as non-coding", "SetSelectionNonCodingAction", "Set selection as non coding", null, AppIcons.getCodingNoneIcon());

class DecFontSizeAction extends AliAction{	
	public DecFontSizeAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.zoomOut();
	}
}
public AliAction decFontSizeAction = new DecFontSizeAction("Decrease Font Size", "DecFontSizeAction", "Decrease font size - can also be done with Mouse-Wheel and Ctrl-button or -", OSNativeUtils.getDecreaseFontSizeKeyAccelerator(), AppIcons.getDecFontSize());

		
class IncFontSizeAction extends AliAction{	
public IncFontSizeAction(String name, String actionCommandKey,
			String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
		super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
		aliViewWindow.zoomIn();
	}
}
public AliAction incFontSizeAction = new IncFontSizeAction("Increase Font Size", "IncFontSizeAction", "Increase font size - can also be done with Mouse-Wheel and Ctrl-button or +", OSNativeUtils.getIncreaseFontSizeKeyAccelerator(), AppIcons.getIncFontSize());


class HighlightConsAction extends AliAction{	
	public HighlightConsAction(String name, String actionCommandKey,
				String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
			super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
			if(e.getSource() instanceof AbstractButton){
				boolean isSelected = ((AbstractButton) e.getSource()).isSelected();
				putValue(Action.SELECTED_KEY, isSelected);
				aliViewWindow.setHighlightConsensus(isSelected);
				if(isSelected){
					aliViewWindow.setHighlightNonConsensus(!isSelected);
					aliViewWindow.setHighlightDiff(!isSelected);
					aliViewWindow.setShowTranslation(!isSelected);
				}
			}
	}
	
}
public AliAction highlightConsAction = new HighlightConsAction("Highlight consensus characters", "HighlightConsAction", "Highligt majority rule consensus characters", null, AppIcons.getHighlightConsIcon());

class HighlightNonConsAction extends AliAction{	
	public HighlightNonConsAction(String name, String actionCommandKey,
				String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
			super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
			if(e.getSource() instanceof AbstractButton){
				boolean isSelected = ((AbstractButton) e.getSource()).isSelected();
				aliViewWindow.setHighlightNonConsensus(isSelected);
				putValue(Action.SELECTED_KEY, isSelected);
				if(isSelected){
					aliViewWindow.setHighlightDiff(!isSelected);
					aliViewWindow.setShowTranslation(!isSelected);
					aliViewWindow.setHighlightConsensus(!isSelected);
				}
			}
	}
}
public AliAction highlightNonConsAction = new HighlightNonConsAction("Highlight Non-consensus characters", "HighlightNonConsAction", "Highligt difference from majority rule consensus", null, AppIcons.getHighlightNonConsIcon());


class HighlightDiffAction extends AliAction{	
	public HighlightDiffAction(String name, String actionCommandKey,
				String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
			super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
			if(e.getSource() instanceof AbstractButton){
				boolean isSelected = ((AbstractButton) e.getSource()).isSelected();
				aliViewWindow.setHighlightDiff(isSelected);
				putValue(Action.SELECTED_KEY, isSelected);
				if(isSelected){
					aliViewWindow.setShowTranslation(!isSelected);
					aliViewWindow.setHighlightNonConsensus(!isSelected);
					aliViewWindow.setHighlightConsensus(!isSelected);
				}
			}
	}
}
public AliAction highlightDiffAction = new HighlightDiffAction("Highlight diff from a selected sequence", "HighlightDiffAction", "<html>Highligt difference from one selected \"trace\"-sequence<br>(Select trace sequence by right clicking on target)</html>", null, AppIcons.getDiffIcon());

class ToggleTranslationAction extends AliAction{	
	public ToggleTranslationAction(String name, String actionCommandKey,
				String tooltip, KeyStroke accelerator, ImageIcon smallIcon) {
			super(name, actionCommandKey, tooltip, accelerator, smallIcon);
	}
	public void actionPerformed(ActionEvent e){
			if(e.getSource() instanceof AbstractButton){
				boolean isSelected = ((AbstractButton) e.getSource()).isSelected();
				aliViewWindow.setShowTranslation(isSelected);
				putValue(Action.SELECTED_KEY, isSelected);
				if(isSelected){
					aliViewWindow.setHighlightDiff(!isSelected);
					aliViewWindow.setHighlightNonConsensus(!isSelected);
					aliViewWindow.setHighlightConsensus(!isSelected);
				}
				
			}
	}
}
public AliAction toggleTranslationAction = new ToggleTranslationAction("Show as translation", "ToggleTranslationAction", "<html>Highligt difference from one selected \"trace\"-sequence<br>(Select trace sequence by right clicking on target)</html>", OSNativeUtils.getToggleTranslationKeyAccelerator(), AppIcons.getTranslateIcon());

	
}
	
