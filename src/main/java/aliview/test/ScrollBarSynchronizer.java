package aliview.test;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/**
 *        Simple panel that illustrates the synchronization of two JScrollPanes.
 *        The number of elements in the two JList components do not have to be
 *        the same. The ChangeListener will synchronize their respective
 *        BoundedRangeModels and scale as necessary.
 *        <p> Note: Since it is the right JScrollBar's model-ChangeListener that listens
 *        for changes from the left JScrollBar, it is the right JList that controls
 *        the total number of elements shown in the JScrollPanes. </p>
 *
 *        @author  R. Kevin Cole
 */
public class ScrollBarSynchronizer extends JPanel
{
        private MyChangeListener scrollBarListener;


		/**
         *         Two JLists are created and initialized from Vectors and
         *         then added to JScrollPanes to test the synchronized JScrollBars.
         */
        public ScrollBarSynchronizer()
        {
                super( new BorderLayout() );

                //        create something to scroll
                Vector leftData = new Vector();
                Vector rightData = new Vector();
                for( int i = 0; i < 71; i++ )
                {
                        rightData.add( "Right " + i );

                        //        the modulo operator is used to create a different number
                        //        of elements within each scrolling region.
                  //      if( (i % 2) == 0 )
                                leftData.add( "Left " + i );
                }
                JScrollPane leftScrollPane = new JScrollPane( new JList( leftData ) );
              //  leftScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );

          //      leftScrollPane.setVerticalScrollBar(verticalScrollBar)
                
                JScrollPane rightScrollPane = new JScrollPane( new JList( rightData ) );
       
                //leftScrollPane.setVerticalScrollBar(rightScrollPane.getVerticalScrollBar());
                
                JScrollBar  leftBar = leftScrollPane.getVerticalScrollBar();
                JScrollBar  rightBar = rightScrollPane.getVerticalScrollBar();
       
                scrollBarListener = new MyChangeListener(leftBar.getModel());
                rightBar.getModel().addChangeListener( scrollBarListener );
                MyChangeListener listener2 = new MyChangeListener(rightBar.getModel());
                leftBar.getModel().addChangeListener( listener2 );
       
                JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT
                                                , leftScrollPane
                                                , rightScrollPane );
                add( split, BorderLayout.CENTER );
        }
   

        /**
         *         Test routine.
         */
        public static void main( String[] arg )
        {
                JFrame  frame = new JFrame("Test ScrollBarSynchronizer");
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

                frame.setContentPane( new ScrollBarSynchronizer() );
                frame.setSize( 640, 480 );
                frame.setLocation( 100, 100 );
                frame.setVisible(true);
        }
}

/**
 *        Synchronize the data models of any two JComponents that use a
 *         BoundedRangeModel ( such as a JScrollBar, JSlider or ProgressBar).
 *
 *        @see javax.swing.BoundedRangeModel
 *        @see javax.swing.event.ChangeListener
 *        @Author  R. Kevin Cole        
 */
class MyChangeListener implements  ChangeListener
{
        private BoundedRangeModel myModel;

        /**
         *        @param   model  This model is forced to move in synchronization
         *                to this ChangeListener's event-source.
         */
        public MyChangeListener( BoundedRangeModel model )
        {
                myModel = model;
        }


//        - begin - implementation of ChangeListener
//
        /**
         *         Envoked when the target of the listener has changed its state.
         */
        public void stateChanged( ChangeEvent e )
        {
                BoundedRangeModel sourceModel = (BoundedRangeModel) e.getSource();
       
                int sourceDiff  = sourceModel.getMaximum() - sourceModel.getMinimum();
                int destDiff  = myModel.getMaximum() - myModel.getMinimum();
                int destValue = sourceModel.getValue();

                if( sourceDiff != destDiff )
                        destValue   = (destDiff * sourceModel.getValue())/sourceDiff;
       
                myModel.setValue( myModel.getMinimum() + destValue );
        }
//
//        - end   - implementation of ChangeListener

}