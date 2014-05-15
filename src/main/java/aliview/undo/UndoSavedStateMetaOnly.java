package aliview.undo;

import aliview.alignment.AlignmentMeta;

public class UndoSavedStateMetaOnly extends UndoSavedState {
	public AlignmentMeta meta;

	public UndoSavedStateMetaOnly(AlignmentMeta meta){
		this.meta = meta;
	}
}
