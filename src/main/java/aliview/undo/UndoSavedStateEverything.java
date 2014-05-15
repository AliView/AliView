package aliview.undo;

import java.util.List;

import aliview.AliViewWindow;
import aliview.alignment.AlignmentMeta;
import aliview.sequencelist.SequenceListModel;
import aliview.sequences.Sequence;

public class UndoSavedStateEverything extends UndoSavedState{
	
	public String fastaAlignment;
	public AlignmentMeta meta;
	public SequenceListModel sequences;

	public UndoSavedStateEverything(String fastaAlignment,AlignmentMeta meta){
		this.fastaAlignment = fastaAlignment;
		this.meta = meta;
	}

	public UndoSavedStateEverything(SequenceListModel copy, AlignmentMeta meta) {
		this.sequences = copy;
		this.meta = meta;
	}
}
