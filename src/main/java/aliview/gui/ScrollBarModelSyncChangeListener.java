package aliview.gui;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *        Synchronize the data models of any two JComponents that use a
 *         BoundedRangeModel ( such as a JScrollBar, JSlider or ProgressBar).
 *
 *        @see javax.swing.BoundedRangeModel
 *        @see javax.swing.event.ChangeListener
 *        @Author  R. Kevin Cole        
 */
public	class ScrollBarModelSyncChangeListener implements  ChangeListener
{
	private BoundedRangeModel myModel;

	/**
	 *        @param   model  This model is forced to move in synchronization
	 *                to this ChangeListener's event-source.
	 */
	public ScrollBarModelSyncChangeListener( BoundedRangeModel model )
	{
		myModel = model;
	}


	//	        - begin - implementation of ChangeListener
	//
	/**
	 *         Envoked when the target of the listener has changed its state.
	 */
	public void stateChanged( ChangeEvent e ){
		BoundedRangeModel sourceModel = (BoundedRangeModel) e.getSource();

		if( sourceModel.getValue() != myModel.getValue() ){
			myModel.setValue( sourceModel.getValue() );
		}

	}
	//
	//	        - end   - implementation of ChangeListener

}
