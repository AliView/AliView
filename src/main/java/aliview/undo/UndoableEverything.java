package aliview.undo;

import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import aliview.alignment.AlignmentMeta;
import aliview.sequences.Sequence;


public class UndoableEverything extends AbstractUndoableEdit{
	
	private String fastaAlignment;
	private AlignmentMeta meta;

	public UndoableEverything(String fastaAlignment,AlignmentMeta meta){
		this.fastaAlignment = fastaAlignment;
		this.meta = meta;
	}
	
	public String getPresentationName() { return "Edit"; }
	
	
	public void undo() throws CannotUndoException {
		// aliviewWindow.restoreAlignmentFromFasta(fastaAlignment, meta);
	}
	
	public void redo() throws CannotRedoException {
		// aliviewWindow.restoreAlignmentFromFasta(fastaAlignment, meta);
	}
}
