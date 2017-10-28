package aliview;

import java.util.ArrayList;

import aliview.undo.UndoSavedState;
import aliview.undo.UndoSavedStateEverything;

public class UndoList{
	private ArrayList<UndoSavedState> delegate = new ArrayList<UndoSavedState>();
	private int positionPointer = -1;

	public void add(UndoSavedState state) {
		delegate.add(state);
		positionPointer = delegate.size() - 1;
	}

	public boolean hasAvailableUndos() {
		if(delegate.size() > 0 && positionPointer >= 0){
			return true;
		}
		return false;
	}

	public boolean hasAvailableRedos() {
		if(delegate.size() > 0 && positionPointer < (delegate.size()-1)){
			return true;
		}
		return false;
	}

	public void addCurrentState(UndoSavedState state) {
		delegate.add(state);
		positionPointer = delegate.size() - 2;
	}

	public boolean isCurrentStateNeeded() {
		if(positionPointer == delegate.size() - 1){
			return true;
		}
		return false;
	}

	public UndoSavedState getUndoState(){
		UndoSavedState state = null;
		if(hasAvailableUndos()){
			state = delegate.get(positionPointer);
			positionPointer --;
		}
		return state;
	}

	public UndoSavedState getRedoState(){
		UndoSavedState state = null;
		if(hasAvailableRedos()){
			positionPointer ++;
			state = delegate.get(positionPointer);
		}
		return state;
	}

}
