package aliview.undo;

import java.util.List;

import aliview.AliViewWindow;
import aliview.alignment.AlignmentMeta;
import aliview.sequencelist.AlignmentListModel;
import aliview.sequences.Sequence;

public class UndoSavedStateEverything extends UndoSavedState{
	
	public String fastaAlignment;
	public AlignmentMeta meta;
	public AlignmentListModel sequences;

	public UndoSavedStateEverything(String fastaAlignment,AlignmentMeta meta){
		this.fastaAlignment = fastaAlignment;
		this.meta = meta;
	}

	public UndoSavedStateEverything(AlignmentListModel copy, AlignmentMeta meta) {
		this.sequences = copy;
		this.meta = meta;
	}
}
