package aliview.old;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
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
public	class MyChangeListener2 implements  ChangeListener
{
	private BoundedRangeModel myModel;
	private JScrollPane source;
	private JScrollPane dest;

	/**
	 *        @param   model  This model is forced to move in synchronization
	 *                to this ChangeListener's event-source.
	 */
	public MyChangeListener2( JScrollPane source, JScrollPane dest )
	{
		this.source = source;
		this.dest=dest;
	}


	//	        - begin - implementation of ChangeListener
	//
	/**
	 *         Envoked when the target of the listener has changed its state.
	 */
	public void stateChanged( ChangeEvent e )
	{
		dest.getViewport().setViewPosition(source.getViewport().getViewPosition());
	}
	//
	//	        - end   - implementation of ChangeListener

}
