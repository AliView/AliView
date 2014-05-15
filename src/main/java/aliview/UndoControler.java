package aliview;

import aliview.undo.UndoSavedState;

public interface UndoControler {
	
	public void pushUndoState();
	
	public void pushUndoState(UndoSavedState undoSavedState);
	
	public void undo();
		
	public void redo();

}
