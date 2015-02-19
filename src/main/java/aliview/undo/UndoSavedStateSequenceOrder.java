package aliview.undo;

import java.util.List;

import aliview.alignment.AlignmentMeta;
import aliview.sequencelist.AlignmentListModel;
import aliview.sequences.Sequence;

public class UndoSavedStateSequenceOrder extends UndoSavedState{
	public AlignmentMeta meta;
	public List<Sequence> sequencesBackend;

	public UndoSavedStateSequenceOrder(List<Sequence> sequencesBackendCopy,AlignmentMeta meta){
		this.sequencesBackend = sequencesBackendCopy;
		this.meta = meta;
	}
}
