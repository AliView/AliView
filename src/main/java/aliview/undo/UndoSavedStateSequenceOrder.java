package aliview.undo;

import java.util.List;

import aliview.alignment.AlignmentMeta;
import aliview.sequencelist.AlignmentListModel;
import aliview.sequences.Sequence;

public class UndoSavedStateSequenceOrder extends UndoSavedState{
	public AlignmentMeta meta;
	public AlignmentListModel sequenceListModel;

	public UndoSavedStateSequenceOrder(AlignmentListModel sequenceListModel,AlignmentMeta meta){
		this.sequenceListModel = sequenceListModel;
		this.meta = meta;
	}
}
