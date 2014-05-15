package aliview.undo;

import java.util.List;

import aliview.alignment.AlignmentMeta;
import aliview.sequencelist.SequenceListModel;
import aliview.sequences.Sequence;

public class UndoSavedStateSequenceOrder extends UndoSavedState{
	public AlignmentMeta meta;
	public SequenceListModel sequenceListModel;

	public UndoSavedStateSequenceOrder(SequenceListModel sequenceListModel,AlignmentMeta meta){
		this.sequenceListModel = sequenceListModel;
		this.meta = meta;
	}
}
